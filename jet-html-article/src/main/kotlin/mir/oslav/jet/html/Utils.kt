package mir.oslav.jet.html

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
fun String.toHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
fun String.normalizedLink(): String = this
    .removePrefix(prefix = "\"")
    .removePrefix(prefix = " ")
    .removeSuffix(suffix = "/")
    .removeSuffix(suffix = "\"")
    .removeSuffix(suffix = " ")


fun String.normalizedAttributeValue(): String = this
    .removePrefix(prefix = " ")
    .removeSuffix(suffix = "/")
    .removeSuffix(suffix = " ")


fun String.sub(s: Int, e: Int): String = try {
    this.substring(startIndex = s, endIndex = e)
} catch (exception: StringIndexOutOfBoundsException) {
    exception.printStackTrace()
    throw exception
}

fun String.iOf(char: Char, startIndex: Int): Int {
    return try {
        this.indexOf(char = char, startIndex = startIndex)
            .takeIf { index -> index != -1 }
            ?: throw StringIndexOutOfBoundsException()
    } catch (exception: StringIndexOutOfBoundsException) {
        val clipped = this.sub(s = startIndex, e = this.length)
        Log.e(
            "mirek",
            "Failed to get index of char $char " +
                    "because its not in content from start $startIndex!\n" +
                    "Content:\n" +
                    clipped
        )
        throw exception
    }
}


fun String.iOf(string: String, startIndex: Int): Int {
    return try {
        this.indexOf(string = string, startIndex = startIndex)
            .takeIf { index -> index != -1 }
            ?: throw StringIndexOutOfBoundsException()
    } catch (exception: StringIndexOutOfBoundsException) {
        val clipped = this.sub(s = startIndex, e = this.length)
        Log.e(
            "mirek",
            "Failed to get index of char $string " +
                    "because its not in content from start $startIndex!\n" +
                    "Content:\n" +
                    clipped
        )
        throw exception
    }
}


fun Spannable.toAnnotatedString(primaryColor: Color): AnnotatedString {
    val builder = AnnotatedString.Builder(this.toString())
    val copierContext = CopierContext(primaryColor)
    SpanCopier.entries.forEach { copier ->
        getSpans(0, length, copier.spanClass).forEach { span ->
            copier.copySpan(span, getSpanStart(span), getSpanEnd(span), builder, copierContext)
        }
    }
    return builder.toAnnotatedString()
}

private data class CopierContext(
    val primaryColor: Color,
)

private enum class SpanCopier {
    URL {
        override val spanClass = URLSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            context: CopierContext
        ) {
            val urlSpan = span as URLSpan
            destination.addStringAnnotation(
                tag = name,
                annotation = urlSpan.url,
                start = start,
                end = end,
            )
            destination.addStyle(
                style = SpanStyle(
                    color = context.primaryColor,
                    textDecoration = TextDecoration.Underline
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
            context: CopierContext
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
            context: CopierContext
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
            context: CopierContext
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
        context: CopierContext
    )
}