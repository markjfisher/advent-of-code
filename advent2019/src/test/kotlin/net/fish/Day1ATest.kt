package net.fish

import net.fish.Helpers.loadResourceLinesAsInts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day1ATest {
    @Test
    fun `can load resources`() {
        assertThat(loadResourceLinesAsInts("/test-masses.txt")).containsExactly(1,2,3,100,5000)
    }

    @Test
    fun `calculate fuels`() {
        assertThat(Day1A.calculateFuel(listOf(12))).isEqualTo(2)
        assertThat(Day1A.calculateFuel(listOf(14))).isEqualTo(2)
        assertThat(Day1A.calculateFuel(listOf(1969))).isEqualTo(654)
        assertThat(Day1A.calculateFuel(listOf(100756))).isEqualTo(33583)
        assertThat(Day1A.calculateFuel(listOf(12, 14))).isEqualTo(4)
        assertThat(Day1A.calculateFuel(listOf(1969, 100756))).isEqualTo(654+33583)
    }
}