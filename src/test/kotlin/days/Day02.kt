package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day02 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 2",
            "src/test/resources/days/02/prod1.txt, 479"
        ]
    )
    fun question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val sum = lines.map {
            val levels = it.split(' ').map { it.toInt() }

            isLevelSafe(levels)
        }
            .sumOf { if (it) 1 else 0 as Int }

        assertThat(sum).isEqualTo(expected)
    }

    private fun isLevelSafe(xs: List<Int>): Boolean {

        val levelDiffs = (0..< xs.size - 1).map {
            xs[it] - xs[it + 1]
        }

        return levelDiffs.all { it in 1..3 } || levelDiffs.all { it in -3  .. -1 }
    }

    /**
     * Drop by index, -1 drops nothing.
     */
    private fun <T> dropElem(dropIndex: Int, xs: List<T>): List<T> {

        return if (dropIndex == -1)
            xs
        else
            xs.flatMapIndexed { i, v ->
                if (i == dropIndex)
                    emptyList()
                else listOf(v)
            }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 4",
            "src/test/resources/days/02/prod1.txt, 531"
        ]
    )
    fun question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val sum = lines.map {
            val levels = it.split(' ').map { it.toInt() }

            val foundAt = (-1 ..< levels.size)
                .find { isLevelSafe(dropElem(it, levels)) }

            foundAt != null
        }
            .sumOf { if (it) 1 else 0 as Int }

        assertThat(sum).isEqualTo(expected)
    }

}