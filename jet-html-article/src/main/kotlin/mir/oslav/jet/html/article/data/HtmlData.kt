package mir.oslav.jet.html.article.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
data class HtmlData constructor(
    val elements: List<HtmlElement>,
    val headData: HtmlHeadData?,
    val error: HtmlDataError? = null,
) {

    companion object {
        val empty: HtmlData = HtmlData(
            elements = emptyList(),
            error = null,
            headData = null,
        )
    }


    val isEmpty: Boolean
        get() = elements.isEmpty()
                && error == null


    data class HtmlDataError constructor(
        val message: String,
        val cause: Throwable?
    )
}