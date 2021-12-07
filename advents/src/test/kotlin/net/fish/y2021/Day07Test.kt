package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test {
    val initialInput = resourcePath("/2021/day07-test.txt").first().split(",").map { it.toInt() }

    @Test
    fun `should minimize input data part 1`() {
        assertThat(Day07.doPart1(initialInput)).isEqualTo(Pair(37, 2))
    }

    @Test
    fun `should minimize input data part 2`() {
        assertThat(Day07.doPart2(initialInput)).isEqualTo(Pair(168, 5))
    }
}