///
/// Created by Miroslav Hýbler on 03.01.2024
///

//
// Created by Miroslav Hýbler on 03.01.2024.
//
#include <string>
#include "utils/IndexWrapper.h"

#ifndef JET_HTML_ARTICLE_CONTENTPARSER_H
#define JET_HTML_ARTICLE_CONTENTPARSER_H

/**
 * Basic content parser, gets the html text as input and parses it into elements to be showed natively
 * later.
 * @since 1.0.0
 */
class ContentParser {

public:
    std::string  tempContent;
    bool hasContentToProcess;
    std::string title;
    std::string base;

private:
    bool mHasNextStep;
    int length;
    std::string input;
    std::string actualTagContext;
    IndexWrapper index;


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
     * Tries to the next step of the process if possible.
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
    bool hasContent();


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
     * @param e End index. The program already knowns where <head> tag has closing, this
     * @since 1.0.0
     */
    void parseHeadData(int e);


    void parseBodyData();

};


#endif //JET_HTML_ARTICLE_CONTENTPARSER_H
