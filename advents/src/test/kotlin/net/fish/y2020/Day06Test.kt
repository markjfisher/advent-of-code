package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {
    private val data = resourcePath("/2020/day06-test.txt").joinToString("\n")

    @Test
    fun `should count unique answers by group`() {
        assertThat(Day06.listOfUniqueAnswersByGroup(data)).containsExactly(3, 3, 3, 1, 1)
    }

    @Test
    fun `should count each groups common answers`() {
        assertThat(Day06.listOfCommonAnswersByGroup(data)).containsExactly(3, 0, 1, 1, 1)
    }

}