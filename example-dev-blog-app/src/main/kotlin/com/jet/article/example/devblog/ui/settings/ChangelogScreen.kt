package com.jet.article.example.devblog.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jet.article.example.devblog.composables.TitleTopBar
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.example.devblog.ui.LocalDimensions


/**
 * @author Miroslav Hýbler <br>
 * created on 17.09.2024
 */
@Composable
fun ChangelogScreen(
    navHostController: NavHostController,
) {
    val dimensions = LocalDimensions.current
    Scaffold(
        topBar = {
            TitleTopBar(
                text = stringResource(id = R.string.settings_changelog_title),
                onNavigationIcon = navHostController::navigateUp,
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                contentPadding = remember {
                    PaddingValues(
                        top = dimensions.topLinePadding,
                        bottom = dimensions.bottomLinePadding,
                    )
                },
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            ) {
                items(items = changelog) {
                    ChangelogItem(item = it)
                }
            }
        },
        bottomBar = {
            GithubBottomBar()
        }
    )
}


@Composable
private fun ChangelogItem(
    modifier: Modifier = Modifier,
    item: Changelog,
) {
    val density = LocalDensity.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalPadding()
    ) {
        Text(
            text = item.version,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(id = item.titleRes),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        item.changes.forEach { changeStringRes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(
                            top = with(density) {
                                MaterialTheme.typography.bodyMedium.lineHeight.toDp() / 2f - 4.dp
                            }
                        )
                        .size(width = 8.dp, height = 8.dp),
                    painter = painterResource(id = R.drawable.ic_list_item),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier= Modifier.width(width = 4.dp))

                Text(
                    modifier = Modifier
                        .weight(weight = 1f),
                    text = stringResource(id = changeStringRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}


/**
 * @param version Version of the app
 * @param titleRes Title of the change sumarizing changes
 * @param changes List of changes for new [version]
 */
data class Changelog constructor(
    val version: String,
    @StringRes val titleRes: Int,
    @StringRes val changes: List<Int>
)

val changelog: List<Changelog> = listOf(
    Changelog(
        version = "1.0.0",
        titleRes = R.string.settings_changelog_title_1_0_0,
        changes = listOf(
            R.string.settings_changelog_1_0_0__0,
            R.string.settings_changelog_1_0_0__1,
            R.string.settings_changelog_1_0_0__2,
        )
    ),
)