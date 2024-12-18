import java.util.function.Predicate

fun <T> djikstraComputeMoveLayer(start: List<T>, select: java.util.function.Function<T, List<T>>): List<T> {

    return start.flatMap { select.apply(it) }.distinct()
}

fun <T> djikstraComputeToCompletion(
    start: List<T>,
    select: java.util.function.Function<T, List<T>>
): List<List<T>> {

    return generateSequence(start) {
        djikstraComputeMoveLayer(it, select)
    }
        .takeWhile { it.isNotEmpty() }.toList()
}

/**
 * Like 1 but doesn't go in loops when there's regions rather than a strict path.
 *
 * Simplified IO.
 */
fun <T> djikstraComputeRegionToCompletion(
    start: List<T>,
    select: java.util.function.Function<T, List<T>>
): List<T> {

    val visited = mutableSetOf<T>()
    visited.addAll(start)

    generateSequence(start) {
        val nextOnes = djikstraComputeMoveLayer(it, select)

        val newOnes = nextOnes.toSet().minus(visited)

        visited += newOnes

        newOnes.toList()
    }
        .takeWhile { it.isNotEmpty() }.count()

    return visited.toList()
}

fun <T> djikstraCountOfDiscretePaths(
    start: T,
    select: java.util.function.Function<T, List<T>>,
    count: Int,
    isDestination: Predicate<T>
): Int {

    if (isDestination.test(start)) {
        return count + 1
    } else {

        val options = djikstraComputeMoveLayer(listOf(start), select)

        val sum = options.map {
            djikstraCountOfDiscretePaths(it, select, count, isDestination)
        }.sum()

        return sum
    }
}