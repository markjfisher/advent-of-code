package net.fish.y2023

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {

    @Test
    fun `can do part 1`() {
        assertThat(Day06.doPart1(listOf(7, 15, 30), listOf(9, 40, 200))).isEqualTo(288)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day06.doPart2(listOf(71530), listOf(940200))).isEqualTo(71503)
    }

}