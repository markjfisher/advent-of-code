package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test {
    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day01-test.txt")
        val v = Day01.doPart1(data)
        assertThat(v).isEqualTo(142)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day01b-test.txt")
        val v = Day01.doPart2(data)
        assertThat(v).isEqualTo(281)
    }
    @Test
    fun `can replace names with numbers`() {
        val d = "oneighthree1oneightwo"
        assertThat(Day01.replaceNamesWithNumbers(d)).containsExactly(1, 8, 3, 1, 1, 8, 2)
    }

}