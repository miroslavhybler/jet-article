cmake_minimum_required(VERSION 3.22.1)
project("article")

add_library(
        UTILS
        Utils.h Utils.cpp
        IndexWrapper.h IndexWrapper.cpp
#        StringRef.h StringRef.cpp,
        ExcludeRule.h ExcludeRule.cpp
        Constants.h
)
include_directories(${CMAKE_CURRENT_SOURCE_DIR})
target_include_directories(UTILS INTERFACE ${CMAKE_CURRENT_SOURCE_DIR})