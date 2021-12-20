package net.fish.y2021

import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {
    private val testData = resourcePath("/2021/day20-test.txt")
    private val testData2 = resourcePath("/2021/day20-2-test.txt")

    @Test
    fun `can do part 1 and 2 on test data`() {
        assertThat(Day20.solve(testData, 2)).isEqualTo(35)
        assertThat(Day20.solve(testData, 50)).isEqualTo(3351)
    }

    @Test
    fun `can evolve when bit 0 is set in algorithm`() {
        val trench = Day20.TrenchMap.parseInput(testData2)
        val e1 = trench.evolve(1)
        println(e1.stringGrid())
    }

    @Test
    fun `can parse input data`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        assertThat(trench.algorithm).isEqualTo("..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#")
        assertThat(trench.imageMap).containsExactly(
            Point(0, 0), Point(3, 0),
            Point(0, 1),
            Point(0, 2), Point(1, 2), Point(4, 2),
            Point(2, 3),
            Point(2, 4), Point(3, 4), Point(4, 4)
        )
    }

    @Test
    fun `can get point values in image`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        assertThat(Day20.TrenchMap.pointValue(Point(2, 2), trench.algorithm, trench.imageMap, trench.imageMap.bounds(), trench.shouldConsiderInfinite)).isEqualTo(34)
    }

    @Test
    fun `can print the grid for lols`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        val grid = trench.stringGrid()
        println(grid)
        assertThat(grid).isEqualTo(
            """
            .......
            .#..#..
            .#.....
            .##..#.
            ...#...
            ...###.
            .......
        
        """.trimIndent()
        )
    }

    @Test
    fun `can evolve grid`() {
        val trench = Day20.TrenchMap.parseInput(testData)
        trench.evolve(1)
        assertThat(trench.stringGrid()).isEqualTo(
            """
            .........
            ..##.##..
            .#..#.#..
            .##.#..#.
            .####..#.
            ..#..##..
            ...##..#.
            ....#.#..
            .........
            
            """.trimIndent()
        )

        trench.evolve(1)
        assertThat(trench.stringGrid()).isEqualTo(
            """
            ...........
            ........#..
            ..#..#.#...
            .#.#...###.
            .#...##.#..
            .#.....#.#.
            ..#.#####..
            ...#.#####.
            ....##.##..
            .....###...
            ...........
            
            """.trimIndent()
        )
    }

    @Test
    fun `conway iterator`() {
        val conwayData = resourcePath("/2021/day20-conway.txt")
        val trench = Day20.TrenchMap.parseInput(conwayData)
        for (i in 0 until 5) {
            println("iteration $i")
            println(trench.stringGrid(onChar = '█', offChar = ' '))
            trench.evolve(1)
        }

    }
}