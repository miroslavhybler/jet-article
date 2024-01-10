package mir.oslav.jet.html.article.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
data class HtmlData constructor(
    val loadingStates: LoadingStates,
    val elements: List<HtmlElement>,
    val headData: HtmlHeadData?,
    val error: HtmlDataError? = null,
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
            isFullyLoaded = false
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
    ) {
       companion object {
            val appending: LoadingStates = LoadingStates(
                isLoading = true,
                isAppending = true,
                message = null
            )
        }
    }

    data class HtmlDataError constructor(
        val message: String,
        val cause: Throwable?
    )
}