package mir.oslav.jet.html.data


/**
 * Holds the result of monitoring the parsing process
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
data class ParseMetrics constructor(
    val startTime: Long,
    val endTime: Long,
    val averageDurationPerTag: Double,
    val totalTags: Int,
    val ignoredTags: Int,
    val usedTags: Int,
) {

    val duration: Long = endTime - startTime
}