@file:OptIn(ExperimentalMaterial3Api::class)

package com.jet.article.example.devblog.composables

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.example.devblog.ui.Routes


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@Composable
fun TitleTopBar(
    modifier: Modifier = Modifier,
    text: String,
    onNavigationIcon: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    TopAppBar(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .then(other = modifier)
            .statusBarsPadding(),
        title = { Text(text = text) },
        navigationIcon = {
            if (onNavigationIcon != null) {
                IconButton(
                    onClick = onNavigationIcon,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = contentDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )

                    }
                )
            }

        }
    )

}


@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    text: String,
    navHostController: NavHostController,
) {
    TopAppBar(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .horizontalPadding()
            .statusBarsPadding()
            .then(other = modifier),
        title = { Text(text = text) },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_android),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        actions = {
            IconButton(
                onClick = {
                    navHostController.navigate(route = Routes.settings)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "TODO",
                )
            }
        }
    )

}



@Composable
fun PostTopBar(
    modifier: Modifier = Modifier,
    title: String,
    @FloatRange(from = 0.0, to = 1.0)
    backgroundAlpha: Float,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onNavigationIcon: (() -> Unit)? =null,
    navigationActionContentDescription: String? = null,
) {
    LargeTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = backgroundAlpha),
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor =MaterialTheme.colorScheme.background.copy(alpha = backgroundAlpha),
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        actions = actions,
        navigationIcon = {
            if (onNavigationIcon != null){
                IconButton(
                    onClick = onNavigationIcon,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = navigationActionContentDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}