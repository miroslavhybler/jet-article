///
/// Created by Miroslav Hýbler on 10.01.2024
///

#include "Rule.h"


Rule::Rule(RuleType type) {
    this->mType = type;
}

Rule::~Rule() {

}

std::string Rule::getTag() {
    return mTag;
}

RuleType Rule::getType() {
    return mType;
}