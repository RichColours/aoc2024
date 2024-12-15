package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import util.getWithIteratorIf
import java.math.BigInteger
import java.util.*

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

        val files = (0..<(line.length / 2) + 1)
            .map {
                File(
                    it,
                    lineIt.nextChar().digitToInt(),
                    if (!lineIt.hasNext()) 0 else lineIt.nextChar().digitToInt()
                )
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

        class CompactedBlockIterator : Iterator<Block> {

            private val backwardsBlockIterator = BackwardsBlockIterator()
            private val filesIterator = files.iterator()

            private var file = filesIterator.next()
            private var fileAndSpaceIndex = 0

            override fun next(): Block {

                return if (fileAndSpaceIndex in (0..<file.nBlocks)) {
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
        val checksum = compactedBlocks.foldIndexed(BigInteger.ZERO) { index, acc, block ->
            acc.add(
                BigInteger.valueOf(block.file.id.toLong()).multiply(BigInteger.valueOf(index.toLong()))
            )
        }.toLong()

        assertThat(checksum).isEqualTo(expected)
    }

    /*********************************************************************************/

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/09/samp1.txt, 2858",
            "src/test/resources/days/09/prod1.txt, 6467290479134",
        ]
    )
    fun question2(inputFile: String, expected: Long) {

        val lines = filePathToLines(inputFile)
        assert(lines.size == 1)

        val line = lines[0]

        val lineIt = line.iterator()

        data class File2(
            val id: Int,
            var position: Int,
            val size: Int
        )

        data class FreeSpace(
            var position: Int,
            var size: Int
        )

        val filesById = mutableMapOf<Int, File2>()
        val freeSpaceChain = LinkedList<FreeSpace>()

        println("Creating files ...")

        filesById.also { // Just for scoping

            var position = 0

            (0..<(line.length / 2) + 1)
                .forEach {
                    val id = it
                    val size = lineIt.nextChar().digitToInt()
                        .also { it != 0 }
                    val spaceAfter = if (!lineIt.hasNext()) 0 else lineIt.nextChar().digitToInt()
                    val file = File2(id, position, size)
                    val freeSpace = FreeSpace(position + size, spaceAfter)

                    position += (file.size + spaceAfter)

                    filesById[id] = file
                    freeSpaceChain += freeSpace
                }
        }

        assert(!lineIt.hasNext()) // Make sure all input is consumed

        val highestFileId = filesById.size - 1

        println("Compacting ... nFiles=${filesById.size}")

        // Move each file at most once
        // Only move it <

        (highestFileId downTo 0).forEach { id ->

            val file = filesById[id]!!

            // Find earliest freeSpace that fits and is < file position

            val freeSpaceAndIterator = freeSpaceChain.getWithIteratorIf { space ->
                space.size >= file.size && space.position < file.position
            }

            if (freeSpaceAndIterator != null) {

                val freeSpace = freeSpaceAndIterator.first
                val freeSpaceIterator = freeSpaceAndIterator.second

                //println("Moving file ${file.id} from ${file.position} to ${freeSpace.position}")
                //println("FreeSpace = $freeSpace")

                file.position = freeSpace.position

                if (freeSpace.size == file.size) {
                    freeSpaceIterator.remove()
                } else {
                    freeSpace.position += file.size
                    freeSpace.size -= file.size
                }

                // Don't need to compact free space of where file was cus we're never going back there
            }
        }

        var checksum = BigInteger.ZERO

        val allFiles = filesById.values.sortedBy { it.position }

        allFiles.forEach { file ->
            (0..<file.size).forEach { fileBlockIndex ->
                checksum += ((file.position + fileBlockIndex).toBigInteger() * file.id.toBigInteger())
            }
        }

        assertThat(checksum.toLong()).isEqualTo(expected)
    }
}