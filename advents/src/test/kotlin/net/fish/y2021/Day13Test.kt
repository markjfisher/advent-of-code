package net.fish.y2021

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class Day13Test {
    private val testData = resourcePath("/2021/day13-test.txt")

    @Test
    fun `can create FoldingGrid`() {
        val grid = Day13.createFoldingGrid(testData)
        assertThat(grid.points).hasSize(18)
        assertThat(grid.folds).hasSize(2)

        assertThat(grid.points).containsExactlyInAnyOrder(
            Point(6, 10),
            Point(0, 14),
            Point(9, 10),
            Point(0, 3),
            Point(10, 4),
            Point(4, 11),
            Point(6, 0),
            Point(6, 12),
            Point(4, 1),
            Point(0, 13),
            Point(10, 12),
            Point(3, 4),
            Point(3, 0),
            Point(8, 4),
            Point(1, 10),
            Point(2, 14),
            Point(8, 10),
            Point(9, 0)
        )

        assertThat(grid.folds).containsExactlyInAnyOrder(
            Day13.Fold(true, 7),
            Day13.Fold(false, 5)
        )
    }

    @Test
    fun `can fold grid once`() {
        val grid = Day13.createFoldingGrid(testData)
        val newGrid = grid.fold(1)
        newGrid.printGrid()
        assertThat(newGrid.points).hasSize(17)
        assertThat(newGrid.points).containsExactlyInAnyOrder(
            Point(0, 0), Point(2, 0), Point(3, 0), Point(6, 0), Point(9, 0),
            Point(0, 1), Point(4, 1),
            Point(6, 2), Point(10, 2),
            Point(0, 3), Point(4, 3),
            Point(1, 4), Point(3, 4), Point(6, 4), Point(8, 4), Point(9, 4), Point(10, 4)
        )
    }

    @Test
    fun `can complete all folds`() {
        val grid = Day13.createFoldingGrid(testData)
        val newGrid = grid.fold()
        newGrid.printGrid()
    }
}