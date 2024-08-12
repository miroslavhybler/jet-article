///
/// Created by Miroslav Hýbler  on 08.01.2024
///

#include "ContentFilter.h"
#include "../utils/Utils.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"


bool ContentFilter::isTagValidForNextProcessing(
        const std::string &tag,
        const std::string &tagBody
) {

    if (rules.empty()) {
        //No exclude rules specified, tag is considered valid
        return true;
    }

    tempClasses.clear();

    for (ExcludeRule rule: rules) {

        utils::extractClasses(tagBody, tempClasses);

        bool isValid = isValidBasedOnRule(tag, tagBody, rule);
        if (!isValid) {
            utils::log(
                    "CONTENT-FILTER",
                    "Tag: " + tagBody + " filtered out because of rule: " + rule.toString()
            );

            return false;
        }
    }
    //Tag passed all the rules, it is valid
    return true;
}


bool ContentFilter::isValidBasedOnRule(
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


bool ContentFilter::isValidBasedOnRuleTagIncluded(
        const std::string &tag,
        const std::string &tagBody,
        ExcludeRule &rule,
        const bool &isUsingId,
        const bool &isUsingClazz,
        const bool &isUsingKeyword
) {
    bool isTagMatch = utils::fastCompare(tag, rule.getTag());

    if (isTagMatch) {
        //Tag is matching, need to check its attributes

        if (isUsingId) {
            std::string tagId = utils::getTagAttribute(tagBody, "id");
            if (isWordPresented(rule.getId(), tagId)) {
                //Tag and tag id are matching, tag is not valid
                return false;
            } else {
                return true;
            }
        }
        if (isUsingClazz) {
            utils::extractClasses(tagBody, tempClasses);

            bool isPresented = isWordPresented(rule.getClazz(), tempClasses);

            if (isPresented) {
                //Tag is matching and its class containing class from rule
                //Tag is not valid
                return false;
            } else {
                return true;
            }
        }
        if (isUsingKeyword) {
            if (isKeywordPresented(rule.getKeyword(), tagBody)) {
                //Tag is matching and rule keyword is presented
                //Tag is not valid
                return false;
            }
        }
        //Rule is using tag only, since isTagMatch is true tag is not valid
        return false;
    } else {
        //Tag is not matching so no need to check other rules
        return true;
    }
}


bool ContentFilter::isValidBasedOnRuleTagNotIncluded(
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


bool ContentFilter::isKeywordPresented(
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


bool ContentFilter::isWordPresented(
        const std::string_view &word,
        const std::string_view &input,
        const bool isContainsEnabled
) {

    if (word.empty() || input.empty()) {
        return false;
    }

    utils::log(
            "CONTENT-FILTER",
            "isWordPresented comparing -- " + std::string(word) + " == " +
            std::string(input) + " " + utils::boolToString(isContainsEnabled)
    );

    bool isWord = utils::fastCompare(word, input);

    utils::log(
            "CONTENT-FILTER",
            "equals: " + utils::boolToString(isWord)
    );

    if (isWord) {
        return true;
    }

    if (isContainsEnabled) {
        return input.find(word) != std::string_view::npos;
    }


    return false;
}


bool ContentFilter::isWordPresented(
        const std::string_view &word,
        const std::vector<std::string_view> &classes,
        const bool isContainsEnabled
) {

    if (classes.empty() || word.empty()) {
        return false;
    }

    for (std::string_view clazz: classes) {
        utils::log(
                "CONTENT-FILTER",
                "isWordPresented in classes -- " + std::string(word) + " == " + std::string(clazz)
        );
        if (isWordPresented(word, clazz, isContainsEnabled)) {
            //Active rule found based on tag and its clazz
            //Or keyword found withing class body
            return true;
        }
    }
    return false;
}


void ContentFilter::addRule(ExcludeRule rule) {
    rules.push_back(rule);
}


void ContentFilter::clearAllResources() {
    rules.clear();
}

#pragma clang diagnostic pop