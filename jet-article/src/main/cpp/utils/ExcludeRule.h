///
/// Created by Miroslav Hýbler on 10.01.2024
///


#ifndef JET_HTML_ARTICLE_RULE_H
#define JET_HTML_ARTICLE_RULE_H

#include <string>


/**
 * @since 1.0.0
 */
class ExcludeRule {

private:
    std::string mTag;
    std::string mClazz;

public:

    ExcludeRule(std::string tag);

    ExcludeRule(std::string tag, std::string clazz);


    ~ExcludeRule();

    std::string getTag();

    std::string getClazz();
};


#endif //JET_HTML_ARTICLE_RULE_H
