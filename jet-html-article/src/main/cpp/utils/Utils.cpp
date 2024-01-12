///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <string>
#include <stack>
#include <jni.h>
#include <android/log.h>
#include "IndexWrapper.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
namespace utils {


    std::stack<int> tempStack;


    void log(const char *tag, const std::string message) {
        /** True will enable in android logcat */
        __android_log_print(
                ANDROID_LOG_DEBUG,
                tag,
                "%s",
                message.c_str()
        );
    }


    inline void ltrim(std::string &s) {
        s.erase(s.begin(), std::find_if(s.begin(), s.end(), [](unsigned char ch) {
            return !std::isspace(ch);
        }));
    }

    inline void rtrim(std::string &s) {
        s.erase(std::find_if(s.rbegin(), s.rend(), [](unsigned char ch) {
            return !std::isspace(ch);
        }).base(), s.end());
    }


    inline void trim(std::string &s) {
        ltrim(s);
        rtrim(s);
    }


    bool fastCompare(const std::string s1, const std::string s2) {
        char ch1 = s1[0];
        char ch2 = s2[0];

        if (ch1 == ch2) {
            return strcmp(s1.c_str(), s2.c_str()) == 0;
        }
        return false;
    }

    int indexOf(const std::string &input, const std::string &sub, const int i) {
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


    int indexOfOrThrow(const std::string &input, const std::string &sub, const int i) {
        int index = indexOf(input, sub, i);

        if (index == -1) {
            std::string error = "Unable to find index of " + sub + " from: " + std::to_string(i);
            utils::log("INDEX_OF", error);
            throw error;
        }

        return index;
    }


    std::string getTagName(const std::string tagBody) {
        std::string name = tagBody;

        if (tagBody.find(' ')) {
            int ei = indexOf(tagBody, " ", 0);
            if (ei > 0) {
                name = tagBody.substr(0, ei);
            }
        }
        trim(name);

        //TODO maybe remove tolower
        //for (int x = 0; x < name.length(); x++) {
        //    putchar(tolower(name[x]));
        // }

        return name;
    }


    bool canProcessIncomingTag(std::string input, int l, IndexWrapper index) {
        //TODO chyba je tady, je něco špatně s indexem
        if (input[index.getTempIndex()] != '<') {
            std::string error = "Char at "
                                + std::to_string(index.getTempIndex())
                                + " != '<'   ::    char: '"
                                + std::string(1, input[index.getTempIndex()])
                                + "'";
            utils::log("UTILS", error);
            throw error;
        }


        int i = index.getTempIndex();
        if ((i + 3) < l) {
            int il = i + 3;
            std::string sub = input.substr(i + 1, 3);
            if (utils::fastCompare(sub, "!--")) {
                int ei = utils::indexOfOrThrow(input, "-->", il);
                index.moveTempIndex(ei);
                return false;
            }
        }

        if ((i + 15) < l) {
            int il = i + 15;
            std::string sub = input.substr(i + 1, 14);
            if (utils::fastCompare(sub, "!doctype html>")) {
                index.moveTempIndex(il);
                return false;
            }
        }

        if (i + 12 < l) {
            int il = i + 12;
            std::string sub = input.substr(i + 1, 12);
            if (utils::fastCompare(sub, "/![cdata[//>")) {
                index.moveTempIndex(il);
                return false;
            }
        }
        return true;
    }


    //TODO what if there is '<' char in the content
    int findClosingTag(
            const std::string input,
            const std::string searchedTag,
            IndexWrapper index,
            const int e
    ) {

        if (utils::fastCompare(searchedTag, "img") || searchedTag == "img") {
            std::string  error = "IMAGE";
            utils::log("mirek", error);
            throw error;
        }

        int i = index.getTempIndex();
        //Clearing tempStack before another use
        while (!tempStack.empty()) {
            tempStack.pop();
        }
        int end = e > 0 ? e : input.length();
        while (i >= index.getIndex() && i < end) {
            char ch = input[i];
            if (ch != '<') {
                i += 1;
                continue;
            }

            //TODO toto odhalí další chyby
            index.moveTempIndex(i);
            //char is '<'
            if (!utils::canProcessIncomingTag(input, input.length(), index)) {
                //Unable to process
                i = index.getTempIndex();
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
                    if (!tempStack.empty()) {
                        //Stack is not empty, means that we found closing of inner same tag
                        tempStack.pop();
                    } else {
                        return i;
                    }
                }
            } else {
                if (utils::fastCompare(searchedTag, rawTagName)) {
                    //Push because inside tag is another one, like p in p -> <p><p>...</p></p>
                    tempStack.push(i);
                }
            }

            i = tei + 1;
        }

        std::string error = "Unable to find closing for: " + searchedTag
                            + " at: " + index.toString();
        utils::log("CLOSING", error);
        throw error;
    }


    //TODO out list
    void groupPairTagContents(
            const std::string input,
            const std::string tag,
            const int s = 0,
            const int e = 0
    ) {
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
                int ctsi = utils::indexOfOrThrow(input, "</" + tag + ">", i);
                //TODO add to out list
            }
            i += tagBodyLength;
        }
    }
}
#pragma clang diagnostic pop