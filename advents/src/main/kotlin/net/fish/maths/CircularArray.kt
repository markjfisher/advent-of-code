package net.fish.maths

data class CircularArray(val initialData: List<Long> = emptyList()) {
    private var data: MutableList<Long> = initialData.toMutableList()

    init {
        if (initialData.isEmpty()) { throw Exception("You must initialise with some data") }
    }

    fun sum(): Long = data.sum()

    fun rotateLeft() {
        data = (data.drop(1) + data.first()).toMutableList()
    }

    operator fun get(index: Int): Long = data[index]
    operator fun set(index: Int, value: Long) { data[index] = value }

}