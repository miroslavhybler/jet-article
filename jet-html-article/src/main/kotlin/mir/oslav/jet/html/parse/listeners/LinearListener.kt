package mir.oslav.jet.html.parse.listeners

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


    override fun onImage(image: HtmlElement.Image) {
        if (!elements.contains(image)) {
            elements.add(image)
        }
    }

    override fun onQuote(quote: HtmlElement.Quote) {
        elements.add(quote)
    }

    override fun onCode(code: HtmlElement.Code) {
        elements.add(code)
    }

    override fun onTable(table: HtmlElement.Table) {
        elements.add(table)
    }

    override fun onTextBlock(textBlock: HtmlElement.TextBlock) {
        if (!elements.contains(textBlock)) {
            elements.add(textBlock)
        }
    }

    override fun onTitle(title: HtmlElement.Title) {
        elements.add(title)
    }

    override fun onTitle(title: String) {

    }

    override fun onAddress(address: HtmlElement.Address) {
        if (!elements.contains(address)) {
            elements.add(address)
        }
    }


    override fun onDataRequested(
        metering: HtmlParseMetering?,
        headData: HtmlHeadData?,
        loadingStates: HtmlData.LoadingStates,
        isFullyLoaded: Boolean
    ): HtmlData {
        //TODO loading states
        return HtmlData(
            elements = elements,
            metering = metering,
            headData = headData,
            error = null,
            loadingStates =loadingStates,
            isFullyLoaded = isFullyLoaded
        )
    }

    override fun clear() {
        elements.clear()
    }
}