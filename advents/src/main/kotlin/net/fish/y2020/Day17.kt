package net.fish.y2020

import net.fish.AroundSpace
import net.fish.Day
import net.fish.resourceLines

object Day17 : Day {
    private val data = resourceLines(2020, 17)

    fun toCube(data: List<String>, dimensions: Int): ConwayCube {
        val cube = ConwayCube(dimensions = dimensions)
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, v ->
                if (v == '#') {
                    val coordinates = generateSequence { 0 }.take(dimensions).toMutableList().also { it[0] = x; it[1] = y }
                    cube.add(CCLocation(coordinates))
                }
            }
        }
        return cube
    }

    override fun part1() = runPuzzle(data, 3)
    override fun part2() = runPuzzle(data, 4)

    fun runPuzzle(data: List<String>, dimensions: Int): Int {
        val cube = toCube(data, dimensions)
        cube.run(6)
        return cube.grid.size
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

data class ConwayCube(
    // This contains only locations of active values
    var grid: MutableSet<CCLocation> = mutableSetOf(),
    val dimensions: Int = 3
) {
    // this generates a list of CCLocations at all coordinates around the origin for any dimension
    // e.g. [-1, -1, -1], [-1, -1, 0], ... in 3 dimensions
    private val neighbourRelativeLocations = AroundSpace(dimensions).map { CCLocation(it) }

    // return a list of all locations around the given point
    fun locationsAround(loc: CCLocation) = neighbourRelativeLocations.map { loc.add(it) }

    fun add(loc: CCLocation) = grid.add(loc)
    fun isActive(loc: CCLocation) = grid.contains(loc)

    fun step() {
        // find all locations touching anything active. we don't need to consider anything further than 1 position away
        val allTouchingLocations = grid.flatMap { locationsAround(it) }.toSet()

        // recalculate the grid
        grid = allTouchingLocations.fold(mutableSetOf()) { g, location ->
            val neighboursCount = locationsAround(location)
                .filter { it != location && isActive(it) }
                .count()

            when {
                (neighboursCount == 2 || neighboursCount == 3) && isActive(location) -> g.add(location)
                (neighboursCount == 3) && !isActive(location) -> g.add(location)
            }
            g
        }
    }

    fun run(iterations: Int) {
        (0 until iterations).forEach {
            step()
        }
    }
}

data class CCLocation(
    val coordinates: List<Int>
) {
    fun add(loc: CCLocation) : CCLocation {
        return CCLocation(loc.coordinates.mapIndexed { i, value -> value + coordinates[i] })
    }
}

/*
--- Day 17: Conway Cubes ---
As your flight slowly drifts through the sky, the Elves at the Mythical Information Bureau
at the North Pole contact you. They'd like some help debugging a malfunctioning experimental
energy source aboard one of their super-secret imaging satellites.

The experimental energy source is based on cutting-edge technology: a set of Conway Cubes
contained in a pocket dimension! When you hear it's having problems, you can't help but agree
to take a look.

The pocket dimension contains an infinite 3-dimensional grid. At every integer 3-dimensional
coordinate (x,y,z), there exists a single cube which is either active or inactive.

In the initial state of the pocket dimension, almost all cubes start inactive. The only
exception to this is a small flat region of cubes (your puzzle input); the cubes in this
region start in the specified active (#) or inactive (.) state.

The energy source then proceeds to boot up by executing six cycles.

Each cube only ever considers its neighbors: any of the 26 other cubes where any of their
coordinates differ by at most 1. For example, given the cube at x=1,y=2,z=3, its neighbors
include the cube at x=2,y=2,z=2, the cube at x=0,y=2,z=3, and so on.

During a cycle, all cubes simultaneously change their state according to the following rules:

If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains active.
Otherwise, the cube becomes inactive.

If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active.
Otherwise, the cube remains inactive.

The engineers responsible for this experimental energy source would like you to simulate
the pocket dimension and determine what the configuration of cubes should be at the end of
the six-cycle boot process.

For example, consider the following initial state:

.#.
..#
###

Even though the pocket dimension is 3-dimensional, this initial state represents a small 2-dimensional
slice of it. (In particular, this initial state defines a 3x3x1 region of the 3-dimensional space.)

Simulating a few cycles from this initial state produces the following configurations, where the result
of each cycle is shown layer-by-layer at each given z coordinate (and the frame of view follows the
active cells in each cycle):

Before any cycles:

z=0
.#.
..#
###


After 1 cycle:

z=-1
#..
..#
.#.

z=0
#.#
.##
.#.

z=1
#..
..#
.#.


After 2 cycles:

z=-2
.....
.....
..#..
.....
.....

z=-1
..#..
.#..#
....#
.#...
.....

z=0
##...
##...
#....
....#
.###.

z=1
..#..
.#..#
....#
.#...
.....

z=2
.....
.....
..#..
.....
.....


After 3 cycles:

z=-2
.......
.......
..##...
..###..
.......
.......
.......

z=-1
..#....
...#...
#......
.....##
.#...#.
..#.#..
...#...

z=0
...#...
.......
#......
.......
.....##
.##.#..
...#...

z=1
..#....
...#...
#......
.....##
.#...#.
..#.#..
...#...

z=2
.......
.......
..##...
..###..
.......
.......
.......

After the full six-cycle boot process completes, 112 cubes are left in the active state.

Starting with your given initial configuration, simulate six cycles.
How many cubes are left in the active state after the sixth cycle?
 */

/*
For some reason, your simulated results don't match what the experimental energy source
engineers expected. Apparently, the pocket dimension actually has four spatial dimensions, not three.

The pocket dimension contains an infinite 4-dimensional grid. At every integer 4-dimensional coordinate
(x,y,z,w), there exists a single cube (really, a hypercube) which is still either active or inactive.

Each cube only ever considers its neighbors: any of the 80 other cubes where any of their coordinates
differ by at most 1. For example, given the cube at x=1,y=2,z=3,w=4, its neighbors include the cube
at x=2,y=2,z=3,w=3, the cube at x=0,y=2,z=3,w=4, and so on.

The initial state of the pocket dimension still consists of a small flat region of cubes. Furthermore,
the same rules for cycle updating still apply: during each cycle, consider the number of active
neighbors of each cube.

For example, consider the same initial state as in the example above. Even though the pocket dimension
is 4-dimensional, this initial state represents a small 2-dimensional slice of it. (In particular,
this initial state defines a 3x3x1x1 region of the 4-dimensional space.)

Simulating a few cycles from this initial state produces the following configurations, where the result
of each cycle is shown layer-by-layer at each given z and w coordinate:

Before any cycles:

z=0, w=0
.#.
..#
###


After 1 cycle:

z=-1, w=-1
#..
..#
.#.

z=0, w=-1
#..
..#
.#.

z=1, w=-1
#..
..#
.#.

z=-1, w=0
#..
..#
.#.

z=0, w=0
#.#
.##
.#.

z=1, w=0
#..
..#
.#.

z=-1, w=1
#..
..#
.#.

z=0, w=1
#..
..#
.#.

z=1, w=1
#..
..#
.#.


After 2 cycles:

z=-2, w=-2
.....
.....
..#..
.....
.....

z=-1, w=-2
.....
.....
.....
.....
.....

z=0, w=-2
###..
##.##
#...#
.#..#
.###.

z=1, w=-2
.....
.....
.....
.....
.....

z=2, w=-2
.....
.....
..#..
.....
.....

z=-2, w=-1
.....
.....
.....
.....
.....

z=-1, w=-1
.....
.....
.....
.....
.....

z=0, w=-1
.....
.....
.....
.....
.....

z=1, w=-1
.....
.....
.....
.....
.....

z=2, w=-1
.....
.....
.....
.....
.....

z=-2, w=0
###..
##.##
#...#
.#..#
.###.

z=-1, w=0
.....
.....
.....
.....
.....

z=0, w=0
.....
.....
.....
.....
.....

z=1, w=0
.....
.....
.....
.....
.....

z=2, w=0
###..
##.##
#...#
.#..#
.###.

z=-2, w=1
.....
.....
.....
.....
.....

z=-1, w=1
.....
.....
.....
.....
.....

z=0, w=1
.....
.....
.....
.....
.....

z=1, w=1
.....
.....
.....
.....
.....

z=2, w=1
.....
.....
.....
.....
.....

z=-2, w=2
.....
.....
..#..
.....
.....

z=-1, w=2
.....
.....
.....
.....
.....

z=0, w=2
###..
##.##
#...#
.#..#
.###.

z=1, w=2
.....
.....
.....
.....
.....

z=2, w=2
.....
.....
..#..
.....
.....
After the full six-cycle boot process completes, 848 cubes are left in the active state.

Starting with your given initial configuration, simulate six cycles in a 4-dimensional
space. How many cubes are left in the active state after the sixth cycle?



 */