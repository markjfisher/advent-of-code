package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day3BaseTest {
    @Test
    fun `finding intersections`() {
        assertThat(
            Day3Base().findIntersections(
                listOf(Pair(2, 3), Pair(3, 4), Pair(4, 5)),
                listOf(Pair(4, 5), Pair(3, 4), Pair(0, 0))
            )
        ).containsExactlyInAnyOrder(Pair(3, 4), Pair(4, 5))
    }

    @Test
    fun `adding up coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day3Base().addUp(Pair(0, 0), coordinates, 5)).isEqualTo(Pair(0, 5))
        assertThat(coordinates)
            .containsExactly(Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5))
    }

    @Test
    fun `adding down coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day3Base().addDown(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(10, 5))
        assertThat(coordinates)
            .containsExactly(Pair(10, 9), Pair(10, 8), Pair(10, 7), Pair(10, 6), Pair(10, 5))
    }

    @Test
    fun `adding right coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day3Base().addRight(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(15, 10))
        assertThat(coordinates)
            .containsExactly(Pair(11, 10), Pair(12, 10), Pair(13, 10), Pair(14, 10), Pair(15, 10))
    }

    @Test
    fun `adding left coordinates`() {
        val coordinates = mutableListOf<Pair<Int, Int>>()
        assertThat(Day3Base().addLeft(Pair(10, 10), coordinates, 5)).isEqualTo(Pair(5, 10))
        assertThat(coordinates)
            .containsExactly(Pair(9, 10), Pair(8, 10), Pair(7, 10), Pair(6, 10), Pair(5, 10))
    }

    @Test
    fun `convert directions to coordinates`() {
        assertThat(Day3Base().convertWirePathsToCoordinates(listOf("R2", "U2", "L2", "D1")))
            .containsExactly(Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(1, 2), Pair(0, 2), Pair(0, 1))
        assertThat(Day3Base().convertWirePathsToCoordinates(listOf("L2", "D2", "R2", "U1")))
            .containsExactly(Pair(-1, 0), Pair(-2, 0), Pair(-2, -1), Pair(-2, -2), Pair(-1, -2), Pair(0, -2), Pair(0, -1))
    }

}