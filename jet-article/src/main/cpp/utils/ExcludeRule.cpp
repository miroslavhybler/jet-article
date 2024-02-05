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


ExcludeRule::~ExcludeRule() {

}

std::string ExcludeRule::getTag() {
    return mTag;
}

std::string ExcludeRule::getClazz() {
    return mClazz;
}