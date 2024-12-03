package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day03 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp1.txt, 161",
            "src/test/resources/days/03/prod1.txt, 175015740"
        ]
    )
    fun question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val mulInstruction = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")

        val muls = lines.flatMap {

            val muls = mulInstruction.findAll(it).toList()
            muls.map { it.groupValues[1].toInt() to it.groupValues[2].toInt() }
        }

        val sum = muls.map { it.first * it.second }.sum()

        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp2.txt, 48",
            "src/test/resources/days/03/prod1.txt, 112272912"
        ]
    )
    fun question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val instructionPattern = Regex("(mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\))")

        val instructions = lines.flatMap {
            instructionPattern.findAll(it).toList()
        }

        val (_: Boolean, sum: Int) = instructions.fold(Pair(true, 0)) { acc, it ->

            val op = it.value.takeWhile { it != '(' }

            when (op) {

                "mul" -> {
                    if (acc.first) {
                        true to acc.second + (it.groupValues[2].toInt() * it.groupValues[3].toInt())
                    } else {
                        false to acc.second
                    }
                }

                "do" -> {
                    true to acc.second
                }

                "don't" -> {
                    false to acc.second
                }

                else -> throw Exception("Invalid case $op")
            }
        }

        assertThat(sum).isEqualTo(expected)
    }

}