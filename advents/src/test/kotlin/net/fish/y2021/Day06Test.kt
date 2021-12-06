package net.fish.y2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {
    @Test
    fun `iterations works as expected`() {
        val fishTimes = listOf(3, 4, 3, 1, 2)
        assertThat(Day06.doIterations(fishTimes, 1)).isEqualTo(5)
    }

    @Test
    fun `80 iterations works as expected`() {
        val fishTimes = listOf(3, 4, 3, 1, 2)
        assertThat(Day06.doIterations(fishTimes, 80)).isEqualTo(5934)
    }

    @Test
    fun `256 iterations works as expected`() {
        val fishTimes = listOf(3, 4, 3, 1, 2)
        assertThat(Day06.doIterations(fishTimes, 256)).isEqualTo(26984457539)
    }
}