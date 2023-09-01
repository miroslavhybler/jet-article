package mir.oslav.jet.html.parse

import mir.oslav.jet.html.composables.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.Monitoring


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
abstract class HtmlArticleParserListener constructor() {


    open fun onTitle(title: String): Unit = Unit

    open fun onImage(image: HtmlElement.Parsed.Image): Unit = Unit

    open fun onQuote(quote: HtmlElement.Parsed.Quote): Unit = Unit

    open fun onTextBlock(textBlock: HtmlElement.Parsed.TextBlock): Unit = Unit

    open fun onTable(table: HtmlElement.Parsed.Table): Unit = Unit

    open fun onAddress(address: HtmlElement.Parsed.Address): Unit = Unit

    abstract fun onDataRequested(config: HtmlConfig, monitoring: Monitoring): HtmlData.Success

    abstract fun clear(): Unit
}