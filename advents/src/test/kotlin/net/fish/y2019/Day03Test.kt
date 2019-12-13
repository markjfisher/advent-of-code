package net.fish.y2019

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day03Test {
    @Test
    fun `finding intersections`() {
        assertThat(Day03.findIntersections(
            listOf(Pair(2, 3), Pair(3, 4), Pair(4, 5)),
            listOf(Pair(4, 5), Pair(3, 4), Pair(0, 0))
        )).containsExactlyInAnyOrder(Pair(3, 4), Pair(4, 5))
    }

    @Test
    fun `adding up coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day03.addUp(Pair(0, 0), coordinates, 5)).isEqualTo(Pair(0, 5))
        assertThat(coordinates).containsExactly(Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5))
    }

    @Test
    fun `adding down coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day03.addDown(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(10, 5))
        assertThat(coordinates).containsExactly(Pair(10, 9), Pair(10, 8), Pair(10, 7), Pair(10, 6), Pair(10, 5))
    }

    @Test
    fun `adding right coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day03.addRight(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(15, 10))
        assertThat(coordinates).containsExactly(Pair(11, 10), Pair(12, 10), Pair(13, 10), Pair(14, 10), Pair(15, 10))
    }

    @Test
    fun `adding left coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day03.addLeft(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(5, 10))
        assertThat(coordinates).containsExactly(Pair(9, 10), Pair(8, 10), Pair(7, 10), Pair(6, 10), Pair(5, 10))
    }

    @Test
    fun `convert directions to coordinates`() {
        assertThat(Day03.convertWirePathsToCoordinates(listOf("R2", "U2", "L2", "D1")))
            .containsExactly(Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(1, 2), Pair(0, 2), Pair(0, 1))
        assertThat(Day03.convertWirePathsToCoordinates(listOf("L2", "D2", "R2", "U1")))
            .containsExactly(Pair(-1, 0), Pair(-2, 0), Pair(-2, -1), Pair(-2, -2), Pair(-1, -2), Pair(0, -2), Pair(0, -1))
    }

    @Test
    fun `manhattan distance`() {
        val path1 = Day03.convertWirePathsToCoordinates(listOf("R8", "U5", "L5", "D3"))
        val path2 = Day03.convertWirePathsToCoordinates(listOf("U7", "R6", "D4", "L4"))
        assertThat(Day03.manhattanDistance(path1, path2)).isEqualTo(6)

        val path3 = Day03.convertWirePathsToCoordinates(listOf("R75", "D30", "R83", "U83", "L12", "D49", "R71", "U7", "L72"))
        val path4 = Day03.convertWirePathsToCoordinates(listOf("U62", "R66", "U55", "R34", "D71", "R55", "D58", "R83"))
        assertThat(Day03.manhattanDistance(path3, path4)).isEqualTo(159)

        val path5 = Day03.convertWirePathsToCoordinates(listOf("R98", "U47", "R26", "D63", "R33", "U87", "L62", "D20", "R33", "U53", "R51"))
        val path6 = Day03.convertWirePathsToCoordinates(listOf("U98", "R91", "D20", "R16", "D67", "R40", "U7", "R15", "U6", "R7"))
        assertThat(Day03.manhattanDistance(path5, path6)).isEqualTo(135)
    }

    @Test
    fun `minimum signal delay`() {
        val path1 = Day03.convertWirePathsToCoordinates(listOf("R8", "U5", "L5", "D3"))
        val path2 = Day03.convertWirePathsToCoordinates(listOf("U7", "R6", "D4", "L4"))
        assertThat(Day03.minimumSignalDelay(path1, path2)).isEqualTo(30)

        val path3 = Day03.convertWirePathsToCoordinates(listOf("R75", "D30", "R83", "U83", "L12", "D49", "R71", "U7", "L72"))
        val path4 = Day03.convertWirePathsToCoordinates(listOf("U62", "R66", "U55", "R34", "D71", "R55", "D58", "R83"))
        assertThat(Day03.minimumSignalDelay(path3, path4)).isEqualTo(610)

        val path5 = Day03.convertWirePathsToCoordinates(listOf("R98", "U47", "R26", "D63", "R33", "U87", "L62", "D20", "R33", "U53", "R51"))
        val path6 = Day03.convertWirePathsToCoordinates(listOf("U98", "R91", "D20", "R16", "D67", "R40", "U7", "R15", "U6", "R7"))
        assertThat(Day03.minimumSignalDelay(path5, path6)).isEqualTo(410)
    }

    @Test
    fun `stepsTo counts correctly`() {
        assertThat(Day03.stepsTo(Pair(0, 0), listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2)))).isEqualTo(0) // not found
        assertThat(Day03.stepsTo(Pair(2, 2), listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2)))).isEqualTo(4)
    }

}