package net.fish.y2023

import net.fish.geometry.Point
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day10Test {
    @Test
    fun `can parse puzzle`() {
        val data = resourcePath("/2023/day10-test1.txt")
        val pipeMaze = Day10.parsePuzzle(data)
        assertThat(pipeMaze.points).containsExactlyEntriesOf(mapOf(
            Point(0, 0) to '.',
            Point(1, 0) to '.',
            Point(2, 0) to '.',
            Point(3, 0) to '.',
            Point(4, 0) to '.',

            Point(0, 1) to '.',
            Point(1, 1) to 'S',
            Point(2, 1) to '-',
            Point(3, 1) to '7',
            Point(4, 1) to '.',

            Point(0, 2) to '.',
            Point(1, 2) to '|',
            Point(2, 2) to '.',
            Point(3, 2) to '|',
            Point(4, 2) to '.',

            Point(0, 3) to '.',
            Point(1, 3) to 'L',
            Point(2, 3) to '-',
            Point(3, 3) to 'J',
            Point(4, 3) to '.',

            Point(0, 4) to '.',
            Point(1, 4) to '.',
            Point(2, 4) to '.',
            Point(3, 4) to '.',
            Point(4, 4) to '.',
        ))
    }

    @Test
    fun `can find loop 1`() {
        val data = resourcePath("/2023/day10-test1.txt")
        val pipeMaze = Day10.parsePuzzle(data)
        val loop = pipeMaze.findLoop()
        assertThat(loop).containsExactly(
            Point(x=1, y=1),
            Point(x=1, y=2),
            Point(x=1, y=3),
            Point(x=2, y=3),
            Point(x=3, y=3),
            Point(x=3, y=2),
            Point(x=3, y=1),
            Point(x=2, y=1)
        )
    }

    @Test
    fun `can find loop 2`() {
        val data = resourcePath("/2023/day10-test2.txt")
        val pipeMaze = Day10.parsePuzzle(data)
        val loop = pipeMaze.findLoop()
        assertThat(loop).containsExactly(
            Point(x=1, y=1),
            Point(x=1, y=2),
            Point(x=1, y=3),
            Point(x=2, y=3),
            Point(x=3, y=3),
            Point(x=3, y=2),
            Point(x=3, y=1),
            Point(x=2, y=1)
        )
    }

    @Test
    fun `can find loop 3`() {
        val data = resourcePath("/2023/day10-test3.txt")
        val pipeMaze = Day10.parsePuzzle(data)
        val loop = pipeMaze.findLoop()
        assertThat(loop).containsExactly(
            Point(x=0, y=2),
            Point(x=0, y=3),
            Point(x=0, y=4),
            Point(x=1, y=4),
            Point(x=1, y=3),
            Point(x=2, y=3),
            Point(x=3, y=3),
            Point(x=4, y=3),
            Point(x=4, y=2),
            Point(x=3, y=2),
            Point(x=3, y=1),
            Point(x=3, y=0),
            Point(x=2, y=0),
            Point(x=2, y=1),
            Point(x=1, y=1),
            Point(x=1, y=2)
        )
    }

    @Test
    fun `can do part 1`() {
        val d1 = resourcePath("/2023/day10-test1.txt")
        assertThat(Day10.doPart1(d1)).isEqualTo(4)
        val d2 = resourcePath("/2023/day10-test2.txt")
        assertThat(Day10.doPart1(d2)).isEqualTo(4)
        val d3 = resourcePath("/2023/day10-test3.txt")
        assertThat(Day10.doPart1(d3)).isEqualTo(8)
    }

    @Test
    fun `can replace maze with simpler version`() {
        val d1 = resourcePath("/2023/day10-test7.txt")
        val m1 = Day10.parsePuzzle(d1)
        // show we start with maze with extra bits
        assertThat(m1.points).containsExactlyEntriesOf(mapOf(
            Point(0, 0) to '|',
            Point(1, 0) to '.',
            Point(2, 0) to '.',
            Point(3, 0) to '.',
            Point(4, 0) to '.',

            Point(0, 1) to 'L',
            Point(1, 1) to 'S',
            Point(2, 1) to '-',
            Point(3, 1) to '7',
            Point(4, 1) to 'F',

            Point(0, 2) to '.',
            Point(1, 2) to '|',
            Point(2, 2) to '7',
            Point(3, 2) to '|',
            Point(4, 2) to '.',

            Point(0, 3) to '.',
            Point(1, 3) to 'L',
            Point(2, 3) to '-',
            Point(3, 3) to 'J',
            Point(4, 3) to '-',

            Point(0, 4) to '.',
            Point(1, 4) to '.',
            Point(2, 4) to 'J',
            Point(3, 4) to '.',
            Point(4, 4) to '.',
        ))

        val m2 = m1.removeNonLooped()
        // show it reduces to the original simple map, with all the non connected parts removed
        assertThat(m2.points).containsExactlyEntriesOf(mapOf(
            Point(0, 0) to '.',
            Point(1, 0) to '.',
            Point(2, 0) to '.',
            Point(3, 0) to '.',
            Point(4, 0) to '.',

            Point(0, 1) to '.',
            Point(1, 1) to 'S',
            Point(2, 1) to '-',
            Point(3, 1) to '7',
            Point(4, 1) to '.',

            Point(0, 2) to '.',
            Point(1, 2) to '|',
            Point(2, 2) to '.',
            Point(3, 2) to '|',
            Point(4, 2) to '.',

            Point(0, 3) to '.',
            Point(1, 3) to 'L',
            Point(2, 3) to '-',
            Point(3, 3) to 'J',
            Point(4, 3) to '.',

            Point(0, 4) to '.',
            Point(1, 4) to '.',
            Point(2, 4) to '.',
            Point(3, 4) to '.',
            Point(4, 4) to '.',
        ))
    }

    @Test
    fun `can do part 2`() {
        val d1 = resourcePath("/2023/day10-test6.txt")
        assertThat(Day10.doPart2(d1)).isEqualTo(4)
        val d2 = resourcePath("/2023/day10-test5.txt")
        assertThat(Day10.doPart2(d2)).isEqualTo(10)
        val d3 = resourcePath("/2023/day10-test4.txt")
        assertThat(Day10.doPart2(d3)).isEqualTo(8)
    }
}