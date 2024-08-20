@file:Suppress("OPT_IN_USAGE")

package com.jet.article.example.devblog.ui.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jet.article.data.HtmlArticleData
import com.jet.article.example.devblog.R
import com.jet.article.example.devblog.composables.TitleTopBar
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.article.ui.JetHtmlArticle
import com.jet.article.ui.Link
import com.jet.article.ui.LinkClickHandler
import com.jet.utils.plus


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@Composable
fun PostPane(
    data: HtmlArticleData,
    onOpenContests: () -> Unit,
    listState: LazyListState,
) {
    val context = LocalContext.current
    val dimensions = LocalDimensions.current
    val mainState = LocalMainScreenState.current

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

    Scaffold(
        topBar = {
            TitleTopBar(
                text = data.firstTitle?.text ?: "",
            )
        },
        content = { paddingValues ->
            JetHtmlArticle(
                listState = listState,
                modifier = Modifier,
                contentPadding = paddingValues + PaddingValues(
                    start = dimensions.topLinePadding,
                    top = dimensions.topLinePadding,
                    end = dimensions.sidePadding,
                    //56.dp from FabPrimaryTokens.ContainerHeight
                    bottom = dimensions.bottomLinePadding + 56.dp,
                ),
                data = data,
                verticalArrangement = Arrangement.spacedBy(space = 24.dp),
                linkClickCallback = linkCallback,
            )
        },
        floatingActionButton = {
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