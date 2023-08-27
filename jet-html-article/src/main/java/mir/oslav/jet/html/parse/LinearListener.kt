package mir.oslav.jet.html.parse

import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeader
import mir.oslav.jet.html.data.Monitoring


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
class LinearListener constructor() : HtmlArticleParserListener() {


    private var title: String = ""
    private val elements: MutableSet<HtmlElement> = mutableSetOf()


    override fun onImage(image: HtmlElement.Image) {
        if (!elements.contains(image)) {
            elements.add(image)
        }
    }

    override fun onQuote(quote: HtmlElement.Quote) {
        elements.add(quote)
    }

    override fun onTable(table: HtmlElement.Table) {
        elements.add(table)
    }

    override fun onTextBlock(textBlock: HtmlElement.TextBlock) {
        if (!elements.contains(textBlock)) {
            elements.add(textBlock)
        }
    }

    override fun onTitle(title: String) {
        this.title = title
    }


    override fun onDataRequested(monitoring: Monitoring): HtmlData.Success {
        return HtmlData.Success(
            title = title,
            htmlElements = ArrayList(elements),
            monitoring = monitoring,
            header = HtmlHeader.TopBarHeader(
                title = title,
                image = elements.filterIsInstance<HtmlElement.Image>().first().url
            )
        )
    }

    override fun clear() {
        elements.clear()
        title = ""
    }
}