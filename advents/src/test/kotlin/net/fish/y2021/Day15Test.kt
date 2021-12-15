package net.fish.y2021

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day15Test {
    private val testData = resourcePath("/2021/day15-test.txt")

    @Test
    fun `can read test data and create part 1 path`() {
        val chitonGrid = Day15.createChitonAStar(testData)

        val start = chitonGrid.grid.square(0, 0)!!
        val end = chitonGrid.grid.square(9, 9)!!
        val result = chitonGrid.findPath(start, end)
        val path = result.first
        assertThat(path.map { Point(it.x, it.y) }).containsExactly(
            Point(x=0, y=0),
            Point(x=0, y=1),
            Point(x=0, y=2),
            Point(x=1, y=2),
            Point(x=2, y=2),
            Point(x=3, y=2),
            Point(x=4, y=2),
            Point(x=5, y=2),
            Point(x=6, y=2),
            Point(x=6, y=3),
            Point(x=7, y=3),
            Point(x=7, y=4),
            Point(x=8, y=4),
            Point(x=8, y=5),
            Point(x=8, y=6),
            Point(x=8, y=7),
            Point(x=8, y=8),
            Point(x=9, y=8),
            Point(x=9, y=9)
        )
        val cost = result.second.toInt() - chitonGrid.storage.getData(start)!!.cost
        assertThat(cost).isEqualTo(40)
    }

    @Test
    fun `can do part 1`() {
        assertThat(Day15.doPart1(testData)).isEqualTo(40)
    }
}