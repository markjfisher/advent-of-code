package net.fish.maths

data class CircularArray(val size: Int) {
    private val data = Array(size) {0L}.toMutableList()
    fun shift() {
        val zero = data[0]
        data.drop(1)
        data[size - 1] = zero
    }
    operator fun get(index: Int): Long = data[index]
    operator fun set(index: Int, value: Long) {
        data[index] = value
    }

}