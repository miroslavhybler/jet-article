///
/// Created by Miroslav Hýbler on 19.02.2024
///


#ifndef JET_ARTICLE_CONTENTANALYZER_H
#define JET_ARTICLE_CONTENTANALYZER_H


#include <string>
#include <vector>
#include "ParserComponent.h"
#include "../utils/IndexWrapper.h"
#include "../utils/Utils.h"
#include "../utils/Constants.h"


class ContentAnalyzer : public AbstractParserComponent {

public:
    std::string currentTagName = "";
    std::string currentTagClass = "";

private:
    int actualInputStart = 0;
    int actualInputEnd = 0;
    std::vector<std::string> currentTagAttributeKeys;
    std::map<std::string, std::string> currentTagAttributes;


    bool mHasBodyContext;
    bool mWasHtmlTagFound;
    int length;
    bool hasContentToProcess;
    bool wasHeadParsed;
    int tempContentIndexStart = -1;
    int tempContentIndexEnd = -1;
    std::vector<std::string_view> tempOutputVector;
    std::vector<std::vector<std::string_view>> tableHolder;
    bool isAbortingWithException;
    ErrorCode error;
    std::string errorMessage = "";
    int temporaryOutIndex = 0;


public:
    ContentAnalyzer();


    ~ContentAnalyzer();


    void setInput(std::string input);


    void setRange(int s, int e);


    void doNextStep();

    std::string getCurrentTagAttributeName(int index);

    std::string getCurrentTagAttributeValue(std::string attributeName);


    int getCurrentAttributesSize();


    int getCurrentTagStartIndex();


    int getCurrentTagEndIndex();


    void clearAllResources();


    void abortWithError(ErrorCode cause);
};


#endif //JET_ARTICLE_CONTENTANALYZER_H
