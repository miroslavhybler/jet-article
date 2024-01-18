@file:Suppress("RedundantConstructorKeyword", "RedundantVisibilityModifier")

package mir.oslav.jet.html.article.data

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
sealed class HtmlElement private constructor() {


    /**
     * @since 1.0.0
     */
    public data class Image internal constructor(
        val url: String,
        val description: String?,
    ) : HtmlElement()


    /**
     * @param styledText Styled text containing simple html formatting tags like b, i, u,...
     * @param cleanText Text from styled text without any formatting. This is set for additional need.
     * @since 1.0.0
     */
    //TODO html text a raw text pro talkback
    public data class TextBlock internal constructor(
        val styledText: String,
        val cleanText: String,
    ) : HtmlElement()


    /**
     * @param text Content text of title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    public data class Title internal constructor(
        val text: String,
        val titleTag: String,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    public data class Quote internal constructor(
        val text: String,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    public data class Table internal constructor(
        val rows: List<String>,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    public data class BasicList internal constructor(
        val items: List<String>,
        val isOrdered: Boolean,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    public data class DescriptionList internal constructor(
        val items: List<String>,
        val isOrdered: Boolean,
    ) : HtmlElement()

    /**
     * @since 1.0.0
     */
    public data class Address internal constructor(
        val content: String,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    public data class Code internal constructor(
        val content: String,
    ) : HtmlElement()
}