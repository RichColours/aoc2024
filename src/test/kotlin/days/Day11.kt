package days

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.countDigits
import util.filePathToLines
import util.getLeftAndRightHalfDigits
import util.isEven

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

    private fun addToStoneMap(map: MutableMap<Long, Long>, stone: Long, num: Long) {

        if (!map.containsKey(stone))
            map[stone] = num
        else {
            val numAlready = map[stone]!!
            map[stone] = numAlready + num
        }
    }

    private fun blinkStone(blinkCache: MutableMap<Long, List<Long>>, stone: Long): List<Long> {

        if (blinkCache.containsKey(stone)) {
            return blinkCache[stone]!!
        } else {

            val blinked = when {
                stone == 0L -> listOf(1L)

                stone.countDigits().isEven() -> {
                    val pair = stone.getLeftAndRightHalfDigits()
                    listOf(pair.first, pair.second)
                }

                else -> listOf(stone * 2024L)
            }

            blinkCache[stone] = blinked
            return blinked
        }
    }


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
            "src/test/resources/days/11/prod1.txt, 75, -1, 228449040027793",
        ]
    )
    fun question2(inputFile: String, blinks: Int, expectedStonesString: String, expected: Long) {

        //val expectedStones = expectedStonesString.split(' ').map { it.toLong() }

        val startingStones = filePathToLines(inputFile)[0].split(' ').map { it.toLong() }

        val stonesMap = mutableMapOf<Long, Long>()

        // Load stones
        startingStones.forEach {
            addToStoneMap(stonesMap, it, 1)
        }

        val blinkCache = mutableMapOf<Long, List<Long>>()

        (1..blinks).forEach {

            val processStonesMap = stonesMap.toList().toMap()

            processStonesMap.forEach { (keyStone, processN) ->

                val existingN = stonesMap[keyStone]!!
                stonesMap[keyStone] = existingN - processN

                val replacementStones = blinkStone(blinkCache, keyStone)

                replacementStones.forEach { replacementStone ->
                    addToStoneMap(stonesMap, replacementStone, processN)
                }
            }
        }

        //println(stonesMap.filter { (k, v) -> v != 0L })

        val sum = stonesMap.values.sum()

//        if (expectedStones[0] != -1L)
//            assertThat(stonesAfterBlinks).containsExactly(*expectedStones.toTypedArray())

        assertThat(sum).isEqualTo(expected)
    }


}