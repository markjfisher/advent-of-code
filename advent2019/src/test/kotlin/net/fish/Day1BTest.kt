package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day1BTest {
    @Test
    fun `recursive fuel calculations`() {
        assertThat(Day1B.calculateFuel(listOf(14))).isEqualTo(2)
        assertThat(Day1B.calculateFuel(listOf(1969))).isEqualTo(966)
        assertThat(Day1B.calculateFuel(listOf(100756))).isEqualTo(50346)
        assertThat(Day1B.calculateFuel(listOf(14, 1969, 100756))).isEqualTo(2 + 966 + 50346)
    }
}