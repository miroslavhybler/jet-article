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
    std::string mId;

public:

    ExcludeRule(std::string tag);

    ExcludeRule(std::string tag, std::string clazz);

    ExcludeRule(std::string tag, std::string clazz, std::string id);


    ~ExcludeRule();

    const std::string getTag();

    const std::string getClazz();

    const std::string getId();

};


#endif //JET_HTML_ARTICLE_RULE_H
