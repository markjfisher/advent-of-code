package net.fish.y2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day03Test {
    val testData = listOf(
        "00100",
        "11110",
        "10110",
        "10111",
        "10101",
        "01111",
        "00111",
        "11100",
        "10000",
        "11001",
        "00010",
        "01010"
    )

    @Test
    fun `test part 1`() {
        assertThat(Day03.doPart1(testData)).isEqualTo(22 * 9)
    }

    @Test
    fun `test part 2`() {
        assertThat(Day03.doPart2(testData)).isEqualTo(230)
    }

    @Test
    fun `lsb checks`() {
        val l = listOf("01111", "01010")
        assertThat(Day03.lsb(l, 2)).isEqualTo(0)
    }

    @Test
    fun `msb checks`() {
        val l = listOf("01111", "01010")
        assertThat(Day03.msb(l, 2)).isEqualTo(1)
    }

    @Test
    fun `gamma function`() {
        val gamma = Day03.calculateGamma(testData)
        assertThat(gamma).isEqualTo(22)
        assertThat(Day03.calculateEpsilon(gamma, 5)).isEqualTo(9)
    }
}