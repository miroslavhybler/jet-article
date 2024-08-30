package com.jet.article.example.devblog.ui.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.composables.DevelopmentAnimation
import com.jet.article.example.devblog.composables.TitleTopBar
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.example.devblog.ui.BaseViewModel
import com.jet.article.example.devblog.ui.DevBlogAppTheme
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.utils.isExtraLargeScreen
import com.jet.utils.plus


/**
 * @author Miroslav Hýbler <br>
 * created on 19.08.2024
 */
@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingsViewModel,
) {


    SettingsScreenContent(
        navHostController = navHostController,
    )
}


@Composable
private fun SettingsScreenContent(
    navHostController: NavHostController,
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TitleTopBar(
                text = "Settings & about",
                onNavigationIcon = navHostController::navigateUp,
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(
                        paddingValues = PaddingValues(
                            top = dimensions.topLinePadding,
                            bottom = dimensions.bottomLinePadding
                        ) + paddingValues
                    )
            ) {

                Spacer(modifier = Modifier.weight(weight = 1f))


                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .horizontalPadding()
                        .wrapContentSize()
                        .clip(shape = CircleShape)
                        .clickable(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW)
                                        .setData("https://github.com/miroslavhybler/jet-article".toUri())
                                )
                            }
                        )
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp)
                ) {

                    Icon(
                        modifier = Modifier.size(size = 16.dp),
                        painter = painterResource(id = R.drawable.ic_logo_github),
                        contentDescription = null,
                    )

                    Text(
                        modifier = Modifier,
                        text = "Available on Github",
                    )
                }
            }
        }
    )
}



@Composable
@PreviewLightDark
private fun SettingsScreenPreview() {
    DevBlogAppTheme {
        SettingsScreenContent(
            navHostController = rememberNavController()
        )
    }
}