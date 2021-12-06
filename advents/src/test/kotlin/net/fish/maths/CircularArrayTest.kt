package net.fish.maths

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CircularArrayTest {
    @Test
    fun `can create get and set values`() {
        val ca = CircularArray(Array(5) { 0L }.toList())
        assertThat(ca[0]).isEqualTo(0)

        ca[0] = 1
        assertThat(ca[0]).isEqualTo(1)
    }

    @Test
    fun `can shift values`() {
        val ca = CircularArray(listOf(1, 2))
        ca.rotateLeft()
        assertThat(ca[0]).isEqualTo(2)
        assertThat(ca[1]).isEqualTo(1)
    }
}