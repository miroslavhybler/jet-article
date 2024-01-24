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

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
//TODO string_view
namespace utils {

    int tempInt;


    std::function<bool(unsigned char)> trimPred = [](unsigned char ch) {
        return !std::isspace(ch);
    };


    void log(
            const char *tag,
            const std::string &message,
            android_LogPriority prio = ANDROID_LOG_DEBUG
    ) {
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


    inline void trim(std::string &s) {
        ltrim(s);
        rtrim(s);
    }


    void split(
            std::string input,
            const char separator,
            std::vector<std::string> &outList
    ) {
        int s = 0;
        int i = 0;
        int l = input.length();
        while (i < l) {
            char ch = input[i];
            if (ch == separator) {
                outList.push_back(input.substr(s, i - s));
                s = i;
            }
            i += 1;
        }
    }


    bool fastCompare(const std::string &s1, const std::string &s2) {
        char ch1 = s1[0];
        char ch2 = s2[0];
        if (ch1 != ch2) {
            return false;
        }
        return s1 == s2;
    }


    int indexOf(const std::string &input, const std::string &sub, const int &i) {
        typename std::string::const_iterator sit = input.begin();
        std::advance(sit, i);
        typename std::string::const_iterator it = std::search(
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


    int indexOfOrThrow(const std::string &input, const std::string &sub, const int &i) {
        int index = indexOf(input, sub, i);

        if (index == -1) {
            std::string error = "Unable to find index of " + sub
                                + " from: " + std::to_string(i)
                                + " until: " + std::to_string(input.length());
            utils::log("UTILS", error);
            throw NO_INDEX_FOUND;
        }

        return index;
    }


    std::string getTagName(const std::string &tagBody) {
        std::string name = tagBody;

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
            const std::string &input,
            const int &l,
            IndexWrapper &index,
            int &outIndex
    ) {
        int i = index.getIndex();
        outIndex = i;
        if ((i + 3) < l) {
            int il = i + 3;
            std::string sub = input.substr(i + 1, 3);
            if (utils::fastCompare(sub, "!--")) {
                int ei = utils::indexOfOrThrow(input, "-->", il);
                outIndex = ei + 4;
                return false;
            }
        }

        if ((i + 15) < l) {
            int il = i + 15;
            std::string sub = input.substr(i + 1, 14);
            if (utils::fastCompare(sub, "!doctype html>")) {
                outIndex = il;
                return false;
            }
        }

        if (i + 12 < l) {
            int il = i + 12;
            std::string sub = input.substr(i + 1, 12);
            if (utils::fastCompare(sub, "/![cdata[//>")) {
                outIndex = il;
                return false;
            }
        }
        return true;
    }


    int findClosingTag(
            const std::string &input,
            const std::string &searchedTag,
            IndexWrapper &index,
            const int &e
    ) {
        int i = index.getIndex();
        int outI = i;
        //Clearing tempStack before another use

        tempInt = 0;

        int end = e > 0 ? e : input.length();
        while (i >= index.getIndex() && i < end) {
            char ch = input[i];
            if (ch != '<' && i < end) {
                i += 1;
                continue;
            }


            //char is '<'
            if (!utils::canProcessIncomingTag(input, input.length(), index, outI)) {
                //Unable to process
                if (i == outI) {
                    i += 1;
                } else {
                    i = outI;
                }
                continue;
            }
            //TagType closing index, index of next '>'
            int tei = utils::indexOfOrThrow(input, ">", i);
            // -1 to remove '<' at the end
            int tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);
            bool isClosingTag = rawTagName.find('/', 0) == 0;
            if (isClosingTag) {
                std::string tagName = rawTagName.substr(1, rawTagName.length());
                bool isSearched = utils::fastCompare(tagName, searchedTag);
                if (isSearched) {
                    if (tempInt > 0) {
                        //Stack is not empty, means that we found closing of inner same tag
                        tempInt -= 1;
                    } else {
                        return i;
                    }
                }
            } else {
                if (utils::fastCompare(searchedTag, rawTagName)) {
                    //Push because inside tag is another one, like p in p -> <p><p>...</p></p>
                    tempInt += 1;
                }
            }

            i = tei + 1;
        }

        std::string error = "Unable to find closing for: " + searchedTag
                            + " at: " + index.toString();
        utils::log("UTILS", error);
        throw NO_CLOSING_TAG_FOUND;
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


    std::string getTagAttribute(const std::string &tagBody, const std::string &attributeName) {
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

            if (aei == -1) {
                //attribute not found
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
            const std::string &input,
            const std::string &tag,
            const int &s,
            const int &e,
            std::vector<std::string> &outputList
    ) {
        if (!outputList.empty()) {
            outputList.clear();
        }

        int end = e != 0 ? e : input.length();
        int i = s;
        while (i < end) {
            char ch = input[i];
            if (ch != '<') {
                i += 1;
                continue;
            }

            int tei = utils::indexOfOrThrow(input, ">", i);
            int tagBodyLength = tei - i - 1;
            std::string tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);

            if (utils::fastCompare(tag, rawTagName)) {
                std::string closingTag = "</" + tag + ">";
                int ctsi = utils::indexOfOrThrow(input, closingTag, tei);
                std::string foundTag = input.substr(tei + 1, ctsi - tei - 1);
                outputList.push_back(foundTag);
                i = ctsi + 1;
            } else {
                i += tagBodyLength;
            }
        }
    }


    void extractClasses(const std::string &tagBody, std::vector<std::string> &outList) {
        std::string separator = "class=\"";
        int s = utils::indexOf(tagBody, separator, 0);
        if (s == -1) {
            //Tag has no classes
            return;
        }
        int e = utils::indexOfOrThrow(tagBody, "\"", s + separator.length());
        std::string classes = tagBody.substr(s, e - s);

        if (!outList.empty()) {
            outList.clear();
        }

        utils::split(classes, ' ', outList);
    }
}
#pragma clang diagnostic pop