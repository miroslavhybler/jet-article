///
/// Created by Miroslav Hýbler on 03.01.2024
///

//
// Created by Miroslav Hýbler on 03.01.2024.
//
#include <string>
#include "utils/IndexWrapper.h"
#include "utils/Constants.h"

#ifndef JET_HTML_ARTICLE_CONTENTPARSER_H
#define JET_HTML_ARTICLE_CONTENTPARSER_H

/**
 * Basic content parser, gets the html text as input and parses it into elements to be showed natively
 * later.
 * @since 1.0.0
 * @author Miroslav Hýbler
 */
class ContentParser {


public:
    std::string title;
    std::string base;

    std::string actualTag = "";
    std::string actualTagBody = "";
    std::string actualTagContent = "";
    TagType contentType = NO_CONTENT;

private:
    bool mHasNextStep;
    bool mHasBodyContext;
    int length;
    bool hasContentToProcess;
    bool wasHeadParsed;
    std::string input;
    std::string actualTagContext;
    IndexWrapper index;
    int tempContentIndexStart = -1;
    int tempContentIndexEnd = -1;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   Public Functions
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////
public:

    ContentParser();

    ~ContentParser();

    /**
     * Sets the html content input to parse. Setting new input will always clear previous work and
     * prepares all necessary things for new process.
     * @param input
     * @since 1.0.0
     */
    void setInput(std::string input);


    /**
     * Tries to the next step of the process if possible. The next step is almost always moving index
     * to the next character and when '<' char is found, tries to process if it's tag or not.
     * @since 1.0.0
     */
    void doNextStep();


    /**
     * Checks if index is same as the input length.
     * @return True when parser is not done parsing yet.
     * @since 1.0.0
     */
    bool hasNextStep();


    /**
     * Returning hasContentToProcess
     * @return True when parser have some content parsed out of supported tags. The content should
     * be send to receiver (Kotlin app) for further processing. False otherwise.
     * @since 1.0.0
     */
    bool hasParsedContentToBeProcessed();


    /**
     *
     * @param hasContent
     * @since 1.0.0
     */
    void hasParsedContentToBeProcessed( bool hasContent);


    /**
     * Moves index to the next '<' char. When the next text is invalid sequence (like: comments)
     * index is moved at the end of sequence and false is returned.
     * @return True if index is pointing at the char '<' which is probably start of tag to process
     * and further processing is required. False otherwise.
     * @since 1.0.0
     */
    bool moveIndexToNextTag();


    std::string getTempContent();


    /**
     * @since 1.0.0
     */
    void clearAllResources();


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   Private Functions
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////

private:

    /**
     * Checks if the index is at the end and sets mHasNextStep value.
     * @since 1.0.0
     */
    void invalidateHasNextStep();


    /**
     * Tries to parse out head data. Parsing out between index.getIndex() and e. Tries to get the
     * title and the base. When the processing of head tag is done, index is moved at the next
     * position after </head>.
     * @param e End index. The program already knows where <head> tag has closing, this
     * @since 1.0.0
     */
    void parseHeadData(int e);


    /**
     * @since 1.0.0
     */
    void parseTagsWithinBodyContext(std::string tag);

};


#endif //JET_HTML_ARTICLE_CONTENTPARSER_H
