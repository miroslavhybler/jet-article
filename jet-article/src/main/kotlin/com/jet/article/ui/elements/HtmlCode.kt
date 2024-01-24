package com.jet.article.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 12.12.2023
 */
@Composable
fun HtmlCode(
    modifier: Modifier = Modifier,
    code: HtmlElement.Code
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .border(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
                width = 1.dp
            )
    ) {
        Text(
            text = remember(key1 = code) {
                code.content.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            },
            modifier = modifier
                .wrapContentWidth()
                .horizontalScroll(state = rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}