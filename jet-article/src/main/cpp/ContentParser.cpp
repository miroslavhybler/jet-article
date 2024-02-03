///
/// Created by Miroslav Hýbler on 03.01.2024
///


#include "ContentParser.h"
#include "BodyProcessor.h"
#include "utils/Utils.h"
#include "utils/Constants.h"

ContentParser::ContentParser() {
    mHasNextStep = false;
    mHasBodyContext = false;
    hasContentToProcess = false;
    wasHeadParsed = false;
    index = IndexWrapper();
    length = 0;
    input = "";
    title = "";
    base = "";
    lang = "";
}


ContentParser::~ContentParser() {
    clearAllResources();
}


void ContentParser::setInput(std::string content) {
    clearAllResources();
    this->input = content;
    length = content.length();
    invalidateHasNextStep();
}


bool ContentParser::hasNextStep() {
    return mHasNextStep;
}


bool ContentParser::hasParsedContentToBeProcessed() {
    return hasContentToProcess;
}


void ContentParser::hasParsedContentToBeProcessed(bool hasContent) {
    this->hasContentToProcess = hasContent;
    this->contentType = NO_CONTENT;
}


std::string ContentParser::getTempContent() {
    int n = tempContentIndexEnd - tempContentIndexStart;
    return input.substr(tempContentIndexStart, n);
}


bool ContentParser::moveIndexToNextTag() {
    if (!mHasNextStep) {
        throw "mNextStep is false";
    }

    if (index.getIndex() >= length) {
        throw "Throwing because index >= length";
    }

    //actual index within input
    char ch = input[index.getIndex()];
    while (ch != '<' && index.getIndex() < length) {
        //continuing next, no valid html content to parse
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


void ContentParser::doNextStep() {
    if (!moveIndexToNextTag()) {
        //No tag to process
        invalidateHasNextStep();
        return;
    }
    //char is < and its probably start of valid tag
    //TagType end index, index of next '>'


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
            int ctsi = utils::findClosingTag(input, tag, index);
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
                int ctsi = utils::findClosingTag(input, tag, index, e);
                std::string titleContent = input.substr(tei + 1, ctsi - tei - 1);
                title = titleContent;
                int i = index.getIndex() + ctsi + 7;
                index.moveIndex(i);
            } catch (ErrorCode e) {
                abortWithError(e);
                return;
            }
        } else if (utils::fastCompare(tag, "base")) {
            try {
                int ctsi = utils::findClosingTag(input, tag, index, e);
                std::string baseContent = input.substr(tei, ctsi - 1);
                base = baseContent;
                int i = index.getIndex() + ctsi + 6;
                index.moveIndex(i);
            } catch (ErrorCode e) {
                abortWithError(e);
                return;
            }
        }

        if (!title.empty() && !base.empty()) {
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

    if (utils::fastCompare(tag, "br/")
        || utils::fastCompare(tag, "br")
        || utils::fastCompare(tag, "input")
        || utils::fastCompare(tag, "source")
        || utils::fastCompare(tag, "meta")
            ) {
        index.moveIndex(tei + 1);
        invalidateHasNextStep();
        return;
    }

    if (utils::fastCompare(tag, "noscript")
        || utils::fastCompare(tag, "script")
        || utils::fastCompare(tag, "svg")
            ) {
        index.moveIndex(tei + 1);
        //Skipping tags that can't be processed by library
        //Can't use findClosingTag because script can contain '<' inside of it and that breaks
        //searching for closing tag
        std::string closingTag = "</" + tag + ">";
        int ctsi;
        try {
            ctsi = utils::indexOfOrThrow(input, closingTag, index.getIndex());
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
        index.moveIndex(ctsi + tag.length() + 3);
        invalidateHasNextStep();
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
        ctsi = utils::findClosingTag(input, tag, index);
        tempContentIndexStart = index.getIndex();
        tempContentIndexEnd = ctsi;
    } catch (ErrorCode e) {
        abortWithError(e);
        return;
    }

    if (utils::fastCompare(tag, "p")
        || utils::fastCompare(tag, "span")
            ) {
        contentType = TEXT;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "h1")
               || utils::fastCompare(tag, "h2")
               || utils::fastCompare(tag, "h3")
               || utils::fastCompare(tag, "h4")
               || utils::fastCompare(tag, "h5")
               || utils::fastCompare(tag, "h6")
               || utils::fastCompare(tag, "h7")
            ) {
        contentType = TITLE;
        hasContentToProcess = true;
    } else if (
            utils::fastCompare(tag, "ul")
            || utils::fastCompare(tag, "ol")
            ) {
        contentType = LIST;
        hasContentToProcess = true;
        utils::groupPairTagContents(
                input, "li", index.getIndex(), ctsi, tempOutputList
        );

        /*
        int next;
        try {
            next = utils::indexOfOrThrow(input, ">", ctsi);
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
        index.moveIndex(next + 1);
        return;
        */

    } else if (utils::fastCompare(tag, "table")) {
        //Table is skipped temporary
        //TODO figure out how to parse out table
        contentType = TABLE;
        hasContentToProcess = false;
        /*
        utils::groupPairTagContents(
                input, "tr", index.getIndex(), ctsi, tempOutputList
        );
         */

        int next;
        try {
            next = utils::indexOfOrThrow(input, ">", ctsi);
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
        index.moveIndex(next + 1);
        return;

    } else if (utils::fastCompare(tag, "blockquote")) {
        contentType = QUOTE;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "address")) {
        contentType = ADDRESS;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "code")) {
        contentType = CODE;
        hasContentToProcess = true;
    } else {
        contentType = NO_CONTENT;
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


void ContentParser::parseImageTag(int tei) {
    contentType = IMAGE;
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


void ContentParser::tryMoveToClosing() {
    std::string closing = "</" + currentTag + ">";
    int ctsi = utils::indexOf(input, closing, index.getIndex());
    if (ctsi == -1) {
        return;
    }
    index.moveIndex(ctsi + closing.length() + 1);
}


void ContentParser::invalidateHasNextStep() {
    mHasNextStep = index.getIndex() < length;
}


int ContentParser::getTempListSize() {
    return tempOutputList.size();
}


std::string_view ContentParser::getTempListItem(int i) {
    auto iterator = tempOutputList.begin();
    std::advance(iterator, i);
    return *iterator;
}


std::string ContentParser::getTempMapItem(std::string attributeName) {
    return tempOutputMap[attributeName];
}


std::string ContentParser::getTitle() {
    return title;
}


std::string ContentParser::getBase() {
    return base;
}

bool ContentParser::isAbortingWithError() {
    return isAbortingWithException;
}


ErrorCode ContentParser::getErrorCode() {
    return error;
}


std::string ContentParser::getErrorMessage() {
    return errorMessage;
}


void ContentParser::abortWithError(ErrorCode cause) {
    this->error = cause;
    isAbortingWithException = true;
    hasContentToProcess = false;
    mHasNextStep = false;

    errorMessage = "ABORTING PARSING WITH ERROR WITH CAUSE: " + std::to_string(cause) + "\n"
                   + index.toString() + "\n"
                   + "body: " + currentTagBody;

    utils::log("PARSER", errorMessage, ANDROID_LOG_ERROR);


    index.moveIndex(length);
}

//TODO check if it's working correct
//TODO wikipedia and then android throws error
void ContentParser::clearAllResources() {
    input = "";
    title = "";
    base = "";
    lang = "";
    currentTag = "";
    currentTagBody = "";
    contentType = NO_CONTENT;

    hasContentToProcess = false;
    mHasBodyContext = false;
    wasHeadParsed = false;
    isAbortingWithException = false;
    error = NO_ERROR;
    errorMessage = "";
    index.reset();

    length = 0;
    tempContentIndexStart = -1;
    tempContentIndexEnd = -1;
    temporaryOutIndex = 0;

    tempOutputList.clear();
    tempOutputMap.clear();
}