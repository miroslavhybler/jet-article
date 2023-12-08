package mir.oslav.jet.html.parse.listeners

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.data.ParseMetrics


/**
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
class GalleryGroupingListener constructor() : LinearListener() {


    override fun onDataRequested(
        config: HtmlConfig,
        monitoring: ParseMetrics,
        headData: HtmlHeadData?
    ): HtmlData.Success {
        val images = elements.filterIsInstance<HtmlElement.Parsed.Image>()
        val gallery = HtmlElement.Constructed.Gallery(images = images, span = config.spanCount)

        return HtmlData.Success(
            elements = ArrayList<HtmlElement>().apply {
                add(gallery)
                addAll(elements)
            },
            metrics = monitoring,
            headData = headData
        )
    }
}