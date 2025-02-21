///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <string>
#include <map>
#include <vector>
#include "../utils/IndexWrapper.h"
#include "../utils/Constants.h"
#include "ParserComponent.h"

#ifndef JET_HTML_ARTICLE_CONTENTPARSER_H
#define JET_HTML_ARTICLE_CONTENTPARSER_H

/**
 * Basic content parser, gets the html text as input and parses it into elements to be showed natively
 * later.
 * @since 1.0.0
 * @author Miroslav Hýbler
 */
class ContentParser : public AbstractParserComponent {


public:
    std::string title;
    std::string lang;

private:
    bool hasContentToProcess = false;
    bool wasHeadParsed = false;
    bool areImagesEnabled = false;
    bool isTextFormattingEnabled = true;

    //TODO docs

    size_t tempContentIndexStart = 0;
    size_t tempContentIndexEnd = 0;
    size_t tempTagIndexStart = 0;
    size_t tempTagIndexEnd = 0;

    bool isAppending = false;
    size_t appendingIndexStart = 0;

    std::vector<std::string_view> tempOutputVector;
    std::vector<std::vector<std::string_view>> tableHolder;
    std::map<std::string, std::string> tempOutputMap;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   Public Functions
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////

public:

    /**
     * @since 1.0.0
     */
    ContentParser();


    /**
     * @since 1.0:0
     */
    ~ContentParser();


    /**
     * Sets the html content input to parse. Setting new input will always clear previous work and
     * prepares all necessary things for new process.
     * @param input
     * @since 1.0.0
     */
    void setInput(std::string &input) override;


    /**
     *
     * @param enabled
     * @since 1.0.0
     */
    void initialize(
            const bool &areImagesEmabled,
            const bool &isSimpleTextFormatAllowed,
            const bool &isQueryingTextOutsideTextTags
    );


    /**
     * Tries to the next step of the process if possible. The next step is almost always moving index
     * to the next character and when '<' char is found, tries to process if it's tag or not.
     * @since 1.0.0
     */
    void doNextStep() override;


    /**
     * Returning hasContentToProcess
     * @return True when parser have some content parsed out of supported tags. The content should
     * be send to receiver (Kotlin app) for further processing. False otherwise.
     * @since 1.0.0
     */
    bool hasParsedContentToBeProcessed() const;


    /**
     *
     * @return
     * @since 1.0.0
     */
    [[nodiscard]] bool hasBodyContext();


    /**
     *
     * @param hasContent
     * @since 1.0.0
     */
    void hasParsedContentToBeProcessed(bool hasContent);


    /**
     *
     * @return Temporary text content of the [input] determined by [tempContentIndexStart]
     * and [tempContentIndexEnd]. Can be also empty.
     * @since 1.0.0
     */
    [[nodiscard]] std::string getTempContent();


    /**
     * Clears all used resources and releases the memory at the end of parsing process
     * @since 1.0.0
     */
    void clearAllResources() override;


    /**
     *
     * @return
     * @since 1.0.0
     */
    [[nodiscard]] size_t getTempListSize();


    /**
     *
     * @return
     * @since 1.0.0
     */
    const std::vector<std::vector<std::string_view>> &getTable();


    /**
     *
     * @param i
     * @return
     * @since 1.0.0
     */
    std::string getTempListItem(size_t i);


    /**
     *
     * @param entry
     * @return
     * @since 1.0.0
     */
    std::string getTempMapItem(const std::string &attributeName);


    /**
     *
     * @return Title of the html article, similar to <title> tag in <head>
     * @since 1.0.0
     */
    std::string getTitle() const;


    /**
     *
     * @return True when process is aborting with error, meaning that provided [input] could not be
     * parsed properly.
     * @since 1.0.0
     */
    const bool isAbortingWithError();


    /**
     * @since 1.0.0
     */
    bool tryMoveToContainerClosing();


    /**
     * @return
     * @since 1.0.0
     */
    bool hasTempAppendedContentToSend();


    /**
     *
     * @return
     */
    size_t getCurrentIndex() {
        return index.getIndex();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   Private Functions
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////

private:


    /**
     * Tries to parse out head data. Parsing out between index.getIndex() and e. Tries to get the
     * title and the base. When the processing of head tag is done, index is moved at the next
     * position after </head>.
     * @param e End index. The program already knows where <head> tag has closing, this
     * @since 1.0.0
     */
    void parseHeadData(size_t e);


    /**
     * @since 1.0.0
     */
    void parseNextTagWithinBodyContext(std::string &tag, size_t &tei);


    /**
     * Parses image information from image tag.
     * @param tei Tag end index, index of '>', helping with speeding up
     * @since 1.0.0
     */

    void parseImgTag(const size_t &tei);


    /**
     *
     * @param ctsi Closing tag start index. Start index of </table> tag.
     * @since 1.0.0
     */
    void parseTableTag(const size_t &ctsi);


    /**
     *
     * @param cause
     * @param message
     * @since 1.0.0
     */
    void abortWithError(ErrorCode cause, std::string message) override;
};


#endif //JET_HTML_ARTICLE_CONTENTPARSER_H