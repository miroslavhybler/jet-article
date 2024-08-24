@file:Suppress("OPT_IN_USAGE")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.jet.article.example.devblog.ui.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jet.article.data.HtmlArticleData
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.composables.PostTopBar
import com.jet.article.example.devblog.data.AdjustedPostData
import com.jet.article.example.devblog.rememberCurrentOffset
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.article.ui.JetHtmlArticleContent
import com.jet.article.ui.Link
import com.jet.article.ui.LinkClickHandler
import com.jet.article.ui.elements.HtmlImage
import com.jet.utils.dpToPx
import com.jet.utils.plus
import com.jet.utils.pxToDp


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@Composable
fun PostPane(
    data: AdjustedPostData,
    onOpenContests: () -> Unit,
    listState: LazyListState,
) {
    val context = LocalContext.current
    val dimensions = LocalDimensions.current
    val mainState = LocalMainScreenState.current
    val density = LocalDensity.current

    val scrolLState = rememberScrollState()
    val scrollOffset by rememberCurrentOffset(state = listState)
    var topBarAlpha by rememberSaveable { mutableFloatStateOf(value = 0f) }
    var imageOffset by remember { mutableStateOf(value = 0) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    var topAppBarHeight by remember { mutableIntStateOf(value = 0) }
    val statusBarPadding = WindowInsets.statusBars.getTop(density = density)


    val headerImageHeight = TopAppBarDefaults.LargeAppBarExpandedHeight
        .plus(other = density.pxToDp(px = statusBarPadding))

    val linkCallback = remember {
        object : LinkClickHandler.LinkCallback() {
            override fun onOtherDomainLink(link: Link.OtherDomainLink) {
                Log.d("link-click", "other domain ${link.fullLink}")
                context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(link.fullLink.toUri())
                )
            }

            override fun onSameDomainLink(link: Link.SameDomainLink) {
                Log.d("link-click", "same domain  ${link.fullLink}")
            }

            override fun onUriLink(link: Link.UriLink, context: Context) {
                Log.d("link-click", "uri  ${link.fullLink}")
            }

            override fun onSectionLink(
                link: Link.SectionLink,
                lazyListState: LazyListState,
                data: HtmlArticleData,
                scrollOffset: Int
            ) {
                Log.d("link-click", "section  ${link.fullLink}")
            }
        }
    }


    LaunchedEffect(
        key1 = scrollOffset,
    ) {
        topBarAlpha = if (scrollOffset > 128) 0.85f else scrollOffset / (128f * 0.85f)

        val max = TopAppBarDefaults.LargeAppBarExpandedHeight
        val min = TopAppBarDefaults.LargeAppBarCollapsedHeight

        val headerImagePx = density.dpToPx(dp = headerImageHeight)
        val maxImageOffset = headerImagePx / 2f
        imageOffset = -((scrollOffset * 0.5f)
            .coerceAtMost(maximumValue = maxImageOffset))
            .toInt()
    }


    Scaffold(
        modifier = Modifier
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PostTopBar(
                modifier = Modifier.onSizeChanged { newSize -> topAppBarHeight = newSize.height },
                title = data.title.text,
                scrollBehavior = scrollBehavior,
                backgroundAlpha = topBarAlpha,
            )
        },
        content = { paddingValues ->
            //TODO custom layout
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                JetHtmlArticleContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
                    containerColor = Color.Transparent,
                    listState = listState,
                    contentPadding = paddingValues + PaddingValues(
                        start = dimensions.topLinePadding,
                        top = dimensions.topLinePadding,
                        end = dimensions.sidePadding,
                        //56.dp from FabPrimaryTokens.ContainerHeight
                        bottom = dimensions.bottomLinePadding + 56.dp,
                    ),
                    data = data.postData,
                    verticalArrangement = Arrangement.spacedBy(space = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    linkClickCallback = linkCallback,
                    image = { image ->
                        HtmlImage(
                            modifier = Modifier.animateContentSize(),
                            data = image,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(height = 128.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                )
                            }
                        )
                    }
                )

                HtmlImage(
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(height = headerImageHeight)
                        .animateContentSize()
                        .offset { IntOffset(x = 0, y = imageOffset) },
                    data = data.headerImage,
                    contentScale = ContentScale.Crop,
                )
            }
        },
        floatingActionButton = {
            //TODO check if titles are empty
            //TODO scroll to top action

            if (mainState.role == ListDetailPaneScaffoldRole.Detail
                || mainState.role == ListDetailPaneScaffoldRole.Extra
            ) {
                FloatingActionButton(
                    onClick = onOpenContests
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_content),
                        contentDescription = "TODO",
                    )
                }
            }
        }
    )
}