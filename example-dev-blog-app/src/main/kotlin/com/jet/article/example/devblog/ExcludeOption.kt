package com.jet.article.example.devblog

/**
 * @author Miroslav Hýbler <br>
 * created on 09.08.2024
 */
public data class ExcludeOption public constructor(
    val tag: String = "",
    val clazz: String = "",
    val id: String = "",
    val keyword: String = "",
) {

    companion object {
        val article: List<ExcludeOption>
            get() = listOf(
                ExcludeOption(clazz = "dropdown-nav"),
                ExcludeOption(clazz = "popout-nav"),
                ExcludeOption(clazz = "adb-header"),

                ExcludeOption(clazz = "icon-sidebar"),

                ExcludeOption(tag = "footer",),
                ExcludeOption(clazz = "adb-footer-section"),
            )
    }
}