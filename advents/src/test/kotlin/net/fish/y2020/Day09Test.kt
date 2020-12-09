package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day09Test {
    private val data = resourcePath("/2020/day09-test.txt").map { it.toLong() }

    @Test
    fun `window over test data finds solution for part1`() {
        assertThat(Day09.doPart1(data, 5)).isEqualTo(127)
    }

    @Test
    fun `min and max values in contiguous block that adds up to part1 result sum to expected value`() {
        assertThat(Day09.doPart2(data, 127)).isEqualTo(62)
    }
}