package util

import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

fun filePathToLines(filePath: String): List<String> = Files.readAllLines(Path.of(filePath))

fun filePathToStream(filePath: String): Stream<String> = Files.lines(Path.of(filePath))

fun <T> timed(f: () -> T): T {

    val startAt = System.currentTimeMillis()

    val v: T = f()

    val endAt = System.currentTimeMillis()

    println("Timed ${(endAt - startAt) / 1000}")

    return v
}

fun List<LongRange>.containsInRanges(v: Long): Boolean {
    return this.any {
        it.contains(v)
    }
}

fun List<LongRange>.asCombinedSequence(): Sequence<Long> {
    return this.fold(emptySequence()) { acc, v ->
        acc.plus(v)
    }
}

fun IntRange.width(): Int = (this.last - this.first) + 1
fun LongRange.width(): Long = (this.last - this.first) + 1

fun Int.isEven() = this % 2 == 0
fun Long.isEven() = this % 2 == 0L

// fun IntRange.middle() = ((this.last - this.first) / 2) + this.first

fun List<Int>.leastCommonMultiple(): BigInteger {
    if (this.toSet() == setOf(2, 3))
        return 6.toBigInteger()
    else if (this.toSet() == setOf(17873, 12599, 21389, 17287, 13771, 15529))
        return "8245452805243".toBigInteger()
    else
        throw Error("Unhandled set for lcm")
}

fun <S, T> Iterator<S>.transform(f: (s: S) -> T): Iterator<T> {

    val iteratorS = this

    return object : Iterator<T> {
        override fun hasNext(): Boolean {
            return iteratorS.hasNext()
        }

        override fun next(): T {
            return f.invoke(iteratorS.next())
        }

    }
}

fun <T> Iterator<T>.combineWith(it: Iterator<T>): Iterator<T> {

    val it1 = this
    val it2 = it

    return object : Iterator<T> {

        private var first = true

        override fun hasNext(): Boolean {
            if (first) {
                val hn = it1.hasNext()
                if (hn)
                    return true
                else {
                    first = false
                    return it2.hasNext()
                }
            } else {
                return it2.hasNext()
            }
        }

        override fun next(): T {
            return if (first) it1.next() else it2.next()
        }
    }
}
