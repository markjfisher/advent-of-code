package net.fish.maths

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MatrixKtTest {
    @Test
    fun `can transpose rows to columns of 2x2`() {
        val table = listOf(listOf(1, 2), listOf(3, 4))
        val transposed = transpose(table)
        assertThat(transposed[0]).containsExactly(1, 3)
        assertThat(transposed[1]).containsExactly(2, 4)
    }

    @Test
    fun `can transpose rows to columns of 5x5`() {
        val table = listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 10), listOf(11, 12, 13, 14, 15), listOf(16, 17, 18, 19, 20), listOf(21, 22, 23, 24, 25))
        val transposed = transpose(table)
        assertThat(transposed[0]).containsExactly(1, 6, 11, 16, 21)
        assertThat(transposed[1]).containsExactly(2, 7, 12, 17, 22)
        assertThat(transposed[2]).containsExactly(3, 8, 13, 18, 23)
        assertThat(transposed[3]).containsExactly(4, 9, 14, 19, 24)
        assertThat(transposed[4]).containsExactly(5, 10, 15, 20, 25)
    }
}