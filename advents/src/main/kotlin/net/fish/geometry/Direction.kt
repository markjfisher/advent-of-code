package net.fish.geometry

import java.lang.Exception
import java.lang.IllegalArgumentException

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    fun cw() = when(this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }

    fun ccw() = when(this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnR(degrees: Int) = when (degrees) {
        90 -> this.cw()
        180 -> this.cw().cw()
        270 -> this.ccw()
        else -> throw Exception("Unknown angle: $degrees")
    }

    fun turnL(degrees: Int) = turnR(360 - degrees)

    companion object {
        fun from(s: String) : Direction = from(s.first())
        fun from(char: Char) : Direction = when(char.toUpperCase()) {
            'N' -> NORTH
            'S' -> SOUTH
            'E' -> EAST
            'W' -> WEST

//            'R' -> EAST
//            'L' -> WEST
//            'U' -> NORTH
//            'D' -> SOUTH
            else -> throw IllegalArgumentException("Can't map $char to Direction")
        }

        fun from(c: Char, heading: Direction): Direction {
            val dir = c.toUpperCase()
            return if (dir == 'F') return heading else from(dir)
        }
    }
}