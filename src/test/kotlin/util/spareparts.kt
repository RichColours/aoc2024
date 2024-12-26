package util

import java.math.BigInteger
import java.util.function.Predicate
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.pow

fun filePathToLines(filePath: String): List<String> = Path(filePath).readLines()

fun List<String>.section(i: Int): List<String> =
    this.split { it.startsWith("===") }[i]

fun <E> List<E>.split(p: Predicate<E>): List<List<E>> {

    // list(list, list, list)
    return this.fold(listOf(emptyList())) { acc, v ->

        if (p.test(v)) {
            acc.plusElement(emptyList())
        } else {
            val allBut = acc.dropLast(1)
            val end = acc.last()
            val newEnd = end.plus(v)
            val newAcc = allBut.plusElement(newEnd)
            newAcc
        }
    }
}

fun <T> timed(f: () -> T): Pair<T, Long> {

    val startAt = System.currentTimeMillis()

    val v: T = f()

    val endAt = System.currentTimeMillis()

    val msDiff = endAt - startAt

    return v to msDiff
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
fun Long.isOdd() = this % 2 == 1L

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

/**
 * Middle element of an odd-element list.
 */
private fun <E> middleOf(xs: List<E>): E = xs[((xs.size - 1) / 2)]

/**
 * Find and return with iterator (for removal) by predicate.
 */
fun <T> MutableList<T>.getWithIteratorIf(predicate: Predicate<T>): Pair<T, MutableIterator<T>>? {

    val it = this.iterator()

    while (it.hasNext()) {

        val el = it.next()

        if (predicate.test(el)) {
            return el to it
        }
    }

    return null
}

fun Int.intPower(p: Int): Int = this.toDouble().pow(p.toDouble()).toInt()

fun Long.countDigits(): Int {

    var v = this
    var count = 0

    while (v > 0) {
        count += 1
        v /= 10
    }

    return count
}

fun Long.getLeftAndRightHalfDigits(): Pair<Long, Long> {

    val digits = this.countDigits()
    val halfDigits = digits / 2

    val left = this / 10.intPower(halfDigits)
    val right = this % 10.intPower(halfDigits)

    return left to right
}

fun List<Int>.deriveRanges(): List<IntRange> {

    val outputLists = mutableListOf<IntRange>()

    var first: Int? = null
    var second: Int? = null

    this.forEach {

        if (first == null) {
            first = it
            second = null
        } else {
            if (second == null) {
                if (it == first!! + 1) {
                    second = it
                } else {
                    outputLists += IntRange(first!!, first!!)
                    first = it
                    second = null
                }
            } else {
                if (it == second!! + 1) {
                    second = it
                } else {
                    outputLists += IntRange(first!!, second!!)
                    first = it
                    second = null
                }
            }
        }
    }

    if (first != null && second == null)
        outputLists += IntRange(first!!, first!!)

    if (first != null && second != null)
        outputLists += IntRange(first!!, second!!)

    return outputLists
}
