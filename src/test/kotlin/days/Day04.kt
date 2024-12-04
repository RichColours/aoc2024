package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.filePathToLines
import util.rotatedView
import util.toGrid

class Day04 {

    private fun findXmasInGrid(grid: Grid<Char>): Int {

        val straights = (0..<grid.height).map { y ->
            (0..<grid.width - 3).map { x ->

                if (
                    grid.valueAt(x + 0, y + 0) == 'X' &&
                    grid.valueAt(x + 1, y + 0) == 'M' &&
                    grid.valueAt(x + 2, y + 0) == 'A' &&
                    grid.valueAt(x + 3, y + 0) == 'S'
                )
                    1
                else
                    0

            }.sum()
        }.sum()

        val diagonals = (0..<grid.height - 3).map { y ->
            (0..<grid.width - 3).map { x ->

                if (
                    grid.valueAt(x + 0, y + 0) == 'X' &&
                    grid.valueAt(x + 1, y + 1) == 'M' &&
                    grid.valueAt(x + 2, y + 2) == 'A' &&
                    grid.valueAt(x + 3, y + 3) == 'S'
                )
                    1
                else
                    0

            }.sum()
        }.sum()

        return straights + diagonals
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/04/samp1.txt, 18",
            "src/test/resources/days/04/test1.txt, 2",
            "src/test/resources/days/04/prod1.txt, 2378"
        ]
    )
    fun question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid1 = lines.toGrid()
        val grid2 = grid1.rotatedView()
        val grid3 = grid2.rotatedView()
        val grid4 = grid3.rotatedView()

        val sum = listOf(grid1, grid2, grid3, grid4).sumOf { findXmasInGrid(it) }

        assertThat(sum).isEqualTo(expected)
    }


    /*********************************************************************************/

    private fun findCrossMasInGrid(grid: Grid<Char>): Int {

        return (1..<grid.height - 1).map { y ->
            (1..<grid.width - 1).map { x ->

                if (
                    grid.valueAt(x - 1, y - 1) == 'M' &&
                    grid.valueAt(x + 0, y + 0) == 'A' &&
                    grid.valueAt(x + 1, y + 1) == 'S' &&
                    grid.valueAt(x - 1, y + 1) == 'M' &&
                    grid.valueAt(x + 1, y - 1) == 'S'
                )
                    1
                else
                    0

            }.sum()
        }.sum()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/04/test2-1.txt, 1",
            "src/test/resources/days/04/samp2-1.txt, 9",
            "src/test/resources/days/04/prod1.txt, 1796"
        ]
    )
    fun question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid1 = lines.toGrid()
        val grid2 = grid1.rotatedView()
        val grid3 = grid2.rotatedView()
        val grid4 = grid3.rotatedView()

        val sum = listOf(grid1, grid2, grid3, grid4).sumOf { findCrossMasInGrid(it) }

        assertThat(sum).isEqualTo(expected)
    }
}