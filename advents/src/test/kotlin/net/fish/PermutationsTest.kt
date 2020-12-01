package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PermutationsTest {
    private fun genList(n: Int) = (0 until n).map { it }

    @Test
    fun `should return paired combinations of list`() {
        assertThat(genList(5).combinations(2).toList()).containsExactly(
            listOf(0, 1), listOf(0, 2), listOf(0, 3), listOf(0, 4),
            listOf(1, 2), listOf(1, 3), listOf(1, 4),
            listOf(2, 3), listOf(2, 4),
            listOf(3, 4)
        )

        assertThat(genList(5).combinations(3).toList()).containsExactly(
            listOf(0, 1, 2), listOf(0, 1, 3), listOf(0, 1, 4),
            listOf(0, 2, 3), listOf(0, 2, 4), listOf(0, 3, 4),
            listOf(1, 2, 3), listOf(1, 2, 4), listOf(1, 3, 4),
            listOf(2, 3, 4)
        )
    }

    @Test
    fun `combinations of lists yields right size`() {
        assertThat(genList(2)).containsExactly(0, 1)

        assertThat(genList(50).combinations(2).toList()).hasSize(1225)
        assertThat(genList(100).combinations(3).toList()).hasSize(161700)
        assertThat(genList(100).combinations(4).toList()).hasSize(3921225)
    }

    @Test
    fun `permutations list should be complete`() {
        assertThat(genList(3).permutations().toList()).containsExactly(
            listOf(0, 1, 2), listOf(0, 2, 1),
            listOf(1, 0, 2), listOf(1, 2, 0),
            listOf(2, 0, 1), listOf(2, 1, 0)
        )

        assertThat(genList(3).permutations(2).toList()).containsExactly(
            listOf(0, 1), listOf(0, 2),
            listOf(1, 0), listOf(1, 2),
            listOf(2, 0), listOf(2, 1)
        )
    }

    @Test
    fun `permutations of lists yields right size`() {
        assertThat(genList(5).permutations().toList()).hasSize(120)
        assertThat(genList(5).permutations(2).toList()).hasSize(20)
    }
}