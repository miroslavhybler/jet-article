package mir.oslav.jet.html.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
class HtmlData constructor(
    val loadingStates: LoadingStates,
    val elements: List<HtmlElement>,
    val headData: HtmlHeadData?,
    val error: HtmlDataError? = null,
    val metering: HtmlParseMetering? = null,
    val isFullyLoaded: Boolean
) {

    companion object {
        val empty: HtmlData = HtmlData(
            loadingStates = LoadingStates(
                isLoading = false,
                isAppending = false,
                message = null
            ),
            elements = emptyList(),
            error = null,
            headData = null,
            isFullyLoaded=false
        )
    }


    val isEmpty: Boolean
        get() = elements.isEmpty()
                && error == null
                && !loadingStates.isLoading
                && !loadingStates.isAppending

    data class LoadingStates constructor(
        val isLoading: Boolean,
        val isAppending: Boolean,
        val message: String?,
    )

    data class HtmlDataError constructor(
        val message: String,
        val cause: Throwable
    )
}