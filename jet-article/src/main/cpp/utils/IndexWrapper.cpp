///
/// Created by Miroslav Hýbler  on 03.01.2024
///


#include "IndexWrapper.h"
#include "Utils.h"


IndexWrapper::IndexWrapper() {

}


IndexWrapper::~IndexWrapper() {

}


void IndexWrapper::moveIndex(const size_t &i) {
    this->index = i;
}


const size_t IndexWrapper::getIndex() {
    return index;
}


const size_t IndexWrapper::getIndexOnStart() {
    return indexOnStart;
}


void IndexWrapper::invalidate() {
    indexOnStart = index;
}


void IndexWrapper::reset() {
    this->index = 0;
}


std::string IndexWrapper::toString() {
    return "index: " + std::to_string(index);
}