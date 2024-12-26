package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import djikstraComputeRegionToCompletion
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.deriveRanges
import util.filePathToLines
import util.toGrid
import java.lang.Integer.max
import java.lang.Integer.min

class Day12 {

    fun <T> regionStartingAt(
        start: Grid.GridElem<T>,
    ): List<Grid.GridElem<T>> {

        val region = djikstraComputeRegionToCompletion(
            listOf(start),
            fun(neighboursOf: Grid.GridElem<T>): List<Grid.GridElem<T>> {

                val neighbours =
                    neighboursOf.neighboursHorizontalAndVertical().filter { it.value() == neighboursOf.value() }
                return neighbours
            }
        )

        //println(region)

        return region
    }

    private fun <T> findAllRegions(grid: Grid<T>): List<List<Grid.GridElem<T>>> {

        val allElements = grid.toMutableSet()
        val regions = mutableListOf<List<Grid.GridElem<T>>>()

        while (allElements.isNotEmpty()) {

            val findFor = allElements.first()
            allElements.remove(findFor)

            val region = regionStartingAt(findFor)
            regions += region

            allElements.removeAll(region.toSet())
        }

        return regions
    }

    private fun <T> regionPrice(region: List<Grid.GridElem<T>>): Int {

        val regionSet = region.toSet()

        val regionPerimeter = region.map { plot ->

            4 - plot.neighboursHorizontalAndVertical().filter { it in regionSet }.count()
        }.sum()

        val regionArea = region.size
        val regionPrice = regionArea * regionPerimeter

        return regionPrice
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/12/samp1a.txt, 140",
            "src/test/resources/days/12/samp1b.txt, 772",
            "src/test/resources/days/12/samp1c.txt, 1930",
            "src/test/resources/days/12/prod1.txt, 1304764",
        ]
    )
    fun question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid = lines.toGrid()

        val regions = findAllRegions(grid)

        assertThat(regions.flatMap { it }.size).isEqualTo(grid.size)

        val regionToPrice = regions.map { regionPrice(it) }

        // Nothing about combining regions

        val sum = regionToPrice.sum()

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/

    data class QuadIntTuple(val int1: Int, val int2: Int, val int3: Int, val int4: Int)

    private fun <T> regionPriceWithPerimeterBulkDiscount(region: List<Grid.GridElem<T>>): Int {

        val regionSet = region.toSet()
        val regionType = regionSet.first().value()

        val (xLow, yLow, xHigh, yHigh) = regionSet.fold(
            QuadIntTuple(Int.MAX_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
        ) { acc, it ->

            val newXLow = min(acc.int1, it.x)
            val newYLow = min(acc.int2, it.y)
            val newXHigh = max(acc.int3, it.x)
            val newYHigh = max(acc.int4, it.y)

            QuadIntTuple(newXLow, newYLow, newXHigh, newYHigh)
        }

        val horizontalStretches: List<IntRange> = (yLow..yHigh).flatMap { y ->

            val topsAndBottoms = listOf(true, false).map { isTop ->

                val xFences: List<Grid.GridElem<T>> = (xLow..xHigh).flatMap { x ->

                    val maybePlot = regionSet.find { it.x == x && it.y == y }

                    if (maybePlot != null) {
                        val neighb = if (isTop) Grid.Position.T else Grid.Position.B

                        val maybeTBNeighbour = maybePlot.neighbour(neighb)

                        if (maybeTBNeighbour == null || maybeTBNeighbour.value() != regionType) {
                            listOf(maybePlot)
                        } else
                            emptyList()
                    } else {
                        emptyList()
                    }
                }

                val points = xFences.map { it.x }
                val ranges = points.deriveRanges()
                ranges
            }

            topsAndBottoms.flatten()
        }

        val verticalStretches: List<IntRange> = (xLow..xHigh).flatMap { x ->

            val leftsAndRights = listOf(true, false).map { isLeft ->

                val yFences: List<Grid.GridElem<T>> = (yLow..yHigh).flatMap { y ->

                    val maybePlot = regionSet.find { it.x == x && it.y == y }

                    if (maybePlot != null) {
                        val neighb = if (isLeft) Grid.Position.L else Grid.Position.R

                        val maybeTBNeighbour = maybePlot.neighbour(neighb)

                        if (maybeTBNeighbour == null || maybeTBNeighbour.value() != regionType) {
                            listOf(maybePlot)
                        } else
                            emptyList()
                    } else {
                        emptyList()
                    }
                }

                val points = yFences.map { it.y }
                val ranges = points.deriveRanges()
                ranges
            }

            leftsAndRights.flatten()
        }

        val regionBulkPerimeter = horizontalStretches.size + verticalStretches.size

        val regionArea = region.size
        val regionPrice = regionArea * regionBulkPerimeter

        //println("Region type=$regionType area=$regionArea bulkPerim=$regionBulkPerimeter price=$regionPrice")

        return regionPrice
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/12/my2a.txt, 4",
            "src/test/resources/days/12/my2d1.txt, 8",
            "src/test/resources/days/12/my2d2.txt, 8",
            "src/test/resources/days/12/my2c1.txt, 8",
            "src/test/resources/days/12/my2c2.txt, 8",
            "src/test/resources/days/12/my2e1.txt, 48",
            "src/test/resources/days/12/my2e2.txt, 48",
            "src/test/resources/days/12/my2b.txt, 24",
            "src/test/resources/days/12/samp1a.txt, 80",
            "src/test/resources/days/12/samp1b.txt, 436",
            "src/test/resources/days/12/samp2a.txt, 236",
            "src/test/resources/days/12/samp2b.txt, 368",
            "src/test/resources/days/12/samp1c.txt, 1206",
            "src/test/resources/days/12/prod1.txt, 811148",
        ]
    )
    fun question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val grid = lines.toGrid()

        val regions = findAllRegions(grid)

        assertThat(regions.flatten().size).isEqualTo(grid.size)

        val regionToPrice = regions.map { regionPriceWithPerimeterBulkDiscount(it) }

        val sum = regionToPrice.sum()

        assertThat(sum).isEqualTo(expected)
    }
}
