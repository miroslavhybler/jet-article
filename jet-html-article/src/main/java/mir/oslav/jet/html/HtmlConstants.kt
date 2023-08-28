package mir.oslav.jet.html

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
object HtmlConstants {


    /**
     * List of supported pair html tags. Other tags are ignored.
     * @since 1.0.0
     */
    val pairTags: List<String> = listOf(

        /*
        "h1",
        "h2",
        "h3",
        "h4",
        "h5",
        "h6",
        "h7",
        */

        "p",
        "b",
        "strong",
        "i",
        "a",
        "u",
        "blockquote",
        "table",
        "title"
    )


    /**
     * List of pair html tags that are styling text, others are ignored.
     * @since 1.0.0
     */
    val styledTags: List<String> = listOf(
        "b",
        "strong",
        "i",
        "a",
        "u",
    )


    /**
     * List of supported tags that are not used as pair
     * @since 1.0.0
     */
    val singleTags: List<String> = listOf(
        "img"
    )
}