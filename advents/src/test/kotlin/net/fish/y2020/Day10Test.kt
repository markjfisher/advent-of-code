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

        // is there a pattern to this sequence?
        assertThat(Day10.doPart2(listOf(1))).isEqualTo(1)
        assertThat(Day10.doPart2(listOf(1, 2))).isEqualTo(2)
        assertThat(Day10.doPart2((1..3).toList())).isEqualTo(4)
        assertThat(Day10.doPart2((1..4).toList())).isEqualTo(7)
        assertThat(Day10.doPart2((1..5).toList())).isEqualTo(13)
        assertThat(Day10.doPart2((1..6).toList())).isEqualTo(24)
        assertThat(Day10.doPart2((1..7).toList())).isEqualTo(44)
        assertThat(Day10.doPart2((1..8).toList())).isEqualTo(81)
        assertThat(Day10.doPart2((1..9).toList())).isEqualTo(149)
        assertThat(Day10.doPart2((1..10).toList())).isEqualTo(274)
        assertThat(Day10.doPart2((1..11).toList())).isEqualTo(504)
        assertThat(Day10.doPart2((1..12).toList())).isEqualTo(927)
        assertThat(Day10.doPart2((1..13).toList())).isEqualTo(1705)
        assertThat(Day10.doPart2((1..100).toList())).isEqualTo(7367864567128947527L)
    }
}