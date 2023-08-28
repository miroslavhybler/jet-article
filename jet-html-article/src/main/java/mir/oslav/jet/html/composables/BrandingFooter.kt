package mir.oslav.jet.html.composables

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.R
import mir.oslav.jet.html.isLandScape


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Composable
fun BrandingFooter(
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current


    val items = remember {
        listOf(
            FooterItem(
                title = "Using Jetpack Compose",
                iconRes = R.mipmap.img_jetpack_compose_logo,
                link = "https://developer.android.com/jetpack/compose",
                iconTint = null
            ),
            //TODO repo url
            FooterItem(
                title = "Github: https://github.com/miroslavhybler",
                iconRes = R.drawable.ic_logo_github,
                link = "https://github.com/miroslavhybler",
                iconTint = colorScheme.onBackground
            )
        )
    }

    if (configuration.isLandScape) {
        BrandingFooterLandscape(modifier = modifier, footerItems = items)
    } else {
        BrandingFooterPortrait(modifier = modifier, footerItems = items)
    }

}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
@Composable
private fun BrandingFooterPortrait(
    modifier: Modifier,
    footerItems: List<FooterItem>
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = HtmlDimensions.sidePadding, vertical = 12.dp),
    ) {

        footerItems.forEach { item ->
            FooterRow(title = item.title, iconRes = item.iconRes, clickableLink = item.link)
        }

    }
}

@Composable
private fun BrandingFooterLandscape(
    modifier: Modifier,
    footerItems: List<FooterItem>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = HtmlDimensions.sidePadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        footerItems.forEach { item ->
            FooterRow(
                title = item.title,
                iconRes = item.iconRes,
                clickableLink = item.link,
                iconTint = item.iconTint
            )
        }
    }
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Composable
private fun FooterRow(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconRes: Int,
    clickableLink: String,
    iconTint: Color? = null,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .wrapContentSize()
            .clip(shape = CircleShape)
            .clickable(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW)
                            .setData(clickableLink.toUri())
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                })
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(size = 20.dp),
            colorFilter = if (iconTint != null) ColorFilter.tint(color = iconTint) else null
        )

        Spacer(modifier = Modifier.width(width = 12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }

}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
data class FooterItem constructor(
    val title: String,
    @DrawableRes val iconRes: Int,
    val link: String,
    val iconTint: Color?
)


@Composable
@Preview
private fun BrandingFooterPreview() {
    BrandingFooter()
}