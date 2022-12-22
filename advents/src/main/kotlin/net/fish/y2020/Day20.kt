package net.fish.y2020

import com.marcinmoskala.math.product
import mu.KotlinLogging
import net.fish.Day
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.maths.createArray
import net.fish.maths.flipMatrixByVertical
import net.fish.resourceStrings
import net.fish.maths.rotateMatrix

private val logger = KotlinLogging.logger { }

object Day20 : Day {
    private val tiles by lazy { toTiles(resourceStrings(2020, 20)) }
    lateinit var solution: Map<Point, TileSolution>

    fun toTiles(data: List<String>): List<Tile> {
        val idExtractor = Regex("""^Tile (\d+):$""")
        return data.map { jigBlock ->
            val jigBlockList = jigBlock.split("\n")
            val id = idExtractor.find(jigBlockList[0])?.destructured!!.let { (id) -> id.toInt() }
            Tile(id, Jig(jigBlockList.drop(1)))
        }
    }

    val monsterShape = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #
    """

    // Calculate the offset points of the monster markers
    val monsterPoints = monsterShape.split("\n").filter { it.isNotBlank() }
        .flatMapIndexed { j, line ->
            line.mapIndexedNotNull { i, c ->
                if (c == '#') Point(i, j) else null
            }
        }

    override fun part1() = doPart1(tiles)
    override fun part2() = doPart2()

    fun solvePuzzle(tiles: List<Tile>): Map<Point, TileSolution> {
        // Fix a first tile's first jig at 0,0 and work around it.
        val fixedValue = TileSolution(tiles[0].id, Point(0, 0), tiles[0].jigs[0])
        return findTileSolutions(mapOf(fixedValue.location to fixedValue), tiles.drop(1))
    }

    fun doPart1(tiles: List<Tile>): Long {
        solution = solvePuzzle(tiles)
        val minX = solution.keys.minByOrNull { it.x }!!.x
        val minY = solution.keys.minByOrNull { it.y }!!.y
        val maxX = solution.keys.maxByOrNull { it.x }!!.x
        val maxY = solution.keys.maxByOrNull { it.y }!!.y
        return listOf(
            solution[Point(minX, minY)]!!.tileId.toLong(),
            solution[Point(minX, maxY)]!!.tileId.toLong(),
            solution[Point(maxX, minY)]!!.tileId.toLong(),
            solution[Point(maxX, maxY)]!!.tileId.toLong()
        ).product()
    }

    fun doPart2(): Int {
        // get all the rotations of the monster jig
        val seaJigs = createMonsterJig().allJigs()

        val mapOfJigToCount = seaJigs.map { jig ->
            jig to countMonstersIn(jig)
        }

        val (jigWithMonsters, count) = mapOfJigToCount.firstOrNull { it.second > 0 } ?: throw Exception("No Monsters here captain!")
        val countOfSea = jigWithMonsters.data.joinToString("").count { it == '#' }
        return countOfSea - count * monsterPoints.size
    }

    fun monsterAt(location: Point, jig: Jig): Boolean {
        return monsterPoints.map { it + location }.all { testPoint ->
            testPoint.x < jig.matrix.size && testPoint.y < jig.matrix.size && jig.matrix[testPoint.y][testPoint.x] == '#'
        }
    }

    fun countMonstersIn(jig: Jig): Int {
        // look in the jig for any monsters.
        // We know the relative offsets of the monster points, so search anywhere in the jig where all points are set
        var found = 0
        for (y in 0 .. jig.matrix.size) {
            for (x in 0 .. jig.matrix.size) {
                if (monsterAt(Point(x, y), jig)) found++
            }
        }
        return found
    }

    private fun createMonsterJig(): Jig {
        // strip the solution into a new Jig to benefit from rotations
        val minPoint = Point(solution.keys.minByOrNull { it.x }!!.x, solution.keys.minByOrNull { it.y }!!.y)
        val maxPoint = Point(solution.keys.maxByOrNull { it.x }!!.x, solution.keys.maxByOrNull { it.y }!!.y)

        val pieceWidth = solution[minPoint]!!.jig.eE.length // any piece from the old solution to get the width of a cell
        // stripping 2 off per cell, * number of cells across
        val jigWidth = (pieceWidth - 2) * (maxPoint.x - minPoint.x + 1)

        val newMatrix = Pair(jigWidth, jigWidth).createArray('.')
        for (y in minPoint.y..maxPoint.y) {
            for (x in minPoint.x..maxPoint.x) {
                val jig = solution[Point(x, y)]!!.jig
                // remove outer edges, and place the data in the new array
                jig.data.drop(1).dropLast(1).forEachIndexed { iy, data ->
                    data.drop(1).dropLast(1).forEachIndexed { ix, c ->
                        val offX = (x - minPoint.x) * (pieceWidth - 2)
                        val offY = (y - minPoint.y) * (pieceWidth - 2)
                        newMatrix[iy + offY][ix + offX] = c
                    }
                }
            }
        }
        return Jig(newMatrix)
    }

    private fun findTileSolutions(solutionMap: Map<Point, TileSolution>, unmatchedTiles: List<Tile>): Map<Point, TileSolution> {
        logger.debug {
            val mdebug = solutionMap.map { it.key to it.value.tileId }
            "finding!\n solutionMap: $mdebug\nunmatchedTiles: ${unmatchedTiles.map{it.id}}"
        }
        if (unmatchedTiles.isEmpty()) return solutionMap

        // the offsets to any location we look at
        val aroundPositions = listOf(Point(-1, 0), Point(1, 0), Point(0, -1), Point(0, 1))

        // what spaces are available to attach to? e.g. initially will only have 1 position filled, 4 slots that can be filled
        val attachableLocations = solutionMap.keys.flatMap { aroundPositions.map { a -> a + it } }.filter { !solutionMap.containsKey(it) }
        logger.debug { " attachableLocations: $attachableLocations" }

        for(loc in attachableLocations) {
            logger.debug { "  checking loc: $loc" }
            // what are the known pieces around our location we are examining? (must be at least 1 as we are a free slot connected to something
            val attachedToLoc = aroundPositions.map { it + loc }.filter { solutionMap.containsKey(it) }.mapNotNull { solutionMap[it] }
            logger.debug { "    attached to loc: ${attachedToLoc.map { it.tileId }}" }

            // look through all available tiles finding one that "fits" (try all rotations/flips) in this location with all the neighbours
            var jigFit: Jig? = null
            val fittingTile = unmatchedTiles.firstOrNull { tile ->
                // Go through all jigs of this tile
                val matchingJig = tile.jigs.firstOrNull { jig ->
                    attachedToLoc.all { it.canAttachTo(jig, loc) }
                }

                // hmmm, feels dirty. reaching into the tile to find the jig that fit and put it into a var outside.
                // hopefully noone will read this.
                if (matchingJig != null) {
                    jigFit = matchingJig
                    logger.debug { "      !! found jig that will fit at $loc:\n${jigFit!!.jigString()}  in tile ${tile.id}" }
                }
                matchingJig != null
            }

            if (fittingTile != null) {
                val tileSolution = TileSolution(fittingTile.id, loc, jigFit!!)
                logger.debug { "    recursing with new solution: $loc -> $tileSolution" }
                // we have a jig and a location, so add it into the solution map and recurse
                return findTileSolutions(solutionMap + (loc to tileSolution), unmatchedTiles - fittingTile)
            }
        }

        return emptyMap()
    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

data class TileSolution(
    val tileId: Int,
    val location: Point,
    val jig: Jig // which rotation/flip fits solution
) {
    // Can the known point attach to a target jig at the given location
    fun canAttachTo(target: Jig, at: Point): Boolean {
        return target.jigsFitTogether(jig, at, location)
    }
}

// A tile is an ID with all the rotations of a Jig
data class Tile(
    val id: Int,
    val jigs: List<Jig>
) {
    constructor(id: Int, jig: Jig): this(id, jig.allJigs())
}

data class Jig(
    val data: List<String>
) {
    constructor(matrix: Array<Array<Char>>): this(matrix.map { it.joinToString("") })

    val matrix: Array<Array<Char>> = toMatrix()

    // A jig has 4 edges we will match on
    val eN = data.first()
    val eE = data.map { it.last() }.joinToString("")
    val eS = data.last()
    val eW = data.map { it.first() }.joinToString("")

    fun jigsFitTogether(other: Jig, myLocation: Point, targetLocation: Point): Boolean {
        logger.debug { "testing if jigs fit together from $myLocation to $targetLocation for:\n${this.jigString()}\n${other.jigString()}" }
        return when (myLocation.directionTo(targetLocation)) {
            NORTH -> eN == other.eS
            WEST -> eW == other.eE
            SOUTH -> eS == other.eN
            EAST -> eE == other.eW
        }
    }

    private fun toMatrix(): Array<Array<Char>> {
        check(data[0].length == data.size)
        var newMatrix = arrayOf<Array<Char>>()
        for (j in data.indices) {
            var row = arrayOf<Char>()
            for (i in data.indices) {
                row += data[j][i]
            }
            newMatrix += row
        }
        return newMatrix
    }

    fun jigString(): String {
        var output = ""
        for (j in data.indices) {
            for (i in data.indices) {
                output += matrix[j][i]
            }
            output += "\n"
        }
        return output
    }

    fun rotate(angle: Int): Jig {
        check(setOf(90, 180, 270).contains(angle))

        val rotations = angle / 90
        var newMatrix = rotateMatrix(matrix)
        (0 until (rotations - 1)).forEach { _ -> newMatrix = rotateMatrix(newMatrix) }
        val newData = mutableListOf<String>()
        for (i in newMatrix.indices) {
            newData.add(newMatrix[i].joinToString(""))
        }
        return Jig(newData)
    }

    fun flip(): Jig {
        val newData = flipMatrixByVertical(matrix).asList().map { it.joinToString("") }
        return Jig(newData)
    }

    fun allJigs(): List<Jig> = listOf(
        // 4 rotations normal
        this, rotate(90), rotate(180), rotate(270),
        // 4 rotatins flipped
        flip(), flip().rotate(90), flip().rotate(180), flip().rotate(270),
    )
}

/*
--- Day 20: Jurassic Jigsaw ---
The high-speed train leaves the forest and quickly carries you south. You can even see a desert
in the distance! Since you have some spare time, you might as well see if there was anything
interesting in the image the Mythical Information Bureau satellite captured.

After decoding the satellite messages, you discover that the data actually contains many small
images created by the satellite's camera array. The camera array consists of many cameras; rather
than produce a single square image, they produce many smaller square image tiles that need to be
reassembled back into a single image.

Each camera in the camera array returns a single monochrome image tile with a random unique
ID number. The tiles (your puzzle input) arrived in a random order.

Worse yet, the camera array appears to be malfunctioning: each image tile has been rotated
and flipped to a random orientation. Your first task is to reassemble the original image
by orienting the tiles so they fit together.

To show how the tiles should be reassembled, each tile's image data includes a border that
should line up exactly with its adjacent tiles. All tiles have this border, and the border
lines up exactly when the tiles are both oriented correctly. Tiles at the edge of the image
also have this border, but the outermost edges won't line up with any other tiles.

For example, suppose you have the following nine tiles:

Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###...

By rotating, flipping, and rearranging them, you can find a square arrangement that causes all adjacent
borders to line up:

#...##.#.. ..###..### #.#.#####.
..#.#..#.# ###...#.#. .#..######
.###....#. ..#....#.. ..#.......
###.##.##. .#.#.#..## ######....
.###.##### ##...#.### ####.#..#.
.##.#....# ##.##.###. .#...#.##.
#...###### ####.#...# #.#####.##
.....#..## #...##..#. ..#.###...
#.####...# ##..#..... ..#.......
#.##...##. ..##.#..#. ..#.###...

#.##...##. ..##.#..#. ..#.###...
##..#.##.. ..#..###.# ##.##....#
##.####... .#.####.#. ..#.###..#
####.#.#.. ...#.##### ###.#..###
.#.####... ...##..##. .######.##
.##..##.#. ....#...## #.#.#.#...
....#..#.# #.#.#.##.# #.###.###.
..#.#..... .#.##.#..# #.###.##..
####.#.... .#..#.##.. .######...
...#.#.#.# ###.##.#.. .##...####

...#.#.#.# ###.##.#.. .##...####
..#.#.###. ..##.##.## #..#.##..#
..####.### ##.#...##. .#.#..#.##
#..#.#..#. ...#.#.#.. .####.###.
.#..####.# #..#.#.#.# ####.###..
.#####..## #####...#. .##....##.
##.##..#.. ..#...#... .####...#.
#.#.###... .##..##... .####.##.#
#...###... ..##...#.. ...#..####
..#.#....# ##.#.#.... ...##.....
For reference, the IDs of the above tiles are:

1951    2311    3079
2729    1427    2473
2971    1489    1171
To check that you've assembled the image correctly, multiply the IDs of the four corner tiles together.
If you do this with the assembled tiles from the example above, you get 1951 * 3079 * 2971 * 1171 = 20899048083289.

Assemble the tiles into an image. What do you get if you multiply together the IDs of the four corner tiles?
 */

/*
Now, you're ready to check the image for sea monsters.

The borders of each tile are not part of the actual image; start by removing them.

In the example above, the tiles become:

.#.#..#. ##...#.# #..#####
###....# .#....#. .#......
##.##.## #.#.#..# #####...
###.#### #...#.## ###.#..#
##.#.... #.##.### #...#.##
...##### ###.#... .#####.#
....#..# ...##..# .#.###..
.####... #..#.... .#......

#..#.##. .#..###. #.##....
#.####.. #.####.# .#.###..
###.#.#. ..#.#### ##.#..##
#.####.. ..##..## ######.#
##..##.# ...#...# .#.#.#..
...#..#. .#.#.##. .###.###
.#.#.... #.##.#.. .###.##.
###.#... #..#.##. ######..

.#.#.### .##.##.# ..#.##..
.####.## #.#...## #.#..#.#
..#.#..# ..#.#.#. ####.###
#..####. ..#.#.#. ###.###.
#####..# ####...# ##....##
#.##..#. .#...#.. ####...#
.#.###.. ##..##.. ####.##.
...###.. .##...#. ..#..###
Remove the gaps to form the actual image:

.#.#..#.##...#.##..#####
###....#.#....#..#......
##.##.###.#.#..######...
###.#####...#.#####.#..#
##.#....#.##.####...#.##
...########.#....#####.#
....#..#...##..#.#.###..
.####...#..#.....#......
#..#.##..#..###.#.##....
#.####..#.####.#.#.###..
###.#.#...#.######.#..##
#.####....##..########.#
##..##.#...#...#.#.#.#..
...#..#..#.#.##..###.###
.#.#....#.##.#...###.##.
###.#...#..#.##.######..
.#.#.###.##.##.#..#.##..
.####.###.#...###.#..#.#
..#.#..#..#.#.#.####.###
#..####...#.#.#.###.###.
#####..#####...###....##
#.##..#..#...#..####...#
.#.###..##..##..####.##.
...###...##...#...#..###

Now, you're ready to search for sea monsters! Because your image is monochrome, a sea monster will look like this:

                  #
#    ##    ##    ###
 #  #  #  #  #  #
When looking for this pattern in the image, the spaces can be anything; only the # need to match.
Also, you might need to rotate or flip your image before it's oriented correctly to find sea monsters.

In the above image, after flipping and rotating it to the appropriate orientation, there are two sea monsters (marked with O):

.####...#####..#...###..
#####..#..#.#.####..#.#.
.#.#...#.###...#.##.O#..
#.O.##.OO#.#.OO.##.OOO##
..#O.#O#.O##O..O.#O##.##
...#.#..##.##...#..#..##
#.##.#..#.#..#..##.#.#..
.###.##.....#...###.#...
#.####.#.#....##.#..#.#.
##...#..#....#..#...####
..#.##...###..#.#####..#
....#.##.#.#####....#...
..##.##.###.....#.##..#.
#...#...###..####....##.
.#.##...#.##.#.#.###...#
#.###.#..####...##..#...
#.###...#.##...#.##O###.
.O##.#OO.###OO##..OOO##.
..O#.O..O..O.#O##O##.###
#.#..##.########..#..##.
#.#####..#.#...##..#....
#....##..#.#########..##
#...#.....#..##...###.##
#..###....##.#...##.##.#

Determine how rough the waters are in the sea monsters' habitat by counting the number of # that are not part of
a sea monster. In the above example, the habitat's water roughness is 273.

How many # are not part of a sea monster?
 */