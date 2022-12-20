package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day20Test {
    @Test
    fun `can do part 1`() {
        assertThat(Day20.doPart1(resourcePath("/2022/day20-test.txt").map { it.toLong() })).isEqualTo(3)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day20.doPart2(resourcePath("/2022/day20-test.txt").map { it.toLong() * 811589153L})).isEqualTo(1623178306)
    }

    @Test
    fun `can decrypt various`() {
        assertThat(Day20.decrypt(listOf(3L, 1L, 0L)).values()).containsExactly(3L, 1L, 0L)
    }
}