@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.jet.article.data.HtmlData
import com.jet.article.openDialApp
import com.jet.article.openEmailApp
import com.jet.article.toDomainName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mir.oslav.jet.html.article.R
import java.net.URISyntaxException


/**
 * @see rememberLinkClickHandler
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 06.02.2024
 */
public class LinkClickHandler internal constructor(
    private val lazyListState: LazyListState,
    private val context: Context,
    private val callback: LinkCallback,
) {

    internal fun handleLink(
        clickedText: AnnotatedString,
        clickOffset: Int,
        articleUrl: String,
        data: HtmlData,
        scrollOffset: Int
    ) {
        val anotations = clickedText.getStringAnnotations(start = clickOffset, end = clickOffset)
        anotations.firstOrNull()?.let { annotation ->
            val link = getLink(rawLink = annotation.item, articleUrl = articleUrl)
            onLink(
                link = link,
                articleUrl = articleUrl,
                data = data,
                scrollOffset = scrollOffset
            )
        }
    }


    //TODO
    private fun onLink(link: Link, articleUrl: String, data: HtmlData, scrollOffset: Int) {
        when (link) {
            is Link.UriLink -> {
                callback.onUriLink(link = link, context = context)
            }

            is Link.SectionLink -> {
                callback.onSectionLink(
                    link = link,
                    lazyListState = lazyListState,
                    data = data,
                    scrollOffset = scrollOffset
                )
            }

            is Link.SameDomainLink -> {
                callback.onSameDomainLink(link = link)
            }

            is Link.OtherDomainLink -> {
                callback.onOtherDomainLink(link = link)
            }
        }
    }


    private fun getLink(rawLink: String, articleUrl: String): Link {
        if (rawLink.startsWith(prefix = "#")) {
            return Link.SectionLink(rawLink = rawLink)
        }

        if (rawLink.startsWith(prefix = "mailto:") || rawLink.startsWith(prefix = "tel:")) {
            return Link.UriLink(rawLink = rawLink)
        }

        val mDomain = try {
            articleUrl.toDomainName()
        } catch (e: URISyntaxException) {
            null
        }
        if (mDomain != null && rawLink.startsWith(prefix = mDomain)) {
            //TODO make final link
            return Link.SameDomainLink(rawLink = rawLink)
        }

        return Link.OtherDomainLink(rawLink = rawLink)
    }


    public open class LinkCallback public constructor() {

        public open fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlData,
            scrollOffset: Int,
        ): Unit = Unit


        public open fun onSameDomainLink(
            link: Link.SameDomainLink
        ): Unit = Unit


        public open fun onOtherDomainLink(
            link: Link.OtherDomainLink,
        ): Unit = Unit


        public open fun onUriLink(
            link: Link.UriLink,
            context: Context,
        ): Unit = Unit
    }


    /**
     * @see rememberDefaultLinkCallback
     * @since 1.0.0
     */
    public class DefaultLinkCallback internal constructor(
        private val snackbarHostState: SnackbarHostState,
        private val coroutineScope: CoroutineScope,
        private val context: Context,
    ) : LinkCallback() {

        override fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlData,
            scrollOffset: Int,
        ) {
            coroutineScope.launch {
                val i = data.elements.indexOfFirst { element ->
                    element.id == link.rawLink.removePrefix(prefix = "#")
                }

                i.takeIf { index -> index != -1 }
                    ?.let { index ->
                        lazyListState.animateScrollToItem(
                            index = index,
                            scrollOffset = scrollOffset
                        )
                    }

            }
        }


        override fun onUriLink(link: Link.UriLink, context: Context) {
            val rawLink = link.rawLink
            when {
                rawLink.startsWith(prefix = "mailto:") -> {
                    context.openEmailApp(email = rawLink.removePrefix(prefix = "mailto:"))
                }

                rawLink.startsWith(prefix = "tel:") -> {
                    context.openDialApp(phoneNumber = rawLink.removePrefix(prefix = "tel:"))
                }

                else -> {
                    showSnackBar(stringRes = R.string.jet_article_unkonw_uri_link)
                }
            }
        }

        override fun onSameDomainLink(link: Link.SameDomainLink) {
            showSnackBar()
        }

        override fun onOtherDomainLink(link: Link.OtherDomainLink) {
            showSnackBar()
        }


        private fun showSnackBar(
            @StringRes stringRes: Int = R.string.jet_article_links_supported
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(stringRes),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}


/**
 *
 * @since 1.0.0
 */
public sealed class Link private constructor(
    open val rawLink: String
) {

    data class UriLink internal constructor(
        override val rawLink: String
    ) : Link(rawLink = rawLink)


    data class SameDomainLink internal constructor(
        override val rawLink: String,
    ) : Link(rawLink = rawLink)


    data class OtherDomainLink internal constructor(
        override val rawLink: String,
    ) : Link(rawLink = rawLink)


    data class SectionLink internal constructor(
        override val rawLink: String,
    ) : Link(rawLink = rawLink)
}


@Composable
internal fun rememberLinkClickHandler(
    lazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    data: HtmlData,
    callback: LinkClickHandler.LinkCallback = rememberDefaultLinkCallback(
        snackbarHostState = snackbarHostState,
        coroutineScope = rememberCoroutineScope(),
        data = data
    )
): LinkClickHandler {
    val context = LocalContext.current
    return remember {
        LinkClickHandler(
            lazyListState = lazyListState,
            context = context,
            callback = callback,
        )
    }
}


@Composable
public fun rememberDefaultLinkCallback(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    data: HtmlData,
    context: Context = LocalContext.current,
): LinkClickHandler.LinkCallback {
    return remember {
        LinkClickHandler.DefaultLinkCallback(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            context = context,
        )
    }
}