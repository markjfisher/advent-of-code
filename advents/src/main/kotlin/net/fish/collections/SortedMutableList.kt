package net.fish.collections

interface SortedMutableList<T> : Iterable<T> {
    val size: Int
    fun add(element: T)
    fun remove(element: T)
    operator fun get(index: Int): T
    operator fun contains(element: T): Boolean
}
