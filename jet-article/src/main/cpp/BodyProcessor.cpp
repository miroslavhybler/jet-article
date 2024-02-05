///
/// Created by Miroslav Hýbler  on 08.01.2024
///

#include "BodyProcessor.h"
#include "Utils.h"

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

    std::vector<ExcludeRule>::iterator iterator = rules.begin();

    for (ExcludeRule rule: rules) {
        bool isValid = isValidBasedOnRule(tag, tagBody, rule);
        if (!isValid) {
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


    if (!isUsingTag && !isUsingClazz) {
        //Rule is empty, no tag and no clazz, this shouldn't occur because it doesn't make any sence.
        return true;
    }


    if (isUsingTag) {
        bool tagMatch = utils::fastCompare(tag, rule.getTag());
        if (isUsingClazz && tagMatch) {
            utils::extractClasses(tagBody, tempClasses);
            for (std::string_view clazz: tempClasses) {
                if (utils::fastCompare(clazz, rule.getClazz())) {
                    //Active rule found based on tag and its clazz
                    return false;
                }
            }
            //No exclude rule based on tag and clazz not found, tag is considered valid
            return true;
        }

        //Exclude rule based on tag only found, tag is considered valid
        return !tagMatch;
    }



    //Using class only,
    for (std::string_view clazz: tempClasses) {
        if (utils::fastCompare(clazz, rule.getClazz())) {
            //Active rule found
            return false;
        }
    }

    //No exclude option found, tag is considered valid
    return true;
}


void BodyProcessor::addRule(ExcludeRule rule) {
    rules.push_back(rule);
}


void BodyProcessor::clearAllResources() {
    rules.clear();
}

#pragma clang diagnostic pop