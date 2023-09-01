package mir.oslav.jet.html

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.08.2023
 */
internal val LocalHtmlDimensions: ProvidableCompositionLocal<HtmlDimensions> =
    compositionLocalOf { HtmlDimensions.empty }
