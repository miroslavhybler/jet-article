package mir.oslav.jet.html.data


/**
 * TODO docs
 * @param tags Tags you want to ignore, use lowercase only
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
data class IgnoreOptions constructor(
    val tags: List<String> = defaultTags,
) {

    companion object {


        /**
         * @since 1.0.0
         */
        val defaultTags: List<String> = listOf("noscript")


        /**
         * @since 1.0.0
         */
        val defaultKeywords: List<String> = listOf("cookies")
    }
}