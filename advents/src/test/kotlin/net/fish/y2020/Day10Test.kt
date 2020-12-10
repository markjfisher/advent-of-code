package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day10Test {
    private val data1 = resourcePath("/2020/day10-test-1.txt").map { it.toInt() }
    private val data2 = resourcePath("/2020/day10-test-2.txt").map { it.toInt() }

    @Test
    fun `should find differences`() {
        assertThat(Day10.differences(data1)).containsExactly(1, 3, 1, 1, 1, 3, 1, 1, 3, 1, 3, 3)
    }

    @Test
    fun `should find 1s times 3s`() {
        assertThat(Day10.doPart1(data1)).isEqualTo(35)
    }

    @Test
    fun `count lines`() {
        assertThat(Day10.doPart2(data1)).isEqualTo(8)
        assertThat(Day10.doPart2(data2)).isEqualTo(19208)
    }
}