package mir.oslav.jet.html.parse.listeners

import mir.oslav.jet.html.composables.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeader
import mir.oslav.jet.html.data.Monitoring


/**
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
class GalleryGroupingListener constructor() : LinearListener() {


    override fun onDataRequested(config: HtmlConfig, monitoring: Monitoring): HtmlData.Success {
        val images = elements.filterIsInstance<HtmlElement.Image>()
        val gallery = HtmlElement.Gallery(images = images, span = config.spanCount)

        return HtmlData.Success(
            title = title,
            htmlElements = ArrayList<HtmlElement>().apply {
                add(gallery)
                addAll(elements)
            },
            header = if (images.isNotEmpty()) {
                HtmlHeader.TopBarHeader(
                    title = title,
                    image = images.first()
                )
            } else HtmlHeader.None,
            monitoring = monitoring
        )
    }
}