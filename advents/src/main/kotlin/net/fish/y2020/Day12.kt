package net.fish.y2020

import net.fish.Day
import net.fish.Direction
import net.fish.PathPositions
import net.fish.addDown
import net.fish.addLeft
import net.fish.addRight
import net.fish.addUp
import net.fish.manhattenDistance
import net.fish.resourceLines

object Day12 : Day {
    private val instructions = resourceLines(2020, 12)

    override fun part1() = manhattenDistance(convertInstructionsToPathP1(instructions).last())
    override fun part2() = manhattenDistance(convertInstructionsToPathP2(instructions).last())

    fun convertInstructionsToPathP1(directions: List<String>, initialHeading: Direction = Direction.EAST): PathPositions {
        // Starting at 0,0 accumulate the coordinates that the directions touch in an infinite grid
        var currentHeading = initialHeading
        var currentPosition = Pair(0, 0)
        val coordinates = mutableListOf<Pair<Int, Int>>()
        coordinates.add(currentPosition)
        directions.forEach { direction ->
            val count = direction.substring(1).toInt()
            currentPosition = when (direction[0]) {
                'N' -> addUp(currentPosition, coordinates, count)
                'E' -> addRight(currentPosition, coordinates, count)
                'S' -> addDown(currentPosition, coordinates, count)
                'W' -> addLeft(currentPosition, coordinates, count)
                'L' -> {
                    currentHeading = currentHeading.turnL(count)
                    currentPosition
                }
                'R' -> {
                    currentHeading = currentHeading.turnR(count)
                    currentPosition
                }
                'F' -> {
                    when (currentHeading) {
                        Direction.NORTH -> addUp(currentPosition, coordinates, count)
                        Direction.EAST -> addRight(currentPosition, coordinates, count)
                        Direction.SOUTH -> addDown(currentPosition, coordinates, count)
                        Direction.WEST -> addLeft(currentPosition, coordinates, count)
                    }
                }
                else -> throw Exception("Unknown direction: $direction")
            }
        }
        return coordinates.toList()
    }

    fun convertInstructionsToPathP2(directions: List<String>, initialWayPoint: Pair<Int, Int> = Pair(10, 1)): PathPositions {
        var wayPoint = initialWayPoint
        var currentPosition = Pair(0, 0)
        val coordinates = mutableListOf<Pair<Int, Int>>()
        coordinates.add(currentPosition)
        directions.forEach { direction ->
            val count = direction.substring(1).toInt()
            when (direction[0]) {
                'N' -> wayPoint = Pair(wayPoint.first, wayPoint.second + count)
                'E' -> wayPoint = Pair(wayPoint.first + count, wayPoint.second)
                'S' -> wayPoint = Pair(wayPoint.first, wayPoint.second - count)
                'W' -> wayPoint = Pair(wayPoint.first - count, wayPoint.second)
                'L' -> wayPoint = rotateWaypoint(count, wayPoint)
                'R' -> wayPoint = rotateWaypoint(360 - count, wayPoint)
                'F' -> currentPosition = move(count, currentPosition, wayPoint, coordinates)
                else -> throw Exception("Unknown direction: $direction")
            }
        }
        return coordinates.toList()
    }

    private fun move(length: Int, currentPosition: Pair<Int, Int>, wayPoint: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>): Pair<Int, Int> {
        var movingLocation = currentPosition
        (0 until length).forEach {
            movingLocation = Pair(movingLocation.first + wayPoint.first, movingLocation.second + wayPoint.second)
            coordinates.add(movingLocation)
        }
        return movingLocation
    }

    private fun rotateWaypoint(degrees: Int, wayPoint: Pair<Int, Int>) = when (degrees) {
        90 -> Pair(-wayPoint.second, wayPoint.first)
        180 -> Pair(-wayPoint.first, -wayPoint.second)
        270 -> Pair(wayPoint.second, -wayPoint.first)
        else -> throw Exception("Unknown angle $degrees")
    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
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