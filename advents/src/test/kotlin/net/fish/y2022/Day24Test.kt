package net.fish.y2022

import net.fish.geometry.Direction
import net.fish.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day24Test {
    @Test
    fun `can do part 1`() {
        val weatherGrid = Day24.toWeatherGrid(listOf(
            "#.######",
            "#>>.<^<#",
            "#.<..<<#",
            "#>v.><>#",
            "#<^v^^>#",
            "######.#"
        ))
        val time = Day24.traverse(weatherGrid)
        assertThat(time).isEqualTo(18)
    }

    @Test
    fun `can create weather grid`() {
        val weatherGrid = Day24.toWeatherGrid(listOf(
            "#.######",
            "#>>.<^<#",
            "#.<..<<#",
            "#>v.><>#",
            "#<^v^^>#",
            "######.#"
        ))
        assertThat(weatherGrid.toGrid()).containsExactly(
            "#.######",
            "#>>.<^<#",
            "#.<..<<#",
            "#>v.><>#",
            "#<^v^^>#",
            "######.#"
        )
        assertThat(weatherGrid.start).isEqualTo(Point(1,0))
        assertThat(weatherGrid.end).isEqualTo(Point(6,5))
        assertThat(weatherGrid.width).isEqualTo(8)
        assertThat(weatherGrid.height).isEqualTo(6)
        assertThat(weatherGrid.blizzard[Point(1,1)]).containsExactly(Direction.EAST)
    }

    @Test
    fun `can move simple blizzard`() {
        val weatherGrid = Day24.toWeatherGrid(listOf(
            "#.###",
            "#>..#",
            "###.#"
        ))
        var blizzard = Day24.moveBlizzard(weatherGrid.blizzard, weatherGrid.width, weatherGrid.height)
        assertThat(Day24.displayBlizzard(blizzard, weatherGrid.width, weatherGrid.height)).containsExactly(".>.")
        blizzard = Day24.moveBlizzard(blizzard, weatherGrid.width, weatherGrid.height)
        assertThat(Day24.displayBlizzard(blizzard, weatherGrid.width, weatherGrid.height)).containsExactly("..>")
        blizzard = Day24.moveBlizzard(blizzard, weatherGrid.width, weatherGrid.height)
        assertThat(Day24.displayBlizzard(blizzard, weatherGrid.width, weatherGrid.height)).containsExactly(">..")
    }

    @Test
    fun `can move complex blizzard`() {
        val weatherGrid = Day24.toWeatherGrid(listOf(
            "#.######",
            "#>>.<^<#",
            "#.<..<<#",
            "#>v.><>#",
            "#<^v^^>#",
            "######.#"
        ))
        var blizzard = Day24.moveBlizzard(weatherGrid.blizzard, weatherGrid.width, weatherGrid.height)
        assertThat(Day24.displayBlizzard(blizzard, weatherGrid.width, weatherGrid.height)).containsExactly(
            ".>3.<.",
            "<..<<.",
            ">2.22.",
            ">v..^<"
        )
    }
}