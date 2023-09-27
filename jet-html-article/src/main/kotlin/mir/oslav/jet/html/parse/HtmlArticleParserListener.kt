package mir.oslav.jet.html.parse

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.ParseMetrics


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
abstract class HtmlArticleParserListener constructor() {


    /**
     * @since 1.0.0
     */
    open fun onTitle(title: String): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onImage(image: HtmlElement.Parsed.Image): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onQuote(quote: HtmlElement.Parsed.Quote): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTextBlock(textBlock: HtmlElement.Parsed.TextBlock): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTitle(title: HtmlElement.Parsed.Title): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTable(table: HtmlElement.Parsed.Table): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onAddress(address: HtmlElement.Parsed.Address): Unit = Unit


    /**
     * @since 1.0.0
     */
    abstract fun onDataRequested(config: HtmlConfig, monitoring: ParseMetrics): HtmlData.Success


    /**
     * @since 1.0.0
     */
    abstract fun clear(): Unit
}