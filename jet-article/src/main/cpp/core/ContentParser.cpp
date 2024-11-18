///
/// Created by Miroslav Hýbler on 03.01.2024
///


#include <string>
#include "ContentParser.h"
#include "ContentFilter.h"
#include "../utils/Utils.h"
#include "../utils/Constants.h"


ContentParser::ContentParser() {
    index = IndexWrapper();
}


ContentParser::~ContentParser() = default;


#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wshadow"

void ContentParser::initialize(
        const bool &areImagesEnabled,
        const bool &isSimpleTextFormatAllowed,
        const bool &isQueringTextOutsideTextTags
) {
    this->areImagesEnabled = areImagesEnabled;
    this->isTextFormattingEnabled = isSimpleTextFormatAllowed;
    this->isQueringTextOutsideTextTags = isQueringTextOutsideTextTags;
}

#pragma clang diagnostic pop


void ContentParser::setInput(std::string &content) {
    clearAllResources();
    this->input = content;
    length = content.length();
    invalidateHasNextStep();
}


bool ContentParser::hasParsedContentToBeProcessed() const {
    return hasContentToProcess;
}


bool ContentParser::hasBodyContext() {
    return mHasBodyContext;
}


void ContentParser::hasParsedContentToBeProcessed(bool hasContent) {
    this->hasContentToProcess = hasContent;
    this->currentContentType = NO_CONTENT;
}


std::string ContentParser::getTempContent() {
    if (currentContentType == NO_CONTENT) {
        return "";
    }

    size_t n = tempContentIndexEnd - tempContentIndexStart;

    if (n == 0) {
        return "";
    }

    if (currentContentType == TEXT || currentContentType == TITLE) {
        std::string tempInput = input.substr(tempContentIndexStart, n);
        utils::trim(tempInput);

        std::string output;

        if (isTextFormattingEnabled) {
            utils::clearUnsupportedTagsFromTextBlock(tempInput, output, 0, tempInput.length());
        } else {
            utils::clearTagsFromText(tempInput, output);
        }

        return output;
    }

    return input.substr(tempContentIndexStart, n);
}


void ContentParser::doNextStep() {

    currentTag = "";
    currentTagBody = "";
    currentTagId = "";
    index.invalidate();

    if (!moveIndexToNextTag()) {
        //No tag to process
        invalidateHasNextStep();
        return;
    }
    //char is < and its probably start of valid tag
    //TagType end index, index of next '>'


    //Saves text that is outside regular text tags
    if (!currentSharedContent.empty() && hasBodyContext()) {
        utils::trim(currentSharedContent);
        if (!currentSharedContent.empty()) {
            utils::log("PARSER", "Text outside tags: " + currentSharedContent);
            tempContentIndexStart = index.getIndexOnStart() + 1;
            tempContentIndexEnd = index.getIndex() - 1;
            currentContentType = TEXT;
            return;
        }
    }


    //Tag end index
    size_t tei;
    try {
        tei = utils::indexOfOrThrow(input, ">", index.getIndex());
    } catch (ErrorCode e) {
        abortWithError(e, utils::emptyString);
        return;
    }

    // -1 to remove '<' at the end
    size_t tagBodyLength = tei - index.getIndex() - 1;
    //tagbody within <>, i + 1 to remove '<'
    currentTagBody = input.substr(index.getIndex() + 1, tagBodyLength);
    currentTagId = utils::getTagAttribute(currentTagBody, "id");
    std::string tag = utils::getTagName(currentTagBody);

    if (currentTagBody == "/body") {
        mHasBodyContext = false;
    }


    //Move index to the next char after tag
    if (mHasBodyContext) {
        parseNextTagWithinBodyContext(tag, tei);
        invalidateHasNextStep();
        return;
    }

    index.moveIndex(tei + 1);

    if (utils::fastCompare(tag, "html")) {
        lang = utils::getTagAttribute(currentTagBody, "lang");
    } else if (!wasHeadParsed && utils::fastCompare(tag, "head")) {
        try {
            //Closing tag start index
            size_t ctsi = utils::findClosingTag(input, tag, index.getIndex());
            parseHeadData(ctsi);
            wasHeadParsed = false;
        } catch (ErrorCode e) {
            abortWithError(e, utils::emptyString);
            return;
        }
    } else if (!mHasBodyContext && utils::fastCompare(tag, "body")) {
        mHasBodyContext = true;
    }

    invalidateHasNextStep();
}


void ContentParser::parseHeadData(size_t e) {
    while (index.getIndex() < e) {
        if (!moveIndexToNextTag()) {
            //No tag to process
            continue;
        }

        //char is < and its probably start of valid tag
        //tag end index, index of next '>'
        size_t tei;
        try {
            tei = utils::indexOfOrThrow(input, ">", index.getIndex());
        } catch (ErrorCode e) {
            abortWithError(e, utils::emptyString);
            return;
        }
        // -1 to remove '<' at the end
        size_t tagBodyLength = tei - index.getIndex() - 1;
        //tagbody within <>, i + 1 to remove '<'
        std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
        std::string tag = utils::getTagName(tagBody);
        index.moveIndex(tei + 1);

        if (utils::fastCompare(tag, "title")) {
            try {
                size_t ctsi = utils::findClosingTag(input, tag, index.getIndex(), e);
                std::string titleContent = input.substr(tei + 1, ctsi - tei - 1);
                title = titleContent;
                size_t i = index.getIndex() + ctsi + 7;
                index.moveIndex(i);
            } catch (ErrorCode e) {
                abortWithError(e, utils::emptyString);
                return;
            }
        }

        if (!title.empty()) {
            break;
        }
    }
    index.moveIndex(e + 1);
    invalidateHasNextStep();
}


void ContentParser::parseNextTagWithinBodyContext(std::string &tag, size_t &tei) {
    if (tag.find('/', 0) == 0) {
        //Skipping closing tag
        //its because after we parse out nested content, we don't know the "right" closing tag
        //example <div><p>...</p></div> after parsing <p> we move behind </p>
        index.moveIndex(tei + 1);
        invalidateHasNextStep();
        return;
    }

    if (!isActualTagValidForNextProcessing(tag, tei)) {
        //Tag is some tag which this library can't support like script, noscript, meta, ...
        //For full list visit isActualTagValidForNextProcessing function
        tempContentIndexStart = 0;
        tempContentIndexEnd = 0;
        return;
    }


    //At this point index is pointing at the sequence starting with '<' which is ready to be
    //processed as tag
    if (utils::fastCompare(tag, "img")) {
        currentTag = tag;
        parseImgTag(tei);
        index.moveIndex(tei + 1);
        return;
    }

    index.moveIndex(tei + 1);
    //closing tag start index
    size_t ctsi;
    try {
        ctsi = utils::findClosingTag(input, tag, index.getIndex());
        tempContentIndexStart = index.getIndex();
        tempContentIndexEnd = ctsi;
    } catch (ErrorCode e) {
        //Html articles has too much html syntax errors like unclosed pair tags or others,
        //so library should keep parsing, browsers are also ignoring these errors
        //Just keep parsing, just keep parsing
        return;
    }

    if (utils::fastCompare(tag, "p")
        || utils::fastCompare(tag, "span")
        || utils::fastCompare(tag, "em")
        || utils::fastCompare(tag, "pre")
            ) {
        currentContentType = TEXT;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "h1")
               || utils::fastCompare(tag, "h2")
               || utils::fastCompare(tag, "h3")
               || utils::fastCompare(tag, "h4")
               || utils::fastCompare(tag, "h5")
               || utils::fastCompare(tag, "h6")
               || utils::fastCompare(tag, "h7")
            ) {
        currentContentType = TITLE;
        hasContentToProcess = true;
    } else if (
            utils::fastCompare(tag, "ul")
            || utils::fastCompare(tag, "ol")
            ) {
        currentContentType = LIST;
        hasContentToProcess = true;
        utils::groupPairTagContents(
                input, "li", index.getIndex(), ctsi, tempOutputVector
        );
    } else if (utils::fastCompare(tag, "table")) {
        currentContentType = TABLE;
        hasContentToProcess = true;
        parseTableTag(ctsi);
    } else if (utils::fastCompare(tag, "blockquote")) {
        currentContentType = QUOTE;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "address")) {
        currentContentType = ADDRESS;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "code")) {
        currentContentType = CODE;
        hasContentToProcess = true;
    } else {
        currentContentType = NO_CONTENT;
        hasContentToProcess = false;
        tempContentIndexStart = 0;
        tempContentIndexEnd = 0;
    }

    currentTag = tag;
    if (hasContentToProcess) {
        //Moves  at next char after closing of pair tag
        index.moveIndex(ctsi);
    } else {
        //Moves at next char after open tag
        //This ussualy means that we are inside container like "div" and need to go deeper for the content
        index.moveIndex(tei);
    }
}


void ContentParser::parseImgTag(const size_t &tei) {

    if (!areImagesEnabled) {
        return;
    }

    currentContentType = IMAGE;
    hasContentToProcess = true;
    tempContentIndexStart = index.getIndex();
    tempContentIndexEnd = tei;

    if (!tempOutputMap.empty()) {
        tempOutputMap.clear();
    }
    size_t n = tempContentIndexEnd - tempContentIndexStart;
    std::string tagBody = input.substr(tempContentIndexStart, n);
    utils::getTagAttributes(tagBody, tempOutputMap);
    invalidateHasNextStep();
}


void ContentParser::parseTableTag(const size_t &ctsi) {
    bool wasHeaderRowParsed = false;
    tableHolder.clear();
    tempOutputVector.clear();
    utils::groupPairTagContents(
            input, "tr", index.getIndex(), ctsi, tempOutputVector
    );
    for (auto row: tempOutputVector) {
        std::vector<std::string_view> columns;
        size_t length = row.length();
        if (!wasHeaderRowParsed) {
            utils::groupPairTagContents(row, "th", 0, length, columns);
            wasHeaderRowParsed = true;
        } else {
            utils::groupPairTagContents(row, "td", 0, length, columns);
        }
        tableHolder.push_back(columns);
    }
}


bool ContentParser::tryMoveToContainerClosing() {
    if (currentContentType == TEXT
        || currentContentType == TITLE
        || currentContentType == QUOTE
        || currentContentType == ADDRESS
        || currentContentType == IMAGE) {
        //For not-container content there is no need to search for closing because index is already
        //at it. E.g. div can contains divs but p should not contain other p.
        return false;
    }

    std::string closing = "</" + currentTag + ">";
    size_t ctsi = utils::findClosingTag(input, currentTag, index.getIndex());
    index.moveIndex(ctsi + closing.length());
    return true;
}


size_t ContentParser::getTempListSize() {
    return tempOutputVector.size();
}


const std::vector<std::vector<std::string_view>> &ContentParser::getTable() {
    return tableHolder;
}


std::string ContentParser::getTempListItem(size_t i) {
    auto iterator = tempOutputVector.begin();
    std::advance(iterator, i);
    std::string_view input = std::string_view(*iterator);
    std::string output;
    size_t s = 0;
    size_t e = iterator->length();
    utils::clearUnsupportedTagsFromTextBlock(input, output, s, e);
    return output;
}


std::string ContentParser::getTempMapItem(const std::string &attributeName) {
    return tempOutputMap[attributeName];
}


std::string ContentParser::getTitle() const {
    return title;
}


const bool ContentParser::isAbortingWithError() {
    return isAbortingWithException;
}


void ContentParser::abortWithError(
        const ErrorCode cause,
        std::string message
) {
    this->error = cause;
    isAbortingWithException = true;
    hasContentToProcess = false;
    mHasNextStep = false;

    errorMessage = "ABORTING PARSING WITH ERROR WITH CAUSE: " + std::to_string(cause) + "\n"
                   + index.toString() + "\n"
                   + "Message: " + message + "\n"
                   + "body: " + currentTagBody + "\n"
                   + "subtring to find problematic area: " + input.substr(index.getIndex(), 50);
    utils::log("PARSER", errorMessage, ANDROID_LOG_ERROR);
    index.moveIndex(length);
}


void ContentParser::clearAllResources() {
    input.clear();
    title.clear();
    lang.clear();
    currentTag.clear();
    currentTagBody.clear();
    currentTagId.clear();
    currentContentType = NO_CONTENT;

    hasContentToProcess = false;
    mHasBodyContext = false;
    wasHeadParsed = false;
    isAbortingWithException = false;

    error = NO_ERROR;
    errorMessage.clear();
    index.reset();
    tableHolder.clear();

    length = 0;
    tempContentIndexStart = 0;
    tempContentIndexEnd = 0;
    temporaryOutIndex = 0;

    tempOutputVector.clear();
    tempOutputMap.clear();
}