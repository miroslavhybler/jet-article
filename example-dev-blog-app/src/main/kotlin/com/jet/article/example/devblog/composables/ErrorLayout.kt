package com.jet.article.example.devblog.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.example.devblog.ui.DevBlogAppTheme

/**
 * @author Miroslav Hýbler<br>
 * created on 14.09.2024
 */
private val innerPadding: Dp = 24.dp

@Composable
fun ErrorLayout(
    modifier: Modifier = Modifier,
    title: String,
    useBackgorund: Boolean = true,
) {
    val context = LocalContext.current

    val contentColor = if (useBackgorund)
        MaterialTheme.colorScheme.onErrorContainer
    else
        MaterialTheme.colorScheme.error

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
            .background(
                color = if (useBackgorund)
                    MaterialTheme.colorScheme.errorContainer
                else
                    Color.Unspecified,
                shape = if (useBackgorund)
                    MaterialTheme.shapes.large
                else
                    RectangleShape
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .padding(top = innerPadding)
                .size(size = 48.dp),
            painter = painterResource(id = R.drawable.ic_bug),
            contentDescription = null,
            tint = contentColor,
        )

        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor,
        )

        Text(
            modifier = Modifier
                .padding(horizontal = innerPadding),
            text = "App is showing data from source i do not control, this might happen 😕 Please report this bug to me, thanks.",
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(height = 16.dp))


        ReportButton(
            modifier = Modifier
                .align(alignment = Alignment.Start),
            title = "Report on Mail",
            iconRes = R.drawable.ic_email,
            onClick = {},
        )

        ReportButton(
            modifier = Modifier
                .align(alignment = Alignment.Start),
            title = "Report on Github",
            iconRes = R.drawable.ic_logo_github,
            onClick = {},
        )

        Spacer(modifier = Modifier.height(height = 32.dp))

        Text(
            modifier = Modifier
                .padding(bottom = innerPadding),
            text = "Sorry for your inconvenience 😊",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}


@Composable
private fun ReportButton(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape = CircleShape)
            .clickable(onClick = onClick, onClickLabel = "TODO")
            .padding(horizontal = innerPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            modifier = Modifier.size(size = 12.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.width(width = 4.dp))

        Text(
            modifier = Modifier,
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
@PreviewLightDark
private fun ErrorLayoutPreview() {

    DevBlogAppTheme {
        ErrorLayout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            title = "Something went wrong",
        )
    }
}