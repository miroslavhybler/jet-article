package com.jet.article.example.devblog.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jet.article.example.devblog.composables.MainTopBar
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.article.ui.elements.HtmlImage
import com.jet.utils.plus


/**
 * Shows list of articles from [Android Dev Blog](https://android-developers.googleblog.com/) index site.
 * Now this is not the way JetHtmlArticle library is ment to be used, but it's possible.
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@Composable
fun HomeListPane(
    onOpenPost: (index: Int) -> Unit,
    viewModel: HomeListPaneViewModel,
) {

    val data by viewModel.data.collectAsState()

    LaunchedEffect(key1 = Unit) {

        viewModel.loadIndex()
    }


    HomeListPaneContent(
        onOpenPost = onOpenPost,
        data = data,
        lazyListState = viewModel.lazyListState,
    )
}


@Composable
private fun HomeListPaneContent(
    onOpenPost: (index: Int) -> Unit,
    data: List<HomeListPaneViewModel.PostItem>,
    lazyListState: LazyListState,
) {
    val context = LocalContext.current
    val dimensions = LocalDimensions.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MainTopBar(text = "Android Dev Blog")
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier,
                state= lazyListState,
                contentPadding = paddingValues + PaddingValues(
                    start = dimensions.sidePadding,
                    end = dimensions.sidePadding,
                    top = dimensions.topLinePadding,
                    bottom = dimensions.bottomLinePadding,
                ),
                verticalArrangement = Arrangement.spacedBy(space = 24.dp),
            ) {
                itemsIndexed(items = data) { index, item ->
                    //TODO open article on click, get url somehow
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onOpenPost(index) })
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.medium,
                            )
                            .padding(
                                start = 14.dp,
                                end = 14.dp,
                                top = 10.dp,
                                bottom = 16.dp,
                            )
                    ) {
                        HtmlImage(
                            modifier = Modifier,
                            data = item.image
                        )

                        Text(
                            modifier = Modifier,
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            modifier = Modifier,
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            modifier = Modifier,
                            text = item.time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    )
}

