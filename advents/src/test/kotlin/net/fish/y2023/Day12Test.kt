package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {
    @Test
    fun `can do new count`() {
        assertThat(Day12.count("#?..#", listOf(2, 1))).isEqualTo(1)
        assertThat(Day12.count("?", listOf(1))).isEqualTo(1)
        assertThat(Day12.count("??", listOf(1))).isEqualTo(2)
        assertThat(Day12.count("???.###", listOf(1, 1 , 3))).isEqualTo(1)
        assertThat(Day12.count("?###????????", listOf(3, 2, 1))).isEqualTo(10)
        assertThat(Day12.count(".#?.#?.#?.#?.#", listOf(1, 1, 1, 1, 1))).isEqualTo(1)
        assertThat(Day12.count(".??..??...?##.?.??..??...?##.?.??..??...?##.?.??..??...?##.?.??..??...?##.", listOf(1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3))).isEqualTo(16384)
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day12-test.txt")
        val v = Day12.doPart1(data)
        assertThat(v).isEqualTo(21)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day12-test.txt")
        val v = Day12.doPart2(data)
        assertThat(v).isEqualTo(525152)
    }
}