package net.fish.collections

fun <T> List<T>.permutations(): List<List<T>> {
    val permutations: MutableList<List<T>> = mutableListOf()

    permutate(this, listOf(), permutations)

    return permutations
}

private fun <T> permutate(head: List<T>, tail: List<T>, permutations: MutableList<List<T>>) {
    if (head.isEmpty()) {
        permutations += tail
        return
    }

    for (i in head.indices) {
        val newHead = head.filterIndexed { index, _ -> index != i }.toList()
        val newTail = tail + head[i]

        permutate(newHead, newTail, permutations)
    }
}