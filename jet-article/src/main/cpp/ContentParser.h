///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <string>
#include <map>
#include <vector>
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
    std::string lang;

    std::string currentTag = "";
    std::string currentTagBody = "";
    TagType contentType = NO_CONTENT;

private:
    bool mHasNextStep;
    bool mHasBodyContext;
    int length;
    bool hasContentToProcess;
    bool wasHeadParsed;
    std::string input;
    IndexWrapper index;
    int tempContentIndexStart = -1;
    int tempContentIndexEnd = -1;
    std::vector<std::string_view> tempOutputVector;
    std::vector<std::vector<std::string_view>> tableHolder;
    std::map<std::string, std::string> tempOutputMap;
    bool isAbortingWithException;
    ErrorCode error;
    std::string errorMessage = "";
    int temporaryOutIndex = 0;


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
     * @return
     * @since 1.0.0
     */
    bool hasBodyContext();


    /**
     *
     * @param hasContent
     * @since 1.0.0
     */
    void hasParsedContentToBeProcessed(bool hasContent);


    /**
     * Moves index to the next '<' char. When the next text is invalid sequence (like: comments)
     * index is moved at the end of sequence and false is returned.
     * @return True if index is pointing at the char '<' which is probably start of tag to process
     * and further processing is required. False otherwise.
     * @since 1.0.0
     */
    bool moveIndexToNextTag();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string getTempContent();


    /**
     * Clear o
     * @since 1.0.0
     */
    void clearAllResources();


    /**
     *
     * @return
     * @since 1.0.0
     */
    int getTempListSize();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::vector<std::vector<std::string_view>> getTable();


    /**
     *
     * @param i
     * @return
     * @since 1.0.0
     */
    std::string_view getTempListItem(int i);


    /**
     *
     * @param entry
     * @return
     * @since 1.0.0
     */
    std::string getTempMapItem(std::string attributeName);


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string getTitle();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string getBase();


    /**
     *
     * @return
     * @since 1.0.0
     */
    bool isAbortingWithError();


    /**
     *
     * @return Errror code from abortion.
     * @since 1.0.0
     */
    ErrorCode getErrorCode();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string getErrorMessage();


    /**
     * @since 1.0.0
     */
    void tryMoveToClosing();


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
    void parseNextTagWithinBodyContext(std::string &tag, int &tei);


    /**
     * Parses image information from image tag.
     * @param tei Tag end index, index of '>', helping with speeding up
     * @since 1.0.0
     */

    void parseImageTag(const int &tei);


    void parseTableTag(const int &ctsi);


    /**
     * When error in parsing occurs e.g. when closing tag of pair tag is not found, process should
     * be aborted.
     * @param cause Error that causes process abortion. See error code enum.
     * @since 1.0.0
     */
    void abortWithError(ErrorCode cause);
};


#endif //JET_HTML_ARTICLE_CONTENTPARSER_H