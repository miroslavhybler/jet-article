@file:OptIn(ExperimentalTextApi::class)
@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import com.jet.article.data.HtmlElement


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 13.01.2024
 */
//TODO drawable
@Composable
public fun HtmlBasicList(
    modifier: Modifier = Modifier,
    list: HtmlElement.BasicList
) {
    Column(modifier = modifier.fillMaxWidth()) {
        list.items.forEachIndexed { index, s ->
            HtmlTextBlock(text = s)
        }
    }
}