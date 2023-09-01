package mir.oslav.jet.html.composables


/**
 * TODO docs
 * @param spanCount Defines the max span count (max column count)
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
data class HtmlConfig constructor(
    val spanCount: Int = 1,
    val topBarConfig: TopBarConfig = TopBarConfig.SIMPLE
){


    /**
     * @since 1.0.0
     */
    enum class TopBarConfig {
        NONE,
        SIMPLE,
        APPEARING,
        COLLAPSING
    }

}