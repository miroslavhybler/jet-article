package mir.oslav.jet.html.article

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.08.2023
 */
val LocalHtmlDimensions: ProvidableCompositionLocal<HtmlDimensions> =
    compositionLocalOf { HtmlDimensions.empty }
