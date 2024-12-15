package days

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import djitkstraComputeToCompletion
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.filePathToLines
import util.toGrid

class Day10 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/10/samp1a.txt, 2, 2",
            "src/test/resources/days/10/samp1b.txt, 4, 4",
            "src/test/resources/days/10/samp1c.txt, 1|2, 3",
            "src/test/resources/days/10/samp1d.txt, 5|6|5|3|1|3|5|3|5, 36",
            "src/test/resources/days/10/prod1.txt, -1, 501",
        ]
    )
    fun question1(inputFile: String, expectedScoresString: String, expected: Int) {

        val expectedScores = expectedScoresString.split('|').map { it.toInt() }

        val lines = filePathToLines(inputFile)

        val allTheInts = lines.flatMap { it.map { if (it == '.') -1 else it.digitToInt() } }

        val grid = allTheInts.toGrid(lines[0].length)

        val trailHeads = grid.filter { it.value() == 0 }

        val selectFunction = fun(i: Grid.GridElem<Int>): List<Grid.GridElem<Int>> {

            return i.neighboursExc().filter { neighb ->
                neighb.value() == i.value() + 1 &&
                        (neighb.position in listOf(Grid.Position.T, Grid.Position.R, Grid.Position.B, Grid.Position.L))
            }
        }

        val spreads = trailHeads
            .map {
                djitkstraComputeToCompletion(listOf(it), selectFunction)
            }

        val endPoints = spreads.map {
            val trailHeadAllMoves = it.flatten().filter {
                it.value() == 9
            }
            trailHeadAllMoves
        }

        val trailHeadScores = endPoints.map { it.size }
        val sum: Int = trailHeadScores.sum()

        if (expectedScores[0] != -1)
            assertThat(trailHeadScores).containsExactlyInAnyOrder(*expectedScores.toTypedArray())

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/

}