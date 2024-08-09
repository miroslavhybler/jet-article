///
/// Created by Miroslav Hýbler on 22.01.2024
///

#include <jni.h>
#include <string>
#include <map>
#include "utils/Utils.h"
#include "utils/Constants.h"

extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_UtilsNative_getTagAttributes(
        JNIEnv *environment, jobject caller
) {

}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_UtilsNative_clearUnsupportedTagsFromTextBlock(
        JNIEnv *environment, jobject caller, jstring input
) {
    jboolean isCopy = false;
    std::string inputStd = environment->GetStringUTFChars(input, &isCopy);
    std::string output;
    utils::clearUnsupportedTagsFromTextBlock(inputStd, output, 0, inputStd.length());
    return environment->NewStringUTF(output.c_str());
}