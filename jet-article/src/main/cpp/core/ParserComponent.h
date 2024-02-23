///
/// Created by Miroslav Hýbler on 19.02.2024
///


#ifndef JET_ARTICLE_PARSERCOMPONENT_H
#define JET_ARTICLE_PARSERCOMPONENT_H

#include "../utils/IndexWrapper.h"
#include "../utils/Constants.h"
#include "../utils/Utils.h"


class AbstractParserComponent {

    //TODO make protected
public:
    std::string currentTag = "";
    std::string currentTagBody = "";
    std::string currentTagId = "";
    TagType currentContentType = NO_CONTENT;

protected:
    std::string input;
    IndexWrapper index;
    bool mHasNextStep;
    int length;


    bool mHasBodyContext;


    bool isAbortingWithException;
    ErrorCode error;
    std::string errorMessage = "";

    //TODO maybe private
    int temporaryOutIndex = 0;


public:

    virtual void setInput(std::string input) = 0;


    virtual void doNextStep() = 0;


    virtual void clearAllResources() = 0;


    virtual bool isAbortingWithError() {
        return isAbortingWithException;
    }


    /**
     * Checks if index is same as the input length.
     * @return True when parser is not done parsing yet.
     * @since 1.0.0
     */
    bool hasNextStep() {
        return mHasNextStep;
    };


protected:

    /**
     * When error in parsing occurs e.g. when closing tag of pair tag is not found, process should
     * be aborted.
     * @param cause Error that causes process abortion. See error code enum.
     * @since 1.0.0
     */
   virtual void abortWithError(ErrorCode cause) = 0;



   /////////////////////////////////////////////////////////////////////////////////////////////////
   /////
   /////   Final Protected Functions
   /////
   /////////////////////////////////////////////////////////////////////////////////////////////////


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
    bool moveIndexToNextTag() {
        if (!mHasNextStep) {
            throw "mNextStep is false";
        }

        if (index.getIndex() >= length) {
            throw "Throwing because index >= length";
        }

        //actual index within input
        char ch = input[index.getIndex()];
        while (ch != '<' && index.getIndex() < length) {
            //continuing next, no valid content to parse
            index.moveIndex(index.getIndex() + 1);
            invalidateHasNextStep();
            if (!mHasNextStep) {
                return false;
            }
            ch = input[index.getIndex()];
        }

        //char is <
        if (!utils::canProcessIncomingTag(input, length, index.getIndex(), temporaryOutIndex)) {
            //Char < is staring some special sequence like comment <!--
            //Moving cursor to the next '<' char
            index.moveIndex(temporaryOutIndex + 1);
            invalidateHasNextStep();
            return false;
        }
        return true;
    }

};

#endif //JET_ARTICLE_PARSERCOMPONENT_H