cmake_minimum_required(VERSION 3.22.1)
project("article")

SET(
        DEPENDENCIES
        ContentParser.h
        ContentParser.cpp
        ContentFilter.h
        ContentFilter.cpp
        ContentAnalyzer.h
        ContentAnalyzer.cpp
)
include_directories(${CMAKE_CURRENT_SOURCE_DIR})
add_library(CORE ${DEPENDENCIES})
target_include_directories(CORE INTERFACE ${CMAKE_CURRENT_SOURCE_DIR})