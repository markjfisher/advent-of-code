package net.fish.y2023

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day11Test  {
    @Test
    fun `can calculate result`() {
        val universe = Day11.parsePuzzle(resourcePath("/2023/day11-test.txt"))
        // factor of "2" is Part 1 result
        assertThat(Day11.doPart1(universe, 2L)).isEqualTo(374)

        // P2 examples with 10 and 100 factor, as required in the examples.
        assertThat(Day11.doPart1(universe, 10L)).isEqualTo(1030)
        assertThat(Day11.doPart1(universe, 100L)).isEqualTo(8410)
    }

    @Test
    fun `can parse universe`() {
        val data = resourcePath("/2023/day11-test.txt")
        val universe = Day11.parsePuzzle(data)
        assertThat(universe.width).isEqualTo(10)
        assertThat(universe.height).isEqualTo(10)
        assertThat(universe.points).containsExactly(
            Point(x=3, y=0),
            Point(x=7, y=1),
            Point(x=0, y=2),
            Point(x=6, y=4),
            Point(x=1, y=5),
            Point(x=9, y=6),
            Point(x=7, y=8),
            Point(x=0, y=9),
            Point(x=4, y=9)
        )
        assertThat(universe.emptyColumns).containsExactly(2, 5, 8)
        assertThat(universe.emptyRows).containsExactly(3, 7)
    }

}