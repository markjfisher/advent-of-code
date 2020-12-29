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
        val cube = ConwayCube(grid = mutableSetOf(loc1, loc2))
        assertThat(cube.isActive(CCLocation(listOf(0, 0, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(0, 1, 0)))).isFalse
    }

    @Test
    fun `add items to cube`() {
        val cube = ConwayCube()
        cube.add(loc1)
        cube.add(loc2)
        assertThat(cube.grid).hasSize(2)
        assertThat(cube.isActive(CCLocation(listOf(0, 0, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(0, 1, 0)))).isFalse
    }

    @Test
    fun `loading test data`() {
        val cube = Day17.toCube(data, 3)
        assertThat(cube.grid).hasSize(5)
        // Input data, x goes left to right, y top to bottom
        // .#.
        // ..#
        // ###
        assertThat(cube.isActive(CCLocation(listOf(0, 0, 0)))).isFalse
        assertThat(cube.isActive(CCLocation(listOf(1, 0, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 0, 0)))).isFalse

        assertThat(cube.isActive(CCLocation(listOf(0, 1, 0)))).isFalse
        assertThat(cube.isActive(CCLocation(listOf(1, 1, 0)))).isFalse
        assertThat(cube.isActive(CCLocation(listOf(2, 1, 0)))).isTrue

        assertThat(cube.isActive(CCLocation(listOf(0, 2, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 2, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 2, 0)))).isTrue
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
        assertThat(cube.isActive(CCLocation(listOf(0, 1, -1)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 3, -1)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 2, -1)))).isTrue

        assertThat(cube.isActive(CCLocation(listOf(0, 1, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 2, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 3, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 1, 0)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 2, 0)))).isTrue

        assertThat(cube.isActive(CCLocation(listOf(0, 1, 1)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(1, 3, 1)))).isTrue
        assertThat(cube.isActive(CCLocation(listOf(2, 2, 1)))).isTrue
    }

    @Test
    fun `run puzzles`() {
        assertThat(Day17.runPuzzle(data, 3)).isEqualTo(112)
        assertThat(Day17.runPuzzle(data, 4)).isEqualTo(848)

        // this takes about 6s to run using chunks, 12s to run when not:
        // It takes about 2s when parallelised
        // assertThat(Day17.runPuzzle(data, 5)).isEqualTo(5760)

        // this takes about 4m42s to run with chunk(10)
        // and about 1m9s tu run with 2 parallel threads for calculating new locations, 12 threads for calculating grid
        // assertThat(Day17.runPuzzle(data, 6)).isEqualTo(35936)
        // Does 7D work? NO!
        // assertThat(Day17.runPuzzle(data, 7)).isEqualTo(0)
    }
}