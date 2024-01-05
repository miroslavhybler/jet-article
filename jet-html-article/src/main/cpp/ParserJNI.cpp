///
/// Java Native Interface (JNI) for the parser library.
///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <jni.h>
#include <string>
#include "ContentParser.h"

namespace bridge {

    ContentParser *contentParser = new ContentParser();
}


extern "C" JNIEXPORT void JNICALL
Java_mir_oslav_jet_html_article_parse_ContentParserNative_setContent(
        JNIEnv *environment,
        jobject,
        jstring content
) {
    jboolean isCopy;
    bridge::contentParser->setInput(environment->GetStringUTFChars(content, &isCopy));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_mir_oslav_jet_html_article_parse_ContentParserNative_hasNextStep(JNIEnv *environment,
                                                                      jobject) {
    return bridge::contentParser->hasNextStep();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_mir_oslav_jet_html_article_parse_ContentParserNative_hasContent(JNIEnv *environment, jobject) {
    return bridge::contentParser->hasContent();
}

extern "C" JNIEXPORT void JNICALL
Java_mir_oslav_jet_html_article_parse_ContentParserNative_doNextStep(JNIEnv *environment, jobject) {
    bridge::contentParser->doNextStep();
}


extern "C" JNIEXPORT jstring JNICALL
Java_mir_oslav_jet_html_article_parse_ContentParserNative_getContent(JNIEnv *environment, jobject) {
    bridge::contentParser->hasContentToProcess = false;
    return environment->NewStringUTF(bridge::contentParser->tempContent.c_str());
}