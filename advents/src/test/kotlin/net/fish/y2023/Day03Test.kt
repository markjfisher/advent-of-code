package net.fish.y2023

import net.fish.geometry.Point
import net.fish.resourcePath
import net.fish.y2023.Day03.GridNumber
import net.fish.y2023.Day03.GridSymbol
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day03Test {

    @Test
    fun `can parse grid`() {
        val data = resourcePath("/2023/day03-test.txt")
        val numberGrid = Day03.toNumberGrid(data)
        assertThat(numberGrid.numbers).containsExactly(
            GridNumber(position = Point(x = 0, y = 0), value = 467),
            GridNumber(position = Point(x = 5, y = 0), value = 114),
            GridNumber(position = Point(x = 2, y = 2), value = 35),
            GridNumber(position = Point(x = 6, y = 2), value = 633),
            GridNumber(position = Point(x = 0, y = 4), value = 617),
            GridNumber(position = Point(x = 7, y = 5), value = 58),
            GridNumber(position = Point(x = 2, y = 6), value = 592),
            GridNumber(position = Point(x = 6, y = 7), value = 755),
            GridNumber(position = Point(x = 1, y = 9), value = 664),
            GridNumber(position = Point(x = 5, y = 9), value = 598)
        )
        assertThat(numberGrid.symbols).containsExactly(
            GridSymbol(position = Point(x = 3, y = 1), type = '*'),
            GridSymbol(position = Point(x = 6, y = 3), type = '#'),
            GridSymbol(position = Point(x = 3, y = 4), type = '*'),
            GridSymbol(position = Point(x = 5, y = 5), type = '+'),
            GridSymbol(position = Point(x = 3, y = 8), type = '$'),
            GridSymbol(position = Point(x = 5, y = 8), type = '*')
        )
    }

    @Test
    fun `can parse grid ends`() {
        val data = listOf(".123")
        val numberGrid = Day03.toNumberGrid(data)
        assertThat(numberGrid.numbers).containsExactly(GridNumber(Point(1,0), 123))
    }

        @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day03-test.txt")
        assertThat(Day03.doPart1(data)).isEqualTo(4361)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day03-test.txt")
        assertThat(Day03.findGears(Day03.toNumberGrid(data))).containsExactly(16345, 451490)
        assertThat(Day03.doPart2(data)).isEqualTo(467835)
    }

}