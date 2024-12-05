package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day05 {

    private fun <E> middleOf(xs: List<E>): E = xs[((xs.size - 1) / 2)]

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/05/samp1.txt, true, 61|53|29, 143",
            "src/test/resources/days/05/prod1.txt, false, 4957, 4957"
        ]
    )
    fun question1(
        inputFile: String,
        doMiddlesCheck: Boolean,
        expectedCorrectlyOrderedUpdateMiddlesString: String,
        expected: Int
    ) {

        val lines = filePathToLines(inputFile)

        val expectedCorrectlyOrderedUpdateMiddles = expectedCorrectlyOrderedUpdateMiddlesString
            .split('|')
            .map { it.toInt() }

        assert(expected == expectedCorrectlyOrderedUpdateMiddles.sum())

        val rules = lines.filter { it.contains('|') }
            .map { it.split('|').map { it.toInt() } }
            .map { it[0] to it[1] }

        val updates = lines.filter { it.contains(',') }
            .map { it.split(',').map { it.toInt() } }

        val numRuleMap = mutableMapOf<Int, Pair<MutableSet<Int>, MutableSet<Int>>>()

        rules.forEach { (first, second) ->

            numRuleMap.putIfAbsent(first, Pair(mutableSetOf(), mutableSetOf()))
            numRuleMap.putIfAbsent(second, Pair(mutableSetOf(), mutableSetOf()))

            numRuleMap[first]!!.second.add(second)
            numRuleMap[second]!!.first.add(first)
        }

        fun isUpdateInOrder(us: List<Int>, numRuleMap: Map<Int, Pair<Set<Int>, Set<Int>>>): Boolean {

            return us.all { v ->

                val rulesForV = numRuleMap[v]!!

                val prefix = us.takeWhile { it != v }
                val postFix = us.reversed().takeWhile { it != v }

                rulesForV.first.containsAll(prefix) && rulesForV.second.containsAll(postFix)
            }
        }

        val correctlyOrderedUpdates = updates.filter { isUpdateInOrder(it, numRuleMap) }
        println("correctlyOrderedUpdates = $correctlyOrderedUpdates")

        val correctlyOrderedMiddles: List<Int> = correctlyOrderedUpdates.map { middleOf(it) }
        println("correctlyOrderedMiddles = $correctlyOrderedMiddles")

        val sum = correctlyOrderedMiddles.sum()

        if (doMiddlesCheck)
            assertThat(correctlyOrderedMiddles).isEqualTo(expectedCorrectlyOrderedUpdateMiddles)

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/05/samp1.txt, true, 47|29|47, 123",
            "src/test/resources/days/05/prod1.txt, false, 6938, 6938"
        ]
    )
    fun question2(
        inputFile: String,
        doMiddlesCheck: Boolean,
        expectedCorrectlyOrderedUpdateMiddlesString: String,
        expected: Int
    ) {

        val lines = filePathToLines(inputFile)

        val expectedCorrectlyOrderedUpdateMiddles = expectedCorrectlyOrderedUpdateMiddlesString
            .split('|')
            .map { it.toInt() }

        assert(expected == expectedCorrectlyOrderedUpdateMiddles.sum())

        val rules = lines.filter { it.contains('|') }
            .map { it.split('|').map { it.toInt() } }
            .map { it[0] to it[1] }

        val updates = lines.filter { it.contains(',') }
            .map { it.split(',').map { it.toInt() } }

        val numRuleMap = mutableMapOf<Int, Pair<MutableSet<Int>, MutableSet<Int>>>()

        rules.forEach { (first, second) ->

            numRuleMap.putIfAbsent(first, Pair(mutableSetOf(), mutableSetOf()))
            numRuleMap.putIfAbsent(second, Pair(mutableSetOf(), mutableSetOf()))

            numRuleMap[first]!!.second.add(second)
            numRuleMap[second]!!.first.add(first)
        }

        fun isUpdateInOrder(us: List<Int>, numRuleMap: Map<Int, Pair<Set<Int>, Set<Int>>>): Boolean {

            return us.all { v ->

                val rulesForV = numRuleMap[v]!!

                val prefix = us.takeWhile { it != v }
                val postFix = us.reversed().takeWhile { it != v }

                rulesForV.first.containsAll(prefix) && rulesForV.second.containsAll(postFix)
            }
        }

        fun putInOrder(us: List<Int>, numRuleMap: Map<Int, Pair<Set<Int>, Set<Int>>>): List<Int> {

            val ordered = mutableListOf<Int>()

            // Put the rest in
            us.forEach { addMeIn ->

                val rulesForV = numRuleMap[addMeIn]!!

                val insertAt = (0..< ordered.size)
                    .firstOrNull {
                        // First index where the ordered item is not in the left rules
                        ordered[it] !in rulesForV.first
                    } ?: ordered.size

                ordered.add(insertAt, addMeIn)
            }

            return ordered
        }

        val wrongOrderedUpdates = updates.filterNot { isUpdateInOrder(it, numRuleMap) }
        println("wrongOrderedUpdates = $wrongOrderedUpdates")

        val reworkedUpdates = wrongOrderedUpdates.map { putInOrder(it, numRuleMap) }
        println("    reworkedUpdates = $reworkedUpdates")

        val correctlyOrderedMiddles: List<Int> = reworkedUpdates.map { middleOf(it) }
        println("correctlyOrderedMiddles = $correctlyOrderedMiddles")

        val sum = correctlyOrderedMiddles.sum()

        if (doMiddlesCheck)
            assertThat(correctlyOrderedMiddles).isEqualTo(expectedCorrectlyOrderedUpdateMiddles)

        assertThat(sum).isEqualTo(expected)
    }

}