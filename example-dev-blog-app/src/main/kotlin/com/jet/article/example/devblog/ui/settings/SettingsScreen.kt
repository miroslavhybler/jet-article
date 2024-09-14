package com.jet.article.example.devblog.ui.settings

import android.content.Intent
import android.graphics.Paint.Align
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jet.article.example.devblog.BuildConfig
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.composables.TitleTopBar
import com.jet.article.example.devblog.data.SettingsStorage
import com.jet.article.example.devblog.horizontalPadding
import com.jet.article.example.devblog.ui.DevBlogAppTheme
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.utils.plus
import kotlinx.coroutines.launch


/**
 * @author Miroslav Hýbler <br>
 * created on 19.08.2024
 */
val darkModeOptions: List<Int> = listOf(
    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    AppCompatDelegate.MODE_NIGHT_YES,
    AppCompatDelegate.MODE_NIGHT_NO,
)

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingsViewModel,
) {


    val settings by viewModel.settings.collectAsState(initial = SettingsStorage.Settings())

    SettingsScreenContent(
        navHostController = navHostController,
        settings = settings,
        onNewSettings = {
            viewModel.viewModelScope.launch {
                viewModel.settingsStorage.saveSettings(settings = it)
            }
        },
    )
}


@Composable
private fun SettingsScreenContent(
    navHostController: NavHostController,
    settings: SettingsStorage.Settings,
    onNewSettings: (SettingsStorage.Settings) -> Unit,
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TitleTopBar(
                text = stringResource(R.string.settings_title),
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingsSwitch(
                        modifier = Modifier.horizontalPadding(),
                        title = stringResource(R.string.settings_dynamic_colors_label),
                        isChecked = settings.isUsingDynamicColors,
                        onCheckedChange = {
                            onNewSettings(settings.copy(isUsingDynamicColors = it))
                        }
                    )
                }

                SettingsDropdown(
                    modifier = Modifier.horizontalPadding(),
                    title = stringResource(R.string.settings_dark_mode_label),
                    items = darkModeOptions,
                    transform = {
                        SettingsStorage.Settings.nightModeString(
                            context = context,
                            flags = it,
                        )
                    },
                    subtitle = settings.nightModeString(context = context),
                    onSelected = {
                        onNewSettings(settings.copy(nightModeFlags = it))
                    }
                )

                Spacer(modifier = Modifier.weight(weight = 1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.settings_version),
                        style = MaterialTheme.typography.titleSmall,
                    )

                    Spacer(modifier = Modifier.width(width = 24.dp))

                    Text(
                        modifier = Modifier,
                        text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }


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
                        text = stringResource(R.string.settings_github),
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
            navHostController = rememberNavController(),
            settings = SettingsStorage.Settings(),
            onNewSettings = { _ -> }
        )
    }
}