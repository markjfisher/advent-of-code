package net.fish.maths

import kotlin.math.ceil

data class LongCircArrayEntry(val v: Long, val initIndex: Int)

data class MovingEntriesCircularArray(val initialData: List<Long>) {
    private var data: MutableList<LongCircArrayEntry> = mutableListOf()
    init { init() }

    fun init() {
        data.clear()
        initialData.forEachIndexed { i, v -> data.add(LongCircArrayEntry(v, i)) }
    }

    private val linkCount = data.size - 1

    fun values(): List<Long> = data.map { it.v }
    fun findZero(): Int = data.indexOf(data.find { it.v == 0L }!!)

    fun at(index: Int): Long {
        // calculate how many times we need to add size to get to positive index
        val n = if (index < 0 ) ceil(-index.toFloat() / data.size).toInt() else 0
        return data[(index + n * data.size) % data.size].v
    }

    fun move(entry: LongCircArrayEntry, count: Long) {
        // no movement
        if (count == 0L || count % linkCount == 0L) return

        val currentIndex = data.indexOf(entry)
        val newIndex = currentIndex + count
        val insertPosition = when {
            newIndex >= data.size -> newIndex % linkCount
            newIndex < 0 -> (newIndex % linkCount) + linkCount
            else -> newIndex
        }

        data.removeAt(currentIndex)
        data.add(insertPosition.toInt(), entry)
    }
}
