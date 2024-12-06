package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.filePathToLines
import util.section
import util.toGrid

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



    private fun <T> gridPlusObstacleFormsLoop(initGridData: List<String>, obsX: Int, obsY: Int): Boolean {

        val newGrid = initGridData.mapIndexed { index, v ->
            if (index == obsY) {
                val s = v.mapIndexed { index, c ->
                    if (index == obsX) {
                        '#'
                    } else {
                        c
                    }
                }
                String(s.toCharArray())
            } else {
                v
            }
        }.toGrid()

        val init = newGrid.find { it.value() == '^' }!! to Grid.Position.T

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

    data class CoordAndHeading constructor(
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

        val grid = lines.toGrid()

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

        val sum = path.drop(1).count {

            gridPlusObstacleFormsLoop<Char>(lines, it.x, it.y)
        }

        assertThat(sum).isEqualTo(expected)
    }

}