///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <string>
#include <stack>
#include <jni.h>
#include <android/log.h>
#include "IndexWrapper.h"

namespace utils {

    bool isLogEnabled = false;

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


    int indexOf(const std::string &input, const std::string &sub, int i) {
        typename std::string::const_iterator sit = input.begin();
        std::advance(sit, i);
        typename std::string::const_iterator it = std::search(
                sit,
                input.end(),
                sub.begin(),
                sub.end()
        );
        if (it != input.end()) return it - input.begin();
        else return -1;
    }


    std::string getTagName(std::string tagBody) {
        std::string name = tagBody;

        if (tagBody.find(' ')) {
            int ei = indexOf(tagBody, " ", 0);
            name = tagBody.substr(0, ei);
        }

        for (int x = 0; x < name.length(); x++) {
            putchar(tolower(name[x]));
        }

        return name;
    }


    bool canProcessIncomingTag(std::string input, IndexWrapper index) {
        int i = index.getTempIndex();
        int l = index.getLength();
        if ((i + 3) < l) {
            int il = i + 3;
            std::string sub = input.substr(i, il);
            if (sub == "<!--") {
                int ei = utils::indexOf(input, "-->", il);
                index.setTempIndex(ei);
                return false;
            }
        }

        if ((i + 15) < l) {
            int il = i + 15;
            std::string sub = input.substr(i, il);
            if (sub == "<!doctype html>") {
                index.setTempIndex(il);
                return false;
            }
        }

        if (i + 12 < l) {
            int il = i + 12;
            std::string sub = input.substr(i, il);
            if (sub == "</![cdata[//>") {
                index.setTempIndex(il);
                return false;
            }
        }
        return true;
    }


    int findClosingTag(std::string input, std::string searchedTag, IndexWrapper index, int e) {
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

            //char is '<'
            if (!canProcessIncomingTag(input, index)) {
                //Unable to process
                i = index.getTempIndex();
                continue;
            }

            //Tag closing index, index of next '>'
            int tei = utils::indexOf(input, ">", i);
            // -1 to remove '<' at the end
            int tagBodyLength = tei - i - 1;
            //tagbody within <>, i + 1 to remove '<'
            std::string tagBody = input.substr(i + 1, tagBodyLength);
            std::string rawTagName = utils::getTagName(tagBody);
            bool isClosingTag = rawTagName.find('/', 0) == 0;

            if (isClosingTag) {
                std::string tagName = rawTagName.substr(1, rawTagName.length());

                bool isSearched = tagName == searchedTag;
                utils::log(
                        "mirek",
                        "com: " + searchedTag + " == " + tagName + " isMatch: " +
                        std::to_string(isSearched)
                );

                if (isSearched) {
                    if (!tempStack.empty()) {
                        utils::log(
                                "mirek",
                                "popping: " +  std::to_string(tempStack.top())
                        );
                        //Stack is not empty, means that we found closing of inner same tag
                        tempStack.pop();
                    } else {
                        return i;
                    }
                }
            } else {
                if (searchedTag == rawTagName) {
                    //Push because inside tag is another one, like p in p -> <p><p>...</p></p>
                    tempStack.push(i);
                }
            }

            i = tei + 1;
        }

        std::string substr = input.substr(index.getIndex(), end - index.getIndex());
        utils::log(
                "mirek",
                "Unable to find closing for: " + searchedTag + " in: \n" + substr
        );
        throw "Unable to find closing for: " + searchedTag;
    }

}