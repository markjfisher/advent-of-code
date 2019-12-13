package net.fish.y2019

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day01Test {
    @Test
    fun `calculate fuels`() {
        assertThat(Day01.calculateFuel(listOf(12))).isEqualTo(2)
        assertThat(Day01.calculateFuel(listOf(14))).isEqualTo(2)
        assertThat(Day01.calculateFuel(listOf(1969))).isEqualTo(654)
        assertThat(Day01.calculateFuel(listOf(100756))).isEqualTo(33583)
        assertThat(Day01.calculateFuel(listOf(12, 14))).isEqualTo(4)
        assertThat(Day01.calculateFuel(listOf(1969, 100756))).isEqualTo(654+33583)
    }

    @Test
    fun `recursive fuel calculations`() {
        assertThat(Day01.calculateFuelRecursively(listOf(14))).isEqualTo(2)
        assertThat(Day01.calculateFuelRecursively(listOf(1969))).isEqualTo(966)
        assertThat(Day01.calculateFuelRecursively(listOf(100756))).isEqualTo(50346)
        assertThat(Day01.calculateFuelRecursively(listOf(14, 1969, 100756))).isEqualTo(2 + 966 + 50346)
    }

}