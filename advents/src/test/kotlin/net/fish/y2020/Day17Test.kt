package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day17Test {
    val data = resourcePath("/2020/day17-test.txt")

    val loc1 = CCLocation(listOf(0, 0, 0))
    val loc2 = CCLocation(listOf(1, 0, 0))

    @Test
    fun `fill cube and find values`() {
        val cube = ConwayCube(grid = mutableListOf(loc1, loc2))
        assertThat(cube.at(CCLocation(listOf(0, 0, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(0, 1, 0)))).isFalse
    }

    @Test
    fun `add items to cube`() {
        val cube = ConwayCube()
        cube.add(loc1)
        cube.add(loc2)
        assertThat(cube.grid).hasSize(2)
        assertThat(cube.at(CCLocation(listOf(0, 0, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(0, 1, 0)))).isFalse
    }

    @Test
    fun `loading test data`() {
        val cube = Day17.toCube(data, 3)
        assertThat(cube.grid).hasSize(5)
        // Input data, x goes left to right, y top to bottom
        // .#.
        // ..#
        // ###
        assertThat(cube.at(CCLocation(listOf(0, 0, 0)))).isFalse
        assertThat(cube.at(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 0, 0)))).isFalse

        assertThat(cube.at(CCLocation(listOf(0, 1, 0)))).isFalse
        assertThat(cube.at(CCLocation(listOf(1, 1, 0)))).isFalse
        assertThat(cube.at(CCLocation(listOf(2, 1, 0)))).isTrue

        assertThat(cube.at(CCLocation(listOf(0, 2, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 2, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 2, 0)))).isTrue
    }

    @Test
    fun `life step p1`() {
        val cube = Day17.toCube(data, 3)
        assertThat(cube.grid).hasSize(5)
        cube.step()

        // Should turn into:
        // z=-1
        //#..
        //..#
        //.#.
        //
        // z=0
        //#.#
        //.##
        //.#.
        //
        // z=1
        //#..
        //..#
        //.#.
        assertThat(cube.grid).hasSize(11)
        assertThat(cube.at(CCLocation(listOf(0, 1, -1)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 3, -1)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 2, -1)))).isTrue

        assertThat(cube.at(CCLocation(listOf(0, 1, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 2, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 3, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 1, 0)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 2, 0)))).isTrue

        assertThat(cube.at(CCLocation(listOf(0, 1, 1)))).isTrue
        assertThat(cube.at(CCLocation(listOf(1, 3, 1)))).isTrue
        assertThat(cube.at(CCLocation(listOf(2, 2, 1)))).isTrue
    }

    @Test
    fun `run puzzles`() {
        assertThat(Day17.runPuzzle(data, 3)).isEqualTo(112)
        assertThat(Day17.runPuzzle(data, 4)).isEqualTo(848)
        // this takes about 40s to run:
        // assertThat(Day17.runPuzzle(data, 5)).isEqualTo(5760)
    }
}