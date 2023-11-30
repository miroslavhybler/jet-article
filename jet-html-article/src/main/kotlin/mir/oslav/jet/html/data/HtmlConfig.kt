package mir.oslav.jet.html.data

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection


/**
 * TODO docs
 * @param spanCount Defines the max span count (max column count)
 * @param domain Fallback domain name for loading resources
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
data class HtmlConfig constructor(
    val spanCount: Int = 1,
    val domain: String? = null
) {

}