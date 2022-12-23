package net.fish.y2022

import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.resourceStrings
import net.fish.y2022.Day22.rotatePoints
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day22Test {
    @Test
    fun `can do part 1`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        assertThat(Day22.doPart1(forest)).isEqualTo(6032)
    }

    @Test
    fun `can do part 2`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        assertThat(Day22.forestWalk(forest, 2)).isEqualTo(5031)
    }

    @Test
    fun `can do part 2 on test data in input format`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test2.txt", trim = false))
        assertThat(Day22.doPart2(forest, 4)).isEqualTo(10004)
    }

    @Test
    fun `translated 2nd test data matches original`() {
        val forest1 = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        val forest2 = Day22.toForest(resourceStrings("/2022/day22-test2.txt", trim = false))
        val translated = Day22.translateToStandardCube(forest2, 4)
        assertThat(forest1.asLines()).containsExactly(
            "        ...#    ",
            "        .#..    ",
            "        #...    ",
            "        ....    ",
            "...#.......#    ",
            "........#...    ",
            "..#....#....    ",
            "..........#.    ",
            "        ...#....",
            "        .....#..",
            "        .#......",
            "        ......#."
        )
        assertThat(translated.asLines()).containsExactly(
            "        ...#    ",
            "        .#..    ",
            "        #...    ",
            "        ....    ",
            "...#.......#    ",
            "........#...    ",
            "..#....#....    ",
            "..........#.    ",
            "        ...#....",
            "        .....#..",
            "        .#......",
            "        ......#."
        )
        assertThat(forest1.asLines()).containsExactlyElementsOf(translated.asLines())
    }

    @Test
    fun `can perform all moves`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        forest.performMoves()
        assertThat(forest.location).isEqualTo(Point(7, 5))
        assertThat(forest.facing).isEqualTo(Direction.EAST)
    }

    @Test
    fun `can perform all moves around cube`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        forest.performMoves(2)
        assertThat(forest.location).isEqualTo(Point(6, 4))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)
    }

    @Test
    fun `can create forest`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        assertThat(forest.facing).isEqualTo(Direction.EAST)
        assertThat(forest.location).isEqualTo(Point(8, 0))
        assertThat(forest.locations).hasSize(96)
        assertThat(forest.locations.containsKey(Point(8, 0)))
        assertThat(forest.locations[Point(8, 0)]).isEqualTo(Day22.ForestTile.EMPTY)
        assertThat(forest.locations[Point(11, 0)]).isEqualTo(Day22.ForestTile.WALL)
        assertThat(forest.moves).containsExactly("10", "R", "5", "L", "5", "R", "10", "L", "4", "R", "5", "L", "5")
        assertThat(forest.cubeSize).isEqualTo(4)
        assertThat(forest.g1).isEqualTo(Pair(Point(8, 0), Point(11, 3)))
        assertThat(forest.g2).isEqualTo(Pair(Point(0, 4), Point(3, 7)))
        assertThat(forest.g3).isEqualTo(Pair(Point(4, 4), Point(7, 7)))
        assertThat(forest.g4).isEqualTo(Pair(Point(8, 4), Point(11, 7)))
        assertThat(forest.g5).isEqualTo(Pair(Point(8, 8), Point(11, 11)))
        assertThat(forest.g6).isEqualTo(Pair(Point(12, 8), Point(15, 11)))
    }

    @Test
    fun `can move until hit wall`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        forest.move()
        assertThat(forest.location).isEqualTo(Point(9, 0))
        forest.move()
        assertThat(forest.location).isEqualTo(Point(10, 0))
        // next location is blocked by wall
        forest.move()
        assertThat(forest.location).isEqualTo(Point(10, 0))
    }

    @Test
    fun `can wrap around grid`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        // Move north from 5,4 wraps to 5,7
        forest.location = Point(5, 4)
        forest.facing = Direction.NORTH
        forest.move()
        assertThat(forest.location).isEqualTo(Point(5, 7))

        // and backwards
        forest.facing = Direction.SOUTH
        forest.move()
        assertThat(forest.location).isEqualTo(Point(5, 4))

        // 0,6 West -> 11,6 East -> 0,6
        forest.location = Point(0, 6)
        forest.facing = Direction.WEST
        forest.move()
        assertThat(forest.location).isEqualTo(Point(11, 6))

        // and backwards
        forest.facing = Direction.EAST
        forest.move()
        assertThat(forest.location).isEqualTo(Point(0, 6))

        // Wraps into a wall should not move N/S
        forest.location = Point(3, 7)
        forest.facing = Direction.SOUTH
        forest.move()
        assertThat(forest.location).isEqualTo(Point(3, 7))

        // Wraps into a wall should not move E/W
        forest.location = Point(0, 4)
        forest.facing = Direction.WEST
        forest.move()
        assertThat(forest.location).isEqualTo(Point(0, 4))
    }

    @Test
    fun `can do multiple moves`() {
        // move then blocked by wall
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        forest.move(1, 10)
        assertThat(forest.location).isEqualTo(Point(10, 0))

        // move then blocked by wrapped wall
        forest.location = Point(3, 4)
        forest.facing = Direction.WEST
        forest.move(1, 10)
        assertThat(forest.location).isEqualTo(Point(0, 4))

        // wrap until hit same location
        forest.location = Point(5, 4)
        forest.facing = Direction.NORTH
        forest.move(1, 4)
        assertThat(forest.location).isEqualTo(Point(5, 4))
    }

    @Test
    fun `can rotate L or R`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))
        forest.rotate("L")
        assertThat(forest.facing).isEqualTo(Direction.NORTH)
        forest.rotate("L")
        assertThat(forest.facing).isEqualTo(Direction.WEST)
        forest.rotate("L")
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)
        forest.rotate("L")
        assertThat(forest.facing).isEqualTo(Direction.EAST)

        forest.rotate("R")
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)
        forest.rotate("R")
        assertThat(forest.facing).isEqualTo(Direction.WEST)
        forest.rotate("R")
        assertThat(forest.facing).isEqualTo(Direction.NORTH)
        forest.rotate("R")
        assertThat(forest.facing).isEqualTo(Direction.EAST)
    }

    @Test
    fun `can move around cube in steps`() {
        val forest = Day22.toForest(resourceStrings("/2022/day22-test.txt", trim = false))

        // Blocked around cube going N
        forest.location = Point(6, 4)
        forest.facing = Direction.NORTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(6, 4))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)

        // *******************************************************************
        // g1
        // *******************************************************************
        // Normal move from g1 N to g2 and back
        forest.location = Point(9, 0)
        forest.facing = Direction.NORTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(2, 4))
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)
        forest.facing = Direction.NORTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(9, 0))
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)

        // Normal move from g1 W to g3 and back
        forest.location = Point(8, 3)
        forest.facing = Direction.WEST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(7, 4))
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)
        forest.facing = Direction.NORTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(8, 3))
        assertThat(forest.facing).isEqualTo(Direction.EAST)

        // Normal move from g1 E to g3 and back
        forest.location = Point(11, 2)
        forest.facing = Direction.EAST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(15, 9))
        assertThat(forest.facing).isEqualTo(Direction.WEST)
        forest.facing = Direction.EAST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(11, 2))
        assertThat(forest.facing).isEqualTo(Direction.WEST)

        // *******************************************************************
        // g2
        // *******************************************************************
        // g2 W to g6 N
        forest.location = Point(0, 4)
        forest.facing = Direction.WEST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(15, 11))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)
        forest.facing = Direction.SOUTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(0, 4))
        assertThat(forest.facing).isEqualTo(Direction.EAST)

        // g2 S to g5 N
        forest.location = Point(2, 7)
        forest.facing = Direction.SOUTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(9, 11))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)
        forest.facing = Direction.SOUTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(2, 7))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)

        // *******************************************************************
        // g3
        // *******************************************************************
        // g3 S to g5 E
        forest.location = Point(6, 7)
        forest.facing = Direction.SOUTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(8, 9))
        assertThat(forest.facing).isEqualTo(Direction.EAST)
        forest.facing = Direction.WEST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(6, 7))
        assertThat(forest.facing).isEqualTo(Direction.NORTH)

        // *******************************************************************
        // g4
        // *******************************************************************
        // g4 E to g6 S
        forest.location = Point(11, 5)
        forest.facing = Direction.EAST
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(14, 8))
        assertThat(forest.facing).isEqualTo(Direction.SOUTH)
        forest.facing = Direction.NORTH
        forest.move(part = 2, count = 1)
        assertThat(forest.location).isEqualTo(Point(11, 5))
        assertThat(forest.facing).isEqualTo(Direction.WEST)

    }

    @Test
    fun `can rotate 2x2 points cw 90`() {
        val side = mapOf(
            Point(0, 0) to Day22.ForestTile.WALL,
            Point(1, 0) to Day22.ForestTile.EMPTY,
            Point(0, 1) to Day22.ForestTile.EMPTY,
            Point(1, 1) to Day22.ForestTile.EMPTY,
        )
        // Goes Clockwise
        var rotated = side.rotatePoints(1, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.WALL,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.EMPTY,
            )
        )

        rotated = rotated.rotatePoints(1, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.WALL,
            )
        )

        rotated = rotated.rotatePoints(1, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.WALL,
                Point(1, 1) to Day22.ForestTile.EMPTY,
            )
        )

        rotated = rotated.rotatePoints(1, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.WALL,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.EMPTY,
            )
        )
    }

    @Test
    fun `can rotate 3x3 points cw 90`() {
        // ##.    .##    ...    ...    ##.
        // #.. -> ..# -> ..# -> #.. -> #..
        // ...    ...    .##    ##.    ...
        val side = mapOf(
            Point(0, 0) to Day22.ForestTile.WALL,
            Point(1, 0) to Day22.ForestTile.WALL,
            Point(2, 0) to Day22.ForestTile.EMPTY,
            Point(0, 1) to Day22.ForestTile.WALL,
            Point(1, 1) to Day22.ForestTile.EMPTY,
            Point(2, 1) to Day22.ForestTile.EMPTY,
            Point(0, 2) to Day22.ForestTile.EMPTY,
            Point(1, 2) to Day22.ForestTile.EMPTY,
            Point(2, 2) to Day22.ForestTile.EMPTY,
        )
        // Goes Clockwise
        var rotated = side.rotatePoints(1, 3)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.WALL,
                Point(2, 0) to Day22.ForestTile.WALL,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.EMPTY,
                Point(2, 1) to Day22.ForestTile.WALL,
                Point(0, 2) to Day22.ForestTile.EMPTY,
                Point(1, 2) to Day22.ForestTile.EMPTY,
                Point(2, 2) to Day22.ForestTile.EMPTY,
            )
        )

        rotated = rotated.rotatePoints(1, 3)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(2, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.EMPTY,
                Point(2, 1) to Day22.ForestTile.WALL,
                Point(0, 2) to Day22.ForestTile.EMPTY,
                Point(1, 2) to Day22.ForestTile.WALL,
                Point(2, 2) to Day22.ForestTile.WALL,
            )
        )

        rotated = rotated.rotatePoints(1, 3)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(2, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.WALL,
                Point(1, 1) to Day22.ForestTile.EMPTY,
                Point(2, 1) to Day22.ForestTile.EMPTY,
                Point(0, 2) to Day22.ForestTile.WALL,
                Point(1, 2) to Day22.ForestTile.WALL,
                Point(2, 2) to Day22.ForestTile.EMPTY,
            )
        )

        rotated = rotated.rotatePoints(1, 3)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.WALL,
                Point(1, 0) to Day22.ForestTile.WALL,
                Point(2, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.WALL,
                Point(1, 1) to Day22.ForestTile.EMPTY,
                Point(2, 1) to Day22.ForestTile.EMPTY,
                Point(0, 2) to Day22.ForestTile.EMPTY,
                Point(1, 2) to Day22.ForestTile.EMPTY,
                Point(2, 2) to Day22.ForestTile.EMPTY,
            )
        )
    }

    @Test
    fun `can rotate multiple`() {
        val side = mapOf(
            Point(0, 0) to Day22.ForestTile.WALL,
            Point(1, 0) to Day22.ForestTile.EMPTY,
            Point(0, 1) to Day22.ForestTile.EMPTY,
            Point(1, 1) to Day22.ForestTile.EMPTY,
        )
        // Goes Clockwise
        var rotated = side.rotatePoints(2, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.EMPTY,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.WALL,
            )
        )

        // effectively CCW
        rotated = rotated.rotatePoints(3, 2)
        assertThat(rotated).containsExactlyEntriesOf(
            mapOf(
                Point(0, 0) to Day22.ForestTile.EMPTY,
                Point(1, 0) to Day22.ForestTile.WALL,
                Point(0, 1) to Day22.ForestTile.EMPTY,
                Point(1, 1) to Day22.ForestTile.EMPTY,
            )
        )
    }

    @Test
    fun `can convert between coordinate systems`() {
        // From Input to Test shape
        // s1
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(4, 0), 4)).isEqualTo(Point(8, 0))
        // s2
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(1, 14), 4)).isEqualTo(Point(1, 5))
        // s3
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(1, 9), 4)).isEqualTo(Point(6, 5))
        // s4
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(5, 5), 4)).isEqualTo(Point(9, 5))
        // s5
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(6, 9), 4)).isEqualTo(Point(10, 9))
        // s6
        assertThat(Day22.Forest.convertToTestShapeCoordinate(Point(9, 1), 4)).isEqualTo(Point(14, 10))

        // From Test to Input shape
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(9, 1), Direction.NORTH, 4)).isEqualTo(Pair(Point(5, 1), Direction.NORTH))
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(1, 5), Direction.NORTH, 4)).isEqualTo(Pair(Point(1, 14), Direction.WEST))
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(4, 4), Direction.NORTH, 4)).isEqualTo(Pair(Point(0, 11),  Direction.EAST))
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(9, 5), Direction.NORTH, 4)).isEqualTo(Pair(Point(5, 5),  Direction.NORTH))
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(10, 9), Direction.NORTH, 4)).isEqualTo(Pair(Point(6, 9),  Direction.NORTH))
        assertThat(Day22.Forest.convertToInputShapeCoordinate(Point(14, 10), Direction.NORTH, 4)).isEqualTo(Pair(Point(9, 1),  Direction.SOUTH))
    }

    @Test
    fun `can create rotation matrix`() {
        val rot90 = Day22.Forest.createRotationMap(5)
        assertThat(rot90[Point(0, 0)]!!).isEqualTo(Point(4, 0))
        assertThat(rot90[Point(1, 0)]!!).isEqualTo(Point(4, 1))
        assertThat(rot90[Point(2, 0)]!!).isEqualTo(Point(4, 2))
        assertThat(rot90[Point(2, 2)]!!).isEqualTo(Point(2, 2))

        val rot180 = Day22.Forest.createRotationMap(5, 2)
        assertThat(rot180[Point(0, 0)]!!).isEqualTo(Point(4, 4))
        assertThat(rot180[Point(1, 0)]!!).isEqualTo(Point(3, 4))
        assertThat(rot180[Point(2, 0)]!!).isEqualTo(Point(2, 4))
        assertThat(rot180[Point(2, 2)]!!).isEqualTo(Point(2, 2))
    }
}