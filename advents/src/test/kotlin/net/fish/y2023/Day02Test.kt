package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day02Test {
    @Test
    fun `can parse games`() {
        val games = Day02.toGames(resourcePath("/2023/day02-test.txt"))
        assertThat(games).hasSize(5)
        assertThat(games[0]).isEqualTo(Day02.Game(1, listOf(Day02.SubGame(4, 0, 3), Day02.SubGame(1, 2, 6), Day02.SubGame(0, 2, 0))))
    }

    @Test
    fun `can do part 1`() {
        val games = Day02.toGames(resourcePath("/2023/day02-test.txt"))
        assertThat(Day02.doPart1(games)).isEqualTo(8)
    }

    @Test
    fun `can do part 2`() {
        val games = Day02.toGames(resourcePath("/2023/day02-test.txt"))
        assertThat(Day02.doPart2(games)).isEqualTo(2286)
    }

}