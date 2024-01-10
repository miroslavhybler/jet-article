///
/// Created by Miroslav Hýbler on 10.01.2024
///


#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#ifndef JET_HTML_ARTICLE_CONSTANTS_H
#define JET_HTML_ARTICLE_CONSTANTS_H


/**
 * Sipmifies the meaning of html tags.
 * @since 1.0.0
 */
enum TagType {
    NO_CONTENT = -1,
    IMAGE = 1,
    PARAGRAPH = 2,
    QUOTE = 3,
    TITLE = 4,
    TABLE = 5,
    ADDRESS = 6,
    LIST = 7,
    CODE = 8,
    // TODO container
};

#endif //JET_HTML_ARTICLE_CONSTANTS_H

#pragma clang diagnostic pop