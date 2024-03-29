package net.fish

import net.fish.maths.D3
import net.fish.maths.PairCombinations
import net.fish.maths.combinations
import net.fish.maths.permutations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PermutationsTest {
    private fun genList(n: Int) = (0 until n).map { it }

    @Test
    fun `permutations of 3 lots of 1 to 3`() {
        val d3x3 = D3(3)
        assertThat(d3x3.toList()).containsExactly(
            listOf(1, 1, 1), listOf(1, 1, 2), listOf(1, 1, 3),
            listOf(1, 2, 1), listOf(1, 2, 2), listOf(1, 2, 3),
            listOf(1, 3, 1), listOf(1, 3, 2), listOf(1, 3, 3),
            listOf(2, 1, 1), listOf(2, 1, 2), listOf(2, 1, 3),
            listOf(2, 2, 1), listOf(2, 2, 2), listOf(2, 2, 3),
            listOf(2, 3, 1), listOf(2, 3, 2), listOf(2, 3, 3),
            listOf(3, 1, 1), listOf(3, 1, 2), listOf(3, 1, 3),
            listOf(3, 2, 1), listOf(3, 2, 2), listOf(3, 2, 3),
            listOf(3, 3, 1), listOf(3, 3, 2), listOf(3, 3, 3)
        )
    }

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
        // assertThat(genList(100).combinations(4).toList()).hasSize(3921225)
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

    @Test
    fun `pair combinations`() {
        val pc1 = PairCombinations(4)
        val asListOfPairsOfIntegers = pc1.map { it }
        assertThat(asListOfPairsOfIntegers).containsExactly(
            listOf(0, 0), listOf(0, 1), listOf(0, 2), listOf(0, 3),
            listOf(1, 0), listOf(1, 1), listOf(1, 2), listOf(1, 3),
            listOf(2, 0), listOf(2, 1), listOf(2, 2), listOf(2, 3),
            listOf(3, 0), listOf(3, 1), listOf(3, 2), listOf(3, 3),
        )
    }

}