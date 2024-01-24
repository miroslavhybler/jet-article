///
/// Created by Miroslav Hýbler on 10.01.2024
///


#ifndef JET_HTML_ARTICLE_RULE_H
#define JET_HTML_ARTICLE_RULE_H

#include <string>


/**
 * @since 1.0.0
 */
enum RuleType {
    TAG,
    CLAZZ
};


/**
 * @since 1.0.0
 */
class IgnoreRule {

private:
    std::string mTag;
    std::string mClazz;
    RuleType mType;

public:
    IgnoreRule(RuleType type);

    IgnoreRule(RuleType type, std::string tag);

    IgnoreRule(RuleType type, std::string tag, std::string clazz);


    ~IgnoreRule();

    std::string getTag();

    std::string getClazz();

    RuleType getType();
};


#endif //JET_HTML_ARTICLE_RULE_H
