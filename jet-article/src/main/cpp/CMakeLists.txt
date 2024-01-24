cmake_minimum_required(VERSION 3.22.1)
project("jet-article")


add_library(
        ${CMAKE_PROJECT_NAME} SHARED
        ContentParser.h ContentParser.cpp
        BodyProcessor.h BodyProcessor.cpp

        ParserJNI.cpp
        UtilsJNI.cpp
)

add_subdirectory(
        utils
)

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        UTILS
        android
        log
)