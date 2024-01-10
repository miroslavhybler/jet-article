///
/// Created by Miroslav Hýbler on 03.01.2024
///

#ifndef JET_HTML_ARTICLE_UTILS_H
#define JET_HTML_ARTICLE_UTILS_H


#include "IndexWrapper.h"

/**
 * @since 1.0.0
 */
namespace utils {


    /**
     * Tries to find index of substring within input
     * @param input Input for searching substring
     * @param sub Substring you want to search
     * @param i Start index
     * @return index of first found substring, -1 if not found
     * @since 1.0.0
     */
    int indexOf(const std::string &input, const std::string &sub, const int i);


    /**
     * Tries to find index of substring within input
     * @param input Input for searching substring
     * @param sub Substring you want to search
     * @param i Start index
     * @throws Exception when TODO
     * @return index of first found substring
     * @since 1.0.0
     */
    int indexOfOrThrow(const std::string &input, const std::string &sub, const int i);


    /**
     * Extracts tag name out of the tag body and makes it lowercase.
     * @param tagBody TagType body within <> e.g. <a href="">
     * @return Lowercase tag name parsed out of tagBody
     * @since 1.0.0
     */
    std::string getTagName(const std::string tagBody);


    /**
     * Logs message in logcat
     * @param tag TagType of the message
     * @param message Message body
     * @since 1.0.0
     */
    void log(const char *tag, const std::string message);


    /**
     * Fastly compares given strings. Checks first chars at fist because at 90% of scenarios first
     * characters will be different. When first chars are same, results is from strcmp() function.
     * @param s1 Input string 1
     * @param s2 Input string 2
     * @return True when s1 and s2 are equeal strings, false otherwise.
     * @since 1.0.0
     */
    bool fastCompare(const std::string s1, const std::string s2);


    /**
     * Called after Parser finds a '<' char and needs to check if its valid tag. Checks for sequences
     * that are not supported like comments, cdata and doctype. If the input at index after the '<'
     * is considered unsupported, index will be moved at the end of the invalid sequence. The index
     * has to be set at '<' otherwise output is irrelevant.
     *
     * Note: There is no check whatever is tag valid, the sequence after '<' is considered being
     * able to parse.
     * @param input Input
     * @param l Length of input
     * @param index IndexWrapper that will be used.
     * @return True if next string to process is valid tag syntax
     * @since 1.0.0
     */
    bool canProcessIncomingTag(
            const std::string input,
            const int l,
            IndexWrapper index
    );


    /**
     * Tries to find the right closing tag for tag. When tag contains same tags within like <p><p></p></p>
     * it will folds every same inner tag into a stack and then popping it out. When the tag is found
     * and stack for inner tags is empty, found tag is considered being right closing tag.
     *
     * Make sure to clip content or set index correctly. The content in which you are going to search
     * has to be with clipped of the start tag. Otherwise the opening tag would be pushed into stack
     * too and program fails.
     * E.g:
     * searching for <p> must be in clipped content ..... </p>
     *
     * Note: There is no validation of pair tags inside, you are responsible for searching the proper
     * pair tag.
     * @param input Input string in which closing tag will be searched
     * @param tag Lowercase pair tag name you are searching for
     * @param index actual index. Searching will be start from this index.
     * @param e End index. Optional, if value is less than 0, input.length() will be used.
     * @throw When closing tag was not found within content
     * @return Index if start of the closing tag, index of '<' char
     * @since 1.0.0
     */
    int findClosingTag(
            const std::string input,
            const std::string tag,
            IndexWrapper index,
            const int e = 0
    );


    /**
     *
     * @param input
     * @param tag
     * @param s
     * @param e
     * @since 1.0.0
     */
    void groupPairTagContents(
            const std::string input,
            const std::string tag,
            const int s = 0,
            const int e = 0
    );
}

#endif //JET_HTML_ARTICLE_UTILS_H
