///
/// Created by Miroslav Hýbler on 19.02.2024
///

#include <jni.h>
#include <string>
#include <map>
#include "utils/Utils.h"
#include "utils/Constants.h"
#include "jni.h"
#include "core/ContentAnalyzer.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"
namespace jni {
    ContentAnalyzer *analyzer = new ContentAnalyzer();
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_AnalyzerNative_setInput(
        JNIEnv *environment,
        jobject caller,
        jstring content
) {
    jboolean isCopy;
    jni::analyzer->setInput(environment->GetStringUTFChars(content, &isCopy));
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_AnalyzerNative_hasNextStep(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->hasNextStep();
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_AnalyzerNative_setRange(
        JNIEnv *environment, jobject caller, jint s, jint e
) {
    jni::analyzer->setRange(s, e);
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_AnalyzerNative_doNextStep(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->doNextStep();
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentContentType(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->currentContentType;
}



extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTag(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::analyzer->currentTag.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagId(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::analyzer->currentTagId.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagName(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::analyzer->currentTagName.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagClass(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::analyzer->currentTagClass.c_str());
}



extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagStartIndex(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->getCurrentTagStartIndex();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagEndIndex(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->getCurrentTagEndIndex();
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_AnalyzerNative_hasPairTagContent(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->hasPairTagContent();
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getPairTagContent(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::analyzer->currentPairTagContent.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentTagAttributesCount(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->getCurrentAttributesSize();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentAttributeName(
        JNIEnv *environment, jobject caller, jint index
) {
    std::string name = jni::analyzer->getCurrentTagAttributeName(index);
    return environment->NewStringUTF(name.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getCurrentAttributeValue(
        JNIEnv *environment, jobject caller, jstring name
) {
    jboolean isCopy;
    std::string nameNative = environment->GetStringUTFChars(name, &isCopy);
    std::string attributeValue = jni::analyzer->getCurrentTagAttributeValue(nameNative);
    return environment->NewStringUTF(attributeValue.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_AnalyzerNative_isAbortingWithError(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->isAbortingWithError();
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_AnalyzerNative_getErrorCode(
        JNIEnv *environment, jobject caller
) {
    return jni::analyzer->getErrorCode();
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_AnalyzerNative_getErrorMessage(
        JNIEnv *environment, jobject caller
) {
    std::string msg = jni::analyzer->getErrorMessage();
    return environment->NewStringUTF(msg.c_str());
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_AnalyzerNative_clearAllResources(
        JNIEnv *environment, jobject caller
) {
    jni::analyzer->clearAllResources();
}

#pragma clang diagnostic pop