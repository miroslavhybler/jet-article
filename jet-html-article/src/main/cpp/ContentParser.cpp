///
/// Created by Miroslav Hýbler on 03.01.2024
///


#include "ContentParser.h"
#include "utils/Utils.h"


ContentParser::ContentParser() {
    mHasNextStep = false;
    hasContentToProcess = false;
    length = 0;
    input = "";
    tempContent = "";
    index = IndexWrapper();

    title = "";
    base = "";
}

ContentParser::~ContentParser() {

}

void ContentParser::setInput(std::string content) {
    this->input = content;
    length = content.length();
    index.setLength(length);
    index.setIndex(0);
    invalidateHasNextStep();
}


bool ContentParser::hasNextStep() {
    return mHasNextStep;
}


bool ContentParser::hasContent() {
    return hasContentToProcess;
}


void ContentParser::doNextStep() {
    if (!mHasNextStep) {
        throw "mNextStep is false";
    }

    if (index.getIndex() >= length) {
        throw "Throwing because index >= length";
    }

    //actual index within input
    const char ch = input[index.getIndex()];

    if (ch != '<') {
        //continuing next, no valid html content to parse
        index.setIndex(index.getIndex() + 1);
        invalidateHasNextStep();
        return;
    }

    //char is <
    if (!utils::canProcessIncomingTag(input, index)) {
        //Char < is staring some special sequence like comment <!--
        index.moveToTempIndex();
        invalidateHasNextStep();
        return;
    }

    //char is < and its probably start of valid tag

    //Tag end index, index of next '>'
    int tei = utils::indexOf(input, ">", index.getIndex());

    if (input[tei] != '>') {
        std::string wCh(1, input[tei]);
        throw "Throwing because char at " + std::to_string(tei) + "is not '>'!"
              + "char is " + wCh + "!!";
    }

    // -1 to remove '<' at the end
    int tagBodyLength = tei - index.getIndex() - 1;
    //tagbody within <>, i + 1 to remove '<'
    std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
    std::string tag = utils::getTagName(tagBody);

    hasContentToProcess = true;
    tempContent = tag;

    index.setIndex(tei + 1);


    if (tag == "head") {
        std::string closingTag = "/head";
        //Closing tag start index
        int ctsi = utils::findClosingTag(input, tag, index);
        std::string tagContent = input.substr(tei, ctsi - 1);
        if (ctsi == -1) {
            index.setIndex(tei + 1);
            invalidateHasNextStep();
            return;
        }

        parseHeadData(ctsi);
    }

    if (tag == "body") {
        //TODO nějak pořešit body
    }

    invalidateHasNextStep();
}


void ContentParser::parseHeadData(int e) {
    while (index.getIndex() < e) {
        char ch = input[index.getIndex()];

        if (ch != '<') {
            //continuing next, no valid html content to parse
            index.setIndex(index.getIndex() + 1);
            invalidateHasNextStep();
            continue;
        }

        //char is <
        if (!utils::canProcessIncomingTag(input, index)) {
            //Char < is staring some special sequence like comment <!--
            index.moveToTempIndex();
            invalidateHasNextStep();
            return;
        }

        //char is < and its probably start of valid tag

        //Tag end index, index of next '>'
        int tei = utils::indexOf(input, ">", index.getIndex());
        // -1 to remove '<' at the end
        int tagBodyLength = tei - index.getIndex() - 1;
        //tagbody within <>, i + 1 to remove '<'
        std::string tagBody = input.substr(index.getIndex() + 1, tagBodyLength);
        std::string tag = utils::getTagName(tagBody);
        index.setIndex(index.getIndex() + tag.length() + 1);

        if (tag == "title") {
            std::string closingTag = "/title";
            int ctsi = utils::findClosingTag(input, tag, index, e);

            std::string tagContent = input.substr(tei, ctsi - 1);
            title = tagContent;
            int i = index.getIndex() + ctsi + closingTag.length() + 1;

            utils::log("mirek",
                       "index: " + std::to_string(index.getIndex())
                       + " ctsi: " + std::to_string(ctsi)
                       + " length:" + std::to_string(closingTag.length())
            );
            index.setIndex(i);
        } else if (tag == "base") {
            std::string closingTag = "/" + tag;
            int ctsi = utils::findClosingTag(input, tag, index, e);
            std::string tagContent = input.substr(tei, ctsi - 1);
            base = tagContent;
            int i = index.getIndex() + ctsi + closingTag.length() + 1;
            index.setIndex(i);
            utils::log("mirek", "base found at: " + std::to_string(i));
        }

        if (!title.empty() && !base.empty()) {
            index.setIndex(e + 1);
            break;
        }
    }

    std::string msg = "title: " + title + " base: " + base;
    utils::log("mirek", msg);

    invalidateHasNextStep();
}


void ContentParser::parseBodyData() {

}


void ContentParser::invalidateHasNextStep() {
    mHasNextStep = index.getIndex() < length;
}