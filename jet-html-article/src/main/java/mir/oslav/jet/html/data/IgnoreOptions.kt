package mir.oslav.jet.html.data


/**
 * TODO docs
 * @param classes
 * @param tags
 * @param keywords
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
data class IgnoreOptions constructor(
    val classes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val keywords: List<String> = listOf("cookies")
) {


    val isEmpty: Boolean
        get() = classes.isEmpty()
                && tags.isEmpty()
                && keywords.isEmpty()
}