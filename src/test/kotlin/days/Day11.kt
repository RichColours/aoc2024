package days

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.*

class Day11 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/11/samp1a.txt, 1, 1 2024 1 0 9 9 2021976, 7",
            "src/test/resources/days/11/samp1b.txt, 1, 253000 1 7, 3",
            "src/test/resources/days/11/samp1b.txt, 2, 253 0 2024 14168, 4",
            "src/test/resources/days/11/samp1b.txt, 3, 512072 1 20 24 28676032, 5",
            "src/test/resources/days/11/samp1b.txt, 4, 512 72 2024 2 0 2 4 2867 6032, 9",
            "src/test/resources/days/11/samp1b.txt, 5, 1036288 7 2 20 24 4048 1 4048 8096 28 67 60 32, 13",
            "src/test/resources/days/11/samp1b.txt, 6, 2097446912 14168 4048 2 0 2 4 40 48 2024 40 48 80 96 2 8 6 7 6 0 3 2, 22",
            "src/test/resources/days/11/samp1b.txt, 25, -1, 55312",
            "src/test/resources/days/11/prod1.txt, 25, -1, 193269",
        ]
    )
    fun question1(inputFile: String, blinks: Int, expectedStonesString: String, expected: Int) {

        val expectedStones = expectedStonesString.split(' ').map { it.toLong() }

        val startingStones = filePathToLines(inputFile)[0].split(' ').map { it.toLong() }

        val transformStone = fun(i: Long): List<Long> {

            return when {
                i == 0L -> listOf(1)

                i.countDigits().isEven() -> {
                    val pair = i.getLeftAndRightHalfDigits()
                    listOf(pair.first, pair.second)
                }

                else -> listOf(i * 2024L)
            }
        }

        val stonesAfterBlinks = (1..blinks).fold(startingStones) { acc, i ->

            acc.flatMap {
                transformStone(it)
            }
        }

        val sum = stonesAfterBlinks.count()

        if (expectedStones[0] != -1L)
            assertThat(stonesAfterBlinks).containsExactly(*expectedStones.toTypedArray())

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/


}