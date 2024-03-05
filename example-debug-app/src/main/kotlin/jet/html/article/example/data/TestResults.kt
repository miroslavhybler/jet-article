package jet.html.article.example.data


/**
 * @author Miroslav Hýbler <br>
 * created on 05.02.2024
 */
data class TestResults constructor(
    val durationsMillis: List<Long>,
    val durationsNano: List<Long>
) {


    val millisAverage: Double get() = durationsMillis.average()
    val nanoAverage: Double get() = durationsNano.average()
}