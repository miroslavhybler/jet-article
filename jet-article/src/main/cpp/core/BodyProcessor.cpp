///
/// Created by Miroslav Hýbler  on 08.01.2024
///

#include "BodyProcessor.h"
#include "../utils/Utils.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"


bool BodyProcessor::isTagValidForNextProcessing(
        const std::string &tag,
        const std::string &tagBody
) {

    if (rules.empty()) {
        //No exclude rules specified, tag is considered valid
        return true;
    }

    tempClasses.clear();
    std::vector<ExcludeRule>::iterator iterator = rules.begin();

    for (ExcludeRule rule: rules) {
        bool isValid = isValidBasedOnRule(tag, tagBody, rule);
        if (!isValid) {
            utils::log(
                    "PROCESSOR",
                    "Tag: " + tagBody + " kicked out because of rule: " + rule.toString()
            );

            return false;
        }
    }
    return true;
}


bool BodyProcessor::isValidBasedOnRule(
        const std::string &tag,
        const std::string &tagBody,
        ExcludeRule &rule
) {
    bool isUsingTag = !rule.getTag().empty();
    bool isUsingClazz = !rule.getClazz().empty();
    bool isUsingId = !rule.getId().empty();
    bool isUsingKeyword = !rule.getKeyword().empty();


    if (!isUsingTag && !isUsingClazz && !isUsingId && !isUsingKeyword) {
        //Rule is empty, no tag and no clazz, this shouldn't occur because it doesn't make any sence.
        //If it occurs, just return silently
        return true;
    }

    if (isUsingKeyword && !isUsingTag && !isUsingId && !isUsingClazz) {
        //Using keyword only, this is the worst performace option avaliable because keyword is being
        //compared with all tag id and classes
        if (isKeywordPresented(rule.getKeyword(), tagBody)) {
            return false;
        }
    }


    if (isUsingTag) {
        return isValidBasedOnRuleTagIncluded(
                tag, tagBody, rule,
                isUsingId, isUsingClazz, isUsingKeyword
        );
    } else {
        return isValidBasedOnRuleTagNotIncluded(
                tagBody, rule,
                isUsingId, isUsingClazz, isUsingKeyword
        );
    }
}


bool BodyProcessor::isValidBasedOnRuleTagIncluded(
        const std::string &tag,
        const std::string &tagBody,
        ExcludeRule &rule,
        const bool &isUsingId,
        const bool &isUsingClazz,
        const bool &isUsingKeyword
) {
    bool tagMatch = utils::fastCompare(tag, rule.getTag());

    if (tagMatch) {
        if (isUsingId) {
            std::string tagId = utils::getTagAttribute(tagBody, "id");
            if (isWordPresented(rule.getId(), tagId)) {
                return false;
            }
        }
        if (isUsingClazz) {
            utils::extractClasses(tagBody, tempClasses);
            if (isWordPresented(rule.getKeyword(), tempClasses)) {
                return false;
            }
        }
        if (isUsingKeyword) {
            if (isKeywordPresented(rule.getKeyword(), tagBody)) {
                return false;
            }
        }
        //Exclude rule not found, tag is same but other rules are applied
        return true;
    } else {
        //Tag is not matching so no need to check other rules
        return true;
    }
}


bool BodyProcessor::isValidBasedOnRuleTagNotIncluded(
        const std::string &tagBody,
        ExcludeRule &rule,
        const bool &isUsingId,
        const bool &isUsingClazz,
        const bool &isUsingKeyword
) {
    if (isUsingId) {
        std::string tagId = utils::getTagAttribute(tagBody, "id");
        if (tagId == rule.getId()) {
            return false;
        }
    }

    if (isUsingClazz) {
        for (std::string_view clazz: tempClasses) {
            if (utils::fastCompare(clazz, rule.getClazz())) {
                //Active rule found
                return false;
            }
        }
    }

    if (isUsingKeyword) {
        if (isKeywordPresented(rule.getKeyword(), tagBody)) {
            return false;
        }
    }

    //Nothing found
    return true;
}


bool BodyProcessor::isKeywordPresented(
        const std::string_view &keyword,
        const std::string &tagBody
) {
    utils::extractClasses(tagBody, tempClasses);
    std::string tagId = utils::getTagAttribute(tagBody, "id");

    if (isWordPresented(keyword, tagId, true)
        || isWordPresented(keyword, tempClasses, true)
            ) {
        return true;
    }

    return false;
}


bool BodyProcessor::isWordPresented(
        const std::string_view &word,
        const std::string_view &input,
        const bool isContainsEnabled
) {
    bool isWord = utils::fastCompare(word, input);

    if (isWord) {
        return true;
    }

    if (isContainsEnabled) {
        return input.find(word) != std::string_view::npos;
    }


    return false;
}


bool BodyProcessor::isWordPresented(
        const std::string_view &word,
        const std::vector<std::string_view> &classes,
        const bool isContainsEnabled
) {
    for (std::string_view clazz: tempClasses) {
        if (isWordPresented(word, clazz, isContainsEnabled)) {
            //Active rule found based on tag and its clazz
            //Or keyword found withing class body
            return true;
        }
    }
    return false;
}


void BodyProcessor::addRule(ExcludeRule rule) {
    rules.push_back(rule);
}


void BodyProcessor::clearAllResources() {
    rules.clear();
}

#pragma clang diagnostic pop