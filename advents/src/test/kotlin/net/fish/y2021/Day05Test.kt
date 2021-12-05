package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test {
    private val vectors = Day05.toThermalVectors(resourcePath("/2021/day05-test.txt"))

    @Test
    fun `can solve test data part 1`() {
        assertThat(Day05.doPart1(vectors)).isEqualTo(5)
    }

    @Test
    fun `can solve test data part 2`() {
        assertThat(Day05.doPart2(vectors)).isEqualTo(12)
    }

    @Test
    fun `diagonals give correct coords`() {
        val v1 = Day05.ThermalVector(Day05.Point(0, 0), Day05.Point(1, 1))
        assertThat(v1.walkDiagonal()).containsExactlyInAnyOrder(Day05.Point(0, 0), Day05.Point(1, 1))
        val v2 = Day05.ThermalVector(Day05.Point(1, 1), Day05.Point(0, 0))
        assertThat(v2.walkDiagonal()).containsExactlyInAnyOrder(Day05.Point(0, 0), Day05.Point(1, 1))
        val v3 = Day05.ThermalVector(Day05.Point(1, 0), Day05.Point(0, 1))
        assertThat(v3.walkDiagonal()).containsExactlyInAnyOrder(Day05.Point(1, 0), Day05.Point(0, 1))
        val v4 = Day05.ThermalVector(Day05.Point(0, 1), Day05.Point(1, 0))
        assertThat(v4.walkDiagonal()).containsExactlyInAnyOrder(Day05.Point(1, 0), Day05.Point(0, 1))
    }

    @Test
    fun `can extract thermal vectors`() {
        assertThat(Day05.toThermalVectors(listOf("0,9 -> 5,9", "8,0 -> 0,8"))).containsExactly(
            Day05.ThermalVector(Day05.Point(0, 9), Day05.Point(5, 9)),
            Day05.ThermalVector(Day05.Point(8, 0), Day05.Point(0, 8))
        )
    }

    @Test
    fun `can get all points on vector`() {
        assertThat(Day05.ThermalVector(Day05.Point(1, 1), Day05.Point(1, 3)).allPointsOnVector()).containsExactly(
            Day05.Point(1, 1),
            Day05.Point(1, 2),
            Day05.Point(1, 3)
        )
    }
}