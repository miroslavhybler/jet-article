///
/// Created by Miroslav Hýbler  on 03.01.2024
///

//
// Created by Miroslav Hýbler on 03.01.2024.
//

#ifndef JET_HTML_ARTICLE_INDEXWRAPPER_H
#define JET_HTML_ARTICLE_INDEXWRAPPER_H

#include <string>

/**
 * Holds the actual index while processing html input.
 * @since 1.0.0
 */
class IndexWrapper {

private:
    int index = 0;
    int tempIndex = 0;

public:
    IndexWrapper();

    ~IndexWrapper();


    /**
     * Sets new index value. If the new value is bigger than tempIndex is updated
     * as well. TempIndex should always be same or bigger than index.
     * @param i New index value
     * @since 1.0.0
     */
    void moveIndex(int i);

    /**
     * Sets new tempIndex value.
     * @param i New tempIndex value.
     * @since 1.0.0
     */
    void setTempIndex(int i);


    /**
     * Sets new index value based in temp index.
     * @return
     * @since 1.0.0
     */
    bool moveToTempIndex();


    /**
     *
     * @return Actual index value.
     * @since 1.0.0
     */
    int getIndex();


    /**
     *
     * @return Actual tempIndex value.
     * @since 1.0.0
     */
    int getTempIndex();


    /**
     * @since 1.0.0
     */
    void reset();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string toString();

};


#endif //JET_HTML_ARTICLE_INDEXWRAPPER_H
