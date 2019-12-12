package net.fish

import java.lang.Exception

/*
The gravity assist was successful, and you're well on your way to the Venus refuelling station. During the rush back
on Earth, the fuel management system wasn't completely installed, so that's next on the priority list.

Opening the front panel reveals a jumble of wires. Specifically, two wires are connected to a central port and extend
outward on a grid. You trace the path each wire takes as it leaves the central port, one wire per line of text
(your puzzle input).

The wires twist and turn, but the two wires occasionally cross paths. To fix the circuit, you need to find the
intersection point closest to the central port. Because the wires are on a grid, use the Manhattan distance for
this measurement. While the wires do technically cross right at the central port where they both start, this point
does not count, nor does a wire count as crossing with itself.

For example, if the first wire's path is R8,U5,L5,D3, then starting from the central port (o),
it goes right 8, up 5, left 5, and finally down 3:

...........
...........
...........
....+----+.
....|....|.
....|....|.
....|....|.
.........|.
.o-------+.
...........
Then, if the second wire's path is U7,R6,D4,L4, it goes up 7, right 6, down 4, and left 4:

...........
.+-----+...
.|.....|...
.|..+--X-+.
.|..|..|.|.
.|.-X--+.|.
.|..|....|.
.|.......|.
.o-------+.
...........
These wires cross at two locations (marked X), but the lower-left one is closer to the central port:
its distance is 3 + 3 = 6.

Here are a few more examples:

R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83 = distance 159
R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = distance 135
What is the Manhattan distance from the central port to the closest intersection?
*/

open class Day3Base {

    fun findIntersections(points1: List<Pair<Int, Int>>, points2: List<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return points1.intersect(points2)
    }

    fun convertWirePathsToCoordinates(directions: List<String>): List<Pair<Int, Int>> {
        // Starting at 0,0 accumulate the coordinates that the directions touch in an infinite grid
        val coordinates = mutableListOf<Pair<Int, Int>>()
        var currentPosition = Pair(0, 0)
        directions.forEach { direction ->
            val count = direction.substring(1).toInt()
            currentPosition = when (direction[0]) {
                'D' -> addDown(currentPosition, coordinates, count)
                'U' -> addUp(currentPosition, coordinates, count)
                'L' -> addLeft(currentPosition, coordinates, count)
                'R' -> addRight(currentPosition, coordinates, count)
                else -> throw BadDirection("Unknown direction: $direction")
            }
        }
        return coordinates.toList()
    }

    fun addUp(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
        val range = (from.second + 1) until (from.second + length + 1)
        range.forEach { coordinates.add(Pair(from.first, it)) }
        return Pair(from.first, from.second + length)
    }

    fun addDown(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
        val progression = (from.second - 1) downTo (from.second - length)
        progression.forEach { coordinates.add(Pair(from.first, it)) }
        return Pair(from.first, from.second - length)
    }

    fun addRight(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
        val range = (from.first + 1) .. (from.first + length)
        range.forEach { coordinates.add(Pair(it, from.second)) }
        return Pair(from.first + length, from.second)
    }

    fun addLeft(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
        val range = (from.first - 1) downTo from.first - length
        range.forEach { coordinates.add(Pair(it, from.second)) }
        return Pair(from.first - length, from.second)
    }

    class BadDirection(message: String): Exception(message)
}