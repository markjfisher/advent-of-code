package net.fish.y2023

import net.fish.resourcePath
import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test {

    @Test
    fun `can do parse data`() {
        val data = resourceStrings("/2023/day05-test.txt")
        assertThat(Day05.toAlmanac(data).seeds).containsExactly(
            79L, 14L, 55L, 13L
        )
        assertThat(Day05.toAlmanac(data).mappings).containsExactly(
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(98, 2, 50),
                Day05.RangeInterval(50, 48, 52),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(15, 37, 0),
                Day05.RangeInterval(52, 2, 37),
                Day05.RangeInterval(0, 15, 39),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(53, 8, 49),
                Day05.RangeInterval(11, 42, 0),
                Day05.RangeInterval(0, 7, 42),
                Day05.RangeInterval(7, 4, 57),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(18, 7, 88),
                Day05.RangeInterval(25, 70, 18),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(77, 23, 45),
                Day05.RangeInterval(45, 19, 81),
                Day05.RangeInterval(64, 13, 68),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(69, 1, 0),
                Day05.RangeInterval(0, 69, 1),
            )),
            Day05.RangeMapping(intervals = listOf(
                Day05.RangeInterval(56, 37, 60),
                Day05.RangeInterval(93, 4, 56),
            )),
        )
    }

    @Test
    fun `can do part 1`() {
        val data = resourceStrings("/2023/day05-test.txt")
        assertThat(Day05.doPart1(data)).isEqualTo(35)
    }

    @Test
    fun `can do part 2`() {
        val data = resourceStrings("/2023/day05-test.txt")
        assertThat(Day05.doPart2(data)).isEqualTo(46)
    }

}