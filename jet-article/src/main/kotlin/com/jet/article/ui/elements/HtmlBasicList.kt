@file:OptIn(ExperimentalTextApi::class)
@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jet.article.data.HtmlElement
import com.jet.article.R

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 13.01.2024
 */
@Composable
public fun HtmlBasicList(
    modifier: Modifier = Modifier,
    list: HtmlElement.BasicList,
    @DrawableRes bulletRes: Int = R.drawable.ic_jet_article_list_item,
    bulletTint: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val density = LocalDensity.current
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        list.items.forEachIndexed { index, s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(size = with(density) { style.lineHeight.toDp() }),
                    painter = painterResource(id = bulletRes),
                    contentDescription = null,
                    tint = bulletTint
                )
                HtmlTextBlock(text = s, style = style)
            }
        }
    }
}