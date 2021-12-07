package net.fish.maths

data class CircularArray(val initialData: List<Long> = emptyList()) {
    private var data: MutableList<Long> = initialData.toMutableList()
    private var arrayIndex: Int = 0

    init {
        if (initialData.isEmpty()) { throw Exception("You must initialise with some data") }
    }

    fun sum(): Long = data.sum()

    fun rotateLeft() {
        arrayIndex = (arrayIndex + 1) % data.size
    }

    operator fun get(index: Int): Long = data[(index + arrayIndex) % data.size]
    operator fun set(index: Int, value: Long) { data[(index + arrayIndex) % data.size] = value }

}