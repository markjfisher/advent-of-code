package net.fish.y2023

import net.fish.geometry.Point
import net.fish.geometry.asStringGrid
import net.fish.geometry.gridString
import net.fish.network.findShortestPath
import net.fish.network.findShortestPathByPredicate
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day23Test {
    @Test
    fun `can parse icegrid`() {
        val data = """
            #.#####
            #.>...#
            #.###.#
            #.#...#
            #.#.#.#
            #...#.#
            #####.#
        """.trimIndent().split("\n")

        // convert to grid, then get string representation of it to prove it parsed.
        val iceGrid = Day23.toIceGrid(data)
        assertThat(iceGrid.locations.gridString()).isEqualTo("""
            #.#####
            #.>...#
            #.###.#
            #.#...#
            #.#.#.#
            #...#.#
            #####.#
        """.trimIndent())
    }

    @Test
    fun `can find paths`() {
        val data = """
            #.#####
            #..>..#
            #.#.#.#
            #.#...#
            #.###.#
            #.#...#
            #.#.#.#
            #...#.#
            #####.#
        """.trimIndent().split("\n")
        val iceGrid = Day23.toIceGrid(data)
        val paths = iceGrid.paths()
        // we have 2 paths through the maze, as one of the moves is forced
        assertThat(paths).hasSize(2)
        assertThat(paths[0].asStringGrid()).isEqualTo("""
            #....
            #####
            ....#
            ....#
            ....#
            ....#
            ....#
            ....#
            ....#
        """.trimIndent())
        assertThat(paths[1].asStringGrid()).isEqualTo("""
            #....
            #....
            #....
            #....
            #....
            #.###
            #.#.#
            ###.#
            ....#
        """.trimIndent())
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day23-test.txt")
        val v = Day23.doPart1(data)
        assertThat(v).isEqualTo(94)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day23-test.txt")
        val v = Day23.doPart2(data)
        assertThat(v).isEqualTo(154)
    }

}