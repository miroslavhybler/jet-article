package mir.oslav.jet.html.listeners

import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.HtmlElementOld
import mir.oslav.jet.html.article.data.HtmlHeadData
import mir.oslav.jet.html.article.data.HtmlParseMetering
import mir.oslav.jet.html.HtmlArticleParserListener


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Deprecated(message = "use new")
open class LinearListenerOld constructor() : HtmlArticleParserListener() {


    protected val elements: MutableList<HtmlElementOld> = mutableListOf()


    override fun onImage(image: HtmlElementOld.Image) {
        if (!elements.contains(image)) {
            elements.add(image)
        }
    }

    override fun onQuote(quote: HtmlElementOld.Quote) {
        elements.add(quote)
    }

    override fun onCode(code: HtmlElementOld.Code) {
        elements.add(code)
    }

    override fun onTable(table: HtmlElementOld.Table) {
        elements.add(table)
    }

    override fun onTextBlock(textBlock: HtmlElementOld.TextBlock) {
        if (!elements.contains(textBlock)) {
            elements.add(textBlock)
        }
    }

    override fun onTitle(title: HtmlElementOld.Title) {
        elements.add(title)
    }

    override fun onTitle(title: String) {

    }

    override fun onAddress(address: HtmlElementOld.Address) {
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
            elements = throw IllegalStateException(),
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