///
/// Java Native Interface (JNI) for the parser library.
///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <jni.h>
#include <string>
#include "ContentParser.h"
#include "BodyProcessor.h"
#include "utils/Utils.h"
#include "utils/Constants.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"

namespace jni {
    ContentParser *contentParser = new ContentParser();
    BodyProcessor *processor = new BodyProcessor();

    bool isContentForVisualAvailable = false;
    TagType tag = NO_CONTENT;
}


extern "C" JNIEXPORT void JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_setContent(
        JNIEnv *environment,
        jobject caller,
        jstring content
) {
    jboolean isCopy;
    jni::contentParser->setInput(environment->GetStringUTFChars(content, &isCopy));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_hasNextStep(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->hasNextStep();
}


extern "C" JNIEXPORT jboolean JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_hasContent(
        JNIEnv *environment, jobject caller
) {
    return jni::isContentForVisualAvailable;
}


extern "C" JNIEXPORT void JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_doNextStep(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->doNextStep();

    if (jni::contentParser->hasParsedContentToBeProcessed()) {
        jni::isContentForVisualAvailable = jni::processor->isTagValidForNextProcessing(
                jni::contentParser->actualTag,
                jni::contentParser->actualTagBody
        );
    }
}
extern "C" JNIEXPORT jint JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_getContentType(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->contentType;
}

extern "C" JNIEXPORT jstring JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_getContent(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->hasParsedContentToBeProcessed(false);
    jni::isContentForVisualAvailable = false;
    return environment->NewStringUTF(jni::contentParser->getTempContent().c_str());
}


extern "C" JNIEXPORT void JNICALL
Java_mir_oslav_jet_html_article_ContentParserNative_clearAllResources(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->clearAllResources();
    jni::processor->clearAllResources();
}


#pragma clang diagnostic pop