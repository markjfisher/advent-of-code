package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day05.toSeatId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test {
    private val seatPatterns = resourcePath("/2020/day05-test.txt")

    @Test
    fun `test data ids`() {
        assertThat(seatPatterns.map { it.toSeatId() }).containsExactly(357, 567, 119, 820)
    }

    @Test
    fun `finding seat id`() {
        assertThat(Day05.findSeatId(listOf(0, 2))).isEqualTo(1)
        assertThat(Day05.findSeatId(listOf(8, 9, 10, 12, 13))).isEqualTo(11)
    }

    @Test
    fun `converting data to binary`() {
        assertThat(Day05.toBinary("FBFBBFF")).isEqualTo(0b0101100)
        assertThat(Day05.toBinary("RLR")).isEqualTo(0b101)
    }
}