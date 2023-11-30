package net.fish.maths

import kotlin.math.ceil

data class CircularArray(val initialData: List<Long> = emptyList()) {
    private var data: MutableList<Long> = initialData.toMutableList()
    private var arrayIndex: Int = 0

    init {
        if (initialData.isEmpty()) { throw Exception("You must initialise with some data") }
    }

    fun sum(): Long = data.sum()

    private fun normalizeIndex(index: Int): Int {
        val n = if (index < 0 ) ceil(-index.toFloat() / data.size).toInt() else 0
        return (index + arrayIndex + n * data.size) % data.size
    }

    fun rotateLeft(count: Int = 1) {
        arrayIndex = (arrayIndex + count) % data.size
    }

    fun rotateRight(count: Int = 1) {
        val actualCount = count % data.size // remove chunks
        if (actualCount > arrayIndex) arrayIndex += data.size // account for potentially going negative and avoid it
        arrayIndex -= actualCount
    }

    operator fun get(index: Int): Long = data[normalizeIndex(index)]
    operator fun set(index: Int, value: Long) { data[normalizeIndex(index)] = value }

    fun add(index: Int, value: Long) {
        val addIndex = normalizeIndex(index)
        data.add(addIndex, value)
        if (addIndex < arrayIndex) arrayIndex++
    }

    fun add(value: Long) {
        add(0, value)
        arrayIndex++
    }

    // remove entry at index and return its value, like popping it out of the stack
    fun remove(index: Int): Long {
        val removeIndex = normalizeIndex(index)
        if (removeIndex < arrayIndex) arrayIndex--
        return data.removeAt(removeIndex)
    }

    // remove first, like popping top off stack
    fun remove(): Long {
        return remove(0)
    }

    fun toList(): List<Long> {
        // return the current order from the arrayIndex to the end with wrapping
        val theList = mutableListOf<Long>()
        (0 until data.size).forEach {
            theList.add(get(it))
        }
        return theList.toList()
    }
}