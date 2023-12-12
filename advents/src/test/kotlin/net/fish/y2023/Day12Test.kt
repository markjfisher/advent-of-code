package net.fish.y2023

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {
    @Test
    fun `can find combinations`() {
        assertThat(Day12.opDamExtractor(".")).containsExactly(".")
        assertThat(Day12.opDamExtractor("?")).containsExactly(".", "#")
        assertThat(Day12.opDamExtractor(".?.")).containsExactly("...", ".#.")
        assertThat(Day12.opDamExtractor("??")).containsExactlyInAnyOrder("..", ".#", "#.", "##")
    }

    @Test
    fun `can find sequence lengths`() {
        assertThat(Day12.opDamToSeq(".")).isEmpty()
        assertThat(Day12.opDamToSeq("#")).containsExactly(1)
        assertThat(Day12.opDamToSeq("#.")).containsExactly(1)
        assertThat(Day12.opDamToSeq("#.#")).containsExactly(1,1)
        assertThat(Day12.opDamToSeq("##.#")).containsExactly(2,1)
        assertThat(Day12.opDamToSeq("#.##")).containsExactly(1,2)
        assertThat(Day12.opDamToSeq("#.##.")).containsExactly(1,2)
        assertThat(Day12.opDamToSeq("#.##..")).containsExactly(1,2)
        assertThat(Day12.opDamToSeq("....#.....##..#####.........")).containsExactly(1,2,5)
    }

    @Test
    fun `can find matching count`() {
        assertThat(Day12.matchingOpDam("???.###", listOf(1, 1, 3))).isEqualTo(1)
        assertThat(Day12.matchingOpDam(".??..??...?##.", listOf(1, 1, 3))).isEqualTo(4)
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
        assertThat(v).isEqualTo(0)
    }
}