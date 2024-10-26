@file:Suppress(
    "RedundantVisibilityModifier",
    "RedundantUnitReturnType",
    "DATA_CLASS_COPY_VISIBILITY_WILL_BE_CHANGED_WARNING",
)

package com.jet.article.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.jet.article.data.HtmlArticleData
import com.jet.article.openDialApp
import com.jet.article.openEmailApp
import com.jet.article.toDomainName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.jet.article.R
import java.net.URISyntaxException


/**
 * @param callback
 * @see LinkCallback
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 06.02.2024
 */
@Keep
public class LinkClickHandler internal constructor() {

    var data: HtmlArticleData = HtmlArticleData.empty
        internal set


    var callback: LinkCallback? = null


    /**
     * @param data Parsed data
     */
    internal fun handleLink(
        link: String,
    ): Unit {
        val linkData = getLink(
            rawLink = link,
            articleUrl = data.url,
        )
        onLink(
            link = linkData,
            data = data,
        )
    }


    /**
     * @since 1.0.0
     */
    internal fun onLink(
        link: Link,
        data: HtmlArticleData,
    ): Unit {

        if (callback == null) {
            Log.d("LinkClickHandler", "onLink called but callback is null")
            return
        }

        when (link) {
            is Link.UriLink -> callback?.onUriLink(link = link,)
            is Link.SectionLink ->  callback?.onSectionLink(link = link,)
            is Link.SameDomainLink -> callback?.onSameDomainLink(link = link)
            is Link.OtherDomainLink -> callback?.onOtherDomainLink(link = link)
        }
    }


    private fun getLink(
        rawLink: String,
        articleUrl: String,
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
    @Keep
    public open class LinkCallback public constructor() {

        /**
         * @since 1.0.0
         */
        public open fun onSectionLink(
            link: Link.SectionLink,
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
        ): Unit = Unit
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
@Keep
@Immutable
public sealed class Link private constructor(
    open val rawLink: String,
    open val fullLink: String,
) {

    @Keep
    @Immutable
    data class UriLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(rawLink = rawLink, fullLink = fullLink)


    @Keep
    @Immutable
    data class SameDomainLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink
    )


    @Keep
    @Immutable
    data class OtherDomainLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink
    )


    @Keep
    @Immutable
    data class SectionLink internal constructor(
        override val rawLink: String,
        override val fullLink: String,
    ) : Link(
        rawLink = rawLink,
        fullLink = fullLink,
    )
}


////////////////////////////////////////////////////////////////////////////////////////////////
/////
/////   Compose Functions
/////
////////////////////////////////////////////////////////////////////////////////////////////////


@Composable
public fun rememberDefaultLinkCallback(): LinkClickHandler.LinkCallback {
    return remember {
        LinkClickHandler.LinkCallback()
    }
}


fun onSectionLink(
    coroutineScope: CoroutineScope,
    data: HtmlArticleData,
    lazyListState: LazyListState,
    link: Link.SectionLink,
    scrollOffset: Int
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