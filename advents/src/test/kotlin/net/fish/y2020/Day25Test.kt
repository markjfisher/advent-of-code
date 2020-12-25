package net.fish.y2020

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Day25Test {

    @BeforeEach
    fun loadMap() {
        Day25.createHandshakeMap(12)
    }

    @Test
    fun `can find private loop number`() {
        assertThat(Day25.findLoopNumber(5764801)).isEqualTo(8)
        assertThat(Day25.findLoopNumber(17807724)).isEqualTo(11)
    }
}