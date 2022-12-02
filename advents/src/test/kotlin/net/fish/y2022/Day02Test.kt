package net.fish.y2022

import net.fish.y2022.Day02.HandTypes.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day02Test {
    val testData = listOf(
        "A Y",
        "B X",
        "C Z"
    )

    @Test
    fun `test part 1`() {
        assertThat(Day02.doPart1(testData)).isEqualTo(15)
    }

    @Test
    fun `test part 2`() {
        assertThat(Day02.doPart2(testData)).isEqualTo(12)
    }

    @Test
    fun `scoring different hand types`() {
        assertThat(Day02.scoreOf(ROCK, ROCK)).isEqualTo(4)
        assertThat(Day02.scoreOf(ROCK, PAPER)).isEqualTo(8)
        assertThat(Day02.scoreOf(ROCK, SCISSORS)).isEqualTo(3)

        assertThat(Day02.scoreOf(PAPER, ROCK)).isEqualTo(1)
        assertThat(Day02.scoreOf(PAPER, PAPER)).isEqualTo(5)
        assertThat(Day02.scoreOf(PAPER, SCISSORS)).isEqualTo(9)

        assertThat(Day02.scoreOf(SCISSORS, ROCK)).isEqualTo(7)
        assertThat(Day02.scoreOf(SCISSORS, PAPER)).isEqualTo(2)
        assertThat(Day02.scoreOf(SCISSORS, SCISSORS)).isEqualTo(6)
    }
}