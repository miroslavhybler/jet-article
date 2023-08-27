package mir.oslav.jet.html.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
sealed class HtmlData private constructor(
    open val title: String
) {


    /**
     * @since 1.0.0
     */
    data object Empty : HtmlData(title = "")


    /**
     * @since 1.0.0
     */
    data class Success constructor(
        override val title: String,
        val htmlElements: List<HtmlElement>,
        val header: HtmlHeader,
        val monitoring: Monitoring,
    ) : HtmlData(title = title)


    /**
     * @since 1.0.0
     */
    data class Invalid constructor(
        override val title: String,
        val message: String,
        val exception: Exception,
    ) : HtmlData(title = title)
}