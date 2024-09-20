///
/// Created by Miroslav Hýbler on 19.02.2024
///


#ifndef JET_ARTICLE_PARSERCOMPONENT_H
#define JET_ARTICLE_PARSERCOMPONENT_H

#include "../utils/IndexWrapper.h"
#include "../utils/Constants.h"
#include "../utils/Utils.h"


class AbstractParserComponent {

    //TODO make protected and getters
public:
    std::string currentTag;
    std::string currentTagBody;
    std::string currentTagId;
    std::string currentContentOutsideTag;
    TagType currentContentType = NO_CONTENT;

protected:
    std::string input;
    IndexWrapper index;
    bool mHasNextStep;
    size_t length;
    bool isQueringTextOutsideTextTags = false;

    /**
     * True when parser is currently within <body> tag
     * @since 1.0.0
     */
    bool mHasBodyContext;


    bool isAbortingWithException;
    ErrorCode error;
    std::string errorMessage;

    //TODO protected
    size_t temporaryOutIndex = 0;


public:

    virtual void setInput(std::string &input) = 0;


    virtual void doNextStep() = 0;


    virtual void clearAllResources() = 0;


    [[nodiscard]] bool isAbortingWithError() const {
        return isAbortingWithException;
    }


    /**
     *
     * @return Errror code from abortion.
     * @since 1.0.0
     */
    ErrorCode getErrorCode() {
        return error;
    }


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string getErrorMessage() {
        return errorMessage;
    }


    /**
     * Checks if index is same as the input length.
     * @return True when parser is not done parsing yet.
     * @since 1.0.0
     */
    [[nodiscard]] bool hasNextStep() const {
        return mHasNextStep;
    };


protected:

    /**
     * When error in parsing occurs e.g. when closing tag of pair tag is not found, process should
     * be aborted.
     * @param cause Error that causes process abortion. See error code enum.
     * @since 1.0.0
     */
    virtual void abortWithError(
            ErrorCode cause,
            std::string message
    ) = 0;



    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   Final Protected Functions
    /////
    /////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @since 1.0.0
     */
    void invalidateHasNextStep() {
        mHasNextStep = index.getIndex() < length;
    }


    /**
     * Moves index to the next '<' char. When the next text is invalid sequence (like: comments)
     * index is moved at the end of sequence and false is returned.
     * @return True if index is pointing at the char '<' which is probably start of tag to process
     * and further processing is required. False otherwise.
     * @since 1.0.0
     */
    [[nodiscard]] bool moveIndexToNextTag() {
        if (!mHasNextStep) {
            //Compoent can't do next step.
            return false;
        }

        size_t i = index.getIndex();
        if (i >= length) {
            //Index is at the and, unable to make next step.
            invalidateHasNextStep();
            utils::log("PARSER-COMPONENT", "out of range, i >= length || i < 0");
            return false;
        }

        currentContentOutsideTag.clear();
        //actual index within input
        char ch;
        try {
            ch = input[i];
        } catch (const std::out_of_range &e) {
            utils::log("PARSER-COMPONENT", "out of range");
            return false;
        }

        while (ch != '<' && i < length) {
            if (isQueringTextOutsideTextTags && ch != '>' && mHasBodyContext) {
                currentContentOutsideTag += ch;
            }
            i += 1;
            try {
                ch = input[i];
            } catch (const std::out_of_range &e) {
                index.moveIndex(i);
                utils::log("PARSER-COMPONENT", "out of range in while");
                return false;
            }
        }

        if (i >= length) {
            //Next tag was not found and i gets out of the length of the content
            index.moveIndex(length);
            mHasNextStep = false;
            return false;
        }

        index.moveIndex(i);
        //char is <
        if (!utils::canProcessIncomingTag(input, length, i, temporaryOutIndex)) {
            //Char < is staring some special sequence like comment <!--
            //Moving cursor to the next '<' char
            index.moveIndex(temporaryOutIndex + 1);
            invalidateHasNextStep();
            utils::log("PARSER-COMPONENT",
                       "cant process incoming tag, index: " +
                       std::to_string(index.getIndex()) +
                       " length: " +
                       std::to_string(length));
            return false;
        }
        return true;
    }


    /**
     *
     * @param tag
     * @param tei Tag end index
     * @return
     * @since 1.0.0
     */
    bool isActualTagValidForNextProcessing(
            const std::string &tag,
            const size_t &tei
    ) {
        if (utils::fastCompare(tag, "br/")
            || utils::fastCompare(tag, "br")
            || utils::fastCompare(tag, "hr")
            || utils::fastCompare(tag, "hr/")
            || utils::fastCompare(tag, "input")
            || utils::fastCompare(tag, "source")
            || utils::fastCompare(tag, "meta")
                ) {
            index.moveIndex(tei + 1);
            invalidateHasNextStep();
            return false;
        }


        if (utils::fastCompare(tag, "noscript")
            || utils::fastCompare(tag, "script")
            || utils::fastCompare(tag, "svg")
            || utils::fastCompare(tag, "button")
            || utils::fastCompare(tag, "input")
            || utils::fastCompare(tag, "form")
                ) {
            index.moveIndex(tei + 1);
            //Skipping tags that can't be processed by library
            //Can't use findClosingTag because script can contain '<' inside of it and that breaks
            //searching for closing tag
            std::string closingTag = "</" + tag + ">";
            size_t ctsi;
            try {
                ctsi = utils::findUnsupportedTagClosing(input, tag, index.getIndex());
            } catch (ErrorCode e) {
                abortWithError(e, utils::emptyString);
                return false;
            }
            index.moveIndex(ctsi + closingTag.length());
            invalidateHasNextStep();
            return false;
        }

        return true;
    }
};

#endif //JET_ARTICLE_PARSERCOMPONENT_H