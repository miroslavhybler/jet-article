package com.jet.article.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 13.01.2024
 */
//TODO drawable
@Composable
public fun HtmlBasicList(
    modifier: Modifier = Modifier,
    list: HtmlElement.BasicList
) {

    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        list.items.forEachIndexed { index, s ->
            Text(
                text = remember(key1 = s) {
                    val text = if (list.isOrdered) "${index + 1}. $s" else s
                    text.toHtml()
                        .toSpannable()
                        .toAnnotatedString(primaryColor = colorScheme.primary)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}