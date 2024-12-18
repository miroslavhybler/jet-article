///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <string>
#include <stack>
#include <jni.h>
#include <vector>
#include <map>
#include <android/log.h>
#include "IndexWrapper.h"
#include "Constants.h"
#include "Utils.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
namespace utils {

    /**
     * Temporary working integer, should be always used only within scope of one function.
     * @since 1.0.0
     */
    //TODO size_t
    int tempWorkingInt;


    /**
     * True when logging is enabled, false otherwise. Used for development and debugging only,
     * doesn't make sense to use logging in final app.
     * @since 1.0.0
     */
    bool isLoggingEnabled = false;


    std::function<bool(unsigned char)> trimPred = [](unsigned char ch) -> bool {
        return !std::isspace(ch);
    };


    void setIsLoggingEnabled(bool isEnabled) {
        isLoggingEnabled = isEnabled;
    }


    void log(
            const char *tag,
            const std::string &message,
            android_LogPriority prio
    ) {
        if (!isLoggingEnabled) {
            return;
        }

        __android_log_print(
                prio,
                tag,
                "%s",
                message.c_str()
        );
    }


    inline void ltrim(std::string &s) {
        s.erase(s.begin(), std::find_if(s.begin(), s.end(), trimPred));
    }

    inline void rtrim(std::string &s) {
        s.erase(std::find_if(s.rbegin(), s.rend(), trimPred).base(), s.end());
    }


    void trim(std::string &s) {
        ltrim(s);
        rtrim(s);
    }


    void split(
            std::string_view &input,
            const char &separator,
            std::vector<std::string_view> &outList
    ) {
        size_t s = 0;
        size_t i = 0;
        size_t l = input.length();

        if (input.find(separator, 0) == std::string_view::npos) {

            //Unable to split,
            outList.push_back(input);
            return;
        }

        while (i < l) {
            char ch = input[i];
            if (ch == separator) {
                std::string_view sub = input.substr(s, i - s);
                outList.push_back(sub);
                s = i + 1;
                i += 2;
            } else {
                i += 1;
            }
        }

        if (s != i && i - s > 0) {
            std::string_view sub = input.substr(s, i - s);
            outList.push_back(sub);
        }
    }


    bool fastCompare(
            const std::string_view &s1,
            const std::string_view &s2
    ) {
        char ch1 = s1[0];
        char ch2 = s2[0];
        if (ch1 != ch2) {
            return false;
        }
        return s1 == s2;
    }


    int indexOf(
            const std::string_view &input,
            const std::string &sub,
            const size_t &i
    ) {
        typename std::string_view::const_iterator sit = input.begin();
        std::advance(sit, i);
        typename std::string_view::const_iterator it = std::search(
                sit,
                input.end(),
                sub.begin(),
                sub.end()
        );

        if (it != input.end()) {
            return it - input.begin();
        }

        return -1;
    }


#pragma clang diagnostic push
#pragma ide diagnostic ignored "misc-throw-by-value-catch-by-reference"

    size_t indexOfOrThrow(
            const std::string_view &input,
            const std::string &sub,
            const size_t &i
    ) {
        int index = indexOf(input, sub, i);

        if (index == -1) {
            utils::log(
                    "UTILS",
                    "Unable to find index of " + sub
                    + " from: " + std::to_string(i)
                    + " until: " + std::to_string(input.length())
            );
            throw NO_INDEX_FOUND;
        }

        return index;
    }

#pragma clang diagnostic pop


    std::string getTagName(const std::string_view &tagBody) {
        std::string name = std::string(tagBody);

        if (tagBody.find(' ')) {
            int ei = indexOf(tagBody, " ", 0);
            if (ei > 0) {
                name = tagBody.substr(0, ei);
            }
        }
        trim(name);

        //for (int x = 0; x < name.length(); x++) {
        //    putchar(tolower(name[x]));
        // }

        return name;
    }


    bool canProcessIncomingTag(
            const std::string_view &input,
            const size_t &l,
            const size_t &s,
            size_t &outIndex
    ) {

        if (s >= l) {
            return false;
        }

        size_t i = s;
        outIndex = i;
        if ((i + 3) < l) {
            size_t il = i + 3;
            std::string_view sub;
            try {
                sub = input.substr(i + 1, 3);
            } catch (std::out_of_range &e) {
                return false;
            }

            if (utils::fastCompare(sub, "!--")) {
                size_t ei;
                try {
                    ei = utils::indexOfOrThrow(input, "-->", il);
                } catch (ErrorCode e) {
                    return false;
                }

                outIndex = ei + 3;
                return false;
            }
        }
        if (i + 12 < l) {
            size_t il = i + 12;
            std::string_view sub;

            try {
                sub = input.substr(i + 1, 12);
            } catch (std::out_of_range &e) {
                return false;
            }

            if (utils::fastCompare(sub, "/![cdata[//>")) {
                outIndex = il;
                return false;
            }
        }
        if ((i + 14) < l) {
            size_t il = i + 14;
            std::string_view sub;
            try {
                sub = input.substr(i + 1, 14);
            } catch (std::out_of_range &e) {
                return false;
            }
            if (utils::fastCompare(sub, "!doctype html>")) {
                outIndex = il;
                return false;
            }
        }
        return true;
    }


    size_t findUnsupportedTagClosing(
            const std::string_view &input,
            const std::string &tag,
            size_t s
    ) {
        std::string closingTag = "</" + tag + ">";

        try {
            size_t ctsi = utils::indexOfOrThrow(input, closingTag, s);
            return ctsi;
        } catch (ErrorCode e) {

        }
        utils::log("UTILS",
                   "Unable to find closing for unsupported: " + tag
                   + " at index: " + std::to_string(s)
        );
        throw NO_CLOSING_TAG_FOUND;
    }


    size_t findClosingTag(
            const std::string_view &input,
            const std::string &searchedTag,
            size_t s,
            size_t e
    ) {
        size_t i = s;
        size_t outI = i;
        //Clearing tempStack before another use

        tempWorkingInt = 0;
        size_t length = input.length();
        size_t end = e > 0 ? e : length;
        while (i < end) {
            char ch = input[i];
            if (ch != '<' && i < end) {
                i += 1;
                continue;
            }

            //char is '<'
            if (!utils::canProcessIncomingTag(input, length, i, outI)) {
                //Unable to process
                if (i == outI) {
                    i += 1;
                } else {
                    i = outI;
                }
                continue;
            }
            //TagType closing index, index of next '>'
            size_t tei = utils::indexOfOrThrow(input, ">", i);
            // -1 to remove '<' at the end
            size_t tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string_view tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);
            bool isClosingTag = rawTagName.find('/', 0) == 0;
            if (isClosingTag) {
                std::string tagName = rawTagName.substr(1, rawTagName.length());
                bool isSearched = utils::fastCompare(tagName, searchedTag);
                if (isSearched) {
                    if (tempWorkingInt > 0) {
                        //Stack is not empty, means that we found closing of inner same tag
                        tempWorkingInt -= 1;
                    } else {
                        return i;
                    }
                }
            } else {
                if (unsupportedPairTags.contains(rawTagName)) {
                    //Unsuported tag found, probably script, has to be skipped
                    try {
                        std::string closingTag = "</" + rawTagName + ">";
                        size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                        i = ctsi + closingTag.length();
                    } catch (ErrorCode e) {
                        break;
                    }
                    //Continue the cycle, skipping at the next index after unsupported pair tag
                    continue;
                }


                if (utils::fastCompare(searchedTag, rawTagName)) {
                    //Push because inside tag is another one, like p in p -> <p><p>...</p></p>
                    std::string sub = std::string(input.substr(i, 40));
                    tempWorkingInt += 1;
                }
            }

            i = tei + 1;
        }

        utils::log("UTILS",
                   "Unable to find closing for: " + searchedTag
                   + " at index: " + std::to_string(s)
        );
        throw NO_CLOSING_TAG_FOUND;
    }


    size_t findClosingTagWithLogs(
            const std::string_view &input,
            const std::string &tag,
            size_t s,
            size_t e
    ) {
        size_t i = s;
        size_t outI = i;
        //Clearing tempStack before another use

        tempWorkingInt = 0;

        utils::log("UTILS", "===================================");
        utils::log(
                "UTILS",
                "\t\tSearch Started at: " + std::to_string(i)
                + " for tag: " + tag);
        utils::log("UTILS", "===================================");

        size_t length = input.length();
        size_t end = e > 0 ? e : length;

        while (i < end) {
            char ch = input[i];
            if (ch != '<' && i < end) {
                i += 1;
                continue;
            }


            //char is '<'
            if (!utils::canProcessIncomingTag(input, input.length(), i, outI)) {
                //Unable to process
                if (i == outI) {
                    i += 1;
                } else {
                    i = outI;
                }
                continue;
            }
            //TagType closing index, index of next '>'
            size_t tei = utils::indexOfOrThrow(input, ">", i);
            // -1 to remove '<' at the end
            size_t tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string_view tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);
            bool isClosingTag = rawTagName.find('/', 0) == 0;
            if (isClosingTag) {
                std::string tagName = rawTagName.substr(1, rawTagName.length());
                bool isSearched = utils::fastCompare(tagName, tag);
                if (isSearched) {
                    if (tempWorkingInt > 0) {
                        //Stack is not empty, means that we found closing of inner same tag
                        tempWorkingInt -= 1;
                        utils::log(
                                "UTILS",
                                "Poping at " + std::to_string(i)
                                + " count: " + std::to_string(tempWorkingInt)
                                + " sub " + std::string(input.substr(i, 25))
                        );
                    } else {
                        return i;
                    }
                }
            } else {

                if (unsupportedPairTags.contains(rawTagName)) {
                    //Unsuported tag found, probably script, has to be skipped
                    utils::log("UTILS", "Skipping search becasuse of " + rawTagName);
                    try {
                        size_t ctsiAlt = utils::findUnsupportedTagClosing(
                                input,
                                rawTagName,
                                tei
                        );
                        std::string closingTag = "</" + rawTagName + ">";
                        size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                        i = ctsiAlt + closingTag.length();
                        utils::log("UTILS",
                                   "CTSI: " + std::to_string(ctsi)
                                   + " CTSI2: " + std::to_string(ctsiAlt)
                        );

                        if (ctsiAlt != ctsi) {
                            throw "CTSI alt != CTSI";
                        }

                        utils::log("UTILS", "Skipping to index: " + std::to_string(i));
                    } catch (ErrorCode e) {
                        utils::log("UTILS", "Skipping failed");
                        break;
                    }
                    //Continue the cycle, skipping at the next index after unsupported pair tag
                    continue;
                }


                if (utils::fastCompare(tag, rawTagName)) {
                    //Push because inside tag is another one, like p in p -> <p><p>...</p></p>
                    tempWorkingInt += 1;

                    utils::log(
                            "UTILS",
                            "Pushing at " + std::to_string(i)
                            + " count: " + std::to_string(tempWorkingInt)
                            + " sub " + std::string(input.substr(i, 25))
                    );
                }
            }

            i = tei + 1;
        }

        utils::log("UTILS",
                   "Unable to find closing for: " + tag
                   + " at index: " + std::to_string(s)
        );
        throw NO_CLOSING_TAG_FOUND;
    }


    void clearUnsupportedTagsFromTextBlock(
            std::string &input,
            std::string &output,
            size_t s,
            size_t e
    ) {
        size_t i = s;
        size_t outI = i;
        output.clear();

        while (i < e) {
            char ch = input[i];
            if (ch != '<') {
                output += ch;
                i += 1;
                continue;
            }

            if (!utils::canProcessIncomingTag(input, input.length(), i, outI)) {
                //Unable to process
                if (i == outI) {
                    i += 1;
                } else {
                    i = outI;
                }
                continue;
            }

            //TagType closing index, index of next '>'
            size_t tei = utils::indexOfOrThrow(input, ">", i);
            // -1 to remove '<' at the end
            size_t tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);

            if (rawTagName == "img") {
                i = tei + 1;
                continue;
            }

            if (unsupportedPairTagsForTextBlock.contains(rawTagName)) {
                //Unsuported tag found, probably script, has to be skipped
                try {
                    std::string closingTag = "</" + rawTagName + ">";
                    size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                    i = ctsi + closingTag.length();
                } catch (ErrorCode e) {
                    break;
                }
                //Continue the cycle, skipping at the next index after unsupported pair tag
                continue;
            } else {
                try {
                    std::string closingTag = "</" + rawTagName + ">";
                    size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                    output += std::string(input, i, (ctsi + closingTag.length()) - i);
                    i = ctsi + closingTag.length();
                } catch (ErrorCode e) {
                    i = tei + 1;
                    continue;
                }
            }

        }
    }

    void clearUnsupportedTagsFromTextBlock(
            std::string_view &input,
            std::string &output,
            size_t s,
            size_t e
    ) {
        size_t i = s;
        size_t outI = i;
        size_t length = input.length();
        output.clear();

        while (i < e) {
            char ch = input[i];
            if (ch != '<') {
                output += ch;
                i += 1;
                continue;
            }

            if (!utils::canProcessIncomingTag(input, length, i, outI)) {
                //Unable to process
                if (i == outI) {
                    i += 1;
                } else {
                    i = outI;
                }
                continue;
            }

            //TagType closing index, index of next '>'
            size_t tei = utils::indexOfOrThrow(input, ">", i);
            // -1 to remove '<' at the end
            size_t tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string_view tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);

            if (rawTagName == "img") {
                i = tei + 1;
                continue;
            }

            if (unsupportedPairTagsForTextBlock.contains(rawTagName)) {
                //Unsuported tag found, probably script, has to be skipped
                try {
                    std::string closingTag = "</" + rawTagName + ">";
                    size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                    i = ctsi + closingTag.length();
                } catch (ErrorCode e) {
                    break;
                }
                //Continue the cycle, skipping at the next index after unsupported pair tag
                continue;
            } else {
                try {
                    std::string closingTag = "</" + rawTagName + ">";
                    size_t ctsi = utils::indexOfOrThrow(input, closingTag, i);
                    output += std::string(input, i, (ctsi + closingTag.length()) - i);
                    i = ctsi + closingTag.length();
                } catch (ErrorCode e) {
                    i = tei + 1;
                    continue;
                }
            }

        }
    }

    void clearTagsAndEntitiesFromText(
            const std::string_view &input,
            std::string &output
    ) {
        if (input.empty()) {
            return;
        }

        output.clear();
        bool insideTag = false;

        size_t length = input.length();
        size_t i = 0;
        while (i < length) {
            char c = input[i];
            if (c == '<') {
                insideTag = true;
                i += 1;
            } else if (c == '>' && insideTag) {
                insideTag = false;
                i += 1;
            } else if (c == '&') {
                //Entity end index
                size_t endI;

                try {
                    endI = indexOfOrThrow(input, ";", i);
                } catch (ErrorCode &e) {
                    //Text is not an entity
                    endI = -1;
                    i += 1;
                }
                if (endI != -1) {
                    //+1 to include ; into entity string
                    std::string_view entity = input.substr(i, endI - i + 1);
                    std::string ch = decodeHtmlEntity(std::string(entity));
                    output += ch;
                    i += entity.length();
                } else {
                    output += c;
                }
            } else if (!insideTag) {
                output += c;
                i += 1;
            } else {
                i += 1;
            }
        }
    }


    void clearTagsFromText(
            const std::string_view &input,
            std::string &output
    ) {
        if (input.empty()) {
            return;
        }

        output.clear();
        bool insideTag = false;
        for (char c: input) {
            if (c == '<') {
                insideTag = true;
            } else if (c == '>' && insideTag) {
                insideTag = false;
            } else if (!insideTag) {
                output += c;
            }
        }
    }


    std::string decodeHtmlEntity(
            const std::string &entity
    ) {
        if (entity.substr(0, 2) == "&#" && entity.back() == ';') {
            if (entity[2] == 'x' || entity[2] == 'X') {
                return decodeHtmlEntityHexadecimal(entity);
            } else {
                return decodeHtmlEntityDecimal(entity);
            }
        }
        return "";
    }


    std::string decodeHtmlEntityDecimal(
            const std::string &entity
    ) {
        std::string decimalNumber = entity.substr(2, entity.size() - 3);  // Extract decimal number
        int codePoint = 0;

        try {
            codePoint = std::stoi(decimalNumber, nullptr, 10);  // Convert decimal string to integer
        } catch (const std::invalid_argument &e) {
            return "";  // Return empty if conversion fails
        }

        // Convert codePoint to UTF-8 or ASCII
        std::string result;
        if (codePoint < 128) {
            // Basic ASCII
            result += static_cast<char>(codePoint);
        } else {
            // Convert to UTF-8
            if (codePoint <= 0x7FF) {
                result += static_cast<char>(0xC0 | ((codePoint >> 6) & 0x1F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            } else if (codePoint <= 0xFFFF) {
                result += static_cast<char>(0xE0 | ((codePoint >> 12) & 0x0F));
                result += static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            } else if (codePoint <= 0x10FFFF) {
                result += static_cast<char>(0xF0 | ((codePoint >> 18) & 0x07));
                result += static_cast<char>(0x80 | ((codePoint >> 12) & 0x3F));
                result += static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            }
        }

        return result;
    }


    std::string decodeHtmlEntityHexadecimal(
            const std::string &entity
    ) {
        std::string hexNumber = entity.substr(3, entity.size() - 4);  // Extract hexadecimal number
        int codePoint = 0;

        try {
            codePoint = std::stoi(hexNumber, nullptr, 16);  // Convert hex string to integer
        } catch (const std::invalid_argument &e) {
            return "";  // Return empty if conversion fails
        }

        // Convert codePoint to UTF-8 or ASCII
        std::string result;
        if (codePoint < 128) {
            // Basic ASCII
            result += static_cast<char>(codePoint);
        } else {
            // Convert to UTF-8
            if (codePoint <= 0x7FF) {
                result += static_cast<char>(0xC0 | ((codePoint >> 6) & 0x1F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            } else if (codePoint <= 0xFFFF) {
                result += static_cast<char>(0xE0 | ((codePoint >> 12) & 0x0F));
                result += static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            } else if (codePoint <= 0x10FFFF) {
                result += static_cast<char>(0xF0 | ((codePoint >> 18) & 0x07));
                result += static_cast<char>(0x80 | ((codePoint >> 12) & 0x3F));
                result += static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F));
                result += static_cast<char>(0x80 | (codePoint & 0x3F));
            }
        }

        return result;
    }

    void getTagAttributes(
            const std::string &tagBody,
            std::map<std::string, std::string> &outMap
    ) {
        int i = 0;
        while (i < tagBody.length()) {
            int ni = indexOf(tagBody, " ", i);
            if (ni == -1) {
                //Tag has no attributes defined within it's body or we read all attributes already
                return;
            }

            //atrribute name start index
            int asi = ni + 1;
            //attribute name end
            int aei = indexOf(tagBody, "\"", asi);
            //atribute value end
            int avi = indexOf(tagBody, "\"", aei + 1);

            if (aei == -1 && avi == -1) {
                //Since attribute value wasn't found withing "", it's probably in apostrofes ''
                aei = indexOf(tagBody, "\'", asi);
                avi = indexOf(tagBody, "\'", aei + 1);
            }

            if (aei == -1) {
                //attribute not found,
                return;
            }
            //Minus 1 to remove '=' from attribute name e.g. class="hello"
            std::string attributeName = tagBody.substr(asi, aei - asi - 1);
            //Plus 1 to remove '=' from attribute value
            //Minus 1 to remove '"' from attribute value
            std::string attributeValue = tagBody.substr(aei + 1, avi - aei - 1);
            utils::trim(attributeName);
            utils::trim(attributeValue);
            outMap[attributeName] = attributeValue;
            i = aei;
        }
    }


    std::string getTagAttribute(
            const std::string &tagBody,
            const std::string &attributeName
    ) {
        int i = 0;
        while (i < tagBody.length()) {
            int ni = indexOf(tagBody, " ", i);
            if (ni == -1) {
                //Tag has no attributes defined within it's body or we read all attributes already
                return "";
            }

            //atrribute name start index
            int asi = ni + 1;
            //attribute name end
            int aei = indexOf(tagBody, "\"", asi);
            //atribute value end
            int avi = indexOf(tagBody, "\"", aei + 1);

            if (aei == -1 && avi == -1) {
                //Since attribute value wasn't found withing "", it's probably in apostrofes ''
                aei = indexOf(tagBody, "\'", asi);
                avi = indexOf(tagBody, "\'", aei + 1);
            }

            if (aei == -1) {
                //attribute not found nor within "" nor '', it's probably not valid
                return "";
            }
            //Minus 1 to remove '=' from attribute name e.g. class="hello"
            std::string foundAttributeName = tagBody.substr(asi, aei - asi - 1);
            //Plus 1 to remove '=' from attribute value
            //Minus 1 to remove '"' from attribute value
            std::string attributeValue = tagBody.substr(aei + 1, avi - aei - 1);
            utils::trim(foundAttributeName);
            utils::trim(attributeValue);

            if (utils::fastCompare(attributeName, foundAttributeName)) {
                return attributeValue;
            }

            i = aei;
        }
        return "";
    }


    void groupPairTagContents(
            const std::string_view &input,
            const std::string &tag,
            const size_t &s,
            const size_t &e,
            std::vector<std::string_view> &outputList
    ) {
        if (!outputList.empty()) {
            outputList.clear();
        }

        size_t end = e != 0 ? e : input.length();
        size_t i = s;
        while (i < end) {
            char ch = input[i];
            if (ch != '<') {
                i += 1;
                continue;
            }

            size_t tei = utils::indexOfOrThrow(input, ">", i);
            size_t tagBodyLength = tei - i - 1;
            std::string tagBody = std::string(input.substr(i + 1, tagBodyLength));
            std::string rawTagName = utils::getTagName(tagBody);

            if (utils::fastCompare(tag, rawTagName)) {
                std::string closingTag = "</" + tag + ">";
                size_t ctsi = utils::indexOfOrThrow(input, closingTag, tei);
                std::string_view foundTag = input.substr(tei + 1, ctsi - tei - 1);
                outputList.push_back(foundTag);
                i = ctsi + 1;
            } else {
                i += tagBodyLength;
            }
        }
    }


    void extractClasses(
            const std::string_view &tagBody,
            std::vector<std::string_view> &outList
    ) {
        bool isSinglemark = false;
        std::string separator = "class=\"";
        int s = utils::indexOf(tagBody, separator, 0);
        if (s == -1) {
            separator = "class=\'";
            isSinglemark = true;
            s = utils::indexOf(tagBody, separator, 0);
        }


        if (s == -1) {
            //Tag has no classes
            return;
        }

        std::string endCh = isSinglemark ? "\'" : "\"";
        size_t e = utils::indexOfOrThrow(tagBody, endCh, s + separator.length());
        std::string_view classes = tagBody.substr(
                s + separator.length(),
                e - (s + separator.length())
        );

        if (!outList.empty()) {
            outList.clear();
        }

        utils::split(classes, ' ', outList);
    }


    bool isTagSingleTag(std::string &tagBody) {
        int pos = tagBody.length() - 1;
        bool hasClosing = tagBody.find('/', pos) == 0;
        return hasClosing;
    }


    bool isTagPairTag(std::string &tagBody) {
        return !isTagSingleTag(tagBody);
    }


    std::string listToString(std::vector<std::string_view> &list) {
        std::string output;


        for (const auto text: list) {
            output += text;
            output += ",";
        }

        return output;
    }

    std::string boolToString(bool value) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
#pragma clang diagnostic pop