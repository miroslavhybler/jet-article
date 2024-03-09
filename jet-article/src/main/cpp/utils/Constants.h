///
/// Created by Miroslav Hýbler on 10.01.2024
///


#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#ifndef JET_HTML_ARTICLE_CONSTANTS_H
#define JET_HTML_ARTICLE_CONSTANTS_H

#include <set>

/**
 * Sipmifies the meaning of html tags.
 * @since 1.0.0
 */
enum TagType {
    NO_CONTENT = -1,
    IMAGE = 1,
    TEXT = 2,
    QUOTE = 3,
    TITLE = 4,
    TABLE = 5,
    ADDRESS = 6,
    LIST = 7,
    CODE = 8,
};


enum ErrorCode {
    NO_ERROR = -1,
    NO_INDEX_FOUND = 1,
    NO_CLOSING_TAG_FOUND = 2,

    CONTENT_NOT_HTML = 100000
};


const std::set<std::string> unsupportedPairTagsForTextBlock = {
        "noscript",
        "script",
        "svg",
        "div"
};

const std::set<std::string> unsupportedPairTags = {
        "noscript",
        "script",
        "svg",
};



#endif //JET_HTML_ARTICLE_CONSTANTS_H

#pragma clang diagnostic pop