import java.util.function.Predicate

fun <T> djikstraComputeMoveLayer(start: List<T>, select: java.util.function.Function<T, List<T>>): List<T> {

    return start.flatMap { select.apply(it) }.distinct()
}

fun <T> djitkstraComputeToCompletion(
    start: List<T>,
    select: java.util.function.Function<T, List<T>>
): List<List<T>> {

    return generateSequence(start) {
        djikstraComputeMoveLayer(it, select)
    }
        .takeWhile { it.isNotEmpty() }.toList()
}

fun <T> djitkstraCountOfDiscretePaths(
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
            djitkstraCountOfDiscretePaths(it, select, count, isDestination)
        }.sum()

        return sum
    }
}