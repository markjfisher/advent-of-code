package net.fish.y2023

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day13Test {

    @Test
    fun `can generate reflection pairs`() {
        assertThat(Day13.checkPairs(0, 6)).containsExactly(Pair(0,1))
        assertThat(Day13.checkPairs(1, 6)).containsExactly(Pair(1,2), Pair(0,3))
        assertThat(Day13.checkPairs(2, 6)).containsExactly(Pair(2,3), Pair(1,4),Pair(0,5))
        assertThat(Day13.checkPairs(3, 6)).containsExactly(Pair(3,4), Pair(2,5),Pair(1,6))
        assertThat(Day13.checkPairs(4, 6)).containsExactly(Pair(4,5), Pair(3,6))
        assertThat(Day13.checkPairs(5, 6)).containsExactly(Pair(5,6))
        assertThat(Day13.checkPairs(6, 6)).isEmpty()
    }

    @Test
    fun `can find reflections`() {
        val data = resourceStrings("/2023/day13-test.txt")
        val grids = Day13.toAshRockGrid(data)
        assertThat(grids[0].columnReflection()).isEqualTo(5)
        assertThat(grids[1].rowReflection()).isEqualTo(4)
    }

    @Test
    fun `can find reflections2`() {
        val data = resourceStrings("/2023/day13-test2.txt")
        val grids = Day13.toAshRockGrid(data)
        assertThat(grids[0].rowReflection()).isEqualTo(12)
    }

    @Test
    fun `can smudge test data 1`() {
        val data = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
        """.trimIndent()
        val grids = Day13.toAshRockGrid(listOf(data)).map { it.smudge() }
        assertThat(grids[0]).isEqualTo(Pair(3, -1))
    }

    @Test
    fun `can smudge test data 2`() {
        val data = """
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
        """.trimIndent()
        val grids = Day13.toAshRockGrid(listOf(data)).map { it.smudge() }
        assertThat(grids[0]).isEqualTo(Pair(1, -1))
    }

    @Test
    fun `can smudge test data 3`() {
        val data = """
            .#.###.#.
            .##.#..##
            ...##....
            ...##....
            .##.#..##
            .#.###.#.
            #.##..#..
            #..#..#..
            #.###.###
            #.###.###
            #..#.....
            #.##..#..
            .#.###.#.
            .##.#..##
            ...##....
        """.trimIndent()
        val grids = Day13.toAshRockGrid(listOf(data)).map { it.smudge() }
        assertThat(grids[0]).isEqualTo(Pair(9, -1))
    }

    @Test
    fun `can find reflections3`() {
        val data = resourceStrings("/2023/day13-test3.txt")
        val grids = Day13.toAshRockGrid(data)
        assertThat(grids[0].rowReflection()).isEqualTo(-1)
        assertThat(grids[0].columnReflection()).isEqualTo(12)
    }

    @Test
    fun `can do part 1`() {
        val data = resourceStrings("/2023/day13-test.txt")
        val v = Day13.doPart1(data)
        assertThat(v).isEqualTo(405)
    }

    @Test
    fun `can do part 2`() {
        val data = resourceStrings("/2023/day13-test.txt")
        val v = Day13.doPart2(data)
        assertThat(v).isEqualTo(400)
    }

}