package net.fish.y2023

import net.fish.resourcePath
import net.fish.y2023.Day04.Card
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day04Test {

    @Test
    fun `can do parse cards`() {
        val data = resourcePath("/2023/day04-test.txt")
        assertThat(Day04.toCards(data)).containsExactly(
            Card(num = 1, numbers = listOf(41, 48, 83, 86, 17), winners = listOf(83, 86, 6, 31, 17, 9, 48, 53)),
            Card(num = 2, numbers = listOf(13, 32, 20, 16, 61), winners = listOf(61, 30, 68, 82, 17, 32, 24, 19)),
            Card(num = 3, numbers = listOf(1, 21, 53, 59, 44), winners = listOf(69, 82, 63, 72, 16, 21, 14, 1)),
            Card(num = 4, numbers = listOf(41, 92, 73, 84, 69), winners = listOf(59, 84, 76, 51, 58, 5, 54, 83)),
            Card(num = 5, numbers = listOf(87, 83, 26, 28, 32), winners = listOf(88, 30, 70, 12, 93, 22, 82, 36)),
            Card(num = 6, numbers = listOf(31, 18, 13, 56, 72), winners = listOf(74, 77, 10, 23, 35, 67, 36, 11))
        )
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day04-test.txt")
        assertThat(Day04.doPart1(data)).isEqualTo(13)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day04-test.txt")
        assertThat(Day04.doPart2(data)).isEqualTo(30)
    }

}