package com.jet.article.example.devblog.data

import androidx.annotation.IntRange


/**
 * @author Miroslav Hýbler <br>
 * created on 12.09.2024
 */
class SimpleDate constructor(
    val year: Int,
    val month: Month,
    @IntRange(from = 0, to = 31)
    val dayOfMonth: Int,
) : Comparable<SimpleDate> {


    fun getDateString(): String {
        return "$dayOfMonth ${month.displayName} $year"
    }

    override fun compareTo(other: SimpleDate): Int {
        return when {
            this.year > other.year -> 1
            this.year < other.year -> -1
            else -> {
                when {
                    this.month > other.month -> 1
                    this.month < other.month -> -1
                    else -> {
                        when {
                            this.dayOfMonth > other.dayOfMonth -> 1
                            this.dayOfMonth < other.dayOfMonth -> -1
                            else -> 0
                        }
                    }
                }
            }
        }
    }
}