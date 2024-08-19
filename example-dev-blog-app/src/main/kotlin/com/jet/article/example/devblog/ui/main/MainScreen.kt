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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jet.article.data.HtmlArticleData
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
    val htmlData by viewModel.htmlData.collectAsState()

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>(
        scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    )

    BackHandler(enabled = state.role != ListDetailPaneScaffoldRole.List) {
        state.onNavigateBack()
        navigator.navigateBack(backNavigationBehavior = BackNavigationBehavior.PopLatest)
        //   navigator.navigateTo(pane = state.role)

        if (state.role == ListDetailPaneScaffoldRole.List) {
            viewModel.onBack()
        }
    }

    MainScreenContent(
        state = state,
        htmlData = htmlData,
        onLoad = viewModel::loadArticle,
        navigator = navigator,
        navHostController=navHostController,
    )
}


@Composable
fun MainScreenContent(
    state: MainScreenState,
    htmlData: HtmlArticleData,
    onLoad: (url: String) -> Unit,
    navigator: ThreePaneScaffoldNavigator<Nothing>,
    navHostController: NavHostController,
) {
    val context = LocalContext.current

    val postListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(state.role) {
        if (state.role == ListDetailPaneScaffoldRole.List) {
            return@LaunchedEffect
        }

        onLoad("TODO")

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        CompositionLocalProvider(
            LocalMainScreenNavigator provides navigator,
            LocalMainScreenState provides state,
        ) {

            // PostEmptyPane()

            ListDetailPaneScaffold(
                modifier = Modifier.fillMaxSize(),
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane {
                        HomeListPane(
                            onOpenPost = {
                                state.seletedIndex = it
                                state.role = ListDetailPaneScaffoldRole.Detail
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
                            htmlData.isEmpty -> {
                                PostEmptyPane()
                            }

                            else -> {
                                PostPane(
                                    onOpenContests = {
                                        if (state.role == ListDetailPaneScaffoldRole.Extra) {
                                            state.onNavigateBack()
                                            navigator.navigateTo(pane = state.role)
                                        } else {
                                            state.role = ListDetailPaneScaffoldRole.Extra
                                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra)
                                        }
                                    },
                                    data = htmlData,
                                    listState = postListState,
                                )
                            }
                        }
                    }
                },
                extraPane = {
                    AnimatedPane {
                        ContentsPane(
                            data = htmlData,
                            onSelected = { index, title ->
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                                coroutineScope.launch {
                                    delay(timeMillis = 400)
                                    //TODO offset
                                    postListState.animateScrollToItem(index = index)
                                }
                            },
                        )
                    }
                }
            )
        }
    }
}


class MainScreenState constructor(
    initialRole: ThreePaneScaffoldRole
) {

    var role: ThreePaneScaffoldRole by mutableStateOf(value = initialRole)

    var seletedIndex: Int by mutableIntStateOf(value = -1)

    fun onNavigateBack() {
        role = when (role) {
            ListDetailPaneScaffoldRole.Extra -> ListDetailPaneScaffoldRole.Detail
            ListDetailPaneScaffoldRole.Detail -> ListDetailPaneScaffoldRole.List
            ListDetailPaneScaffoldRole.List -> ListDetailPaneScaffoldRole.List
            else -> throw IllegalStateException("")
        }

        if (role == ListDetailPaneScaffoldRole.List) {
            seletedIndex = -1
        }
    }

    object Saver : androidx.compose.runtime.saveable.Saver<MainScreenState, Bundle> {

        val ThreePaneScaffoldRole.saveAbleName: String
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
            }
        }

        override fun restore(value: Bundle): MainScreenState {
            return MainScreenState(
                initialRole = fromSaveableName(name = value.getString("mss_role") ?: "")
            )
        }
    }
}


@Composable
fun rememberMainScreenState(
    initialRole: ThreePaneScaffoldRole = ListDetailPaneScaffoldRole.List,
): MainScreenState {
    return rememberSaveable(saver = MainScreenState.Saver) {
        MainScreenState(
            initialRole = initialRole
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