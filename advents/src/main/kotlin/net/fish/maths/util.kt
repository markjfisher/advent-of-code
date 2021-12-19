package net.fish.maths

fun <T> List<T>.product(): List<Pair<T, T>> {
    return flatMapIndexed { index, first ->
        subList(index + 1, size).map { second ->
            first to second
        }
    }
}

fun <T, V> List<T>.product(fn: (Pair<T, T>) -> V): List<V> {
    return product().map { fn(it) }
}

fun <T, V> List<T>.productIndexed(fn: (i1: Int, v1: T, i2: Int, v2: T) -> V): List<V> {
    return flatMapIndexed { i1, v1 ->
        subList(i1 + 1, size).mapIndexed { i2, v2 ->
            fn(i1, v1, i1 + 1 + i2, v2)
        }
    }
}

fun <T> Iterable<T>.withLCounts(): Map<T, Long> {
    return groupBy { it }.mapValues { (_, c) -> c.size.toLong() }
}

fun <T> Iterable<T>.withCounts(): Map<T, Int> {
    return groupBy { it }.mapValues { (_, c) -> c.size }
}