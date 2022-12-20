package net.fish.y2019

import net.fish.Day
import net.fish.geometry.PathPositions
import net.fish.geometry.findIntersections
import net.fish.geometry.move
import net.fish.resourceLines
import net.fish.geometry.stepsTo
import net.fish.geometry.wireManhattanDistance
import net.fish.y2022.Day20

object Day03 : Day {
    override val warmUps: Int = 0
    private val wireData by lazy {
        resourceLines(2019, 3)
            .map { wire -> wire.split(",") }
            .map { convertWirePathsToCoordinates(it) }
    }

    init {
        if (wireData.size != 2) throw Exception("Bad wire data")
    }

    override fun part1()= wireManhattanDistance(wireData[0], wireData[1])
    override fun part2()= minimumSignalDelay(wireData[0], wireData[1])

    fun convertWirePathsToCoordinates(directions: List<String>): PathPositions {
        // Starting at 0,0 accumulate the coordinates that the directions touch in an infinite grid
        val coordinates = mutableListOf<Pair<Int, Int>>()
        var currentPosition = Pair(0, 0)
        directions.forEach { direction ->
            val count = direction.substring(1).toInt()
            currentPosition = when (direction[0]) {
                'D' -> move(currentPosition, count, Pair(0, -1), coordinates)
                'U' -> move(currentPosition, count, Pair(0, 1), coordinates)
                'L' -> move(currentPosition, count, Pair(-1, 0), coordinates)
                'R' -> move(currentPosition, count, Pair(1, 0), coordinates)
                else -> throw Exception("Unknown direction: $direction")
            }
        }
        return coordinates.toList()
    }


    fun minimumSignalDelay(path1: PathPositions, path2: PathPositions): Int {
        return findIntersections(path1, path2)
            .map { intersection -> stepsTo(intersection, path1) + stepsTo(intersection, path2) }
            .minOrNull() ?: 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

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

/*
It turns out that this circuit is very timing-sensitive; you actually need to minimize the signal delay.

To do this, calculate the number of steps each wire takes to reach each intersection; choose the intersection where
the sum of both wires' steps is lowest. If a wire visits a position on the grid multiple times, use the steps value
from the first time it visits that position when calculating the total value of a specific intersection.

The number of steps a wire takes is the total number of grid squares the wire has entered to get to that location,
including the intersection being considered. Again consider the example from above:

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
In the above example, the intersection closest to the central port is reached after
8+5+5+2 = 20 steps by the first wire and
7+6+4+3 = 20 steps by the second wire for a total of 20+20 = 40 steps.

However, the top-right intersection is better:
the first wire takes only 8+5+2 = 15 and
the second wire takes only 7+6+2 = 15, a total of 15+15 = 30 steps.

Here are the best steps for the extra examples from above:

R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83 = 610 steps
R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = 410 steps
What is the fewest combined steps the wires must take to reach an intersection?
*/
