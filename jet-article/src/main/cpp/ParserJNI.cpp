///
/// Java Native Interface (JNI) for the parser library.
///
/// Created by Miroslav Hýbler on 03.01.2024
///

#include <jni.h>
#include <string>
#include <map>
#include "ContentParser.h"
#include "BodyProcessor.h"
#include "utils/Utils.h"
#include "utils/Constants.h"
#include "jni.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"

namespace jni {
    ContentParser *contentParser = new ContentParser();
    BodyProcessor *processor = new BodyProcessor();

    bool isContentForVisualAvailable = false;
    TagType tag = NO_CONTENT;
}

extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_setInput(
        JNIEnv *environment,
        jobject caller,
        jstring content
) {
    jboolean isCopy;
    jni::contentParser->setInput(environment->GetStringUTFChars(content, &isCopy));
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_ParserNative_hasNextStep(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->hasNextStep();
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_ParserNative_hasContent(
        JNIEnv *environment, jobject caller
) {
    return jni::isContentForVisualAvailable;
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_doNextStep(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->doNextStep();

    std::string currentTag = jni::contentParser->currentTag;

    if (jni::contentParser->hasParsedContentToBeProcessed()) {
        jni::isContentForVisualAvailable = jni::processor->isTagValidForNextProcessing(
                currentTag,
                jni::contentParser->currentTagBody
        );

        if (!jni::isContentForVisualAvailable) {
            jni::contentParser->tryMoveToClosing();
        }
    }
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getContentType(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->contentType;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getContent(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->getTempContent().c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getCurrentTag(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->currentTag.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getTitle(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->getTitle().c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getBase(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->getBase().c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_jet_article_ParserNative_isAbortingWithError(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->isAbortingWithError();
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getErrorCode(
        JNIEnv *environment, jobject caller
) {
    ErrorCode code = jni::contentParser->getErrorCode();
    utils::log("mirek", "code: " + std::to_string(code));
    return code;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getContentListSize(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->getTempListSize();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getContentListItem(
        JNIEnv *environment, jobject caller, jint index
) {
    return environment->NewStringUTF(jni::contentParser->getTempListItem(index).c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getContentMapItem(
        JNIEnv *environment, jobject caller, jstring attributeName
) {
    jboolean isCopy;
    std::string nameNative = environment->GetStringUTFChars(attributeName, &isCopy);
    std::string value = jni::contentParser->getTempMapItem(nameNative);
    return environment->NewStringUTF(value.c_str());
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_resetCurrentContent(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->hasParsedContentToBeProcessed(false);
    jni::isContentForVisualAvailable = false;
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_clearAllResources(
        JNIEnv *environment, jobject caller
) {
    jni::contentParser->clearAllResources();
    jni::processor->clearAllResources();
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_warmup(
        JNIEnv *environment, jobject caller, jstring content
) {
    jboolean isCopy;
    jni::contentParser->setInput(environment->GetStringUTFChars(content, &isCopy));

    while (jni::contentParser->hasNextStep()) {
        jni::contentParser->doNextStep();
    }

    jni::contentParser->clearAllResources();
    jni::processor->clearAllResources();
}



////////////////////////////////////////////////////////////////////////////////////////////////////
/////
/////   Processor JNI
/////
////////////////////////////////////////////////////////////////////////////////////////////////////



extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ProcessorNative_addRule(
        JNIEnv *environment, jobject caller, jstring tag, jstring clazz
) {
    jboolean outIsCopy;
    jni::processor->addRule(
            IgnoreRule(
                    TAG,
                    environment->GetStringUTFChars(tag, &outIsCopy),
                    environment->GetStringUTFChars(clazz, &outIsCopy)
            )
    );
}

extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ProcessorNative_clearAllResources(
        JNIEnv *environment, jobject caller
) {
    jni::processor->clearAllResources();
}


#pragma clang diagnostic pop