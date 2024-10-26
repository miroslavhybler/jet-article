@file:Suppress("RedundantVisibilityModifier")

package com.jet.article

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.trace
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlArticleData
import com.jet.article.ui.LinkClickHandler
import java.net.URI
import java.net.URISyntaxException
import kotlin.jvm.Throws


@Deprecated("remove")
@Composable
internal fun rememberHtmlText(
    key: Any,
    text: String,
    linkClickHandler: LinkClickHandler?,
): AnnotatedString = trace(sectionName = "rememberHtmlText") {

    /*
    val linkClickHandler = LocalLinkHandler.current
    val articleData = LocalHtmlArticleData.current
    val articleUrl = LocalBaseArticleUrl.current
     */

    val colorScheme = MaterialTheme.colorScheme

    return remember(key1 = key) {
        if (ArticleParser.isSimpleTextFormatAllowed) {
            text.toHtml()
                .toSpannable()
                .toAnnotatedString(
                    primaryColor = colorScheme.primary,
                    linkClickHandler = linkClickHandler,
                )
        } else {
            buildAnnotatedString {
                append(text = text)
            }
        }
    }
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
public fun String.toHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}


@Throws(URISyntaxException::class)
public fun String.toDomainName(): String? {
    val uri = URI(this)
    val domain: String = uri.host ?: return null
    return if (domain.startsWith(prefix = "www.") && domain.length > 4) {
        substring(startIndex = 4, endIndex = domain.length)
    } else domain
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
//TODO split to link and not link
public fun Spannable.toAnnotatedString(
    primaryColor: Color,
    linkClickHandler: LinkClickHandler?,
): AnnotatedString {
    val builder = AnnotatedString.Builder(this.toString())

    SpanCopier.entries.forEach { copier ->
        getSpans(0, length, copier.spanClass).forEach { span ->
            copier.copySpan(
                span = span,
                start = getSpanStart(span),
                end = getSpanEnd(span),
                destination = builder,
                linkClickHandler = linkClickHandler,
            )
        }
    }
    return builder.toAnnotatedString()
}


public fun Spannable.toAnnotatedString(): AnnotatedString {
    val builder = AnnotatedString.Builder(this.toString())

    SpanCopier.entries.forEach { copier ->
        getSpans(0, length, copier.spanClass).forEach { span ->
            copier.copySpan(
                span = span,
                start = getSpanStart(span),
                end = getSpanEnd(span),
                destination = builder,
                linkClickHandler = null,
            )
        }
    }
    return builder.toAnnotatedString()
}


private enum class SpanCopier {
    URL {
        override val spanClass = URLSpan::class.java

        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            linkClickHandler: LinkClickHandler?,
        ) {
            val urlSpan = span as URLSpan

            destination.addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = urlSpan.url,
                    linkInteractionListener = {
                        linkClickHandler?.handleLink(link = urlSpan.url) ?: kotlin.run {
                            Log.w("Jet-Article", "Clicked on link but linkClickHandler is null")
                        }
                    },
                ),
                start = start,
                end = end,
            )
            destination.addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                ),
                start = start,
                end = end,
            )
        }
    },
    FOREGROUND_COLOR {
        override val spanClass = ForegroundColorSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            linkClickHandler: LinkClickHandler?,
        ) {
            val colorSpan = span as ForegroundColorSpan
            destination.addStyle(
                style = SpanStyle(color = Color(colorSpan.foregroundColor)),
                start = start,
                end = end,
            )
        }
    },
    UNDERLINE {
        override val spanClass = UnderlineSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            linkClickHandler: LinkClickHandler?,
        ) {
            destination.addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = start,
                end = end,
            )
        }
    },
    STYLE {
        override val spanClass = StyleSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            linkClickHandler: LinkClickHandler?,
        ) {
            val styleSpan = span as StyleSpan

            destination.addStyle(
                style = when (styleSpan.style) {
                    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                    Typeface.BOLD_ITALIC -> SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )

                    else -> SpanStyle()
                },
                start = start,
                end = end,
            )
        }
    };

    abstract val spanClass: Class<out CharacterStyle>
    abstract fun copySpan(
        span: Any,
        start: Int,
        end: Int,
        destination: AnnotatedString.Builder,
        linkClickHandler: LinkClickHandler?,
    )
}


////////////////////////////////////////////////////////////////////////////////////////////////////
/////
/////   Context utils
/////
////////////////////////////////////////////////////////////////////////////////////////////////////


/**
 * @since 1.0.0
 */
fun Context.openEmailApp(email: String, subject: String? = null, text: String? = null) {
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text)
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(email)),
            ""
        )
    )
}


/**
 * @since 1.0.0
 */
fun Context.openDialApp(phoneNumber: String) {
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_DIAL)
                .setData("tel:${phoneNumber}".toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ""
        )
    )
}


/**
 * @since 1.0.0
 */
fun Context.openMapsApp(address: String) {
    val url = "http://maps.google.com/maps?daddr=$address"
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}