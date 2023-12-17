package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day17Test {
    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day17-test.txt")
        val v = Day17.doPart1(data)
        assertThat(v).isEqualTo(102)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day017-test.txt")
        val v = Day17.doPart2(data)
        assertThat(v).isEqualTo(0)
    }

}