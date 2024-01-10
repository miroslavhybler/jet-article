///
/// Created by Miroslav Hýbler  on 03.01.2024
///

//
// Created by Miroslav Hýbler on 03.01.2024.
//

#include "IndexWrapper.h"
#include "Utils.h"

IndexWrapper::IndexWrapper() {

}

IndexWrapper::~IndexWrapper() {

}

void IndexWrapper::moveIndex(int i) {
    this->index = i;
    if (this->tempIndex < i) {
        //tempIndex should always be same or bigger
        tempIndex = i;
    }
}

void IndexWrapper::setTempIndex(int i) {
    this->tempIndex = i;
}

bool IndexWrapper::moveToTempIndex() {
    if (this->index < this->tempIndex) {
        this->index = this->tempIndex;
        return true;
    }

    return false;
}


int IndexWrapper::getIndex() {
    return index;
}


int IndexWrapper::getTempIndex() {
    return tempIndex;
}


void IndexWrapper::reset() {
    index = 0;
    tempIndex = 0;
}


std::string IndexWrapper::toString() {
    return "index: " + std::to_string(index) + " tempIndex: " + std::to_string(tempIndex);
}