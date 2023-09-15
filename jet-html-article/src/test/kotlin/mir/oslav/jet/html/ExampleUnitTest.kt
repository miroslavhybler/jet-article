package mir.oslav.jet.html

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {


        val text = "<image src=\"https://www.exmplecom\" alt=\"Hello there\"> "
        val body = "src=\"https://www.exmplecom\" alt=\"Hello there\""

        val params = body.split("\" ")
        assertEquals(2, params.size)
    }
}