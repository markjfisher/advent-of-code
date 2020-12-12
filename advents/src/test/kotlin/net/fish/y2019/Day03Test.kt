package net.fish.y2019

import net.fish.findIntersections
import net.fish.move
import net.fish.stepsTo
import net.fish.wireManhattanDistance
import net.fish.y2019.Day03.convertWirePathsToCoordinates
import net.fish.y2019.Day03.minimumSignalDelay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day03Test {
    @Test
    fun `finding intersections`() {
        assertThat(findIntersections(
            listOf(Pair(2, 3), Pair(3, 4), Pair(4, 5)),
            listOf(Pair(4, 5), Pair(3, 4), Pair(0, 0))
        )).containsExactlyInAnyOrder(Pair(3, 4), Pair(4, 5))
    }

    @Test
    fun `adding up coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(move(Pair(0, 0), 5, Pair(0, 1), coordinates)).isEqualTo(Pair(0, 5))
        assertThat(coordinates).containsExactly(Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5))
    }

    @Test
    fun `adding down coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(move(Pair(10, 10), 5, Pair(0, -1), coordinates)).isEqualTo(Pair(10, 5))
        assertThat(coordinates).containsExactly(Pair(10, 9), Pair(10, 8), Pair(10, 7), Pair(10, 6), Pair(10, 5))
    }

    @Test
    fun `adding right coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(move(Pair(10, 10), 5, Pair(1, 0), coordinates)).isEqualTo(Pair(15, 10))
        assertThat(coordinates).containsExactly(Pair(11, 10), Pair(12, 10), Pair(13, 10), Pair(14, 10), Pair(15, 10))
    }

    @Test
    fun `adding left coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(move(Pair(10, 10), 5, Pair(-1, 0), coordinates)).isEqualTo(Pair(5, 10))
        assertThat(coordinates).containsExactly(Pair(9, 10), Pair(8, 10), Pair(7, 10), Pair(6, 10), Pair(5, 10))
    }

    @Test
    fun `convert directions to coordinates`() {
        assertThat(convertWirePathsToCoordinates(listOf("R2", "U2", "L2", "D1")))
            .containsExactly(Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(1, 2), Pair(0, 2), Pair(0, 1))
        assertThat(convertWirePathsToCoordinates(listOf("L2", "D2", "R2", "U1")))
            .containsExactly(Pair(-1, 0), Pair(-2, 0), Pair(-2, -1), Pair(-2, -2), Pair(-1, -2), Pair(0, -2), Pair(0, -1))
    }

    @Test
    fun `manhattan distance`() {
        val path1 = convertWirePathsToCoordinates(listOf("R8", "U5", "L5", "D3"))
        val path2 = convertWirePathsToCoordinates(listOf("U7", "R6", "D4", "L4"))
        assertThat(wireManhattanDistance(path1, path2)).isEqualTo(6)

        val path3 = convertWirePathsToCoordinates(listOf("R75", "D30", "R83", "U83", "L12", "D49", "R71", "U7", "L72"))
        val path4 = convertWirePathsToCoordinates(listOf("U62", "R66", "U55", "R34", "D71", "R55", "D58", "R83"))
        assertThat(wireManhattanDistance(path3, path4)).isEqualTo(159)

        val path5 = convertWirePathsToCoordinates(listOf("R98", "U47", "R26", "D63", "R33", "U87", "L62", "D20", "R33", "U53", "R51"))
        val path6 = convertWirePathsToCoordinates(listOf("U98", "R91", "D20", "R16", "D67", "R40", "U7", "R15", "U6", "R7"))
        assertThat(wireManhattanDistance(path5, path6)).isEqualTo(135)
    }

    @Test
    fun `minimum signal delay`() {
        val path1 = convertWirePathsToCoordinates(listOf("R8", "U5", "L5", "D3"))
        val path2 = convertWirePathsToCoordinates(listOf("U7", "R6", "D4", "L4"))
        assertThat(minimumSignalDelay(path1, path2)).isEqualTo(30)

        val path3 = convertWirePathsToCoordinates(listOf("R75", "D30", "R83", "U83", "L12", "D49", "R71", "U7", "L72"))
        val path4 = convertWirePathsToCoordinates(listOf("U62", "R66", "U55", "R34", "D71", "R55", "D58", "R83"))
        assertThat(minimumSignalDelay(path3, path4)).isEqualTo(610)

        val path5 = convertWirePathsToCoordinates(listOf("R98", "U47", "R26", "D63", "R33", "U87", "L62", "D20", "R33", "U53", "R51"))
        val path6 = convertWirePathsToCoordinates(listOf("U98", "R91", "D20", "R16", "D67", "R40", "U7", "R15", "U6", "R7"))
        assertThat(minimumSignalDelay(path5, path6)).isEqualTo(410)
    }

    @Test
    fun `stepsTo counts correctly`() {
        assertThat(stepsTo(Pair(0, 0), listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2)))).isEqualTo(0) // not found
        assertThat(stepsTo(Pair(2, 2), listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(3, 2)))).isEqualTo(4)
    }

}