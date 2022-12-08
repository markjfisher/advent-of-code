package net.fish.y2022

import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day08Test {
    @Test
    fun `can do part 1`() {
        assertThat(Day08.doPart1(Day08.toTreeGrid(resourcePath("/2022/day08-test.txt")))).isEqualTo(21)
    }

    @Test
    fun `can find scenic score`() {
        val grid = Day08.toTreeGrid(resourcePath("/2022/day08-test.txt"))
        assertThat(grid.scenicScore(Point(2,1))).isEqualTo(4L)
        assertThat(grid.scenicScore(Point(2,3))).isEqualTo(8L)
    }

    @Test
    fun `can find viewing distance`() {
        val grid = Day08.toTreeGrid(resourcePath("/2022/day08-test.txt"))
        assertThat(grid.viewingDistance(Point(2,1), Direction.NORTH)).isEqualTo(1L)
        assertThat(grid.viewingDistance(Point(2,1), Direction.WEST)).isEqualTo(1L)
        assertThat(grid.viewingDistance(Point(2,1), Direction.EAST)).isEqualTo(2L)
        assertThat(grid.viewingDistance(Point(2,1), Direction.SOUTH)).isEqualTo(2L)

        assertThat(grid.viewingDistance(Point(2,3), Direction.NORTH)).isEqualTo(2L)
        assertThat(grid.viewingDistance(Point(2,3), Direction.WEST)).isEqualTo(2L)
        assertThat(grid.viewingDistance(Point(2,3), Direction.SOUTH)).isEqualTo(1L)
        assertThat(grid.viewingDistance(Point(2,3), Direction.EAST)).isEqualTo(2L)
    }

    @Test
    fun `scenic points`() {
        val grid = Day08.toTreeGrid(resourcePath("/2022/day08-test.txt"))
        assertThat(grid.bestScenicPoint()).isEqualTo(Point(2, 3))
        assertThat(grid.maxScenicScore()).isEqualTo(8L)
    }

}