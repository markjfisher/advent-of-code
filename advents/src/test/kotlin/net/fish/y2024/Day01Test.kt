package net.fish.y2024

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test {

    @Test
    fun `can split lines to pairs of ints`() {
        val d1 = """
            6   1
            5   2
            4   3
        """.trimIndent().lines()
        val ls = Day01.linesToLists(d1)
        assertThat(ls.first).containsExactly(4, 5, 6)
        assertThat(ls.second).containsExactly(1, 2, 3)
    }

    @Test
    fun `can do part 1`() {
        val data = Day01.linesToLists(resourcePath("/2024/day01-test.txt"))
        val v = Day01.doPart1(data)
        assertThat(v).isEqualTo(11)
    }
    @Test
    fun `can do part 2`() {
        val data = Day01.linesToLists(resourcePath("/2024/day01-test.txt"))
        val v = Day01.doPart2(data)
        assertThat(v).isEqualTo(31)
    }
}