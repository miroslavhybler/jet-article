///
/// Created by Miroslav Hýbler on 08.01.2024
///

#ifndef JET_HTML_ARTICLE_BODYPROCESSOR_H
#define JET_HTML_ARTICLE_BODYPROCESSOR_H

#include "IgnoreRule.h"
#include <string>
#include <vector>

/**
 * @since 1.0.0
 */
class BodyProcessor {

private:
    std::vector<IgnoreRule> rules;
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


    void addRule(IgnoreRule rule);


    void clearAllResources();
};


#endif //JET_HTML_ARTICLE_BODYPROCESSOR_H
