package net.fish.y2023

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day08Test {
    @Test
    fun `can parse instructions`() {
        val data = resourceStrings("/2023/day08-test1.txt")
        val i1 = Day08.readPuzzle(data)
        assertThat(i1.dirs).isEqualTo("RL")
        assertThat(i1.nodes).containsExactlyEntriesOf(mapOf(
            "AAA" to Pair("BBB", "CCC"),
            "BBB" to Pair("DDD", "EEE"),
            "CCC" to Pair("ZZZ", "GGG"),
            "DDD" to Pair("DDD", "DDD"),
            "EEE" to Pair("EEE", "EEE"),
            "GGG" to Pair("GGG", "GGG"),
            "ZZZ" to Pair("ZZZ", "ZZZ"),
        ))
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day08.doPart1(Day08.readPuzzle(resourceStrings("/2023/day08-test1.txt")))).isEqualTo(2)
        assertThat(Day08.doPart1(Day08.readPuzzle(resourceStrings("/2023/day08-test2.txt")))).isEqualTo(6)
    }

    @Test
    fun `can do part 2`() {
        assertThat(Day08.doPart2(Day08.readPuzzle(resourceStrings("/2023/day08-test3.txt")))).isEqualTo(6)
    }

}