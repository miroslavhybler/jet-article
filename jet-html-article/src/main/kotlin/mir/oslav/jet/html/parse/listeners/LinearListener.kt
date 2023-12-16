package mir.oslav.jet.html.parse.listeners

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.data.HtmlParseMetering
import mir.oslav.jet.html.parse.HtmlArticleParserListener


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
open class LinearListener constructor() : HtmlArticleParserListener() {


    protected val elements: MutableList<HtmlElement> = mutableListOf()


    override fun onImage(image: HtmlElement.Parsed.Image) {
        if (!elements.contains(image)) {
            elements.add(image)
        }
    }

    override fun onQuote(quote: HtmlElement.Parsed.Quote) {
        elements.add(quote)
    }

    override fun onCode(code: HtmlElement.Parsed.Code) {
        elements.add(code)
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
        metering: HtmlParseMetering?,
        headData: HtmlHeadData?,
        loadingStates: HtmlData.LoadingStates,
    ): HtmlData {
        //TODO loading states
        return HtmlData(
            elements = elements,
            metering = metering,
            headData = headData,
            error = null,
            loadingStates =loadingStates
        )
    }

    override fun clear() {
        elements.clear()
    }
}