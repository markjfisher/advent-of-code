package net.fish.y2023

import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day16Test {
    @Test
    fun `can parse maze and print it`() {
        // parse it then turn it back into a string representation
        assertThat(Day16.toBeamMaze(resourcePath("/2023/day16-test.txt")).toString()).isEqualTo("""
            .|...\....
            |.-.\.....
            .....|-...
            ........|.
            ..........
            .........\
            ..../.\\..
            .-.-/..|..
            .|....-|.\
            ..//.|....
        """.trimIndent())
    }

    @Test
    fun `can trace unhit beams`() {
        val bm1 = Day16.toBeamMaze("""
            ..
            ..
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), EAST))).containsExactly(
            Day16.Beam(Point(0, 0), EAST),
            Day16.Beam(Point(1, 0), EAST),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), SOUTH))).containsExactly(
            Day16.Beam(Point(0, 0), SOUTH),
            Day16.Beam(Point(0, 1), SOUTH),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 0), WEST))).containsExactly(
            Day16.Beam(Point(1, 0), WEST),
            Day16.Beam(Point(0, 0), WEST),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 1), NORTH))).containsExactly(
            Day16.Beam(Point(0, 1), NORTH),
            Day16.Beam(Point(0, 0), NORTH),
        )

    }

    @Test
    fun `can trace turned beams by backslash`() {
        val bm1 = Day16.toBeamMaze("""
            .\
            ..
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), EAST))).containsExactly(
            Day16.Beam(Point(0, 0), EAST),
            Day16.Beam(Point(1, 0), EAST),
            Day16.Beam(Point(1, 1), SOUTH),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 1), NORTH))).containsExactly(
            Day16.Beam(Point(1, 1), NORTH),
            Day16.Beam(Point(1, 0), NORTH),
            Day16.Beam(Point(0, 0), WEST),
        )

    }

    @Test
    fun `can trace turned beams by forward slash`() {
        val bm1 = Day16.toBeamMaze("""
            /.
            ..
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 0), WEST))).containsExactly(
            Day16.Beam(Point(1, 0), WEST),
            Day16.Beam(Point(0, 0), WEST),
            Day16.Beam(Point(0, 1), SOUTH),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 1), NORTH))).containsExactly(
            Day16.Beam(Point(0, 1), NORTH),
            Day16.Beam(Point(0, 0), NORTH),
            Day16.Beam(Point(1, 0), EAST),
        )

    }

    @Test
    fun `can start on a turn beam`() {
        val bm1 = Day16.toBeamMaze("""
            /.
            ..
        """.trimIndent().split("\n"))
        // it's as though the beam came in from the given direction, not that it's exiting in that direction
        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), EAST))).containsExactly(
            Day16.Beam(Point(0, 0), EAST),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), SOUTH))).containsExactly(
            Day16.Beam(Point(0, 0), SOUTH),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), WEST))).containsExactly(
            Day16.Beam(Point(0, 0), WEST),
            Day16.Beam(Point(0, 1), SOUTH),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), NORTH))).containsExactly(
            Day16.Beam(Point(0, 0), NORTH),
            Day16.Beam(Point(1, 0), EAST),
        )

    }

    @Test
    fun `can pass through splitter in its direction`() {
        val bm1 = Day16.toBeamMaze("""
            -.
            .|
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 0), WEST))).containsExactly(
            Day16.Beam(Point(1, 0), WEST),
            Day16.Beam(Point(0, 0), WEST),
        )

        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 0), SOUTH))).containsExactly(
            Day16.Beam(Point(1, 0), SOUTH),
            Day16.Beam(Point(1, 1), SOUTH),
        )
    }

    @Test
    fun `can split by hyphen`() {
        val bm1 = Day16.toBeamMaze("""
            ...
            .-.
            ...
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(1, 0), SOUTH))).containsExactly(
            Day16.Beam(Point(1, 0), SOUTH),
            Day16.Beam(Point(1, 1), SOUTH),
            Day16.Beam(Point(0, 1), WEST),
            Day16.Beam(Point(2, 1), EAST),
        )
    }

    @Test
    fun `can split by pipe`() {
        val bm1 = Day16.toBeamMaze("""
            ...
            .|.
            ...
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 1), EAST))).containsExactly(
            Day16.Beam(Point(0, 1), EAST),
            Day16.Beam(Point(1, 1), EAST),
            Day16.Beam(Point(1, 2), SOUTH),
            Day16.Beam(Point(1, 0), NORTH),
        )
    }

    @Test
    fun `can do loop`() {
        val bm1 = Day16.toBeamMaze("""
            .-\
            ...
            .\/
        """.trimIndent().split("\n"))
        assertThat(bm1.traceBeam(Day16.Beam(Point(0, 0), EAST))).containsExactlyInAnyOrder(
            Day16.Beam(Point(0, 0), EAST),
            Day16.Beam(Point(1, 0), EAST),
            Day16.Beam(Point(2, 0), EAST),
            Day16.Beam(Point(2, 1), SOUTH),
            Day16.Beam(Point(2, 2), SOUTH),
            Day16.Beam(Point(1, 2), WEST),
            Day16.Beam(Point(1, 1), NORTH),
            Day16.Beam(Point(1, 0), NORTH),
            Day16.Beam(Point(0, 0), WEST),
        )
    }

    @Test
    fun `tracing test beams locations`() {
        val bm = Day16.toBeamMaze(resourcePath("/2023/day16-test.txt"))
        val beams = bm.traceBeam(Day16.Beam(Point(0,0), EAST))
        assertThat(bm.beamsGrid(beams)).isEqualTo("""
            ######....
            .#...#....
            .#...#####
            .#...##...
            .#...##...
            .#...##...
            .#..####..
            ########..
            .#######..
            .#...#.#..
        """.trimIndent())
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day16-test.txt")
        val v = Day16.doPart1(data)
        assertThat(v).isEqualTo(46)
    }
    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day16-test.txt")
        val v = Day16.doPart2(data)
        assertThat(v).isEqualTo(51)
    }

}