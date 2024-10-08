///
/// Created by Miroslav Hýbler  on 03.01.2024
///

#ifndef JET_HTML_ARTICLE_INDEXWRAPPER_H
#define JET_HTML_ARTICLE_INDEXWRAPPER_H

#include <string>

/**
 * Holds the actual index while processing html input.
 * @since 1.0.0
 */
class IndexWrapper {

private:
    size_t index = 0;
    size_t indexOnStart = 0;

public:
    IndexWrapper();

    ~IndexWrapper();


    /**
     * Sets new index value. If the new value is bigger than tempIndex is updated
     * as well. TempIndex should always be same or bigger than index.
     * @param i New index value
     * @since 1.0.0
     */
    void moveIndex(const size_t &i);


    /**
     *
     * @return Actual index value.
     * @since 1.0.0
     */
    size_t getIndex() const;


    /**
     *
     * @return
     * @since 1.0.0
     */
    size_t getIndexOnStart() const;


    /**
     * @since 1.0.0
     */
    void invalidate();


    /**
     * @since 1.0.0
     */
    void reset();


    /**
     *
     * @return
     * @since 1.0.0
     */
    std::string toString() const;

};


#endif //JET_HTML_ARTICLE_INDEXWRAPPER_H
