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
    data class Image constructor(
        val url: String,
        val description: String?,
        ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    //TODO html text a raw text pro talkback
    data class TextBlock constructor(
        val text: String,
        ) : HtmlElement()


    /**
     * @param text Content text of title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    data class Title constructor(
        val text: String,
        val titleTag: String,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    data class Quote constructor(
        val text: String,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    data class Table constructor(
        val rows: List<List<String>>,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    data class BasicList(
        val items: List<String>,
        val isOrdered: Boolean,
    ) : HtmlElement()


    /**
     * @since 1.0.0
     */
    data class DescriptionList(
        val items: List<String>,
        val isOrdered: Boolean,
        val span: Int
    ) : HtmlElement()

    /**
     * @since 1.0.0
     */
    data class Address constructor(
        val content: String,
    ) : HtmlElement()

    data class Code constructor(
        val content: String,
        ) : HtmlElement()
}