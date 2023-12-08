package mir.oslav.jet.html.parse.listeners

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.data.ParseMetrics
import mir.oslav.jet.html.parse.HtmlArticleParserListener


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
open class LinearListener constructor() : HtmlArticleParserListener() {


    protected val elements: MutableSet<HtmlElement> = mutableSetOf()


    override fun onImage(image: HtmlElement.Parsed.Image) {
        if (!elements.contains(image)) {
            elements.add(image)
        }
    }

    override fun onQuote(quote: HtmlElement.Parsed.Quote) {
        elements.add(quote)
    }

    override fun onTable(table: HtmlElement.Parsed.Table) {
        elements.add(table)
    }

    override fun onTextBlock(textBlock: HtmlElement.Parsed.TextBlock) {
        if (!elements.contains(textBlock)) {
            elements.add(textBlock)
        }
    }

    override fun onTitle(title: HtmlElement.Parsed.Title) {
        elements.add(title)
    }

    override fun onTitle(title: String) {

    }

    override fun onAddress(address: HtmlElement.Parsed.Address) {
        if (!elements.contains(address)) {
            elements.add(address)
        }
    }


    override fun onDataRequested(
        config: HtmlConfig,
        monitoring: ParseMetrics,
        headData: HtmlHeadData?
    ): HtmlData.Success {
        return HtmlData.Success(
            elements = ArrayList(elements),
            metrics = monitoring,
            headData = headData
        )
    }

    override fun clear() {
        elements.clear()
    }
}