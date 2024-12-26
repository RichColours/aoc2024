package util

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class SparePartsTest {

    @Test
    fun testListSplit() {

        val data = listOf("a", "aa", "===", "b", "===", "c", "cc").split { it.startsWith("===") }

        assertThat(data).containsExactly(
            listOf("a", "aa"),
            listOf("b"),
            listOf("c", "cc")
        )
    }

    @Test
    fun deriveRangesSingle() {
        assertThat(listOf(1).deriveRanges()).isEqualTo(listOf(IntRange(1, 1)))
    }

    @Test
    fun deriveRangesSimple1() {
        assertThat(listOf(1, 2).deriveRanges()).isEqualTo(listOf(IntRange(1, 2)))
    }

    @Test
    fun deriveRangesSimple2() {
        assertThat(listOf(1, 3).deriveRanges()).isEqualTo(listOf(IntRange(1, 1), IntRange(3, 3)))
    }

    @Test
    fun deriveRangesSimple3() {
        assertThat(listOf(1, 2, 3).deriveRanges()).isEqualTo(listOf(IntRange(1, 3)))
    }

}