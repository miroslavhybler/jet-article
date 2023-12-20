package mir.oslav.jet.html.data


/**
 * TODO docs
 * @param tags Tags you want to ignore, use lowercase only
 * @param classes Classes you want to ignore, use lowercase only
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
//TODO use pair of tag and class to increase performance
data class IgnoreOptions constructor(
    val classes: List<String> = emptyList(),
) {

    companion object {

        /**
         * @since 1.0.0
         */
        //TODO maybe
        val defaultKeywords: List<String> = listOf("cookies")
    }
}