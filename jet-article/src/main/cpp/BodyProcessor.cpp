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

    std::vector<IgnoreRule>::iterator iterator = rules.begin();
    for (IgnoreRule rule: rules) {
        if (utils::fastCompare(tag, rule.getTag())) {
            switch (rule.getType()) {
                case TAG:
                    //Returning false, because tag is ignored by the rule
                    return false;

                case CLAZZ:
                    utils::extractClasses(tagBody, tempClasses);
                    for (std::string clazz: tempClasses) {
                        if (utils::fastCompare(clazz, rule.getClazz())) {
                            //Active rule found
                            return false;
                        }
                    }

                    break;
            }
        }
    }

    return true;
}


void BodyProcessor::addRule(IgnoreRule rule) {
    rules.push_back(rule);
}


void BodyProcessor::clearAllResources() {
    rules.clear();
}

#pragma clang diagnostic pop