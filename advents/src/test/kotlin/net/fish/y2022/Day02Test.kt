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
        assertThat(ROCK.score(ROCK)).isEqualTo(4)
        assertThat(PAPER.score(ROCK)).isEqualTo(8)
        assertThat(SCISSORS.score(ROCK)).isEqualTo(3)
        assertThat(ROCK.score(PAPER)).isEqualTo(1)
        assertThat(PAPER.score(PAPER)).isEqualTo(5)
        assertThat(SCISSORS.score(PAPER)).isEqualTo(9)
        assertThat(ROCK.score(SCISSORS)).isEqualTo(7)
        assertThat(PAPER.score(SCISSORS)).isEqualTo(2)
        assertThat(SCISSORS.score(SCISSORS)).isEqualTo(6)
    }
}