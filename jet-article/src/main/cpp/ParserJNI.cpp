////////////////////////////////////////////////////////////////////////////////////////////////////
///
/// Java Native Interface (JNI) for the parser library.
///
/// Created by Miroslav Hýbler on 03.01.2024
///
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <jni.h>
#include <string>
#include <map>
#include "core/ContentParser.h"
#include "core/ContentFilter.h"
#include "core/ContentAnalyzer.h"
#include "utils/Utils.h"
#include "utils/Constants.h"
#include "jni.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"


namespace jni {
    ContentParser *contentParser = new ContentParser();
    ContentFilter *processor = new ContentFilter();

    bool isContentForVisualAvailable = false;
    TagType tag = NO_CONTENT;
}

extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_initialize(
        JNIEnv *environment,
        jobject caller,
        jboolean areImagesEnabled,
        jboolean isLoggingEnabled,
        jboolean isSimpleTextFormatAllowed,
        jboolean isQueringTextOutsideTextTags
) {
    jni::contentParser->initialize(
            areImagesEnabled,
            isSimpleTextFormatAllowed,
            isQueringTextOutsideTextTags
    );
    utils::setIsLoggingEnabled(isLoggingEnabled);
}


extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ParserNative_setInput(
        JNIEnv *environment,
        jobject caller,
        jstring content
) {
    jboolean isCopy;
    std::string input = environment->GetStringUTFChars(content, &isCopy);
    jni::contentParser->setInput(input);
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

    if (jni::contentParser->hasBodyContext() && !currentTag.empty()) {
        jni::isContentForVisualAvailable = jni::processor->isTagValidForNextProcessing(
                currentTag,
                jni::contentParser->currentTagBody
        );

        if (!jni::isContentForVisualAvailable) {
            //When content was filtered by processor, move index after the skipped content
            jni::contentParser->tryMoveToContainerClosing();
        }
    } else if (
            jni::contentParser->hasBodyContext() &&
            !jni::contentParser->currentContentOutsideTag.empty()) {

        jni::isContentForVisualAvailable = true;
    } else if (
            jni::contentParser->hasBodyContext() &&
            jni::contentParser->hasParsedContentToBeProcessed()
            ) {
        jni::isContentForVisualAvailable = true;
    }
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getContentType(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->currentContentType;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getContent(
        JNIEnv *environment, jobject caller
) {
    std::string content = jni::contentParser->getTempContent();
    return environment->NewStringUTF(content.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getCurrentTag(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->currentTag.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getCurrentTagId(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->currentTagId.c_str());
}




extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getTitle(
        JNIEnv *environment, jobject caller
) {
    return environment->NewStringUTF(jni::contentParser->getTitle().c_str());
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
    return code;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getErrorMessage(
        JNIEnv *environment, jobject caller
) {
    std::string msg = jni::contentParser->getErrorMessage();

    return environment->NewStringUTF(msg.c_str());
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
    std::string item = jni::contentParser->getTempListItem(index);
    return environment->NewStringUTF(item.c_str());
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


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getTableColumnCount(
        JNIEnv *environment, jobject caller
) {

    std::vector<std::vector<std::string_view>> table = jni::contentParser->getTable();

    if (table.size() == 0) {
        return 0;
    }

    return table[0].size();
}


extern "C" JNIEXPORT jint JNICALL
Java_com_jet_article_ParserNative_getTableRowsCount(
        JNIEnv *environment, jobject caller
) {
    return jni::contentParser->getTable().size();
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_jet_article_ParserNative_getTableCell(
        JNIEnv *environment, jobject caller, jint column, jint row
) {
    std::vector<std::vector<std::string_view>> table = jni::contentParser->getTable();
    std::vector<std::string_view> &r = table[row];
    std::string_view cell = r[column];
    return environment->NewStringUTF(std::string(cell).c_str());
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
    std::string input = environment->GetStringUTFChars(content, &isCopy);
    jni::contentParser->setInput(input);

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
Java_com_jet_article_ContentFilterNative_addExcludeOption(
        JNIEnv *environment,
        jobject caller,
        jstring tag,
        jstring clazz,
        jstring id,
        jstring keyword
) {
    jboolean outIsCopy = false;
    ExcludeRule rule = ExcludeRule(
            environment->GetStringUTFChars(tag, &outIsCopy),
            environment->GetStringUTFChars(clazz, &outIsCopy),
            environment->GetStringUTFChars(id, &outIsCopy),
            environment->GetStringUTFChars(keyword, &outIsCopy)

    );
    jni::processor->addRule(rule);
}

extern "C" JNIEXPORT void JNICALL
Java_com_jet_article_ContentFilterNative_clearAllResources(
        JNIEnv *environment, jobject caller
) {
    jni::processor->clearAllResources();
}


#pragma clang diagnostic pop