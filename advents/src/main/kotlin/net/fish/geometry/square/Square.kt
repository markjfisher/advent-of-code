package net.fish.geometry.square

import net.fish.geometry.grid.GridItem
import kotlin.math.abs

data class Square(
    val x: Int,
    val y: Int,
    val constrainer: SquareConstrainer = DefaultSquareConstrainer()
): GridItem {
    operator fun plus(other: Square) = add(other)
    operator fun minus(other: Square) = subtract(other)
    operator fun times(k: Int) = scale(k)

    fun manhattenDistance() = abs(x) + abs(y)

    override fun simpleValue(): String {
        return String.format("Sq[%d, %d]", x, y)
    }

    fun add(other: Square): Square? = constrainer.constrain(Square(x + other.x, y + other.y, constrainer))
    fun subtract(other: Square): Square? = constrainer.constrain(Square(x - other.x, y - other.y, constrainer))
    fun scale(k: Int): Square? = constrainer.constrain(Square(x * k, y * k, constrainer))

    fun neighbour(d: Int) = constrainer.constrain(this + direction(d))
    override fun neighbours(): List<Square> = directions.mapNotNull { this + it }
    override fun cardinals(): List<Square> = cardinalPoints.mapNotNull { this + it }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Square
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + constrainer.hashCode()
        return result
    }

    companion object {
        val directions = listOf(Square(1, 0), Square(1, 1), Square(0, 1), Square(-1, 1), Square(-1, 0), Square(-1, -1), Square(0, -1), Square(1, -1))
        val cardinalPoints = listOf(Square(1, 0), Square(0, 1), Square(-1, 0), Square(0, -1))
        fun direction(d: Int) = directions[d]
    }
}
