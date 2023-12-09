package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day09Test {
    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day09-test.txt")
        assertThat(Day09.doPart1(data)).isEqualTo(114L)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day09-test.txt")
        assertThat(Day09.doPart2(data)).isEqualTo(2L)
    }
}