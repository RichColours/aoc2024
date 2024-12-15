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
