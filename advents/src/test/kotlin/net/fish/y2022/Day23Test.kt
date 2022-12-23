package net.fish.y2022

import net.fish.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day23Test {
    @Test
    fun `can do part 1`() {
        val elfGrid = Day23.toElfGrid(listOf(
            "..............",
            "..............",
            ".......#......",
            ".....###.#....",
            "...#...#.#....",
            "....#...##....",
            "...#.###......",
            "...##.#.##....",
            "....#..#......",
            "..............",
            "..............",
            "..............",
        ).map { it.toCharArray().toList() })
        val score = Day23.doPart1(elfGrid)
        assertThat(elfGrid.toGrid()).containsExactly(
            "......#.....",
            "..........#.",
            ".#.#..#.....",
            ".....#......",
            "..#.....#..#",
            "#......##...",
            "....##......",
            ".#........#.",
            "...#.#..#...",
            "............",
            "...#..#..#..",
        )

        assertThat(score).isEqualTo(110)
    }

    @Test
    fun `can do part 2`() {
        val elfGrid = Day23.toElfGrid(listOf(
            "..............",
            "..............",
            ".......#......",
            ".....###.#....",
            "...#...#.#....",
            "....#...##....",
            "...#.###......",
            "...##.#.##....",
            "....#..#......",
            "..............",
            "..............",
            "..............",
        ).map { it.toCharArray().toList() })
        val score = Day23.doPart2(elfGrid)
        assertThat(elfGrid.toGrid()).containsExactly(
            ".......#......",
            "....#......#..",
            "..#.....#.....",
            "......#.......",
            "...#....#.#..#",
            "#.............",
            "....#.....#...",
            "..#.....#.....",
            "....#.#....#..",
            ".........#....",
            "....#......#..",
            ".......#......"
        )

        assertThat(score).isEqualTo(20)
    }

    @Test
    fun `can detect static`() {
        val elfGrid = Day23.toElfGrid(listOf(
            "...",
            ".#.",
            "..."
        ).map { it.toCharArray().toList() })
        assertThat(elfGrid.checkStatic()).isTrue
    }

    @Test
    fun `can step grid`() {
        val elfGrid = Day23.toElfGrid(listOf(
            ".....",
            "..##.",
            "..#..",
            ".....",
            "..##.",
            "....."
        ).map { it.toCharArray().toList() })
        assertThat(elfGrid.checkStatic()).isFalse
        elfGrid.step()
        assertThat(elfGrid.toGrid(Point(0, 0), Point(4, 4))).containsExactly(
            "..##.",
            ".....",
            "..#..",
            "...#.",
            "..#..",
        )
        elfGrid.step()
        assertThat(elfGrid.toGrid(Point(0, 0), Point(4, 5))).containsExactly(
            ".....",
            "..##.",
            ".#...",
            "....#",
            ".....",
            "..#.."
        )
        elfGrid.step()
        assertThat(elfGrid.toGrid(Point(0, 0), Point(4, 5))).containsExactly(
            "..#..",
            "....#",
            "#....",
            "....#",
            ".....",
            "..#.."
        )
    }

    @Test
    fun `can do multiple steps`() {
        val elfGrid = Day23.toElfGrid(listOf(
            ".....",
            "..##.",
            "..#..",
            ".....",
            "..##.",
            "....."
        ).map { it.toCharArray().toList() })
        elfGrid.step(3)
        assertThat(elfGrid.toGrid()).containsExactly(
            "..#..",
            "....#",
            "#....",
            "....#",
            ".....",
            "..#.."
        )
    }


}