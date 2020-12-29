package net.fish.y2020

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class Day23Test {
    @Test
    fun `p1 on test data`() {
        val answer = Day23.doPart1(listOf(3, 8, 9, 1, 2, 5, 4, 6, 7))
        assertThat(answer).isEqualTo("67384529")
    }

    @Disabled("takes too long")
    @Test
    fun `p2 on test data`() {
        val answer = Day23.doPart2(listOf(3, 8, 9, 1, 2, 5, 4, 6, 7))
        assertThat(answer).isEqualTo(149245887792)
    }
}