cmake_minimum_required(VERSION 3.22.1)
project("article")

add_library(
        UTILS STATIC
        Utils.h Utils.cpp
        IndexWrapper.h IndexWrapper.cpp
#        StringRef.h StringRef.cpp,
        IgnoreRule.h IgnoreRule.cpp
        Constants.h
)

target_include_directories(UTILS INTERFACE ${CMAKE_CURRENT_SOURCE_DIR})