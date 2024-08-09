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
import com.jet.article.data.HtmlArticleData
import com.jet.article.openDialApp
import com.jet.article.openEmailApp
import com.jet.article.toDomainName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mir.oslav.jet.html.article.R
import java.net.URISyntaxException


/**
 * @param lazyListState
 * @param context
 * @param callback
 * @see LinkCallback
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


    /**
     * @param clickedText Text on which user clicked
     * @param clickOffset Index of first charracted in clicked annotation
     * @param articleUrl Original full url of the article
     * @param data Parsed data
     * @param scrollOffset
     */
    internal fun handleLink(
        clickedText: AnnotatedString,
        clickOffset: Int,
        articleUrl: String,
        data: HtmlArticleData,
        scrollOffset: Int
    ) {
        val anotations = clickedText.getStringAnnotations(
            start = clickOffset,
            end = clickOffset,
        )
        anotations.firstOrNull()?.let { annotation ->
            val link = getLink(
                rawLink = annotation.item,
                articleUrl = articleUrl
            )
            onLink(
                link = link,
                data = data,
                scrollOffset = scrollOffset,
            )
        }
    }


    /**
     * @since 1.0.0
     */
    private fun onLink(
        link: Link,
        data: HtmlArticleData,
        scrollOffset: Int
    ) {
        when (link) {
            is Link.UriLink -> {
                callback.onUriLink(
                    link = link,
                    context = context,
                )
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


    private fun getLink(
        rawLink: String,
        articleUrl: String
    ): Link {
        if (rawLink.startsWith(prefix = "#")) {
            return Link.SectionLink(rawLink = rawLink, fullLink = rawLink)
        }

        if (rawLink.startsWith(prefix = "mailto:") || rawLink.startsWith(prefix = "tel:")) {
            return Link.UriLink(rawLink = rawLink, fullLink = rawLink)
        }

        val mDomain = try {
            articleUrl.toDomainName()
        } catch (e: URISyntaxException) {
            null
        }
        val linkDomain = try {
            rawLink.toDomainName()
        } catch (e: URISyntaxException) {
            null
        }

        val fullLink = validateLink(
            rawLink = rawLink,
            articleUrl = articleUrl
        )

        if (
            (mDomain != null && linkDomain != null)
            && mDomain == linkDomain
        ) {
            //Must be link within same domain
            return Link.SameDomainLink(rawLink = rawLink, fullLink = fullLink)
        }


        return Link.OtherDomainLink(rawLink = rawLink, fullLink = fullLink)
    }


    private fun validateLink(rawLink: String, articleUrl: String): String {
        var fullLink = rawLink
        if (!rawLink.startsWith(prefix = "http://") && !rawLink.startsWith(prefix = "https://")) {
            val base = articleUrl.toDomainName()?.removeSuffix(suffix = "/")
            val end = rawLink.removePrefix(prefix = "/")
            fullLink = "www.$base/$end"
        }

        return fullLink
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   LinkCallback Class
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @since 1.0.0
     */
    public open class LinkCallback public constructor() {

        /**
         * @since 1.0.0
         */
        public open fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlArticleData,
            scrollOffset: Int,
        ): Unit = Unit


        /**
         * @since 1.0.0
         */
        public open fun onSameDomainLink(
            link: Link.SameDomainLink
        ): Unit = Unit


        /**
         * @since 1.0.0
         */
        public open fun onOtherDomainLink(
            link: Link.OtherDomainLink,
        ): Unit = Unit


        /**
         * @since 1.0.0
         */
        public open fun onUriLink(
            link: Link.UriLink,
            context: Context,
        ): Unit = Unit
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////
    /////   DefaultLinkCallback Class
    /////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @see rememberDefaultLinkCallback
     * @since 1.0.0
     */
    public open class DefaultLinkCallback public constructor(
        private val snackbarHostState: SnackbarHostState,
        private val coroutineScope: CoroutineScope,
        private val context: Context,
    ) : LinkCallback() {

        override fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlArticleData,
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
                    showNotSupportedSnackBar(stringRes = R.string.jet_article_unkonw_uri_link)
                }
            }
        }


        override fun onSameDomainLink(link: Link.SameDomainLink) {
            showNotSupportedSnackBar()
        }

        override fun onOtherDomainLink(link: Link.OtherDomainLink) {
            showNotSupportedSnackBar()
        }


        private fun showNotSupportedSnackBar(
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


////////////////////////////////////////////////////////////////////////////////////////////////
/////
/////   Link Class
/////
////////////////////////////////////////////////////////////////////////////////////////////////


/**
 *
 * @since 1.0.0
 */
public sealed class Link private constructor(
    open val rawLink: String,
    open val fullLink: String,
) {

    data class UriLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(rawLink = rawLink, fullLink = fullLink)


    data class SameDomainLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink
    )


    data class OtherDomainLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink
    )


    data class SectionLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink
    )
}


////////////////////////////////////////////////////////////////////////////////////////////////
/////
/////   Compose Functions
/////
////////////////////////////////////////////////////////////////////////////////////////////////


@Composable
internal fun rememberLinkClickHandler(
    lazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    callback: LinkClickHandler.LinkCallback = rememberDefaultLinkCallback(
        snackbarHostState = snackbarHostState,
        coroutineScope = rememberCoroutineScope(),
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