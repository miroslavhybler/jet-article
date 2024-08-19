@file:OptIn(ExperimentalMaterial3Api::class)

package com.jet.article.example.devblog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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