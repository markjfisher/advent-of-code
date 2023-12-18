package net.fish.y2023

import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Point
import net.fish.geometry.asStringGrid
import net.fish.geometry.bounds
import net.fish.network.findShortestPathByPredicate
import net.fish.resourcePath
import net.fish.y2021.GridDataUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day17Test {
    @Test
    fun `can do part 1`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test.txt"))
        val v = Day17.doPart1(grid)
        assertThat(v).isEqualTo(102)
    }

    @Test
    fun `can get paths of part 1 via east path`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test.txt"))
        val start = Day17.PointInDir(Point(0, 0), EAST, 0)
        val end = grid.keys.bounds().second
        val result = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.neighbours().filter { (n) -> n in grid }},
            { _, (p) -> grid[p]!! }
        )
        val path = result.getPath()
        val points = path.map { it.point }
        assertThat(points.asStringGrid()).isEqualTo("""
            ###..####....
            ..####..##...
            .........##..
            ..........#..
            ..........##.
            ...........#.
            ...........#.
            ...........##
            ............#
            ............#
            ...........##
            ...........#.
            ...........##
        """.trimIndent())
    }

    @Test
    fun `can get paths of part 1 via south path and it does not change result`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test.txt"))
        // even though we are facing south, we're allowed to go immediately to east, so solution is same
        val start = Day17.PointInDir(Point(0, 0), SOUTH, 0)
        val end = grid.keys.bounds().second
        val result = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.neighbours().filter { (n) -> n in grid }},
            { _, (p) -> grid[p]!! }
        )
        val path = result.getPath()
        val points = path.map { it.point }
        assertThat(points.asStringGrid()).isEqualTo("""
            ###..####....
            ..####..##...
            .........##..
            ..........#..
            ..........##.
            ...........#.
            ...........#.
            ...........##
            ............#
            ............#
            ...........##
            ...........#.
            ...........##
        """.trimIndent())
    }

    @Test
    fun `can do part 2`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test.txt"))
        val v = Day17.doPart2(grid)
        assertThat(v).isEqualTo(94)
    }

    @Test
    fun `can get paths of 2`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test.txt"))
        val southPathResults = Day17.searchInDirection(grid, SOUTH)
        val southPath = southPathResults.getPath()
        val southPathPoints = southPath.map { it.point }
        assertThat(southPathPoints.asStringGrid()).isEqualTo("""
            #............
            #............
            #............
            #............
            #............
            #............
            #............
            #............
            #####........
            ....#........
            ....#........
            ....#........
            ....#########
        """.trimIndent())
        assertThat(southPathResults.getScore()).isEqualTo(110)

        val eastPathResults = Day17.searchInDirection(grid, EAST)
        val eastPath = eastPathResults.getPath()
        val eastPathPoints = eastPath.map { it.point }
        assertThat(eastPathPoints.asStringGrid()).isEqualTo("""
            #########....
            ........#....
            ........#....
            ........#....
            ........#####
            ............#
            ............#
            ............#
            ............#
            ............#
            ............#
            ............#
            ............#
        """.trimIndent())
        assertThat(eastPathResults.getScore()).isEqualTo(94)

    }

    @Test
    fun `can get paths of 2b`() {
        val grid = GridDataUtils.mapIntPointsFromLines(resourcePath("/2023/day17-test2.txt"))
        val southPathResults = Day17.searchInDirection(grid, SOUTH)
        val southPath = southPathResults.getPath()
        val southPathPoints = southPath.map { it.point }
        assertThat(southPathPoints.asStringGrid()).isEqualTo("""
            #...########
            #...#......#
            #...#......#
            #...#......#
            #####......#
        """.trimIndent())
        assertThat(southPathResults.getScore()).isEqualTo(111)

        val eastPathResults = Day17.searchInDirection(grid, EAST)
        val eastPath = eastPathResults.getPath()
        val eastPathPoints = eastPath.map { it.point }
        assertThat(eastPathPoints.asStringGrid()).isEqualTo("""
            ########....
            .......#....
            .......#....
            .......#....
            .......#####
        """.trimIndent())
        assertThat(eastPathResults.getScore()).isEqualTo(71)

    }

}