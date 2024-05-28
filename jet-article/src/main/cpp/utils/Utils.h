///
/// Created by Miroslav Hýbler on 03.01.2024
///

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wextern-initializer"
#ifndef JET_HTML_ARTICLE_UTILS_H
#define JET_HTML_ARTICLE_UTILS_H

#include <vector>
#include <map>
#include <android/log.h>
#include "IndexWrapper.h"


/**
 * Defines the utils namespace. Containing usefull util functions for working with html tags and strings
 * in general.
 * @since 1.0.0
 */
namespace utils {

    /**
     *
     * @param isEnabled
     * @since 1.0.0
     */
    void setIsLoggingEnabled(bool isEnabled);

    /**
     * Tries to find index of substring within input
     * @param input Input for searching substring
     * @param sub Substring you want to search
     * @param i Start index
     * @return index of first found substring, -1 if not found
     * @since 1.0.0
     */
    int indexOf(const std::string_view &input, const std::string &sub, const int &i);


    /**
     * Tries to find index of substring within input
     * @param input Input for searching substring
     * @param sub Substring you want to search
     * @param i Start index
     * @throws Exception when sub was not found within input from i
     * @return index of first found substring
     * @since 1.0.0
     */
    int indexOfOrThrow(const std::string_view &input, const std::string &sub, const int &i);


    /**
     * Extracts tag name out of the tag body and makes it lowercase.
     * @param tagBody TagType body within <> e.g. <a href="">
     * @return Lowercase tag name parsed out of tagBody
     * @since 1.0.0
     */
    std::string getTagName(const std::string_view &tagBody);


    /**
     * Logs message in andorid logcat. Keep in mind that logging should be used for development purpouses
     * only, any release of library should not include much logs from proccessing because it's slowing
     * it down.
     * @param tag Tag of the message
     * @param message Message body
     * @param prio Priority of the log
     * @since 1.0.0
     */
    void log(
            const char *tag,
            const std::string &message,
            android_LogPriority prio = ANDROID_LOG_DEBUG
    );


    /**
     * Fastly compares given strings. Checks first chars at fist because at 90% of scenarios first
     * characters will be different. When first chars are same, results is from strcmp() function.
     * @param s1 Input string 1
     * @param s2 Input string 2
     * @return True when s1 and s2 are equeal strings, false otherwise.
     * @since 1.0.0
     */
    bool fastCompare(const std::string_view &s1, const std::string_view &s2);


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
            const std::string_view &input,
            const int &l,
            const int &s,
            int &outIndex
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
    * @param s Start index
    * @param e End index. Optional, if value is less than 0, input.length() will be used.
    * @throw When closing tag was not found within content
    * @return Index if start of the closing tag, index of '<' char
    * @since 1.0.0
    */
    const int findClosingTag(
            const std::string_view &input,
            const std::string &tag,
            int s,
            const int e = 0
    );


    const int findClosingTagWithLogs(
            const std::string_view &input,
            const std::string &tag,
            int s,
            const int e = 0
    );


    const int findUnsupportedTagClosing(
            const std::string_view &input,
            const std::string &tag,
            int s
    );


    /**
     *
     * @param input
     * @param output
     * @param s
     * @param e
     * @since 1.0.0
     */
    void clearUnsupportedTagsFromTextBlock(
            std::string &input,
            std::string &output,
            int s,
            int e
    );


    /**
     *
     * @param input
     * @param output
     * @param s
     * @param e
     * @since 1.0.0
     */
    void clearUnsupportedTagsFromTextBlock(
            std::string_view &input,
            std::string &output,
            int s,
            int e
    );


    /**
     * Goes through input from s to e and parses out all tags same as tag and puts it into output list.
     * @param input Input within you want to search for tag. Must be full content input, start and end
     * are defined by s and e.
     * @param tag Pair tag you want to group.
     * @param s Start index of range where to search for tags.
     * @param e End index of range where to search for tags.
     * @param outputList List where result will be stored.
     * @since 1.0.0
     */
    void groupPairTagContents(
            const std::string_view &input,
            const std::string &tag,
            const int &s,
            const int &e,
            std::vector<std::string_view> &outputList
    );


    /**
     * Trims [s] from the left side, removing white chars from the text.
     * @param s String you want to trim. Will be modified.
     * @since 1.0.0
     */
    inline void ltrim(std::string &s);


    /**
     * Trims [s] from the right side, removing white chars from the text.
     * @param s String you want to trim. Will be modified.
     * @since 1.0.0
     */
    inline void rtrim(std::string &s);


    /**
     * Trims [s] from both side, removing white chars from the text.
     * @param s String you want to trim. Will be modified.
     * @since 1.0.0
     */
    void trim(std::string &s);


    /**
     * Splits [input] string by [separator] character and stores output into [outList]
     * @param input Input text to you want split
     * @param separator Characted which will be used to separate [input]
     * @param outList Output list where result will be stored
     * @since 1.0.0
     */
    void split(
            std::string_view &input,
            const char &separator,
            std::vector<std::string_view> &outList
    );


    /**
     * Process tagBody and parses out all attributes and it's values and stores it into outMap.
     * OutMap is cleared at the begging. OutMap can be empty when there are no attributes inside tagBody.
     * Attribute names and values are trimmed.
     * @param tagBody Tag body with <> with brackets removed, e.g. 'img src="url"'
     * @param outMap Output map where result will be stored. Attrubute is key and attribute value
     * is value.
     * @since 1.0.0
     */
    void
    getTagAttributes(
            const std::string &tagBody,
            std::map<std::string, std::string> &outMap
    );


    /**
     * Extracts single tag attribute by [attributeName] from [tagBody].
     * @param tagBody Tag body where to search for attribute
     * @param attributeName Name of the attribute you want to get
     * @return Found attribute value, or empty string when not found. Trimmed.
     * @since 1.0.0
     */
    std::string getTagAttribute(const std::string &tagBody, const std::string &attributeName);


    /**
     * Extracts all classes from [tagBody] from "class" attribute. Results will be stored in [outList].
     * @param tagBody Tag body where to search for classes.
     * @param outList Output list where results will be stored.
     * @since 1.0.0
     */
    void extractClasses(
            const std::string_view &tagBody,
            std::vector<std::string_view> &outList
    );


    /**
     * @return True when tag is considered being single tag based on it's body
     * @param tagBody Body of the tag
     * @since 1.0.0
     */
    bool isTagSingleTag(std::string &tagBody);


    /**
     * @return True when tag is considered being pair tag based on it's body
     * @param tagBody Body of the tag
     * @since 1.0.0
     */
    bool isTagPairTag(std::string &tagBody);
}

#endif //JET_HTML_ARTICLE_UTILS_H

#pragma clang diagnostic pop