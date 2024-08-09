///
/// Created by Miroslav Hýbler on 10.01.2024
///

#include "ExcludeRule.h"


ExcludeRule::ExcludeRule(
        std::string_view tag
) {
    this->mTag = tag;
}


ExcludeRule::ExcludeRule(
        std::string_view tag,
        std::string_view clazz
) {
    this->mTag = tag;
    this->mClazz = clazz;
}


ExcludeRule::ExcludeRule(
        std::string_view tag,
        std::string_view clazz,
        std::string_view id
) {
    this->mTag = tag;
    this->mClazz = clazz;
    this->mId = id;
}

ExcludeRule::ExcludeRule(
        std::string_view tag,
        std::string_view clazz,
        std::string_view id,
        std::string_view keyword
) {
    this->mTag = tag;
    this->mClazz = clazz;
    this->mId = id;
    this->mKeyword = keyword;
}


ExcludeRule::~ExcludeRule() {

}

std::string_view ExcludeRule::getTag() {
    return mTag;
}


std::string_view ExcludeRule::getClazz() {
    return mClazz;
}


std::string_view ExcludeRule::getId() {
    return mId;
}


std::string_view ExcludeRule::getKeyword() {
    return mKeyword;
}

std::string ExcludeRule::toString() {
    std::string message = "ExcludeRule(";
    message += "tag=" + std::string(mTag);
    message += ", clazz=" + std::string(mClazz);
    message += ", id=" + std::string(mId);
    message += ", keyword=" + std::string(mKeyword);
    message += ")";
    return message;
}