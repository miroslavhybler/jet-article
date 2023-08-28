package mir.oslav.jet.html.data


/**
 * @author Miroslav Hýbler <br>
 * created on 27.08.2023
 */
sealed class HtmlHeader private constructor() {

    data object None : HtmlHeader()

    /**
     * TODO docs
     * @since 1.0.0
     */
    data class TopBarHeader constructor(
        val title: String,
        val image: HtmlElement.Image,
    ) : HtmlHeader()


    /**
     * TODO docs
     * @since 1.0.0
     */
    data class FullScreenHeader constructor(
        val title: String,
        val image: String,
    ) : HtmlHeader()
}