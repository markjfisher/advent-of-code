package net.fish

import net.fish.Direction.*
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


data class Point(val x: Int, val y: Int): Comparable<Point> {
    override fun compareTo(other: Point): Int {
        return if (y == other.y) x.compareTo(other.x) else y.compareTo(other.y)
    }

    fun plus(x: Int, y: Int) = Point(this.x + x, this.y + y)
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

    companion object {
        fun distance(a: Point, b: Point): Double =
            sqrt(((b.y - a.y) * (b.y - a.y) + (b.x - a.x) * (b.x - a.x)).toDouble())
    }
}