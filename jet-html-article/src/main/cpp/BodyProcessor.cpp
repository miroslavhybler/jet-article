///
/// Created by Miroslav Hýbler  on 08.01.2024
///

#include "BodyProcessor.h"
#include "Utils.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"


bool BodyProcessor::isTagValidForNextProcessing(
        const std::string tag,
        const std::string tagBody
) {
    if (rules.empty()) {
        return true;
    }

    std::list<Rule>::iterator iterator = rules.begin();
    for (Rule rule: rules) {
        if (utils::fastCompare(tag, rule.getTag())) {
            switch (rule.getType()) {
                case TAG:
                    //Returning false, because tag is ignored by the rule
                    return false;

                case CLAZZ:
                    //TODO check on clazz
                    break;
            }
        }
    }

    return true;
}


void BodyProcessor::addRule(Rule rule) {
    rules.push_back(rule);
}


void BodyProcessor::clearAllResources() {
    rules.clear();
}

#pragma clang diagnostic pop