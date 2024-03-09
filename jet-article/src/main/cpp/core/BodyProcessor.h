///
/// Created by Miroslav Hýbler on 08.01.2024
///

#ifndef JET_HTML_ARTICLE_BODYPROCESSOR_H
#define JET_HTML_ARTICLE_BODYPROCESSOR_H

#include "../utils/ExcludeRule.h"
#include <string>
#include <vector>

/**
 * @since 1.0.0
 */
class BodyProcessor {

private:
    std::vector<ExcludeRule> rules;
    std::vector<std::string_view> tempClasses;

public:

    /**
     *
     * @param tag TagType name e.g. p
     * @param tagBody TagType body within <>, e.g. "p class="normal-text""
     * @param tagContent When tag is pair tag, full content between opening and closing tag
     * @return True when tag is valid based your customized rules and can be processed further.
     * False otherwise.
     * @since 1.0.0
     */
    bool isTagValidForNextProcessing(
            const std::string &tag,
            const std::string &tagBody
    );


    /**
     *
     * @param rule
     * @since 1.0.0
     */
    void addRule(ExcludeRule rule);


    /**
     * @since 1.0.0
     */
    void clearAllResources();


private:


    /**
     *
     * @param tag
     * @param tagBody
     * @param rule
     * @return
     * @since 1.0.0
     */
    bool isValidBasedOnRule(
            const std::string &tag,
            const std::string &tagBody,
            ExcludeRule &rule
    );


    /**
     *
     * @param tag
     * @param tagBody
     * @param rule
     * @param isUsingId
     * @param isUsingClazz
     * @param isUsingKeyword
     * @return
     * @since 1.0.0
     */
    bool isValidBasedOnRuleTagIncluded(
            const std::string &tag,
            const std::string &tagBody,
            ExcludeRule &rule,
            const bool &isUsingId,
            const bool &isUsingClazz,
            const bool &isUsingKeyword
    );


    /**
     *
     * @param tagBody
     * @param rule
     * @param isUsingId
     * @param isUsingClazz
     * @param isUsingKeyword
     * @return
     * @since 1.0.0
     */
    bool isValidBasedOnRuleTagNotIncluded(
            const std::string &tagBody,
            ExcludeRule &rule,
            const bool &isUsingId,
            const bool &isUsingClazz,
            const bool &isUsingKeyword
    );


    bool isKeywordPresented(
            const std::string_view &keyword,
            const std::string &tagBody
    );


    bool isWordPresented(
            const std::string_view &word,
            const std::string_view &input,
            const bool isContainsEnabled = false
    );


    bool isWordPresented(
            const std::string_view &word,
            const std::vector<std::string_view> &classes,
            const bool isContainsEnabled = false
    );
};


#endif //JET_HTML_ARTICLE_BODYPROCESSOR_H
