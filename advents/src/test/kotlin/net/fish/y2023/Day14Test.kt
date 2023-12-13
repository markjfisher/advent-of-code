package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day14Test {
    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day14-test.txt")
        val v = Day14.doPart1(data)
        assertThat(v).isEqualTo(0)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day14-test.txt")
        val v = Day01.doPart2(data)
        assertThat(v).isEqualTo(0)
    }

}