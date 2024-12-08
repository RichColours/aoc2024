package days

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import util.isEven
import util.isOdd
import java.math.BigDecimal
import java.math.BigInteger
import java.util.stream.Collectors

class Day07 {

    data class Equation(
        val test: Long,
        val numbers: List<Long>
    )

    private inline fun applyEquation(combo: Int, numbers: List<Long>): Long {

        var accumulator = numbers[0]

        (0..<numbers.size - 1).forEach { i ->

            if (combo.and(1.shl(i)) == 0) {
                // Add
                accumulator += numbers[i + 1]
            } else {
                // Multiply
                accumulator *= numbers[i + 1]
            }
        }

        return accumulator
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            //"src/test/resources/days/07/pieces1.txt, 0, 3",
            //"src/test/resources/days/07/pieces1.txt, 1, 3",
            "src/test/resources/days/07/samp1.txt, 190|3267|292, 3749",
            "src/test/resources/days/07/prod1.txt, -1, 5512534574980"
        ]
    )
    fun question1(inputFile: String, expectedSolvedString: String, expected: Long) {

        val lines = filePathToLines(inputFile)

        val expectedSolved = expectedSolvedString.split('|').map { it.toLong() }

        val eqs = lines
            .map { it.filterNot { it == ':' }.split(Regex("\\s+")) }
            .map { Equation(it[0].toLong(), it.drop(1).map { it.toLong() }) }

        fun solveEquation(eq: Equation): Long? {

            // First odd check
            if (eq.test.isOdd() && eq.numbers.all { it.isEven() })
                return null

            val numCombos = BigInteger.valueOf(2).pow(eq.numbers.size - 1).toInt()

            return (0..<numCombos)
                .asSequence().map { applyEquation(it, eq.numbers) }
                .firstOrNull {
                    it == eq.test
                }
        }

        val solved = eqs.stream().map { solveEquation(it) }.filter { it != null }.collect(Collectors.toList())

        val solvedNN = solved.map { it!! }

        val sum = solvedNN.sum()

        if (expectedSolved[0] != -1L)
            assertThat(solvedNN).containsExactly(*expectedSolved.toTypedArray())

        assertThat(sum).isEqualTo(expected)
    }

    /*********************************************************************************/

    private infix fun Long.merge(j: Long): Long {

        // How many active columns in j?
        val columns = (0..Long.MAX_VALUE).asSequence()
            .map { j / BigInteger.valueOf(10).pow(it.toInt()).toInt() }
            .takeWhile { it != 0L }
            .count()

        return (this * BigInteger.valueOf(10).pow(columns).toLong()) + j
    }

    private fun applyEquation2(combo: Int, numbers: List<Long>): Long {

        var accumulator = numbers[0]
        var column = combo

        (0..<numbers.size - 1).forEach { i ->

            val thisColumn = column.mod(3)

            if (thisColumn == 0) {
                // Add
                accumulator += numbers[i + 1]
            } else if (thisColumn == 1) {
                // Multiply
                accumulator *= numbers[i + 1]
            } else if (thisColumn == 2)
                // ||
                accumulator = accumulator merge numbers[i + 1]
            else
                throw Exception("Unhandled")

            column /= 3
        }

        return accumulator
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/07/samp1.txt, 190|3267|292|156|7290|192, 11387",
            "src/test/resources/days/07/prod1.txt, -1, 328790210468594"
        ]
    )
    fun question2(inputFile: String, expectedSolvedString: String, expected: Long) {

        val lines = filePathToLines(inputFile)

        val expectedSolved = expectedSolvedString.split('|').map { it.toLong() }

        val eqs = lines
            .map { it.filterNot { it == ':' }.split(Regex("\\s+")) }
            .map { Equation(it[0].toLong(), it.drop(1).map { it.toLong() }) }

        fun solveEquation(eq: Equation): Long? {

            // First odd check
            if (eq.test.isOdd() && eq.numbers.all { it.isEven() })
                return null

            val numCombos = BigInteger.valueOf(3).pow(eq.numbers.size - 1).toInt()

            return (0..<numCombos)
                .asSequence().map { applyEquation2(it, eq.numbers) }
                .firstOrNull {
                    it == eq.test
                }
        }

        val solved = eqs.parallelStream()
            .map { solveEquation(it) }
            .filter { it != null }
            .map { it!! }
            .collect(Collectors.toList())

        val sum = solved.sum()

        if (expectedSolved[0] != -1L)
            assertThat(solved).containsExactlyInAnyOrder(*expectedSolved.toTypedArray())

        assertThat(sum).isEqualTo(expected)
    }
}