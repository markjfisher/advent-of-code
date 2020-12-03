package net.fish.y2020

import com.marcinmoskala.math.product
import net.fish.Day
import net.fish.resourceLines

object Day03 : Day {
    private val forestData = resourceLines(2020, 3)

    val part1Runs = listOf(Delta(3, 1))
    val part2Runs = listOf(Delta(1, 1), Delta(3, 1), Delta(5, 1), Delta(7, 1), Delta(1, 2))

    override fun part1() = traverseForest(forestData, part1Runs)
    override fun part2() = traverseForest(forestData, part2Runs)

    fun traverseForest(forestData: List<String>, runs: List<Delta>): Long = runs
        .map { delta -> skiSequence(forestData, delta).count(Square::isTree) }
        .product()

    fun skiSequence(data: List<String>, delta: Delta): Sequence<Square> =
        generateLocationSequence(delta, data.first().length, data.size)
            .map { (x, y) -> if (data[y][x] == '#') Square.TREE else Square.GAP }

    fun generateLocationSequence(delta: Delta, width: Int, maxRows: Int): Sequence<Location> =
        generateSequence(Location(0, 0)) { current -> current.move(delta, width).takeIf { it.y < maxRows } }

    enum class Square {
        GAP, TREE;

        fun isTree() = this == TREE
    }

    data class Location(val x: Int, val y: Int) {
        // This should really be in a "Mover" class which can hold the data width, but do I really need to go that far... no.
        fun move(delta: Delta, width: Int): Location {
            return Location((x + delta.dx) % width, y + delta.dy)
        }
    }

    data class Delta(val dx: Int, val dy: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

/*
PART 1
With the toboggan login problems resolved, you set off toward the airport. While travel by toboggan might be easy,
it's certainly not safe: there's very minimal steering and the area is covered in trees. You'll need to see which
angles will take you near the fewest trees.

Due to the local geology, trees in this area only grow on exact integer coordinates in a grid. You make a map (your
puzzle input) of the open squares (.) and trees (#) you can see. For example:

..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#
These aren't the only trees, though; due to something you read about once involving arboreal genetics and biome
stability, the same pattern repeats to the right many times:

..##.........##.........##.........##.........##.........##.......  --->
#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....#..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..#...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.##.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........#.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...##....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#  --->
You start on the open square (.) in the top-left corner and need to reach the bottom (below the bottom-most
row on your map).

The toboggan can only follow a few specific slopes (you opted for a cheaper model that prefers rational numbers);
start by counting all the trees you would encounter for the slope right 3, down 1:

From your starting position at the top-left, check the position that is right 3 and down 1. Then, check the position
that is right 3 and down 1 from there, and so on until you go past the bottom of the map.

The locations you'd check in the above example are marked here with O where there was an open square and X where
there was a tree:

..##.........##.........##.........##.........##.........##.......  --->
#..O#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....X..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#O#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..X...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.X#.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#.O..#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........X.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.X#...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...#X....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...X.#.#..#...#.#.#..#...#.#.#..#...#.#  --->
In this example, traversing the map using this slope would cause you to encounter 7 trees.

Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?


 */

/*
PART 2

Time to check the rest of the slopes - you need to minimize the probability of a sudden arboreal stop, after all.

Determine the number of trees you would encounter if, for each of the following slopes, you start at the top-left
corner and traverse the map all the way to the bottom:

Right 1, down 1.
Right 3, down 1. (This is the slope you already checked.)
Right 5, down 1.
Right 7, down 1.
Right 1, down 2.
In the above example, these slopes would find 2, 7, 3, 4, and 2 tree(s) respectively; multiplied together, these produce the answer 336.

What do you get if you multiply together the number of trees encountered on each of the listed slopes?
 */