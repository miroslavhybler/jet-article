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

    if (s < 0 || e > length || s > e) {
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

    if (!mHasNextStep) {
        return;
    }

    index.invalidate();
    currentTag = "";
    currentTagBody = "";
    currentTagId = "";
    currentTagClass = "";
    currentPairTagContent = "";
    currentTagAttributes.clear();


    if (!moveIndexToNextTag()) {
        utils::log("ANALYZER", "Unable to move to next tag");
        return;
    }
    //No tag to process
    invalidateHasNextStep();
    //TODO maybe analyzer return some status to app, unable to move next


    if (!mHasNextStep) {
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
    currentTagStartIndex = index.getIndex();
    currentTagEndIndex = tei;

    if (currentTag.find('/', 0) == 0) {
        //Skipping closing tag
        //its because after we parse out nested content, we don't know the "right" closing tag
        //example <div><p>...</p></div> after parsing <p> we move behind </p>

        if (currentTag == "/body" || currentTag == "/html") {
            //Analyzer is really buggy now and works diferently from parser
            //this prevents form crash at moveIndexToNextTag()
            mHasBodyContext = false;
            index.moveIndex(length);
            mHasNextStep = false;
            return;
        }

        index.moveIndex(tei + 1);
        invalidateHasNextStep();
        return;
    }
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

        if (!isActualTagValidForNextProcessing(currentTag, tei)) {
            //Tag is some tag which this library can't support like script, noscript, meta, ...
            //For full list visit isActualTagValidForNextProcessing function
            return;
        }


        bool isCurrentTagPair = utils::isTagPairTag(currentTagBody);
        if (isCurrentTagPair) {
            //TODO probably when comes to <div> it skipps to </div> instead goint intside
            try {
                int s = tei + 1;
                int ctsi = utils::findClosingTag(input, currentTag, tei);
                currentPairTagContent = input.substr(tei + 1, ctsi - tei - 1);

                int next;
                try {
                    next = utils::indexOfOrThrow(input, ">", ctsi);
                    index.moveIndex(next + 1);
                } catch (ErrorCode e) {
                    abortWithError(e);
                    return;
                }

            } catch (ErrorCode code) {
                std::string message =
                        "Problematic area within " + std::to_string(tei)
                        + " .. " + std::to_string(length) + "\n"
                        + "tei: " + input.substr(tei, 1) + "\n"
                        + "Near substring:\n\n" +
                        input.substr(tei - tagBodyLength, 40)
                        + "\n";

                abortWithError(code, message);
                return;
            }

        } else {
            index.moveIndex(tei + 1);
        }

        //TODO split supported and unsupported tags

        return;
    }


    index.moveIndex(tei + 1);

    if (utils::fastCompare(currentTag, "html")) {
        mWasHtmlTagFound = true;
        // lang = utils::getTagAttribute(currentTagBody, "lang");
    } else if (!wasHeadParsed && utils::fastCompare(currentTag, "head")) {
        try {
            //Closing tag start index
            //   int ctsi = utils::findClosingTag(input, currentTag, index);
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


std::string ContentAnalyzer::getCurrentTagAttributeValue(
        std::string &attributeName
) {
    return currentTagAttributes[attributeName];
}


int ContentAnalyzer::getCurrentAttributesSize() {
    return currentTagAttributes.size();
}


int ContentAnalyzer::getCurrentTagStartIndex() const {
    return currentTagStartIndex;
}

int ContentAnalyzer::getCurrentTagEndIndex() const {
    return currentTagEndIndex;
}

bool ContentAnalyzer::hasPairTagContent() const {
    return !currentPairTagContent.empty();
}

std::string ContentAnalyzer::getCurrentPairTagContent() {
    return currentPairTagContent;
}

void ContentAnalyzer::abortWithError(ErrorCode cause, std::string message) {
    this->error = cause;
    isAbortingWithException = true;
    hasContentToProcess = false;
    mHasNextStep = false;

    errorMessage = "ABORTING ANALIZING WITH ERROR WITH CAUSE: " + std::to_string(cause) + "\n"
                   + index.toString() + "\n"
                   + "Message: " + message + "\n"
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
    currentPairTagContent = "";
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
    currentTagStartIndex = -1;
    currentTagEndIndex = -1;
    temporaryOutIndex = 0;

    tempOutputVector.clear();
    currentTagAttributes.clear();
}