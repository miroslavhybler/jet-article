///
/// Created by Miroslav Hýbler on 10.01.2024
///


#ifndef JET_HTML_ARTICLE_RULE_H
#define JET_HTML_ARTICLE_RULE_H

#include <string>


/**
 * Exlude rule option for the [BodyProcessor] component. Used to exlude some parts of html code.
 * @since 1.0.0
 */
class ExcludeRule {

private:
    std::string_view mTag;
    std::string_view mClazz;
    std::string_view mId;
    std::string_view mKeyword;

public:

    ExcludeRule(std::string_view tag);

    ExcludeRule(std::string_view tag, std::string_view clazz);

    ExcludeRule(std::string_view tag, std::string_view clazz, std::string_view id);

    ExcludeRule(
            std::string_view tag,
                std::string_view clazz,
                std::string_view id,
                std::string_view keyword
                );

    ~ExcludeRule();

    const std::string_view getTag();

    const std::string_view getClazz();

    const std::string_view getId();

    const std::string_view getKeyword();

};


#endif //JET_HTML_ARTICLE_RULE_H
