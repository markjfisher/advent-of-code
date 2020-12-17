package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day17Test {
    val data = resourcePath("/2020/day17-test.txt")

    val loc1 = CCLocation(0, 0, 0)
    val loc2 = CCLocation(1, 0, 0)

    @Test
    fun `fill cube and find values`() {
        val cube = ConwayCube(grid = mutableListOf(loc1, loc2))
        assertThat(cube.at(CCLocation(0, 0, 0))).isTrue
        assertThat(cube.at(CCLocation(1, 0, 0))).isTrue
        assertThat(cube.at(CCLocation(0, 1, 0))).isFalse
    }

    @Test
    fun `add items to cube`() {
        val cube = ConwayCube()
        cube.add(loc1)
        cube.add(loc2)
        assertThat(cube.grid).hasSize(2)
        assertThat(cube.at(CCLocation(0, 0, 0))).isTrue
        assertThat(cube.at(CCLocation(1, 0, 0))).isTrue
        assertThat(cube.at(CCLocation(0, 1, 0))).isFalse
    }

    @Test
    fun `loading test data`() {
        val cube = Day17.toCube(data, 3)
        assertThat(cube.grid).hasSize(5)
        // Input data, x goes left to right, y top to bottom
        // .#.
        // ..#
        // ###
        assertThat(cube.at(CCLocation(0, 0, 0))).isFalse
        assertThat(cube.at(CCLocation(1, 0, 0))).isTrue
        assertThat(cube.at(CCLocation(2, 0, 0))).isFalse

        assertThat(cube.at(CCLocation(0, 1, 0))).isFalse
        assertThat(cube.at(CCLocation(1, 1, 0))).isFalse
        assertThat(cube.at(CCLocation(2, 1, 0))).isTrue

        assertThat(cube.at(CCLocation(0, 2, 0))).isTrue
        assertThat(cube.at(CCLocation(1, 2, 0))).isTrue
        assertThat(cube.at(CCLocation(2, 2, 0))).isTrue
    }

    @Test
    fun `good neighbours`() {
        val cube = Day17.toCube(data, 3)
        assertThat(cube.grid).hasSize(5)
        val neighboursx1y1z0 = cube.neighbours(CCLocation(1, 1, 0))
        assertThat(neighboursx1y1z0).hasSize(5)
        assertThat(neighboursx1y1z0).containsExactlyInAnyOrder(
            CCLocation(1, 0, 0),
            CCLocation(2, 1, 0),
            CCLocation(0, 2, 0),
            CCLocation(1, 2, 0),
            CCLocation(2, 2, 0)
        )

        val neighboursx2y2z0 = cube.neighbours(CCLocation(2, 2, 0))
        assertThat(neighboursx2y2z0).hasSize(2)
        assertThat(neighboursx2y2z0).containsExactlyInAnyOrder(
            CCLocation(2, 1, 0),
            CCLocation(1, 2, 0)
        )
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
        assertThat(cube.at(CCLocation(0, 1, -1))).isTrue
        assertThat(cube.at(CCLocation(1, 3, -1))).isTrue
        assertThat(cube.at(CCLocation(2, 2, -1))).isTrue

        assertThat(cube.at(CCLocation(0, 1, 0))).isTrue
        assertThat(cube.at(CCLocation(1, 2, 0))).isTrue
        assertThat(cube.at(CCLocation(1, 3, 0))).isTrue
        assertThat(cube.at(CCLocation(2, 1, 0))).isTrue
        assertThat(cube.at(CCLocation(2, 2, 0))).isTrue

        assertThat(cube.at(CCLocation(0, 1, 1))).isTrue
        assertThat(cube.at(CCLocation(1, 3, 1))).isTrue
        assertThat(cube.at(CCLocation(2, 2, 1))).isTrue
    }

    @Test
    fun `run puzzles`() {
        assertThat(Day17.runPuzzle(data, 3)).isEqualTo(112)
        // assertThat(Day17.runPuzzle(data, 4)).isEqualTo(848)
    }
}