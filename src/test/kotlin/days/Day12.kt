package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import djikstraComputeRegionToCompletion
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.filePathToLines
import util.toGrid

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

}