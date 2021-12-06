package net.fish.maths

data class CircularArray(
    val initialData: List<Long> = emptyList()
) {
    private var data: MutableList<Long> = initialData.toMutableList()

    init {
        if (initialData.isEmpty()) {
            throw Exception("You must initialise with some data")
        }
    }

    fun rotateLeft() {
        val zero = data[0]
        val newList = data.drop(1).toMutableList()
        newList.add(zero)
        data = newList
    }

    operator fun get(index: Int): Long = data[index]
    operator fun set(index: Int, value: Long) {
        data[index] = value
    }

    fun sum(): Long {
        return data.sum()
    }

}