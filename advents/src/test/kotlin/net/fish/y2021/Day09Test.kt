package net.fish.y2021

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception

internal class Day09Test {
    private val testGrid = Day09.toGrid(resourcePath("/2021/day09-test.txt"))

    @Test
    fun `part 1 on test data`() {
        assertThat(Day09.doPart1(testGrid)).isEqualTo(15)
    }

    @Test
    fun `part 2 on test data`() {
        assertThat(Day09.doPart2(testGrid)).isEqualTo(1134)
    }

    @Test
    fun `can find local minima`() {
        assertThat(testGrid.localMinima()).containsExactlyInAnyOrder(Point(1, 0), Point(9, 0), Point(2, 2), Point(6, 4))
    }

    @Test
    fun `connected points`() {
        assertThat(testGrid.connectPoints(mutableSetOf(Point(1, 0)), testGrid.neighbourPointsNSEW(Point(1, 0)).toMutableSet())).containsExactlyInAnyOrder(
            Point(0, 0), Point(1, 0), Point(0, 1)
        )

        assertThat(testGrid.connectPoints(mutableSetOf(Point(9, 0)), testGrid.neighbourPointsNSEW(Point(9, 0)).toMutableSet())).containsExactlyInAnyOrder(
            Point(9,0), Point(8, 0), Point(7, 0), Point(6, 0), Point(5, 0),
            Point(9,1), Point(8, 1), Point(6, 1),
            Point(9,2)
        )

        assertThat(testGrid.connectPoints(mutableSetOf(Point(2, 2)), testGrid.neighbourPointsNSEW(Point(2, 2)).toMutableSet())).containsExactlyInAnyOrder(
            Point(2, 1), Point(3, 1), Point(4, 1),
            Point(1, 2), Point(2, 2), Point(3, 2), Point(4, 2), Point(5, 2),
            Point(0, 3), Point(1, 3), Point(2, 3), Point(3, 3), Point(4, 3),
            Point(1, 4)
        )

        assertThat(testGrid.connectPoints(mutableSetOf(Point(6, 4)), testGrid.neighbourPointsNSEW(Point(6, 4)).toMutableSet())).containsExactlyInAnyOrder(
            Point(7, 2),
            Point(6, 3), Point(7, 3), Point(8, 3),
            Point(5, 4), Point(6, 4), Point(7, 4), Point(8, 4), Point(9, 4)
        )

    }

    @Test
    fun `grid basins`() {
        val basins = testGrid.basins()
        assertThat(basins.map { it.localMinimum }).containsExactlyInAnyOrder(Point(1, 0), Point(9, 0), Point(2, 2), Point(6, 4))

        // test basin from any point in a basin gives that basin
        basins.forEach { basin ->
            val points = basin.basinPoints
            points.forEach { point ->
                println("checking point $point belongs to basin $basin")
                assertThat(testGrid.calculateBasinFor(point)).isEqualTo(basin)
            }
        }
    }

    @Test
    fun `can create grid from data`() {
        val inputData = listOf("012", "345", "678")
        val grid = Day09.toGrid(inputData)
        assertThat(grid.at(Point(0, 0))).isEqualTo(0)
        assertThat(grid.at(Point(1, 0))).isEqualTo(1)
        assertThat(grid.at(Point(2, 0))).isEqualTo(2)

        assertThat(grid.at(Point(0, 1))).isEqualTo(3)
        assertThat(grid.at(Point(1, 1))).isEqualTo(4)
        assertThat(grid.at(Point(2, 1))).isEqualTo(5)

        assertThat(grid.at(Point(0, 2))).isEqualTo(6)
        assertThat(grid.at(Point(1, 2))).isEqualTo(7)
        assertThat(grid.at(Point(2, 2))).isEqualTo(8)

        assertThrows<Exception> { grid.at(Point(3, 3)) }
    }

    @Test
    fun `boundary of grid`() {
        val inputData = listOf("012", "345", "678")
        val grid = Day09.toGrid(inputData)
        assertThat(grid.boundary).isEqualTo(Pair(Point(0, 0), Point(2, 2)))
    }

    @Test
    fun `can get all points`() {
        assertThat(Day09.toGrid(listOf("01", "23")).allPoints()).containsExactlyInAnyOrder(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1))
    }

    @Test
    fun `can get neighbour NSEW points`() {
        // 012
        // 345
        // 678

        val grid = Day09.Grid(mapOf(
            Point(0, 0) to 0,
            Point(1, 0) to 1,
            Point(2, 0) to 2,
            Point(0, 1) to 3,
            Point(1, 1) to 4,
            Point(2, 1) to 5,
            Point(0, 2) to 6,
            Point(1, 2) to 7,
            Point(2, 2) to 8
        ))
        assertThat(grid.neighbourPointsNSEW(Point(0, 0))).containsExactlyInAnyOrder(
            Point(1, 0), Point(0, 1)
        )
        assertThat(grid.neighbourPointsNSEW(Point(1, 0))).containsExactlyInAnyOrder(
            Point(2, 0), Point(0, 0), Point(1, 1)
        )
        assertThat(grid.neighbourPointsNSEW(Point(2, 0))).containsExactlyInAnyOrder(
            Point(1, 0), Point(2, 1)
        )
        assertThat(grid.neighbourPointsNSEW(Point(0, 2))).containsExactlyInAnyOrder(
            Point(0, 1), Point(1, 2)
        )
        assertThat(grid.neighbourPointsNSEW(Point(2, 2))).containsExactlyInAnyOrder(
            Point(1, 2), Point(2, 1)
        )
        assertThat(grid.neighbourPointsNSEW(Point(1, 1))).containsExactlyInAnyOrder(
            Point(1, 0), Point(2, 1), Point(1, 2), Point(0, 1)
        )
    }

    @Test
    fun `can get neighbour NSEW values`() {
        // 012
        // 345
        // 678
        val grid = Day09.Grid(mapOf(
            Point(0, 0) to 0,
            Point(1, 0) to 1,
            Point(2, 0) to 2,
            Point(0, 1) to 3,
            Point(1, 1) to 4,
            Point(2, 1) to 5,
            Point(0, 2) to 6,
            Point(1, 2) to 7,
            Point(2, 2) to 8
        ))
        assertThat(grid.neighbourValuesNSEW(Point(0, 0))).containsExactlyInAnyOrder(
            1, 3
        )
        assertThat(grid.neighbourValuesNSEW(Point(2, 0))).containsExactlyInAnyOrder(
            1, 5
        )
        assertThat(grid.neighbourValuesNSEW(Point(0, 2))).containsExactlyInAnyOrder(
            3, 7
        )
        assertThat(grid.neighbourValuesNSEW(Point(2, 2))).containsExactlyInAnyOrder(
            5, 7
        )
        assertThat(grid.neighbourValuesNSEW(Point(1, 1))).containsExactlyInAnyOrder(
            1, 3, 5, 7
        )
    }
}