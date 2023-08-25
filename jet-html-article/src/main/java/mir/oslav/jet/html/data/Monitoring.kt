package mir.oslav.jet.html.data


/**
 * Holds the result of monitoring the parsing process
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
sealed class Monitoring private constructor() {


    data object None : Monitoring()


    data class Parse constructor(
        val startTime: Long,
        val endTime: Long,
    ) : Monitoring()
}