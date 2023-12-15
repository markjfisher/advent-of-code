package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day15Test {
    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day15-test.txt")
        val v = Day15.doPart1(data)
        assertThat(v).isEqualTo(1320)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day15-test.txt")
        val v = Day15.doPart2(data)
        assertThat(v).isEqualTo(145)
    }

}