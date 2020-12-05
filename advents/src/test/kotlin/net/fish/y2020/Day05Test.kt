package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day05.changeToBinaryString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test {
    private val seatPatterns = resourcePath("/2020/day05-test.txt")

    @Test
    fun `decoding patterns gives row and column`() {
        assertThat(Day05.decode("BFFFBBFRRR")).isEqualTo(Day05.Seat(row = 70, column = 7))
    }

    @Test
    fun `id of seat`() {
        assertThat(Day05.Seat(row = 70, column = 7).id()).isEqualTo(567)
    }

    @Test
    fun `test data ids`() {
        assertThat(seatPatterns.map { Day05.decode(it).id() }).containsExactly(357, 567, 119, 820)
    }

    @Test
    fun `finding seat id`() {
        assertThat(Day05.findSeatId(listOf(0, 2))).isEqualTo(1)
        assertThat(Day05.findSeatId(listOf(8, 9, 10, 12, 13))).isEqualTo(11)
        assertThat(Day05.findSeatId(listOf(13, 12, 8, 10, 9))).isEqualTo(11)
    }
}