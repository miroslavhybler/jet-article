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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jet.article.example.devblog.R
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


        Box(
            modifier = Modifier
                .wrapContentSize()
                .clipToBounds()
                .padding(horizontal = 32.dp)
        ) {
            Icon(
                modifier = Modifier
                    .wrapContentSize()
                    .scale(scale = 1.3f),
                painter = painterResource(id = R.drawable.android_robot),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Icon(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .scale(scale = 1.5f)
                    .graphicsLayer(
                        rotationZ = 75f,
                        translationX = density.dpToPx(dp = 16.dp),
                        translationY = density.dpToPx(dp = 2.dp)
                    ),
                painter = painterResource(id = R.drawable.ic_wrench),
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
            )
        }
        Text(
            text = "Android developers blog",
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