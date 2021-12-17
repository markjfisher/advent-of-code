package net.fish.geometry

import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import kotlin.math.atan2
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int): Comparable<Point> {
    override fun compareTo(other: Point): Int {
        return if (y == other.y) x.compareTo(other.x) else y.compareTo(other.y)
    }

    fun plus(x: Int, y: Int) = Point(this.x + x, this.y + y)
    fun plus(x: Double, y: Double) = Point(this.x + x.toInt(), this.y + y.toInt())
    fun plus(x: Float, y: Float) = Point(this.x + x.toInt(), this.y + y.toInt())
    operator fun plus(other: Pair<Int, Int>) = Point(x + other.first, y + other.second)
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun plus(direction: Direction) = when(direction) {
        NORTH -> Point(x, y - 1)
        EAST -> Point(x + 1, y)
        SOUTH -> Point(x, y + 1)
        WEST -> Point(x - 1, y)
    }

    fun angle(target: Point): Double {
        return atan2((target.y - y).toDouble(), (target.x - x).toDouble())
    }

    fun distance(other: Point): Double = distance(this, other)

    fun directionTo(other: Point) = when {
        other.x == this.x && other.y < this.y -> NORTH
        other.x == this.x && other.y > this.y -> SOUTH
        other.y == this.y && other.x < this.x -> WEST
        other.y == this.y && other.x > this.x -> EAST
        else -> throw IllegalArgumentException("No Simple direction between $this and $other")
    }

    companion object {
        fun distance(a: Point, b: Point): Double =
            sqrt(((b.y - a.y) * (b.y - a.y) + (b.x - a.x) * (b.x - a.x)).toDouble())
    }

    fun within(bounds: Pair<Point, Point>): Boolean {
        return x >= bounds.first.x && x <= bounds.second.x && y >= bounds.first.y && y <= bounds.second.y
    }
}

fun Collection<Point>.minX() = this.map { it.x }.minOrNull()
fun Collection<Point>.minY() = this.map { it.y }.minOrNull()
fun Collection<Point>.maxX() = this.map { it.x }.maxOrNull()
fun Collection<Point>.maxY() = this.map { it.y }.maxOrNull()
fun Collection<Point>.bounds() =
    this.fold(listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)) { list, point ->
        listOf(
            kotlin.math.min(list[0], point.x),
            kotlin.math.min(list[1], point.y),
            kotlin.math.max(list[2], point.x),
            kotlin.math.max(list[3], point.y)
        )
    }.let { (minX, minY, maxX, maxY) ->
        Point(minX, minY) to Point(maxX, maxY)
    }