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
    std::string currentTagName;
    std::string currentTagClass;
    std::string currentPairTagContent;

private:
    std::vector<std::string> currentTagAttributeKeys;
    std::map<std::string, std::string> currentTagAttributes;

    bool mHasBodyContext{};
    bool wasHeadParsed{};
    size_t currentTagStartIndex = 0;
    size_t currentTagEndIndex = 0;
    std::vector<std::string_view> tempOutputVector;
    std::vector<std::vector<std::string_view>> tableHolder;
    ErrorCode error;
    std::string errorMessage;

public:
    ContentAnalyzer();


    ~ContentAnalyzer();


    void setInput(std::string &input) override;


    void doNextStep() override;

    std::string getCurrentTagAttributeName(int index);

    std::string getCurrentTagAttributeValue(
            std::string &attributeName
    );


    size_t getCurrentAttributesSize();


    [[nodiscard]] size_t getCurrentTagStartIndex() const;

    [[nodiscard]] size_t getCurrentTagEndIndex() const;

    std::string getCurrentPairTagContent();


    [[nodiscard]] bool hasPairTagContent() const;

    void clearAllResources() override;


#pragma clang diagnostic push
#pragma ide diagnostic ignored "google-default-arguments"
    void abortWithError(
            ErrorCode cause,
            std::string message = ""
    ) override;
#pragma clang diagnostic pop
};


#endif //JET_ARTICLE_CONTENTANALYZER_H
