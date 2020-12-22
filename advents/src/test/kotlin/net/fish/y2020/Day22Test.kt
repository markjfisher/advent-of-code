package net.fish.y2020

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day22Test {
    private val game = Day22.toGame(resourceStrings("/2020/day22-test.txt"))
    @Test
    fun `p1 score`() {
        assertThat(game.playEasy().score()).isEqualTo(306)
    }

    @Test
    fun `p2 score`() {
        assertThat(Day22.doPart2(game)).isEqualTo(291)
    }
}