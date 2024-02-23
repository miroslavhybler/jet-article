///
/// Created by Miroslav Hýbler  on 19.02.2024
///

#include "ContentAnalyzer.h"


ContentAnalyzer::ContentAnalyzer() {
    index = IndexWrapper();
}


ContentAnalyzer::~ContentAnalyzer() {

}


void ContentAnalyzer::setInput(std::string content) {
    clearAllResources();
    this->input = content;
    length = content.length();
    invalidateHasNextStep();
}


void ContentAnalyzer::setRange(int s, int e) {

    if (s < 0 || e > length) {
        utils::log(
                "ANALYZER",
                "Unable to analyze becase of invalid range(s=" + std::to_string(s) + ", e=" +
                std::to_string(e) + ")"
        );
        return;
    }

    this->actualInputStart = s;
    this->actualInputEnd = e;
    this->index.moveIndex(s);
}


void ContentAnalyzer::doNextStep() {
    if (!moveIndexToNextTag()) {
        //No tag to process
        invalidateHasNextStep();
        //TODO analyzer return some status to app, unable to move next
        return;
    }
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
    currentTag = utils::getTagName(currentTagBody);

    tempContentIndexStart = index.getIndex();
    tempContentIndexEnd = tei;


    if (mHasBodyContext) {
        currentTagId = utils::getTagAttribute(currentTagBody, "id");
        currentTagName = utils::getTagAttribute(currentTagBody, "name");
        currentTagClass = utils::getTagAttribute(currentTagBody, "class");
        utils::getTagAttributes(currentTagBody, currentTagAttributes);

        for (auto &pair: currentTagAttributes) {
            //Pushing keys (attribute names) to separate array so it can be get better from kotlin code
            currentTagAttributeKeys.push_back(pair.first);
        }

        invalidateHasNextStep();
        //TODO pair tag
        return;
    }


    index.moveIndex(tei + 1);

    if (utils::fastCompare(currentTag, "html")) {
        mWasHtmlTagFound = true;
        // lang = utils::getTagAttribute(currentTagBody, "lang");
    } else if (!wasHeadParsed && utils::fastCompare(currentTag, "head")) {
        try {
            //Closing tag start index
            int ctsi = utils::findClosingTag(input, currentTag, index);
            // parseHeadData(ctsi);
            invalidateHasNextStep();
            wasHeadParsed = false;
        } catch (ErrorCode e) {
            abortWithError(e);
            return;
        }
    } else if (!mHasBodyContext && utils::fastCompare(currentTag, "body")) {
        mHasBodyContext = true;
    }

}


std::string ContentAnalyzer::getCurrentTagAttributeName(int index) {
    return currentTagAttributeKeys[index];
}


std::string ContentAnalyzer::getCurrentTagAttributeValue(std::string attributeName) {
    return currentTagAttributes[attributeName];
}


int ContentAnalyzer::getCurrentAttributesSize() {
    return currentTagAttributes.size();
}


//TODO
int ContentAnalyzer::getCurrentTagStartIndex() {
    return tempContentIndexStart;
}

int ContentAnalyzer::getCurrentTagEndIndex() {
    return tempContentIndexEnd;
}


void ContentAnalyzer::abortWithError(ErrorCode cause) {
    this->error = cause;
    isAbortingWithException = true;
    hasContentToProcess = false;
    mHasNextStep = false;

    errorMessage = "ABORTING ANALIZING WITH ERROR WITH CAUSE: " + std::to_string(cause) + "\n"
                   + index.toString() + "\n"
                   + "body: " + currentTagBody;

    utils::log("ANALYZER", errorMessage, ANDROID_LOG_ERROR);


    index.moveIndex(length);
}


void ContentAnalyzer::clearAllResources() {
    actualInputStart = 0;
    actualInputEnd = 0;

    input = "";
    currentTag = "";
    currentTagBody = "";
    currentTagId = "";
    currentContentType = NO_CONTENT;

    hasContentToProcess = false;
    mHasBodyContext = false;
    mWasHtmlTagFound = false;
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
    currentTagAttributes.clear();
}