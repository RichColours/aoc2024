package util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class GridTest {

    val gridSource = ListOfStringsDataSource(listOf("ab", "cd", "ef"))
    val grid = BaseGrid(gridSource)

    @Test
    fun testRotation1() {

        grid.printGrid()

        println("---")

        val rG = grid.rotatedView()
        rG.printGrid()

        println("---")

        val rrG = rG.rotatedView()
        rrG.printGrid()

        println("---")

        val rrrG = rrG.rotatedView()
        rrrG.printGrid()

        println("---")

        val rrrrG = rrrG.rotatedView()
        rrrrG.printGrid()
    }

    @Test
    fun singleValueOverrideTest() {

        val newSource = gridSource.overrideSingleValue(0, 0, '*')
        val newGrid = newSource.newGrid()

        assertThat(grid.valueAt(0, 0)).isEqualTo('a')
        assertThat(newGrid.valueAt(0, 0)).isEqualTo('*')
        val nnGe = newGrid.elemAt(0, 0)
        val nnGv = nnGe.value()
        assertThat(nnGv).isEqualTo('*')

        val newNewSource = newSource.overrideSingleValue(1, 1, '%')
        val newNewGrid = newNewSource.newGrid()

        assertThat(grid.valueAt(0, 0)).isEqualTo('a')
        assertThat(newGrid.valueAt(0, 0)).isEqualTo('*')
        assertThat(newNewGrid.valueAt(1, 1)).isEqualTo('%')
        assertThat(newNewGrid.elemAt(1, 1).value()).isEqualTo('%')


    }
}