@file:OptIn(ExperimentalMaterial3Api::class)

package com.jet.article.catalog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


/**
 * @author Miroslav Hýbler <br>
 * created on 20.09.2024
 */
@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Jet HTML Article Catalog") }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
            ) {
                items(items = items) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    navHostController.navigate(route = Routes.getRoute(item = it))
                                }
                            )
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Icon(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(size = 32.dp),
                            painter = painterResource(id = it.iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            modifier = Modifier,
                            text = it.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    )
}

data class Item constructor(
    val title: String,
    val asset: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_text,
)

val items = listOf(
    Item(
        title = "Text",
        asset = "text",
    ),
    Item(
        title = "Title",
        asset = "title",
    ),
    Item(
        title = "Quote",
        asset = "quote",
    ),
    Item(
        title = "List",
        asset = "list",
        iconRes = R.drawable.ic_list
    ),
    Item(
        title = "Table",
        asset = "table",
        iconRes = R.drawable.ic_table,
    ),
    Item(
        title = "Address",
        asset = "address",
    ),
)