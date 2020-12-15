package net.fish.y2020

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day15Test {
    @Test
    fun `game test 1`() {
        val game = Day15.SequenceGame()
        game.add(0).add(3).add(6)
        assertThat(game.heard).containsAllEntriesOf(mapOf(0 to 1, 3 to 2, 6 to 3))
        assertThat(game.isFirst).isTrue
        assertThat(game.round).isEqualTo(3)
        assertThat(game.lastSpokenAt).isEqualTo(0)

        game.step()
        assertThat(game.heard).containsAllEntriesOf(mapOf(0 to 4, 3 to 2, 6 to 3))
        assertThat(game.isFirst).isFalse
        assertThat(game.round).isEqualTo(4)
        assertThat(game.lastSpokenAt).isEqualTo(1)

        game.step()
        assertThat(game.heard).containsAllEntriesOf(mapOf(0 to 4, 3 to 5, 6 to 3))
        assertThat(game.isFirst).isFalse
        assertThat(game.round).isEqualTo(5)
        assertThat(game.lastSpokenAt).isEqualTo(2)

        game.step()
        assertThat(game.heard).containsAllEntriesOf(mapOf(0 to 4, 3 to 6, 6 to 3))
        assertThat(game.isFirst).isFalse
        assertThat(game.round).isEqualTo(6)
        assertThat(game.lastSpokenAt).isEqualTo(5)

        game.step()
        assertThat(game.heard).containsAllEntriesOf(mapOf(0 to 4, 3 to 6, 6 to 3, 1 to 7))
        assertThat(game.isFirst).isTrue
        assertThat(game.round).isEqualTo(7)
        assertThat(game.lastSpokenAt).isEqualTo(0)
    }

    @Test
    fun `run game`() {
        assertThat(Day15.runGame(listOf(0, 3, 6), 2020)).isEqualTo(436)
        assertThat(Day15.runGame(listOf(1, 3, 2), 2020)).isEqualTo(1)
        assertThat(Day15.runGame(listOf(2, 1, 3), 2020)).isEqualTo(10)


    }
}