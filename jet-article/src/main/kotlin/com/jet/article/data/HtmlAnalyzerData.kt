@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @author Miroslav Hýbler <br>
 * created on 21.02.2024
 */
public data class ContentTag internal constructor(
    val range: IntRange,
    val tag: TagInfo,
)