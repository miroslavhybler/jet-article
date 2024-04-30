///
/// Created by Miroslav Hýbler on 03.01.2024
///


#include <string>
#include "ContentParser.h"
#include "BodyProcessor.h"
#include "../utils/Utils.h"
#include "../utils/Constants.h"


ContentParser::ContentParser() {
    index = IndexWrapper();
}


ContentParser::~ContentParser() {

}


void ContentParser::setInput(std::string content) {
    clearAllResources();
    this->input = content;
    length = content.length();
    invalidateHasNextStep();
}


bool ContentParser::hasParsedContentToBeProcessed() {
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

    int n = tempContentIndexEnd - tempContentIndexStart;

    if (n == 0) {
        return "";
    }

    if (currentContentType == TEXT || currentContentType == TITLE) {
        std::string tempInput = input.substr(tempContentIndexStart, n);
        utils::trim(tempInput);
        std::string output = "";
        utils::clearUnsupportedTagsFromTextBlock(tempInput, output, 0, tempInput.length());
        return output;
    }

    return input.substr(tempContentIndexStart, n);
}


void ContentParser::doNextStep() {

    currentTag = "";
    currentTagBody = "";
    currentTagId = "";

    if (!moveIndexToNextTag()) {
        //No tag to process
        invalidateHasNextStep();
        return;
    }
    //char is < and its probably start of valid tag
    //TagType end index, index of next '>'


    //Tag end index
    int tei;
    try {
        tei = utils::indexOfOrThrow(input, ">", index.getIndex());
    } catch (ErrorCode e) {
        abortWithError(e);
        return;
    }

    // -1 to remove '<' at the end
    int tagBodyLength = tei - index.getIndex() - 1;
    //tagbody within <>, i + 1 to remove '<'
    currentTagBody = input.substr(index.getIndex() + 1, tagBodyLength);
    currentTagId = utils::getTagAttribute(currentTagBody, "id");
    std::string tag = utils::getTagName(currentTagBody);

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
            int ctsi = utils::findClosingTag(input, tag, index.getIndex());
            parseHeadData(ctsi);
            wasHeadParsed = false;
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
    } else if (!mHasBodyContext && utils::fastCompare(tag, "body")) {
        mHasBodyContext = true;
    }

    invalidateHasNextStep();
}


void ContentParser::parseHeadData(int e) {
    while (index.getIndex() < e) {
        if (!moveIndexToNextTag()) {
            //No tag to process
            continue;
        }

        //char is < and its probably start of valid tag
        //tag end index, index of next '>'
        int tei;
        try {
            tei = utils::indexOfOrThrow(input, ">", index.getIndex());
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
        // -1 to remove '<' at the end
        int tagBodyLength = tei - index.getIndex() - 1;
        //tagbody within <>, i + 1 to remove '<'
        std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
        std::string tag = utils::getTagName(tagBody);
        index.moveIndex(index.getIndex() + tag.length() + 1);

        if (utils::fastCompare(tag, "title")) {
            try {
                int ctsi = utils::findClosingTag(input, tag, index.getIndex(), e);
                std::string titleContent = input.substr(tei + 1, ctsi - tei - 1);
                title = titleContent;
                int i = index.getIndex() + ctsi + 7;
                index.moveIndex(i);
            } catch (ErrorCode e) {
                abortWithError(e);
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


void ContentParser::parseNextTagWithinBodyContext(std::string &tag, int &tei) {
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
        tempContentIndexStart = -1;
        tempContentIndexEnd = -1;
        return;
    }


    //At this point index is pointing at the sequence starting with '<' which is ready to be
    //processed as tag
    if (utils::fastCompare(tag, "img")) {
        parseImageTag(tei + 1);
        return;
    }

    index.moveIndex(tei + 1);
    //closing tag start index
    int ctsi;
    try {
        ctsi = utils::findClosingTag(input, tag, index.getIndex());
        tempContentIndexStart = index.getIndex();
        tempContentIndexEnd = ctsi;
    } catch (ErrorCode e) {
        //Html artycles has too much html syntax errors like unclosed pair tags or others,
        //so library should keep parsing
        //Just keep parsing, just keep parsing
        return;
    }

    if (utils::fastCompare(tag, "p")
        || utils::fastCompare(tag, "span")
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
    } else if (utils::fastCompare(tag, "picture")) {
        //TODO maybe? handle picture, get image url from srcset from  source tag
        hasContentToProcess = false;
    } else if (utils::fastCompare(tag, "table")) {
        //Table is skipped temporary
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
        tempContentIndexStart = -1;
        tempContentIndexEnd = -1;
    }

    currentTag = tag;
    if (hasContentToProcess) {
        //Moves  at next char after closing of pair tag
        int next;
        try {
            next = utils::indexOfOrThrow(input, ">", ctsi);
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
        index.moveIndex(next + 1);
    } else {
        //Moves at next char after open tag
        //This ussualy means that we are inside container like "div" and need to go deeper for the content
        index.moveIndex(tei + 1);
    }
}


void ContentParser::parseImageTag(const int &tei) {
    currentContentType = IMAGE;
    hasContentToProcess = true;
    tempContentIndexStart = index.getIndex();
    tempContentIndexEnd = tei;

    if (!tempOutputMap.empty()) {
        tempOutputMap.clear();
    }
    int n = tempContentIndexEnd - tempContentIndexStart;
    std::string tagBody = input.substr(tempContentIndexStart, n);
    utils::getTagAttributes(tagBody, tempOutputMap);
    index.moveIndex(tei + 1);
    invalidateHasNextStep();
}


void ContentParser::parseTableTag(const int &ctsi) {
    bool wasHeaderRowParsed = false;
    utils::groupPairTagContents(
            input, "tr", index.getIndex(), ctsi, tempOutputVector
    );
    for (int i = 0; i < tempOutputVector.size(); i++) {
        std::vector<std::string_view> columns;
        std::string_view row = tempOutputVector[i];

        if (!wasHeaderRowParsed) {
            utils::groupPairTagContents(row, "th", 0, row.length(), columns);
            wasHeaderRowParsed = true;
        } else {
            utils::groupPairTagContents(row, "td", 0, row.length(), columns);
        }
        tableHolder.push_back(columns);
    }
}


bool ContentParser::tryMoveToContainerClosing() {

    //TODO maybe use direct tag check
    if (currentContentType == TEXT
        || currentContentType == TITLE
        || currentContentType == QUOTE
        || currentContentType == ADDRESS) {
        //For not-container content there is no need to search for closing because index is already
        //at it. E.g. div can contains divs but p should not contain other p.
        return false;
    }

    std::string closing = "</" + currentTag + ">";
    int ctsi = utils::indexOf(input, closing, index.getIndex());
    if (ctsi == -1) {
        return false;
    }
    index.moveIndex(ctsi + closing.length() + 1);
    return true;
}


int ContentParser::getTempListSize() {
    return tempOutputVector.size();
}


const std::vector<std::vector<std::string_view>> &ContentParser::getTable() {
    return tableHolder;
}


std::string ContentParser::getTempListItem(int i) {
    auto iterator = tempOutputVector.begin();
    std::advance(iterator, i);
    std::string_view input = std::string_view(*iterator);
    std::string output = "";
    utils::clearUnsupportedTagsFromTextBlock(input, output, 0, iterator->length());
    return output;
}


std::string ContentParser::getTempMapItem(std::string attributeName) {
    return tempOutputMap[attributeName];
}


std::string ContentParser::getTitle() {
    return title;
}


bool ContentParser::isAbortingWithError() {
    return isAbortingWithException;
}


void ContentParser::abortWithError(ErrorCode cause, std::string message) {
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
    input = "";
    title = "";
    lang = "";
    currentTag = "";
    currentTagBody = "";
    currentTagId = "";
    currentContentType = NO_CONTENT;

    hasContentToProcess = false;
    mHasBodyContext = false;
    wasHeadParsed = false;
    isAbortingWithException = false;
    error = NO_ERROR;
    errorMessage = "";
    index.reset();
    tableHolder.clear();

    length = 0;
    tempContentIndexStart = -1;
    tempContentIndexEnd = -1;
    temporaryOutIndex = 0;

    tempOutputVector.clear();
    tempOutputMap.clear();
}