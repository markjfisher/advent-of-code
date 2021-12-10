package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day10Test {
    private val testData = resourcePath("/2021/day10-test.txt")
    @Test
    fun `should score input data correctly for part 1`() {
        assertThat(Day10.doPart1(testData)).isEqualTo(26397L)
    }

    @Test
    fun `should score input data correctly for part 2`() {
        assertThat(Day10.doPart2(testData)).isEqualTo(288957)
    }
}