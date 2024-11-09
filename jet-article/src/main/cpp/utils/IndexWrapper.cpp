///
/// Created by Miroslav Hýbler  on 03.01.2024
///


#include "IndexWrapper.h"
#include "Utils.h"


IndexWrapper::IndexWrapper() = default;


IndexWrapper::~IndexWrapper() = default;


void IndexWrapper::moveIndex(const size_t &i) {
    this->index = i;
}


size_t IndexWrapper::getIndex() const {
    return index;
}


size_t IndexWrapper::getIndexOnStart() const {
    return indexOnStart;
}


void IndexWrapper::invalidate() {
    indexOnStart = index;
}


void IndexWrapper::reset() {
    this->index = 0;
    this->indexOnStart = 0;
}


std::string IndexWrapper::toString() const {
    return "index: " + std::to_string(index) +
           " indexOnStart: " + std::to_string(indexOnStart);
}