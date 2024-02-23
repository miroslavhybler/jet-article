cmake_minimum_required(VERSION 3.22.1)
project("jet-article")

SET(
        DEPENDENCIES
        ParserJNI.cpp
        AnalyzerJNI.cpp
        UtilsJNI.cpp
)

add_subdirectory(utils)
add_subdirectory(core)

add_library(${CMAKE_PROJECT_NAME} SHARED ${DEPENDENCIES})

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        UTILS
        CORE
        android
        log
)
include_directories(
        utils
        core
)