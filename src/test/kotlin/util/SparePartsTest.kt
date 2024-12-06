package util

import assertk.assertThat
import assertk.assertions.containsExactly
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

}