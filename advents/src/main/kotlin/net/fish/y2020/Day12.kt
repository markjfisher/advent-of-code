package net.fish.y2020

import net.fish.Day
import net.fish.Direction
import net.fish.Direction.EAST
import net.fish.Direction.NORTH
import net.fish.Direction.SOUTH
import net.fish.Direction.WEST
import net.fish.PathPositions
import net.fish.manhattenDistance
import net.fish.move
import net.fish.resourceLines

object Day12 : Day {
    private val instructions = resourceLines(2020, 12)

    override fun part1() = manhattenDistance(toPathP1(instructions).last())
    override fun part2() = manhattenDistance(toPathP2(instructions).last())

    fun toPathP1(directions: List<String>, initialHeading: Direction = EAST): PathPositions {
        val ferry = FerryP1(Pair(0, 0), initialHeading, mutableListOf(Pair(0, 0)))
        return directions.fold(ferry, FerryP1::transform).locationHistory.toList()
    }

    fun toPathP2(directions: List<String>, initialWayPoint: Pair<Int, Int> = Pair(10, 1)): PathPositions {
        val ferry = FerryP2(Pair(0, 0), initialWayPoint, mutableListOf(Pair(0, 0)))
        return directions.fold(ferry, FerryP2::transform).locationHistory.toList()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}

data class FerryP1(
    val position: Pair<Int, Int>,
    val heading: Direction,
    val locationHistory: List<Pair<Int, Int>>
) {
    fun transform(instruction: String): FerryP1 {
        var dc = instruction[0]
        if (dc == 'F') dc = heading.name.first()
        val count = instruction.substring(1).toInt()
        return when (dc) {
            'N' -> moveFerry(count, NORTH)
            'E' -> moveFerry(count, EAST)
            'S' -> moveFerry(count, SOUTH)
            'W' -> moveFerry(count, WEST)
            'R' -> changeHeading(count)
            'L' -> changeHeading(360-count)
            else -> throw Exception("Unknown direction instruction: $instruction")
        }
    }

    private fun moveFerry(count: Int, direction: Direction): FerryP1 {
        val newHistory = locationHistory.toMutableList()
        val newPosition = when(direction) {
            NORTH -> move(position, count, Pair(0, 1), newHistory)
            EAST -> move(position, count, Pair(1, 0), newHistory)
            SOUTH -> move(position, count, Pair(0, -1), newHistory)
            WEST -> move(position, count, Pair(-1, 0), newHistory)
        }
        return FerryP1(newPosition, heading, newHistory.toList())
    }

    private fun changeHeading(degrees: Int): FerryP1 {
        val newHeading = when(degrees) {
            90 -> heading.cw()
            180 -> heading.cw().cw()
            270 -> heading.ccw()
            else -> throw Exception("Unknown angle $degrees")
        }
        return FerryP1(position, newHeading, locationHistory)
    }
}

data class FerryP2(
    val position: Pair<Int, Int>,
    val wayPoint: Pair<Int, Int>,
    val locationHistory: List<Pair<Int, Int>>
) {
    fun transform(instruction: String): FerryP2 {
        val value = instruction.substring(1).toInt()
        return when (instruction[0]) {
            'N' -> moveWaypoint(value, NORTH)
            'E' -> moveWaypoint(value, EAST)
            'S' -> moveWaypoint(value, SOUTH)
            'W' -> moveWaypoint(value, WEST)
            'L' -> rotateWaypoint(value)
            'R' -> rotateWaypoint(360 - value)
            'F' -> forward(value)
            else -> throw Exception("Unknown instruction: $instruction")
        }
    }

    private fun forward(value: Int): FerryP2 {
        var movingLocation = position
        val newHistory = locationHistory.toMutableList()
        (0 until value).forEach {
            movingLocation = Pair(movingLocation.first + wayPoint.first, movingLocation.second + wayPoint.second)
            newHistory.add(movingLocation)
        }
        return FerryP2(movingLocation, wayPoint, newHistory.toList())
    }

    private fun moveWaypoint(value: Int, direction: Direction): FerryP2 {
        val newWaypoint = when (direction) {
            NORTH -> Pair(wayPoint.first, wayPoint.second + value)
            EAST  -> Pair(wayPoint.first + value, wayPoint.second)
            SOUTH -> Pair(wayPoint.first, wayPoint.second - value)
            WEST -> Pair(wayPoint.first - value, wayPoint.second)
        }
        return FerryP2(position, newWaypoint, locationHistory)
    }

    private fun rotateWaypoint(degrees: Int): FerryP2 {
        val newWaypoint = when (degrees) {
            90 -> Pair(-wayPoint.second, wayPoint.first)
            180 -> Pair(-wayPoint.first, -wayPoint.second)
            270 -> Pair(wayPoint.second, -wayPoint.first)
            else -> throw Exception("Unknown angle $degrees")
        }
        return FerryP2(position, newWaypoint, locationHistory)
    }

}

/*
Your ferry made decent progress toward the island, but the storm came in faster than anyone expected.
The ferry needs to take evasive actions!

Unfortunately, the ship's navigation computer seems to be malfunctioning; rather than giving a route
directly to safety, it produced extremely circuitous instructions. When the captain uses the PA system
to ask if anyone can help, you quickly volunteer.

The navigation instructions (your puzzle input) consists of a sequence of single-character actions
paired with integer input values. After staring at them for a few minutes, you work out what they
probably mean:

Action N means to move north by the given value.
Action S means to move south by the given value.
Action E means to move east by the given value.
Action W means to move west by the given value.
Action L means to turn left the given number of degrees.
Action R means to turn right the given number of degrees.
Action F means to move forward by the given value in the direction the ship is currently facing.

The ship starts by facing east. Only the L and R actions change the direction the ship is facing.
(That is, if the ship is facing east and the next instruction is N10, the ship would move north 10 units,
but would still move east if the following action were F.)

For example:

F10
N3
F7
R90
F11

These instructions would be handled as follows:

F10 would move the ship 10 units east (because the ship starts by facing east) to east 10, north 0.
N3 would move the ship 3 units north to east 10, north 3.
F7 would move the ship another 7 units east (because the ship is still facing east) to east 17, north 3.
R90 would cause the ship to turn right by 90 degrees and face south; it remains at east 17, north 3.
F11 would move the ship 11 units south to east 17, south 8.

At the end of these instructions, the ship's Manhattan distance (sum of the absolute values of its east/west position and its north/south position) from its starting position is 17 + 8 = 25.

Figure out where the navigation instructions lead. What is the Manhattan distance between that location and the ship's starting position?


 */

/*
Before you can give the destination to the captain, you realize that the actual action meanings were printed on the
back of the instructions the whole time.

Almost all of the actions indicate how to move a waypoint which is relative to the ship's position:

Action N means to move the waypoint north by the given value.
Action S means to move the waypoint south by the given value.
Action E means to move the waypoint east by the given value.
Action W means to move the waypoint west by the given value.
Action L means to rotate the waypoint around the ship left (counter-clockwise) the given number of degrees.
Action R means to rotate the waypoint around the ship right (clockwise) the given number of degrees.
Action F means to move forward to the waypoint a number of times equal to the given value.

The waypoint starts 10 units east and 1 unit north relative to the ship. The waypoint is relative to the ship;
that is, if the ship moves, the waypoint moves with it.

For example, using the same instructions as above:

F10 moves the ship to the waypoint 10 times (a total of 100 units east and 10 units north), leaving the ship at east 100, north 10.
  The waypoint stays 10 units east and 1 unit north of the ship.
N3 moves the waypoint 3 units north to 10 units east and 4 units north of the ship.
  The ship remains at east 100, north 10.
F7 moves the ship to the waypoint 7 times (a total of 70 units east and 28 units north), leaving the ship at east 170, north 38.
  The waypoint stays 10 units east and 4 units north of the ship.
R90 rotates the waypoint around the ship clockwise 90 degrees, moving it to 4 units east and 10 units south of the ship.
  The ship remains at east 170, north 38.
F11 moves the ship to the waypoint 11 times (a total of 44 units east and 110 units south), leaving the ship at east 214, south 72.
  The waypoint stays 4 units east and 10 units south of the ship.
After these operations, the ship's Manhattan distance from its starting position is 214 + 72 = 286.

Figure out where the navigation instructions actually lead. What is the Manhattan distance between that location and the ship's starting position?
 */