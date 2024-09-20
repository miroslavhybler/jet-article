package com.jet.article.example.devblog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.composables.Android
import com.jet.article.example.devblog.composables.EmptyAnimation
import com.jet.article.example.devblog.ui.DevBlogAppTheme
import com.jet.utils.dpToPx


/**
 * @author Miroslav Hýbler <br>
 * created on 19.08.2024
 */
@Composable
fun PostEmptyPane() {
    val density = LocalDensity.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        EmptyAnimation(modifier = Modifier)

        Android()

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
        )

        Text(
            text = "Pick some post to read 🤓 or enjoy this animation if you like it 😁",
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@Composable
@PreviewLightDark
private fun PostEmptyPanePreview() {
    DevBlogAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PostEmptyPane()
        }
    }
}