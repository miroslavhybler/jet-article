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


/**
 * @since 1.0.0
 */
enum ErrorCode {
    NO_ERROR = -1,
    NO_INDEX_FOUND = 1,
    NO_CLOSING_TAG_FOUND = 2,

    CONTENT_NOT_HTML = 100000
};


/**
 * List of pair tags that has to be romoved from text tags (like p, span, ...) in order to show text
 * correctly without unknonw object reference.
 * @since 1.0.0
 */
const std::set<std::string> unsupportedPairTagsForTextBlock = {
        "noscript",
        "script",
        "svg",
        "div"
};


/**
 * List of pair tags that are not supported by this library in global. For instance it means that
 * jet-article library cannot process <script> tag in any possible way.
 * @since 1.0.0
 */
const std::set<std::string> unsupportedPairTags = {
        "noscript",
        "script",
        "svg",
        "button",
        "input",
        "form",
};



#endif //JET_HTML_ARTICLE_CONSTANTS_H

#pragma clang diagnostic pop