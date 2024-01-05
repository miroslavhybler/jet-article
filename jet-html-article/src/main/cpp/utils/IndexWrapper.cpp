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

void IndexWrapper::setIndex(int i) {
    if (i < this->index || i > this->length) {
        std::string msg =
                "i: " + std::to_string(i) + " index: " + std::to_string(index) + " length: " +
                std::to_string(length);
        utils::log("mirek", msg);
        throw "New index is lower or bigger than length";
    }

    this->index = i;
    if (this->tempIndex < this->index) {
        //tempIndex should always be same or bigger
        tempIndex = i;
    }
}


void IndexWrapper::setLength(int l) {
    this->length = l;
    this->index = 0;
    this->tempIndex = 0;
}

void IndexWrapper::setTempIndex(int i) {
    if (i > length) {
        std::string cause = "New tempIndex is bigger than length"
                            + std::to_string(i) + " > " + std::to_string(length);
        throw;
    }
    this->tempIndex = index;
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

int IndexWrapper::getLength() {
    return length;
}

int IndexWrapper::getTempIndex() {
    return tempIndex;
}