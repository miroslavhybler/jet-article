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

        if (index.getIndex() > length) {
            //TODO solve
            utils::log("mirek", "index is out of bounds");
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

    if (input[tei] != '>') {
        std::string wCh(1, input[tei]);
        std::string error = "Throwing because char at " + std::to_string(tei) + "is not '>'!"
                            + "char is " + wCh + "!!";
        utils::log("PARSER", error);
        throw error;
    }

    // -1 to remove '<' at the end
    int tagBodyLength = tei - index.getIndex() - 1;
    //tagbody within <>, i + 1 to remove '<'
    std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
    std::string tag = utils::getTagName(tagBody);

    //Move index to the next char after tag
    index.moveIndex(tei + 1);

    if (mHasBodyContext) {
        parseTagsWithinBodyContext(tag);
        invalidateHasNextStep();
        return;
    }


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
        //TagType end index, index of next '>'
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
            index.moveIndex(e + 1);
            break;
        }
    }
    invalidateHasNextStep();
}


void ContentParser::parseTagsWithinBodyContext(std::string tag) {
    //TODO figure out something better
    if (tag.find('/', 0) == 0) {
        index.moveIndex(index.getIndex() + tag.length());
        return;
    }


    if (utils::fastCompare(tag, "noscript")
        || utils::fastCompare(tag, "script")
        || utils::fastCompare(tag, "svg")
        || utils::fastCompare(tag, "source")
        || utils::fastCompare(tag, "input")
        || utils::fastCompare(tag, "br/")
            ) {
        //Skipping tags that can't be processed by library
        std::string closingTag = "</" + tag + ">";
        //Can't use findColsingTag because script can contain '<' inside of it and that breaks
        //searching for losing tag
        int ctsi = utils::indexOfOrThrow(input, closingTag, index.getIndex());
        index.moveIndex(ctsi + closingTag.length());
        return;
    }

    //At this point index is pointing at the sequence starting with '<' which is ready to be
    //processed as tag

    if (utils::fastCompare(tag, "img")) {
        contentType = IMAGE;
        int stei = utils::indexOfOrThrow(input, ">", index.getIndex());
        index.moveIndex(stei + 1);
        return;
    }
    hasContentToProcess = true;

    if (utils::fastCompare(tag, "title")) {
        //TODO bug
        utils::log("mirek", "searching title from body, " + index.toString());
    }
    int ctsi = utils::findClosingTag(input, tag, index);
    tempContentIndexStart = index.getIndex();
    tempContentIndexEnd = ctsi;

    //TODO more content types

    if (utils::fastCompare(tag, "p")) {
        contentType = PARAGRAPH;
    } else if (utils::fastCompare(tag, "h1")
               || utils::fastCompare(tag, "h2")
               || utils::fastCompare(tag, "h3")
               || utils::fastCompare(tag, "h4")
               || utils::fastCompare(tag, "h5")
            ) {
        contentType = TITLE;
    } else if (utils::fastCompare(tag, "table")) {
        contentType = TABLE;
    } else if (utils::fastCompare(tag, "blockquote")) {
        contentType = QUOTE;
    } else {
        hasContentToProcess = false;
        tempContentIndexStart = -1;
        tempContentIndexEnd = -1;
    }

    //TODO set other actual things
    //TODO Tags
    actualTag = tag;

    //TODO fix
    index.moveIndex(ctsi + tag.length() + 1);
    invalidateHasNextStep();
}


void ContentParser::invalidateHasNextStep() {
    mHasNextStep = index.getIndex() < length;
}

void ContentParser::clearAllResources() {
    input = "";
}