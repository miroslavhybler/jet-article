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
class Rule {
private:
    std::string mTag;
    std::string mValue;
    RuleType mType;

public:
    Rule(RuleType type);

    ~Rule();

    std::string getTag();

    RuleType getType();
};


#endif //JET_HTML_ARTICLE_RULE_H
