///
/// Created by Miroslav Hýbler on 10.01.2024
///

#include "ExcludeRule.h"


ExcludeRule::ExcludeRule(std::string tag) {
    this->mTag = tag;
}


ExcludeRule::ExcludeRule(std::string tag, std::string clazz) {
    this->mTag = tag;
    this->mClazz = clazz;
}


ExcludeRule::ExcludeRule(std::string tag, std::string clazz, std::string id) {
    this->mTag = tag;
    this->mClazz = clazz;
    this->mId = id;
}


ExcludeRule::~ExcludeRule() {

}

const std::string ExcludeRule::getTag() {
    return mTag;
}

const std::string ExcludeRule::getClazz() {
    return mClazz;
}

const std::string ExcludeRule::getId() {
    return mId;
}