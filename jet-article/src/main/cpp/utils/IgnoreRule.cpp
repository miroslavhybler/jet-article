///
/// Created by Miroslav Hýbler on 10.01.2024
///

#include "IgnoreRule.h"


IgnoreRule::IgnoreRule(RuleType type) {
    this->mType = type;
}

IgnoreRule::IgnoreRule(RuleType type, std::string tag) {
    this->mType = type;
    this->mTag = tag;
}

IgnoreRule::IgnoreRule(RuleType type, std::string tag, std::string clazz) {
    this->mType = type;
    this->mTag = tag;
    this->mClazz = clazz;
}


IgnoreRule::~IgnoreRule() {

}

std::string IgnoreRule::getTag() {
    return mTag;
}

std::string IgnoreRule::getClazz() {
    return mClazz;
}

RuleType IgnoreRule::getType() {
    return mType;
}