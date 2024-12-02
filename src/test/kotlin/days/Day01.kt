package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import kotlin.math.abs

class Day01 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp1.txt, 11",
            "src/test/resources/days/01/prod1.txt, 2057374"
        ]
    )
    fun day01Question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val twoLists: Pair<List<Int>, List<Int>> = lines.map { it.split(Regex("\\s+")) }
            .fold(Pair(emptyList(), emptyList())) { r, t ->
                Pair(
                    r.first.plus(t[0].toInt()),
                    r.second.plus(t[1].toInt())
                )
            }

        val sortedLists = twoLists.first.sorted() to twoLists.second.sorted()

        val sum = (0..<sortedLists.second.size).map {
            abs(sortedLists.first[it] - sortedLists.second[it])
        }
            .sum()

        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp1.txt, 31",
            "src/test/resources/days/01/prod1.txt, 23177084"
        ]
    )
    fun day01Question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val twoLists: Pair<List<Int>, List<Int>> = lines.map { it.split(Regex("\\s+")) }
            .fold(Pair(emptyList(), emptyList())) { r, t ->
                Pair(
                    r.first.plus(t[0].toInt()),
                    r.second.plus(t[1].toInt())
                )
            }

        val rightHisto = HashMap<Int, Int>()

        twoLists.second.forEach {
            rightHisto.compute(it) { _, maybeV -> 1 + (maybeV ?: 0) }
        }

        val sum = twoLists.first.map {
            it * (rightHisto[it] ?: 0)
        }
            .sum()

        assertThat(sum).isEqualTo(expected)
    }
}