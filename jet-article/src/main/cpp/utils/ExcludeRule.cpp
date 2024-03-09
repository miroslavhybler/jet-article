///
/// Created by Miroslav Hýbler on 10.01.2024
///

#include "ExcludeRule.h"


ExcludeRule::ExcludeRule(std::string_view tag) {
    this->mTag = tag;
}


ExcludeRule::ExcludeRule(std::string_view tag, std::string_view clazz) {
    this->mTag = tag;
    this->mClazz = clazz;
}


ExcludeRule::ExcludeRule(std::string_view tag, std::string_view clazz, std::string_view id) {
    this->mTag = tag;
    this->mClazz = clazz;
    this->mId = id;
}

ExcludeRule::ExcludeRule(std::string_view tag, std::string_view clazz, std::string_view id, std::string_view keyword) {
    this->mTag = tag;
    this->mClazz = clazz;
    this->mId = id;
    this ->mKeyword = keyword;
}


ExcludeRule::~ExcludeRule() {

}

const std::string_view ExcludeRule::getTag() {
    return mTag;
}


const std::string_view ExcludeRule::getClazz() {
    return mClazz;
}


const std::string_view ExcludeRule::getId() {
    return mId;
}


const std::string_view ExcludeRule::getKeyword() {
    return mKeyword;
}