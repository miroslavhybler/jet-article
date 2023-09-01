package mir.oslav.jet.html.composables.elements.topbars

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.R


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 27.08.2023
 */
@Composable
fun HtmlTopBarSimple(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    title: String,
    shadow: Dp,
    backgroundAlpha: Float
) {
    val dimensions = LocalHtmlDimensions.current
    Box(
        modifier = Modifier.shadow(
            elevation = shadow,
            spotColor = colorScheme.onBackground
        )
    ) {
        Row(
            modifier = modifier
                .background(
                    color = colorScheme.background.copy(
                        alpha = backgroundAlpha
                    )
                )
                .fillMaxWidth()
                .wrapContentHeight()
                .statusBarsPadding()
                .padding(horizontal = dimensions.sidePadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {


            Icon(
                painter = painterResource(id = R.drawable.ic_html_arrow_back),
                contentDescription = stringResource(id = R.string.jet_html_back_button_content_description),
                modifier = Modifier
                    .size(size = dimensions.clickableIconSize)
                    .clip(shape = CircleShape)
                    .clickable(onClick = navHostController::popBackStack)
                    .padding(all = dimensions.clickableIconPadding),
                tint = colorScheme.onBackground
            )


            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 8.dp, end = 16.dp)
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun SimplePreview() {

    HtmlTopBarSimple(
        title = "Build better apps faster with Jetpack Compose and androidX",
        navHostController = rememberNavController(),
        backgroundAlpha = 1f,
        shadow = Dp.Unspecified
    )
}