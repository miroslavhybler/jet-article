package com.jet.article.example.devblog.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.example.devblog.composables.TitleTopBar
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.ui.elements.HtmlTextBlock


/**
 * @author Miroslav Hýbler <br>
 * created on 14.08.2024
 */
@Composable
fun ContentsPane(
    data: HtmlArticleData,
    onSelected: (index: Int, element: HtmlElement.Title) -> Unit,
) {

    val list = remember(key1 = data) {
        data.elements.mapIndexedNotNull { index, element ->
            if (element is HtmlElement.Title) {
                TitleWithOriginalIndex(
                    title = element,
                    originalIndex = index,
                )
            } else null
        }
    }


    Scaffold(
        topBar = {
            TitleTopBar(text = "Contents")
        },
        content = { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
            ) {
                itemsIndexed(items = list) { index, item ->
                    Text(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .clickable(onClick = { onSelected(item.originalIndex, item.title) })
                            .horizontalPadding()
                            .padding(vertical = 16.dp),
                        text = "${index + 1}. - ${ArticleParser.Utils.clearTagsFromText(item.title.text)}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

        }
    )
}

data class TitleWithOriginalIndex constructor(
    val title: HtmlElement.Title,
    val originalIndex: Int,
)