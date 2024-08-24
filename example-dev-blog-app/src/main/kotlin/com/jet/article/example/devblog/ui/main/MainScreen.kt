@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.jet.article.example.devblog.ui.main

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jet.article.example.devblog.data.AdjustedPostData
import com.jet.utils.dpToPx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Main screen showing lists of posts and post detail using [HomeListPane] and [PostPane].
 * @author Miroslav Hýbler <br>
 * created on 14.08.2024
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
) {
    val state = rememberMainScreenState()
    val htmlData by viewModel.postData.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>(
        scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    )

    BackHandler(
        enabled = state.role != ListDetailPaneScaffoldRole.List
    ) {
        state.onNavigateBack()
        navigator.navigateBack(backNavigationBehavior = BackNavigationBehavior.PopLatest)
        //   navigator.navigateTo(pane = state.role)

        if (state.role == ListDetailPaneScaffoldRole.List) {
            viewModel.onBack()
            coroutineScope.launch {
                delay(timeMillis = 200)
                state.isEmptyPaneVisible = true
            }
        }
    }

    MainScreenContent(
        state = state,
        postData = htmlData,
        onLoad = viewModel::loadPost,
        navigator = navigator,
        navHostController = navHostController,
    )
}


@Composable
fun MainScreenContent(
    state: MainScreenState,
    postData: AdjustedPostData?,
    onLoad: (url: String) -> Unit,
    navigator: ThreePaneScaffoldNavigator<Nothing>,
    navHostController: NavHostController,
) {
    val density = LocalDensity.current
    val postListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        CompositionLocalProvider(
            LocalMainScreenNavigator provides navigator,
            LocalMainScreenState provides state,
        ) {

            ListDetailPaneScaffold(
                modifier = Modifier.fillMaxSize(),
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane {
                        HomeListPane(
                            onOpenPost = { index, item ->
                                state.openPost(url = item.url, index = index)
                                onLoad(item.url)
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                            },
                            viewModel = hiltViewModel(),
                            navHostController = navHostController,
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        when {
                            postData != null -> {
                                PostPane(
                                    onOpenContests = {
                                        if (state.role == ListDetailPaneScaffoldRole.Extra) {
                                            state.onNavigateBack()
                                            navigator.navigateTo(pane = state.role)
                                        } else {
                                            state.openContest()
                                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra)
                                        }
                                    },
                                    data = postData,
                                    listState = postListState,
                                )
                            }
                            state.isEmptyPaneVisible -> {
                                PostEmptyPane()
                            }
                        }
                    }
                },
                extraPane = {
                    AnimatedPane {
                        ContentsPane(
                            data = postData,
                            onSelected = { index, title ->
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                                coroutineScope.launch {
                                    delay(timeMillis = 400)
                                    postListState.animateScrollToItem(
                                        index = index,
                                        scrollOffset = density.dpToPx(dp = 24.dp).toInt(),
                                    )
                                }
                            },
                        )
                    }
                }
            )
        }
    }
}


/**
 * @param initialRole
 * @param initialIsEmptyPaneVisible
 * @param initialIndex
 * @param initialUrl
 */
class MainScreenState constructor(
    initialRole: ThreePaneScaffoldRole,
    initialIsEmptyPaneVisible: Boolean,
    initialIndex: Int,
    initialUrl: String,
) {

    var role: ThreePaneScaffoldRole by mutableStateOf(value = initialRole)
        private set
    var seletedIndex: Int by mutableIntStateOf(value = initialIndex)
        private set
    var actualUrl: String by mutableStateOf(value = initialUrl)
        private set
    var isEmptyPaneVisible: Boolean by mutableStateOf(value = initialIsEmptyPaneVisible)

    fun openPost(url: String, index: Int) {
        this.isEmptyPaneVisible = false
        this.actualUrl = url
        this.seletedIndex = index
        this.role = ListDetailPaneScaffoldRole.Detail
    }


    fun openContest() {
        this.role = ListDetailPaneScaffoldRole.Extra
    }

    fun onNavigateBack() {
        role = when (role) {
            ListDetailPaneScaffoldRole.Extra -> ListDetailPaneScaffoldRole.Detail
            ListDetailPaneScaffoldRole.Detail -> ListDetailPaneScaffoldRole.List
            ListDetailPaneScaffoldRole.List -> ListDetailPaneScaffoldRole.List
            else -> throw IllegalStateException("")
        }

        if (role == ListDetailPaneScaffoldRole.List) {
            seletedIndex = -1
            actualUrl = ""
        }
    }

    object Saver : androidx.compose.runtime.saveable.Saver<MainScreenState, Bundle> {

        private val ThreePaneScaffoldRole.saveAbleName: String
            get() {
                return when (this) {
                    ListDetailPaneScaffoldRole.List -> "list"
                    ListDetailPaneScaffoldRole.Detail -> "detail"
                    ListDetailPaneScaffoldRole.Extra -> "extra"
                    else -> throw IllegalStateException("Unsupported role: $this")
                }
            }

        private fun fromSaveableName(name: String): ThreePaneScaffoldRole {
            return when (name) {
                "list" -> ListDetailPaneScaffoldRole.List
                "detail" -> ListDetailPaneScaffoldRole.Detail
                "extra" -> ListDetailPaneScaffoldRole.Extra
                else -> throw IllegalStateException("Unsupported role: $name")
            }
        }

        override fun SaverScope.save(value: MainScreenState): Bundle {
            return Bundle().apply {
                putString("mss_role", value.role.saveAbleName)
                putBoolean("mss_is_empty_pane_visible", value.isEmptyPaneVisible)
                putString("mss_url", value.actualUrl)
                putInt("mss_index", value.seletedIndex)
            }
        }

        override fun restore(value: Bundle): MainScreenState {
            return MainScreenState(
                initialRole = fromSaveableName(name = value.getString("mss_role") ?: ""),
                initialIsEmptyPaneVisible = value.getBoolean("mss_is_empty_pane_visible"),
                initialIndex = value.getInt("mss_index", -1),
                initialUrl = value.getString("mss_url", ""),
            )
        }
    }
}


@Composable
fun rememberMainScreenState(
    initialRole: ThreePaneScaffoldRole = ListDetailPaneScaffoldRole.List,
    initialIsEmptyPaneVisible: Boolean = true,
    initialIndex: Int = -1,
    initialUrl: String = "",
): MainScreenState {
    return rememberSaveable(saver = MainScreenState.Saver) {
        MainScreenState(
            initialRole = initialRole,
            initialIsEmptyPaneVisible = initialIsEmptyPaneVisible,
            initialIndex = initialIndex,
            initialUrl = initialUrl,
        )
    }
}


val LocalMainScreenNavigator: ProvidableCompositionLocal<ThreePaneScaffoldNavigator<Nothing>> =
    compositionLocalOf(
        defaultFactory = {
            error(
                message = "LocalMainScreenNavigator was not initialized yet or you called it outside the scope." +
                        " LocalMainScreenNavigator should be used only in MainScreen and it's content."
            )
        }
    )

val LocalMainScreenState: ProvidableCompositionLocal<MainScreenState> =
    compositionLocalOf(
        defaultFactory = {
            error(
                message = "LocalMainScreenStae was not initialized yet or you called it outside the scope." +
                        " LocalMainScreenStae should be used only in MainScreen and it's content."
            )
        }
    )