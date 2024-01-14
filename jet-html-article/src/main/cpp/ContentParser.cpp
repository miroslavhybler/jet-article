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
    length = 0;
    input = "";
    index = IndexWrapper();
    title = "";
    base = "";
}


ContentParser::~ContentParser() {
    mHasNextStep = false;
    mHasBodyContext = false;
    hasContentToProcess = false;
    tempOutputList.clear();
    length = 0;
    input = "";
    title = "";
    base = "";
}


void ContentParser::setInput(std::string content) {
    this->input = content;
    hasContentToProcess = false;
    mHasBodyContext = false;
    wasHeadParsed = false;

    length = content.length();
    index.reset();

    title = "";
    base = "";
    tempContentIndexStart = -1;
    tempContentIndexEnd = -1;
    tempOutputList.clear();
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
    return input.substr(tempContentIndexStart, tempContentIndexEnd - tempContentIndexStart);
}


bool ContentParser::moveIndexToNextTag() {
    if (!mHasNextStep) {
        utils::log("INDEX", "mNextStep is false");
        throw "mNextStep is false";
    }

    if (index.getIndex() >= length) {
        utils::log("PARSER", "throwing because index >= length");
        throw "Throwing because index >= length";
    }

    //actual index within input
    char ch = input[index.getIndex()];
    while (ch != '<') {
        //continuing next, no valid html content to parse
        index.moveIndex(index.getIndex() + 1);
        invalidateHasNextStep();
        if (!mHasNextStep) {
            return false;
        }
        ch = input[index.getIndex()];
    }

    //char is <
    if (!utils::canProcessIncomingTag(input, length, index)) {
        //Char < is staring some special sequence like comment <!--
        //Moving cursor to the next '<' char
        index.moveToTempIndex();
        index.moveIndex(index.getIndex() + 1);
        invalidateHasNextStep();
        return false;
    }
    return true;
}


void ContentParser::doNextStep() {
    if (!moveIndexToNextTag()) {
        //No tag to process
        return;
    }

    //char is < and its probably start of valid tag
    //TagType end index, index of next '>'
    int tei = utils::indexOfOrThrow(input, ">", index.getIndex());
    // -1 to remove '<' at the end
    int tagBodyLength = tei - index.getIndex() - 1;
    //tagbody within <>, i + 1 to remove '<'
    std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
    std::string tag = utils::getTagName(tagBody);

    //Move index to the next char after tag
    //TODO unable to proccess image because index is moved at the end of img tag before
    //  index.moveIndex(tei + 1);

    if (mHasBodyContext) {
        parseNextTagWithinBodyContext(tag, tei);
        invalidateHasNextStep();
        return;
    }
    index.moveIndex(tei + 1);

    if (!wasHeadParsed && utils::fastCompare(tag, "head")) {
        //Closing tag start index
        int ctsi = utils::findClosingTag(input, tag, index);
        std::string tagContent = input.substr(tei, ctsi - 1);
        parseHeadData(ctsi);
        wasHeadParsed = false;
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
        int tei = utils::indexOfOrThrow(input, ">", index.getIndex());
        // -1 to remove '<' at the end
        int tagBodyLength = tei - index.getIndex() - 1;
        //tagbody within <>, i + 1 to remove '<'
        std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
        std::string tag = utils::getTagName(tagBody);
        index.moveIndex(index.getIndex() + tag.length() + 1);

        if (utils::fastCompare(tag, "title")) {
            int ctsi = utils::findClosingTag(input, tag, index, e);
            std::string tagContent = input.substr(tei, ctsi - 1);
            title = tagContent;
            int i = index.getIndex() + ctsi + 7;
            index.moveIndex(i);
        } else if (utils::fastCompare(tag, "base")) {
            int ctsi = utils::findClosingTag(input, tag, index, e);
            std::string tagContent = input.substr(tei, ctsi - 1);
            base = tagContent;
            int i = index.getIndex() + ctsi + 6;
            index.moveIndex(i);
        }

        if (!title.empty() && !base.empty()) {
            break;
        }
    }
    index.moveIndex(e + 1);
    invalidateHasNextStep();
}


void ContentParser::parseNextTagWithinBodyContext(std::string tag, int tei) {
    //TODO final version should not need this
    if (tag.find('/', 0) == 0) {
        index.moveIndex(tei + 1);
        index.moveIndex(index.getIndex() + tag.length());
        invalidateHasNextStep();
        return;
    }

    if (utils::fastCompare(tag, "br/")
        || utils::fastCompare(tag, "input")
        || utils::fastCompare(tag, "source")
            ) {
        index.moveIndex(tei + 1);
        return;
    }

    if (utils::fastCompare(tag, "noscript")
        || utils::fastCompare(tag, "script")
        || utils::fastCompare(tag, "svg")
            ) {
        index.moveIndex(tei + 1);
        //Skipping tags that can't be processed by library
        //Can't use findColsingTag because script can contain '<' inside of it and that breaks
        //searching for closing tag
        int ctsi = utils::findClosingTag(input, tag, index);
        index.moveIndex(ctsi + tag.length() + 3);
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
    int ctsi = utils::findClosingTag(input, tag, index);
    tempContentIndexStart = index.getIndex();
    tempContentIndexEnd = ctsi;

    //TODO more content types

    if (utils::fastCompare(tag, "p")) {
        contentType = PARAGRAPH;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "h1")
               || utils::fastCompare(tag, "h2")
               || utils::fastCompare(tag, "h3")
               || utils::fastCompare(tag, "h4")
               || utils::fastCompare(tag, "h5")
            ) {
        contentType = TITLE;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "table")) {
        contentType = TABLE;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "blockquote")) {
        contentType = QUOTE;
        hasContentToProcess = true;
    } else if (utils::fastCompare(tag, "address")) {
        contentType = ADDRESS;
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
    } else if (utils::fastCompare(tag, "code")) {
        contentType = CODE;
        hasContentToProcess = true;
    } else {
        contentType = NO_CONTENT;
        hasContentToProcess = false;
        tempContentIndexStart = -1;
        tempContentIndexEnd = -1;
    }

    actualTag = tag;
    if (hasContentToProcess) {
        index.moveIndex(ctsi + tag.length() + 1);
    } else {
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


void ContentParser::invalidateHasNextStep() {
    mHasNextStep = index.getIndex() < length;
}


int ContentParser::getTempListSize() {
    return tempOutputList.size();
}


std::string ContentParser::getTempListItem(int i) {
    auto iterator = tempOutputList.begin();
    std::advance(iterator, i);
    return *iterator;
}


std::string ContentParser::getTempMapItem(std::string attributeName) {
    return tempOutputMap[attributeName];
}


void ContentParser::clearAllResources() {
    input = "";
}