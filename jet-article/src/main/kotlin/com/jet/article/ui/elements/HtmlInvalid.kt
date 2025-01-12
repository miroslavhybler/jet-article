@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.jet.article.data.HtmlArticleData


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@Composable
public fun HtmlInvalid(
    modifier: Modifier = Modifier,
) {


    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Error occurs while processing article",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = "TODO",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = "TODO",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
        )
    }

}