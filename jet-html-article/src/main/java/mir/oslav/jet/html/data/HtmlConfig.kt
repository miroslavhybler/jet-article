package mir.oslav.jet.html.data

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import mir.oslav.jet.html.composables.CollapsingTopBarScrollConnection
import mir.oslav.jet.html.composables.EmptyScrollConnection
import mir.oslav.jet.html.composables.JetHtmlArticleScaffoldState


/**
 * TODO docs
 * @param spanCount Defines the max span count (max column count)
 * @param topBarConfig Defines the look and behaviour of TopBar
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
data class HtmlConfig constructor(
    val spanCount: Int = 1,
    val topBarConfig: TopBarConfig = TopBarConfig.SIMPLE
) {


    /**
     * @since 1.0.0
     */
    enum class TopBarConfig {


        /**
         * @since 1.0.0
         */
        NONE {
            override fun createScrollConnection(
                scaffoldState: JetHtmlArticleScaffoldState
            ): NestedScrollConnection {
                return EmptyScrollConnection
            }
        },


        /**
         * @since 1.0.0
         */
        SIMPLE {
            override fun createScrollConnection(
                scaffoldState: JetHtmlArticleScaffoldState
            ): NestedScrollConnection {
                return EmptyScrollConnection
            }
        },


        /**
         * @since 1.0.0
         */
        APPEARING {
            override fun createScrollConnection(
                scaffoldState: JetHtmlArticleScaffoldState
            ): NestedScrollConnection {
                return EmptyScrollConnection
            }
        },


        /**
         * @since 1.0.0
         */
        COLLAPSING {
            override fun createScrollConnection(
                scaffoldState: JetHtmlArticleScaffoldState
            ): NestedScrollConnection {
                return CollapsingTopBarScrollConnection(scaffoldState = scaffoldState)
            }
        },


        /**
         * @since 1.0.0
         */
        FULLSCREEN {
            override fun createScrollConnection(
                scaffoldState: JetHtmlArticleScaffoldState
            ): NestedScrollConnection {
                return EmptyScrollConnection
            }
        };



        /**
         * @since 1.0.0
         */
        abstract fun createScrollConnection(
            scaffoldState: JetHtmlArticleScaffoldState
        ): NestedScrollConnection

    }

}