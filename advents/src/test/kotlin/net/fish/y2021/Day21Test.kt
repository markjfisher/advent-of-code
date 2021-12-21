package net.fish.y2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day21Test {
    @Test
    fun `can generate score sequence`() {
        val scores = Day21.generateScores(4, 8)
        val scoresIter = scores.iterator()
        assertThat(scoresIter.next()).isEqualTo(Triple(10, 0, 3))
        assertThat(scoresIter.next()).isEqualTo(Triple(10, 3, 6))
        assertThat(scoresIter.next()).isEqualTo(Triple(14, 3, 9))
        assertThat(scoresIter.next()).isEqualTo(Triple(14, 9, 12))
    }

    @Test
    fun `can score to 1000`() {
        val scores = Day21.generateScores(4, 8)
        val loseStart = scores.dropWhile { it.first < 1000 && it.second < 1000 }
        val next = loseStart.first()
        assertThat(next).isEqualTo(Triple(1000, 745, 993))
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day21.doPart1(Pair(4, 8))).isEqualTo(739785L)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day21.doPart2(Pair(4, 8))).isEqualTo(444356092776315L)
    }

}