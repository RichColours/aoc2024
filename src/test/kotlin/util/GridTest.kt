package util

import org.junit.jupiter.api.Test

class GridTest {

    @Test
    fun testRotation1() {

        val grid = listOf("ab", "cd", "ef").toGrid()
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
}