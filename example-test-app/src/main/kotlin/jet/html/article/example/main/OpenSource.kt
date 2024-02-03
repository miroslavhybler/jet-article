package jet.html.article.example.main

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jet.article.test.R

/**
 * @author Miroslav Hýbler <br>
 * created on 29.01.2024
 */
@Composable
fun OpenSource(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column(modifier = modifier) {
        ItemRow(title = "Source code",
            iconRes = R.drawable.ic_logo_github,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData("https://github.com/miroslavhybler/jet-article".toUri())
                )
            }
        )
    }
}


@Composable
private fun ItemRow(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(width = 12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
@Preview(showBackground = true)
fun OpenSourcePreview() {
    OpenSource()
}