package mir.oslav.jet.html

import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlHeadData


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
//TODO refactor
@Deprecated(message = "Use the new one")
abstract class HtmlArticleParserListener constructor() {


    /**
     * @since 1.0.0
     */
    open fun onTitle(title: String): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onImage(image: HtmlElementOld.Image): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onQuote(quote: HtmlElementOld.Quote): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onCode(code: HtmlElementOld.Code): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTextBlock(textBlock: HtmlElementOld.TextBlock): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTitle(title: HtmlElementOld.Title): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onTable(table: HtmlElementOld.Table): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onBasicList(basicList: HtmlElementOld.BasicList): Unit = Unit


    /**
     * @since 1.0.0
     */
    open fun onAddress(address: HtmlElementOld.Address): Unit = Unit


    /**
     * @since 1.0.0
     */
    abstract fun onDataRequested(
        headData: HtmlHeadData?,
        loadingStates: HtmlData.LoadingStates,
        isFullyLoaded:Boolean
    ): HtmlData


    /**
     * @since 1.0.0
     */
    abstract fun clear(): Unit
}