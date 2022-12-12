package net.fish.y2022

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day12Test {
    @Test
    fun `can parse grid`() {
        val grid = Day12.toGrid(listOf(
            "abEz",
            "Scxp"
        ))
        assertThat(grid.start).isEqualTo(Point(0,1))
        assertThat(grid.end).isEqualTo(Point(2,0))
    }

    @Test
    fun `can find simple path for p1`() {
        val grid = Day12.toGrid(listOf("SbcE"), 4)
        assertThat(grid.shortestPathLength(grid.start, { it == grid.end }) { from, p -> grid.at(p) <= grid.at(from) + 1 }).isEqualTo(3)
    }

    @Test
    fun `can find path with kink for p1`() {
        val grid = Day12.toGrid(listOf(
            "Sbzzz",
            "zabcE"
        ), 4)
        assertThat(grid.shortestPathLength(grid.start, { it == grid.end }) { from, p -> grid.at(p) <= grid.at(from) + 1 }).isEqualTo(5)
    }

    @Test
    fun `can do multiple paths for p1`() {
        val grid = Day12.toGrid(listOf(
            "Sbcbc",
            "zbzzc",
            "babcE"
        ), 4)
        assertThat(grid.shortestPathLength(grid.start, { it == grid.end }) { from, p -> grid.at(p) <= grid.at(from) + 1 }).isEqualTo(6)
    }

    @Test
    fun `can do test part 1`() {
        val grid = Day12.toGrid(resourcePath("/2022/day12-test.txt"))
        assertThat(grid.shortestPathLength(grid.start, { it == grid.end }) { from, p -> grid.at(p) <= grid.at(from) + 1 }).isEqualTo(31)
    }

    @Test
    fun `can do test part 2`() {
        val grid = Day12.toGrid(resourcePath("/2022/day12-test.txt"))
        assertThat(grid.shortestPathLength(grid.end, { grid.at(it) == 1 }) { from, p -> grid.at(from) <= grid.at(p) + 1 }).isEqualTo(29)
    }
}