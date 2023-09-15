package mir.oslav.jet.html.parse.listeners

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.Monitoring
import mir.oslav.jet.html.parse.HtmlArticleParserListener


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
open class LinearListener constructor() : HtmlArticleParserListener() {


    protected var title: String = ""
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
        this.title = title
    }

    override fun onAddress(address: HtmlElement.Parsed.Address) {
        if (!elements.contains(address)) {
            elements.add(address)
        }
    }


    override fun onDataRequested(
        config: HtmlConfig,
        monitoring: Monitoring
    ): HtmlData.Success {
        return HtmlData.Success(
            title = title,
            elements = ArrayList(elements),
            monitoring = monitoring,
            topBar = HtmlElement.Constructed.TopBarHeader(
                title = title,
                image = elements.filterIsInstance<HtmlElement.Parsed.Image>().first(),
                span = config.spanCount
            )
        )
    }

    override fun clear() {
        elements.clear()
        title = ""
    }
}