package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.*

class Day06 {

    private fun nextRightDir(dir: Grid.Position): Grid.Position =
        when (dir) {
            Grid.Position.T -> Grid.Position.R
            Grid.Position.R -> Grid.Position.B
            Grid.Position.B -> Grid.Position.L
            Grid.Position.L -> Grid.Position.T
            else -> throw Exception("Unhandled $dir")
        }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/06/pieces1.txt, 0, 3",
            "src/test/resources/days/06/pieces1.txt, 1, 3",
            "src/test/resources/days/06/samp1.txt, 0, 41",
            "src/test/resources/days/06/prod1.txt, 0, 5030"
        ]
    )
    fun question1(inputFile: String, testSection: Int, expected: Int) {

        val lines = filePathToLines(inputFile).section(testSection)

        val grid = lines.toGrid()

        val init = grid.find { it.value() == '^' }!! to Grid.Position.T

        /*
        seq holds moves but will give duplicate locations when rotating - makes the logic easier
         */
        val seq = generateSequence(init) { (pos, dir) ->

            val neighbs = pos.neighboursExc()

            if (neighbs.find { it.position == dir } == null) {
                null
            } else {
                // Find next neighbour to my rotational right that isn't a #
                val lookingAt = neighbs.first { it.position == dir }

                if (lookingAt.value() != '#')
                    lookingAt to dir
                else {
                    val lookingAtNext = neighbs.first { it.position == nextRightDir(dir) }

                    if (lookingAtNext.value() != '#')
                        lookingAtNext to nextRightDir(dir)
                    else {
                        // double turn situation
                        val lookingAtNextNext = neighbs.first { it.position == nextRightDir(nextRightDir(dir)) }
                        //throw Exception("Double turn, going back on self ...")
                        lookingAtNextNext to nextRightDir(nextRightDir(dir))
                    }
                }

            }
        }

        val sum = seq.map { it.first }.distinct().count()

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/


    private fun gridPlusObstacleFormsLoop(
        gridSource: GridDataSource<Char>,
        obsX: Int,
        obsY: Int,
        startX: Int,
        startY: Int
    ): Boolean {

        val newGrid = gridSource.overrideSingleValue(obsX, obsY, '#').newGrid()

        // start -> obs
        assert((obsX != startX).xor(obsY != startY))

        val dir = if (obsX == startX) {
            if (obsY > startY)
                Grid.Position.B
            else
                Grid.Position.T
        } else if (obsY == startY) {
            if (obsX > startX)
                Grid.Position.R
            else
                Grid.Position.L

        } else
            throw Exception("Logic error")

        val init = newGrid.elemAt(startX, startY) to dir

        val possibleLoop = generateSequence(init) { (pos, dir) ->

            val neighbs = pos.neighboursExc()

            if (neighbs.find { it.position == dir } == null) {
                null
            } else {
                // Find next neighbour to my rotational right that isn't a #
                val lookingAt = neighbs.first { it.position == dir }

                if (lookingAt.value() != '#')
                    lookingAt to dir
                else {
                    val lookingAtNext = neighbs.first { it.position == nextRightDir(dir) }

                    if (lookingAtNext.value() != '#')
                        lookingAtNext to nextRightDir(dir)
                    else {
                        // double turn situation
                        val lookingAtNextNext = neighbs.first { it.position == nextRightDir(nextRightDir(dir)) }
                        //throw Exception("Double turn, going back on self ...")
                        lookingAtNextNext to nextRightDir(nextRightDir(dir))
                    }
                }

            }
        }

        val visited = mutableSetOf<CoordAndHeading>()

        return possibleLoop.any {

            val coh = CoordAndHeading(it.first.x, it.first.y, it.second)

            if (visited.contains(coh)) {

                //println("Obstacle at ${obsX}, ${obsY}")

                //println("Loop at ${it.first.x}, ${it.first.y}")

                //println()
                true
            } else {
                visited.add(coh)
                false
            }
        }
    }

    data class CoordAndHeading(
        val x: Int,
        val y: Int,
        val dir: Grid.Position
    )

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/06/samp1.txt, 0, 6",
            "src/test/resources/days/06/prod1.txt, 0, 1928"
        ]
    )
    fun question2(inputFile: String, testSection: Int, expected: Int) {

        val lines = filePathToLines(inputFile).section(testSection)

        val gridSource = ListOfStringsDataSource(lines)

        val grid = gridSource.newGrid()

        val init = grid.find { it.value() == '^' }!! to Grid.Position.T

        /*
        seq holds moves but will give duplicate locations when rotating - makes the logic easier
         */
        val seq = generateSequence(init) { (pos, dir) ->

            val lookAtNext = pos.neighbour(dir)

            if (lookAtNext == null) {
                null
            } else {
                if (lookAtNext.value() == '#') {
                    pos to when (dir) {
                        Grid.Position.T -> Grid.Position.R
                        Grid.Position.R -> Grid.Position.B
                        Grid.Position.B -> Grid.Position.L
                        Grid.Position.L -> Grid.Position.T
                        else -> throw Exception("Unhandled $dir")
                    }
                } else {
                    lookAtNext to dir
                }
            }
        }

        val path = seq.map { it.first }.distinct().toList()

        // try placing a block on each path (but not the first two) and then walk it, see if a loop is formed

        var counter = 0

        path.forEachIndexed { index, it ->

            if (index != 0) {

                val prev = path[index - 1]

                val loop = timed {
                    gridPlusObstacleFormsLoop(gridSource, it.x, it.y, prev.x, prev.y)
                }
                    .also { println("Took ${it.second}") }.first

                if (loop) counter++
            }

        }

        val sum = counter

        assertThat(sum).isEqualTo(expected)
    }

}