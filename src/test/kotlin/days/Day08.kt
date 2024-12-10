package days

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.*
import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import java.util.stream.Collectors
import java.util.stream.Stream

class Day08 {

    fun calculateBothAntinodes(first: Grid.GridElem<*>, second: Grid.GridElem<*>): Pair<Pair<Int, Int>, Pair<Int, Int>> {

        assert(first != second)

        val diffX = second.x - first.x
        val diffY = second.y - first.y

        val antinodeBehind = first.x - diffX to first.y - diffY
        val antinodeAhead = second.x + diffX to second.y + diffY

        return antinodeBehind to antinodeAhead
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/08/samp1b.txt, 0",
            "src/test/resources/days/08/samp1a.txt, 2",
            "src/test/resources/days/08/samp1c.txt, 4",
            "src/test/resources/days/08/samp1.txt, 14",
            "src/test/resources/days/08/prod1.txt, 278"
        ]
    )
    fun question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid = lines.toGrid()

        val antennaTypeToAntennas = grid
            .filter { it.value() != '.' }
            .groupBy { it.value() }

        val antinodeLocationsByType = antennaTypeToAntennas.keys
            .associate {
                val antennas = antennaTypeToAntennas[it]!!

                val antinodes = antennas.flatMap { outerAntenna ->

                    val antiNodesForOne = antennas.flatMap { innerAntenna ->

                        if (outerAntenna == innerAntenna)
                            emptyList()
                        else {
                            val pair = calculateBothAntinodes(outerAntenna, innerAntenna)
                            listOf(pair.first, pair.second)
                                .filter { grid.isInGrid(it.first, it.second)}
                        }
                    }

                    antiNodesForOne
                }.toSet()

                it to antinodes
            }

        val allAntinodes = antinodeLocationsByType.values.flatten().toSet()

        val sum = allAntinodes.size

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/

    fun calculateAllAntinodes(grid: Grid<*>, first: Grid.GridElem<*>, second: Grid.GridElem<*>): List<Pair<Int, Int>> {

        assert(first != second)

        val diffX = second.x - first.x
        val diffY = second.y - first.y

        val behinds = generateSequence(first.x to first.y) {
            it.first - diffX to it.second - diffY
        }.takeWhile { grid.isInGrid(it.first, it.second) }.toList()

        val aheads = generateSequence(second.x to second.y) {
            it.first + diffX to it.second + diffY
        }.takeWhile { grid.isInGrid(it.first, it.second) }.toList()

        return behinds + aheads
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/08/samp1.txt, 34",
            "src/test/resources/days/08/prod1.txt, 1067"
        ]
    )
    fun question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid = lines.toGrid()

        val antennaTypeToAntennas = grid
            .filter { it.value() != '.' }
            .groupBy { it.value() }

        val antinodeLocationsByType = antennaTypeToAntennas.keys
            .associate {
                val antennas = antennaTypeToAntennas[it]!!

                val antinodes = antennas.flatMap { outerAntenna ->

                    val antiNodesForOne = antennas.flatMap { innerAntenna ->

                        if (outerAntenna == innerAntenna)
                            emptyList()
                        else {
                            val all = calculateAllAntinodes(grid, outerAntenna, innerAntenna)
                            all
                        }
                    }

                    antiNodesForOne
                }.toSet()

                it to antinodes
            }

        val allAntinodes = antinodeLocationsByType.values.flatten().toSet()

        val sum = allAntinodes.size

        assertThat(sum).isEqualTo(expected)
    }

}