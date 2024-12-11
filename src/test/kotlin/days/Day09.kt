package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import java.math.BigInteger

class Day09 {

    data class Block(
        val file: File
    )

    data class File(
        val id: Int,
        val nBlocks: Int,
        val space: Int,

    ) {
        val blockDef: Block = Block(this)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/09/samp1.txt, 1928",
            "src/test/resources/days/09/prod1.txt, 6432869891895",
        ]
    )
    fun question1(inputFile: String, expected: Long) {

        val lines = filePathToLines(inputFile)
        assert(lines.size == 1)

        val line = lines[0]

        val lineIt = line.iterator()

        val files = (0..< (line.length / 2) + 1)
            .map {
                File(
                    it,
                    lineIt.nextChar().digitToInt(),
                    if (!lineIt.hasNext()) 0 else lineIt.nextChar().digitToInt())
                    .also {
                        assert(it.nBlocks != 0)
                    }
            }

        val totalBlocks = files.sumOf { it.nBlocks }

        println(files.size)
        assert(!lineIt.hasNext()) // Make sure all input is consumed

        class BackwardsBlockIterator : Iterator<Block> {

            private val backwardFileIterator = files.reversed().iterator()
            private var file = backwardFileIterator.next()
            private var index = 0

            override fun next(): Block {

                return if (index == file.nBlocks) {
                    file = backwardFileIterator.next()
                    index = 1
                    Block(file)
                } else {
                    Block(file)
                        .also {
                            index += 1
                        }
                }
            }

            override fun hasNext(): Boolean {
                return !(index == file.nBlocks && !backwardFileIterator.hasNext())
            }
        }

        val backwardsData = BackwardsBlockIterator().asSequence().take(50).toList()
        //println("Backwards data = $backwardsData")

        class CompactedBlockIterator : Iterator<Block> {

            private val backwardsBlockIterator = BackwardsBlockIterator()
            private val filesIterator = files.iterator()

            private var file = filesIterator.next()
            private var fileAndSpaceIndex = 0

            override fun next(): Block {

                return if (fileAndSpaceIndex in (0..< file.nBlocks)) {
                    Block(file)
                        .also { fileAndSpaceIndex += 1 }
                } else if (fileAndSpaceIndex in (file.nBlocks..<(file.nBlocks + file.space))) {
                    backwardsBlockIterator.next()
                        .also { fileAndSpaceIndex += 1 }
                } else {
                    // Advance
                    file = filesIterator.next()
                    fileAndSpaceIndex = 1
                    Block(file)
                }
            }

            /**
             * This is deliberately not going to be false because the iterator will be pulled from by the exact right amount
             */
            override fun hasNext(): Boolean = true
        }


        val compactedBlocks = CompactedBlockIterator().asSequence().take(totalBlocks).toList()
        val checksum = compactedBlocks.foldIndexed(BigInteger.ZERO) { index, acc, block -> acc.add(BigInteger.valueOf(block.file.id.toLong()).multiply(BigInteger.valueOf(index.toLong()))) }.toLong()

        assertThat(checksum).isEqualTo(expected)
    }

    /*********************************************************************************/

}