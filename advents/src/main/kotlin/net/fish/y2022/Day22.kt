package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.maths.rotateMatrix
import net.fish.resourceStrings

object Day22 : Day {
    override fun part1() = doPart1(toForest(resourceStrings(2022, 22, trim = false)))
    override fun part2() = doPart2(toForest(resourceStrings(2022, 22, trim = false)), 50)

    fun doPart1(forest: Forest): Int {
        return forestWalk(forest, 1)
    }
    fun doPart2(forest: Forest, size: Int): Int {
        val translated = translateToStandardCube(forest, size)
        forestWalk(translated, 2)
        val (tLocation, tFacing) = Forest.convertToInputShapeCoordinate(translated.location, translated.facing, translated.cubeSize)
        return (tLocation.y + 1) * 1000 + (tLocation.x + 1) * 4 + Forest.facingScore(tFacing)
    }

    fun forestWalk(forest: Forest, part: Int): Int {
        forest.performMoves(part)
        return (forest.location.y + 1) * 1000 + (forest.location.x + 1) * 4 + Forest.facingScore(forest.facing)
    }

    enum class ForestTile { EMPTY, WALL }

    fun toForest(data: List<String>): Forest {
        val locations = data[0].split("\n").foldIndexed(emptyMap<Point, ForestTile>()) { y, ac, line ->
            ac + line.mapIndexedNotNull { x, c ->
                when (c) {
                    ' ' -> null
                    '.' -> Point(x, y) to ForestTile.EMPTY // TODO: these aren't needed if we walk carefully
                    '#' -> Point(x, y) to ForestTile.WALL
                    else -> throw Exception("Unknown tile: $c")
                }
            }
        }.toMap()
        val start = locations.keys.filter { p -> p.y == 0 }.minBy { it.x }

        // Turn 10R5L5 into listOf("10", "R", "5", "L", "5")
        // Assumption: First and last instruction are counts (i.e. 1 more count than a direction), which is true of test and real data
        val counts = data[1].split(Regex("[A-Z]"))
        val turns = data[1].split(Regex("\\d")).filterNot { it.isBlank() }
        val dirs = counts.zip(turns).flatMap { listOf(it.first, it.second) } + counts.last()

        return Forest(locations, start, Direction.EAST, dirs)
    }

    fun translateToStandardCube(forest: Forest, size: Int = 50): Forest {
        // translate the input shape:
        //     1  6    <- 6 rotated 180
        //     4
        //  3  5       <- 3 rotated CW
        //  2          <- 2 rotated CW
        //
        // into test shape:
        //        1
        //  2  3  4
        //        5  6

        // sides from the input shape
        val s1 = Pair(Point(size, 0), Point(2 * size - 1, size - 1))
        val s2 = Pair(Point(0, 3 * size), Point(size - 1, 4 * size - 1))
        val s3 = Pair(Point(0, 2 * size), Point(size - 1, 3 * size - 1))
        val s4 = Pair(Point(size, size), Point(2 * size - 1, 2 * size - 1))
        val s5 = Pair(Point(size, 2 * size), Point(2 * size - 1, 3 * size - 1))
        val s6 = Pair(Point(2 * size, 0), Point(3 * size - 1, size - 1))

        // shift s1 to right
        val s1NewSide = forest.locations.filter { it.key.within(s1) }
            .map { Point(it.key.x + size, it.key.y) to it.value }
            .toMap()

        // s2: translate -150 y to get to upper left location, rotate it CW 90, translate + 50 to get to correct location
        val s2NewSide = forest.locations.filter { it.key.within(s2) }
            .map { Point(it.key.x, it.key.y - 3 * size) to it.value }
            .toMap()
            .rotatePoints(rotations = 1, size = size)
            .map { Point(it.key.x, it.key.y + size) to it.value }
            .toMap()

        // s3: subtract y -100, rotate 90, translate +50, +50
        val s3NewSide = forest.locations.filter { it.key.within(s3) }
            .map { Point(it.key.x, it.key.y - 2 * size) to it.value }
            .toMap()
            .rotatePoints(rotations = 1, size = size)
            .map { Point(it.key.x + size, it.key.y + size) to it.value }
            .toMap()

        // s4: add x 50 to all points
        val s4NewSide = forest.locations.filter { it.key.within(s4) }
            .map { Point(it.key.x + size, it.key.y) to it.value }
            .toMap()

        // s5: add x 50 to all points
        val s5NewSide = forest.locations.filter { it.key.within(s5) }
            .map { Point(it.key.x + size, it.key.y) to it.value }
            .toMap()

        // s6: -100 x, rotate 180, add 150, 100 to all points
        val s6NewSide = forest.locations.filter { it.key.within(s6) }
            .map { Point(it.key.x - 2 * size, it.key.y) to it.value }
            .toMap()
            .rotatePoints(rotations = 2, size = size)
            .map { Point(it.key.x + 3 * size, it.key.y + 2 * size) to it.value }
            .toMap()

        // finally merge all the maps
        val all = s1NewSide + s2NewSide + s3NewSide + s4NewSide + s5NewSide + s6NewSide
        val initialPoint = all.keys.filter { p -> p.y == 0 }.minBy { it.x }

        // sanity test the translation
        assert(initialPoint == Point(2 * size, 0))
        assert(all.keys.size == forest.locations.keys.size)
        assert(all.values.filter { it == ForestTile.WALL }.size == forest.locations.values.filter { it == ForestTile.WALL }.size)

        return Forest(all, initialPoint, Direction.EAST, forest.moves)
    }

    fun Map<Point, ForestTile>.rotatePoints(rotations: Int = 1, size: Int = 50): Map<Point, ForestTile> {
        // perform a 90 degrees clockwise rotation
        var newMatrix = arrayOf<Array<ForestTile>>()
        for (j in 0 until size) {
            var row = arrayOf<ForestTile>()
            for (i in 0 until size) {
                row += this[Point(i, j)]!!
            }
            newMatrix += row
        }
        var rotated = rotateMatrix(newMatrix)
        (0 until rotations - 1).forEach { _ ->
            rotated = rotateMatrix(rotated)
        }
        // now create the new side from rotated Forest Tiles
        val newSide = mutableMapOf<Point, ForestTile>()
        for (j in 0 until size) {
            for (i in 0 until size) {
                newSide[Point(i, j)] = rotated[j][i]
            }
        }
        return newSide.toMap()
    }

    data class Forest(val locations: Map<Point, ForestTile>, var location: Point, var facing: Direction, val moves: List<String>) {
        val cubeSize = locations.count { it.key.y == 0 }

        // These are P2 side coordinates. This needs huge refactoring!
        val g1 = Pair(Point(cubeSize * 2, 0), Point(cubeSize * 3 - 1, cubeSize - 1))
        val g2 = Pair(Point(0, cubeSize), Point(cubeSize - 1, cubeSize * 2 - 1))
        val g3 = Pair(Point(cubeSize, cubeSize), Point(cubeSize * 2 - 1, cubeSize * 2 - 1))
        val g4 = Pair(Point(cubeSize * 2, cubeSize), Point(cubeSize * 3 - 1, cubeSize * 2 - 1))
        val g5 = Pair(Point(cubeSize * 2, cubeSize * 2), Point(cubeSize * 3 - 1, cubeSize * 3 - 1))
        val g6 = Pair(Point(cubeSize * 3, cubeSize * 2), Point(cubeSize * 4 - 1, cubeSize * 3 - 1))

        fun performMoves(part: Int = 1) {
            moves.forEach { m ->
                when (m) {
                    "L", "R" -> rotate(m)
                    else -> move(part, m.toInt())
                }
            }
        }

        private fun debugLocation() {
            val dirNum = facingScore(facing)
            println("$dirNum (${location.x}, ${location.y})")
        }

        fun move(part: Int = 1, count: Int = 1) {
            fun doMove() {
                val (newLocation, newDirection) = moveWithWrapping(part)

                // simple move
                if (locations.containsKey(newLocation) && locations[newLocation] == ForestTile.EMPTY) {
                    location = newLocation
                    facing = newDirection
                    return
                }
                // blocked move
                if (locations.containsKey(newLocation) && locations[newLocation] == ForestTile.WALL) {
                    return
                }
            }
            (0 until count).forEach { _ ->
                doMove()
                // debugLocation()
            }
        }

        fun rotate(d: String) {
            facing = when (d) {
                "L" -> facing.ccw()
                "R" -> facing.cw()
                else -> throw Exception("Unknown direction")
            }
        }

        private fun moveWithWrapping(part: Int): Pair<Point, Direction> {
            val newLocation = location + facing
            if (locations.containsKey(newLocation)) return Pair(newLocation, facing)

            // need to wrap
            return if (part == 1) {
                Pair(when (facing) {
                    Direction.NORTH -> locations.keys.filter { it.x == location.x }.maxBy { it.y }
                    Direction.SOUTH -> locations.keys.filter { it.x == location.x }.minBy { it.y }
                    Direction.EAST -> locations.keys.filter { it.y == location.y }.minBy { it.x }
                    Direction.WEST -> locations.keys.filter { it.y == location.y }.maxBy { it.x }
                }, facing)
            } else {
                // 3D moves are location and direction changes
                val newPair = if (location.within(g1)) {
                    // g1, W -> g3, S
                    // g1, E -> g6, W
                    // g1, N -> g2, S
                    // g1, S -> g4, S // AUTOMATIC
                    when (facing) {
                        Direction.WEST -> Pair(Point(g3.first.x + location.y, g3.first.y), Direction.SOUTH)
                        Direction.EAST -> Pair(Point(g6.second.x, g6.second.y - location.y), Direction.WEST)
                        Direction.NORTH -> Pair(Point(g2.second.x - location.x + g1.first.x, g2.first.y), Direction.SOUTH)
                        Direction.SOUTH -> throw Exception("g1 moving S is already catered for")
                    }
                } else if (location.within(g2)) {
                    // g2, W -> g6, N
                    // g2, E -> g3, E // AUTOMATIC
                    // g2, N -> g1, S
                    // g2, S -> g5, N
                    when (facing) {
                        Direction.WEST -> Pair(Point(g6.second.x + g2.first.y - location.y, g6.second.y), Direction.NORTH)
                        Direction.EAST -> throw Exception("g2 moving E is already catered for")
                        Direction.NORTH -> Pair(Point(g1.second.x - location.x, 0), Direction.SOUTH)
                        Direction.SOUTH -> Pair(Point(g5.first.x + (g2.second.x - location.x), g5.second.y), Direction.NORTH)
                    }
                } else if (location.within(g3)) {
                    // g3, W -> g2, W // AUTOMATIC
                    // g3, E -> g4, E // AUTOMATIC
                    // g3, N -> g1, E
                    // g3, S -> g5, E
                    when (facing) {
                        Direction.WEST -> throw Exception("g3 moving W is already catered for")
                        Direction.EAST -> throw Exception("g3 moving E is already catered for")
                        Direction.NORTH -> Pair(Point(g1.first.x, location.x - g3.first.x), Direction.EAST)
                        Direction.SOUTH -> Pair(Point(g5.first.x, g5.first.y + g3.second.x - location.x), Direction.EAST)
                    }
                } else if (location.within(g4)) {
                    // g4, W -> g3, W // AUTOMATIC
                    // g4, E -> g6, S
                    // g4, N -> g1, N // AUTOMATIC
                    // g4, S -> g5, S // AUTOMATIC
                    when (facing) {
                        Direction.WEST -> throw Exception("g4 moving W is already catered for")
                        Direction.EAST -> Pair(Point(g6.second.x + g4.first.y - location.y, g6.first.y), Direction.SOUTH)
                        Direction.NORTH -> throw Exception("g4 moving N is already catered for")
                        Direction.SOUTH -> throw Exception("g4 moving S is already catered for")
                    }
                } else if (location.within(g5)) {
                    // g5, W -> g3, N
                    // g5, E -> g6, E // AUTOMATIC
                    // g5, N -> g4, N // AUTOMATIC
                    // g5, S -> g2, N
                    when (facing) {
                        Direction.WEST -> Pair(Point(g3.second.x + g5.first.y - location.y, g3.second.y), Direction.NORTH)
                        Direction.EAST -> throw Exception("g5 moving E is already catered for")
                        Direction.NORTH -> throw Exception("g5 moving N is already catered for")
                        Direction.SOUTH -> Pair(Point(g2.second.x - location.x + g5.first.x, g2.second.y), Direction.NORTH)
                    }
                } else if (location.within(g6)) {
                    // g6, W -> g5, W // AUTOMATIC
                    // g6, E -> g1, W
                    // g6, N -> g4, W
                    // g6, S -> g2, E
                    when (facing) {
                        Direction.WEST -> throw Exception("g6 moving W is already catered for")
                        Direction.EAST -> Pair(Point(g1.second.x, g6.second.y - location.y), Direction.WEST)
                        Direction.NORTH -> Pair(Point(g4.second.x, g4.first.y + g6.second.x - location.x), Direction.WEST)
                        Direction.SOUTH -> Pair(Point(0, g2.first.y + g6.second.x - location.x), Direction.EAST)
                    }
                } else {
                    throw Exception("Not in any grid! $location")
                }
                if (!newPair.first.within(g1) && !newPair.first.within(g2) && !newPair.first.within(g3) && !newPair.first.within(g4) && !newPair.first.within(g5) && !newPair.first.within(g6)) {
                    throw Exception("Created bad point: $newPair")
                }
                return newPair
            }
        }

        fun asLines(): List<String> {
            val result = mutableListOf<String>()
            val bounds = locations.keys.bounds()
            for(j in 0 .. bounds.second.y) {
                var row = ""
                for (i in 0 .. bounds.second.x) {
                    row += when(val v = locations[Point(i, j)]) {
                        null -> " "
                        ForestTile.WALL -> "#"
                        ForestTile.EMPTY -> "."
                    }
                }
                result += row
            }
            return result
        }

        companion object {
            fun facingScore(dir: Direction): Int {
                return when (dir) {
                    Direction.EAST -> 0
                    Direction.SOUTH -> 1
                    Direction.WEST -> 2
                    Direction.NORTH -> 3
                }
            }

            private fun createPair(p: Point, size: Int): Pair<Point, Point> {
                return Pair(p, p + Point(size - 1, size - 1))
            }

            fun convertToTestShapeCoordinate(point: Point, size: Int): Point {
                // The Input shapes
                val rotate90 = createRotationMap(size, 1)
                val rotate180 = createRotationMap(size, 2)
                val s1 = createPair(Point(size, 0), size)
                val s2 = createPair(Point(0, 3 * size), size)
                val s3 = createPair(Point(0, 2 * size), size)
                val s4 = createPair(Point(size, size), size)
                val s5 = createPair(Point(size, 2 * size), size)
                val s6 = createPair(Point(2 * size, 0), size)

                return when {
                    point.within(s1) -> point + Point(size, 0)
                    point.within(s2) -> rotate90[point - Point(0, size * 3)]!! + Point(0, size)
                    point.within(s3) -> rotate90[point - Point(0, size * 2)]!! + Point(size, size)
                    point.within(s4) -> point + Point(size, 0)
                    point.within(s5) -> point + Point(size, 0)
                    point.within(s6) -> rotate180[point - Point(2 * size, 0)]!! + Point(3 * size, 2 * size)
                    else -> throw Exception("Point not within Input Shape coordinates")
                }
            }

            fun convertToInputShapeCoordinate(point: Point, facing: Direction, size: Int): Pair<Point, Direction> {
                // The Test shape sides
                val rotate270 = createRotationMap(size, 3)
                val rotate180 = createRotationMap(size, 2)
                val s1 = createPair(Point(2 * size, 0), size)
                val s2 = createPair(Point(0, size), size)
                val s3 = createPair(Point(size, size), size)
                val s4 = createPair(Point(size * 2, size), size)
                val s5 = createPair(Point(size * 2, size * 2), size)
                val s6 = createPair(Point(size *3, size * 2), size)
                return when {
                    point.within(s1) -> Pair(point - Point(size, 0), facing)
                    point.within(s2) -> Pair(rotate270[point - Point(0, size)]!! + Point(0, size * 3), facing.ccw())
                    point.within(s3) -> Pair(rotate270[point - Point(size, size)]!! + Point(0, size * 2), facing.cw())
                    point.within(s4) -> Pair(point - Point(size, 0), facing)
                    point.within(s5) -> Pair(point - Point(size, 0), facing)
                    point.within(s6) -> Pair(rotate180[point - Point(3 * size, 2 * size)]!! + Point(2 * size, 0), facing.cw().cw())
                    else -> throw Exception("Point not within Test Shape coordinates: $point")
                }
            }

            fun createRotationMap(size: Int, rotations: Int = 1): Map<Point, Point> {
                // We need to reverse the rotation to get the correct forward mapping
                val reverseRotationCount = 4 - rotations
                var newMatrix = arrayOf<Array<Point>>()
                for (j in 0 until size) {
                    var row = arrayOf<Point>()
                    for (i in 0 until size) {
                        row += Point(i, j)
                    }
                    newMatrix += row
                }
                var rotated = rotateMatrix(newMatrix)
                (0 until reverseRotationCount - 1).forEach { _ ->
                    rotated = rotateMatrix(rotated)
                }

                // now create the map of original values to rotated coordinates
                val result = mutableMapOf<Point, Point>()
                for (j in 0 until size) {
                    for (i in 0 until size) {
                        result[Point(i, j)] = rotated[j][i]
                    }
                }

                return result.toMap()
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}