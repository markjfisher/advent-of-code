package net.fish.network

/**
 * Find the longest path in a graph with positive edges
 */
fun <T> longestPathDfs(
    graph: Map<T, Map<T, Int>>,
    current: T,
    end: T,
    seen: MutableSet<T> = mutableSetOf()
): Int {
    seen.add(current)
    val answer = if (current == end) {
        0
    } else {
        val max = graph[current]!!.maxOfOrNull { (next, cost) ->
            if (next !in seen) {
                longestPathDfs(graph, next, end, seen) + cost
            } else {
                Int.MIN_VALUE
            }
        } ?: Int.MIN_VALUE
        max
    }
    seen.remove(current)
    return answer
}