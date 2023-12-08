package mir.oslav.jet.html.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
sealed class HtmlData private constructor(
) {


    /**
     * @since 1.0.0
     */
    data object Empty : HtmlData()


    /**
     * @since 1.0.0
     */
    //TODO maybe background support
    data class Success constructor(
        val elements: List<HtmlElement>,
        val metrics: ParseMetrics,
        val headData: HtmlHeadData?
    ) : HtmlData()


    /**
     * @since 1.0.0
     */
    data class Invalid constructor(
        val message: String,
        val exception: Exception,
    ) : HtmlData()


    /**
     * @since 1.0.0
     */
    data class Loading constructor(
        val message: String,
    ) : HtmlData()
}