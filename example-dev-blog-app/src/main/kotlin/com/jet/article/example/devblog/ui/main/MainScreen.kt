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
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.jet.article.data.HtmlArticleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * @author Miroslav Hýbler <br>
 * created on 14.08.2024
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val state = rememberMainScreenState()
    val htmlData by viewModel.htmlData.collectAsState()


    MainScreenContent(
        state = state,
        htmlData = htmlData,
        onLoad = viewModel::loadArticle,
    )
}


@Composable
fun MainScreenContent(
    state: MainScreenState,
    htmlData: HtmlArticleData,
    onLoad: (url: String) -> Unit,
) {
    val context = LocalContext.current
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>(
        scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    )
    val postListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    BackHandler(enabled = state.role != ListDetailPaneScaffoldRole.List) {
        state.onNavigateBack()
        navigator.navigateTo(pane = state.role)
    }

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
        ListDetailPaneScaffold(
            modifier = Modifier.fillMaxSize(),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    HomeListPane(
                        onOpenPost = {
                            state.role = ListDetailPaneScaffoldRole.Detail
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        },
                        viewModel = hiltViewModel(),
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    PostPane(
                        onOpenContests = {
                            state.role = ListDetailPaneScaffoldRole.Extra
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra)
                        },
                        data = htmlData,
                        listState = postListState,
                    )
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


class MainScreenState constructor(
    initialRole: ThreePaneScaffoldRole
) {

    var role: ThreePaneScaffoldRole by mutableStateOf(value = initialRole)

    fun onNavigateBack() {
        role = when (role) {
            ListDetailPaneScaffoldRole.Extra -> ListDetailPaneScaffoldRole.Detail
            ListDetailPaneScaffoldRole.Detail -> ListDetailPaneScaffoldRole.List
            ListDetailPaneScaffoldRole.List -> ListDetailPaneScaffoldRole.List
            else -> throw IllegalStateException("")
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

        fun fromSaveableName(name: String): ThreePaneScaffoldRole {
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