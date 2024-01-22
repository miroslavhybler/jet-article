///
/// Created by Miroslav Hýbler  on 04.01.2024
///

#ifndef JET_HTML_ARTICLE_STRINGREF_H
#define JET_HTML_ARTICLE_STRINGREF_H


class StringRef {

private:
    int si;
    int ei;

public:
    StringRef(int s, int e);
    ~StringRef();
};


#endif //JET_HTML_ARTICLE_STRINGREF_H
