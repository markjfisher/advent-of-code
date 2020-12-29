package net.fish.y2020

import net.fish.maths.flipMatrixByVertical
import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {
    private val data = resourceStrings("/2020/day20-test.txt")
    private val tiles = Day20.toTiles(data)

    @Test
    fun `run solution with test data`() {
        Day20.doPart1(tiles)
    }

    @Test
    fun `can read jigs`() {
        assertThat(tiles).hasSize(9)

        assertThat(tiles[0].jigs[0].matrix).isEqualTo(
            arrayOf(
                arrayOf('.', '.', '#', '#', '.', '#', '.', '.', '#', '.'),
                arrayOf('#', '#', '.', '.', '#', '.', '.', '.', '.', '.'),
                arrayOf('#', '.', '.', '.', '#', '#', '.', '.', '#', '.'),
                arrayOf('#', '#', '#', '#', '.', '#', '.', '.', '.', '#'),
                arrayOf('#', '#', '.', '#', '#', '.', '#', '#', '#', '.'),
                arrayOf('#', '#', '.', '.', '.', '#', '.', '#', '#', '#'),
                arrayOf('.', '#', '.', '#', '.', '#', '.', '.', '#', '#'),
                arrayOf('.', '.', '#', '.', '.', '.', '.', '#', '.', '.'),
                arrayOf('#', '#', '#', '.', '.', '.', '#', '.', '#', '.'),
                arrayOf('.', '.', '#', '#', '#', '.', '.', '#', '#', '#')
            )
        )

        // NORMAL 0
        assertThat(tiles[0].jigs[0].jigString()).isEqualToIgnoringWhitespace("""
            ..##.#..#.
            ##..#.....
            #...##..#.
            ####.#...#
            ##.##.###.
            ##...#.###
            .#.#.#..##
            ..#....#..
            ###...#.#.
            ..###..###
        """.trimIndent())

        // NORMAL 90
        assertThat(tiles[0].jigs[1].jigString()).isEqualToIgnoringWhitespace("""
            .#..#####.
            .#.####.#.
            ###...#..#
            #..#.##..#
            #....#.##.
            ...##.##.#
            .#...#....
            #.#.##....
            ##.###.#.#
            #..##.#...
        """.trimIndent())

        // NORMAL 180
        assertThat(tiles[0].jigs[2].jigString()).isEqualToIgnoringWhitespace("""
            ###..###..
            .#.#...###
            ..#....#..
            ##..#.#.#.
            ###.#...##
            .###.##.##
            #...#.####
            .#..##...#
            .....#..##
            .#..#.##..
        """.trimIndent())

        // NORMAL 270
        assertThat(tiles[0].jigs[3].jigString()).isEqualToIgnoringWhitespace("""
            ...#.##..#
            #.#.###.##
            ....##.#.#
            ....#...#.
            #.##.##...
            .##.#....#
            #..##.#..#
            #..#...###
            .#.####.#.
            .#####..#.
        """.trimIndent())

        // FLIPPED 0
        assertThat(tiles[0].jigs[4].jigString()).isEqualToIgnoringWhitespace("""
            .#..#.##..
            .....#..##
            .#..##...#
            #...#.####
            .###.##.##
            ###.#...##
            ##..#.#.#.
            ..#....#..
            .#.#...###
            ###..###..
        """.trimIndent())

        // FLIPPED 90
        assertThat(tiles[0].jigs[5].jigString()).isEqualToIgnoringWhitespace("""
            #..##.#...
            ##.###.#.#
            #.#.##....
            .#...#....
            ...##.##.#
            #....#.##.
            #..#.##..#
            ###...#..#
            .#.####.#.
            .#..#####.
        """.trimIndent())

        // FLIPPED 180
        assertThat(tiles[0].jigs[6].jigString()).isEqualToIgnoringWhitespace("""
            ..###..###
            ###...#.#.
            ..#....#..
            .#.#.#..##
            ##...#.###
            ##.##.###.
            ####.#...#
            #...##..#.
            ##..#.....
            ..##.#..#.
        """.trimIndent())

        // FLIPPED 270
        assertThat(tiles[0].jigs[7].jigString()).isEqualToIgnoringWhitespace("""
            .#####..#.
            .#.####.#.
            #..#...###
            #..##.#..#
            .##.#....#
            #.##.##...
            ....#...#.
            ....##.#.#
            #.#.###.##
            ...#.##..#
        """.trimIndent())

    }

    @Test
    fun `rotating a jig`() {
        var jigR = tiles[0].jigs[0].rotate(90)
        assertThat(jigR.jigString()).isEqualToIgnoringWhitespace(
            """
            .#..#####.
            .#.####.#.
            ###...#..#
            #..#.##..#
            #....#.##.
            ...##.##.#
            .#...#....
            #.#.##....
            ##.###.#.#
            #..##.#...
        """.trimIndent()
        )

        jigR = tiles[0].jigs[0].rotate(180)
        assertThat(jigR.jigString()).isEqualToIgnoringWhitespace(
            """
            ###..###..
            .#.#...###
            ..#....#..
            ##..#.#.#.
            ###.#...##
            .###.##.##
            #...#.####
            .#..##...#
            .....#..##
            .#..#.##..
        """.trimIndent()
        )

        jigR = tiles[0].jigs[0].rotate(270)
        assertThat(jigR.jigString()).isEqualToIgnoringWhitespace(
            """
            ...#.##..#
            #.#.###.##
            ....##.#.#
            ....#...#.
            #.##.##...
            .##.#....#
            #..##.#..#
            #..#...###
            .#.####.#.
            .#####..#.
        """.trimIndent()
        )

    }

    @Test
    fun `flipping array`() {
        val flippedAsListOfString = flipMatrixByVertical(tiles[0].jigs[0].matrix).asList().map { it.joinToString("") }
        assertThat(flippedAsListOfString).containsExactly(
            ".#..#.##..",
            ".....#..##",
            ".#..##...#",
            "#...#.####",
            ".###.##.##",
            "###.#...##",
            "##..#.#.#.",
            "..#....#..",
            ".#.#...###",
            "###..###.."
        )
    }

    @Test
    fun `flipping jig`() {
        assertThat(tiles[0].jigs[0].flip().jigString()).isEqualToIgnoringWhitespace("""
            .#..#.##..
            .....#..##
            .#..##...#
            #...#.####
            .###.##.##
            ###.#...##
            ##..#.#.#.
            ..#....#..
            .#.#...###
            ###..###..
        """.trimIndent())
    }
}