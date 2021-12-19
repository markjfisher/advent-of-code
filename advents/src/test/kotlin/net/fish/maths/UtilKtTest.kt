package net.fish.maths

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilKtTest {
    @Test
    fun `can create product of a list`() {
        val l = listOf(1, 2, 3, 4)
        val p = l.product()
        assertThat(p).containsExactlyInAnyOrder(
            Pair(1, 2), Pair(1, 3), Pair(1, 4),
            Pair(2, 3), Pair(2, 4),
            Pair(3, 4)
        )
    }

    @Test
    fun `can supply function to product`() {
        val l = listOf(1, 2, 3, 4)
        val p = l.product { (a, b) -> 10 * a + b }
        assertThat(p).containsExactlyInAnyOrder(
            12, 13, 14,
            23, 24,
            34
        )
    }

    @Test
    fun `can get index over products`() {
        val l = listOf(1, 2, 3, 4)
        val p = l.productIndexed { i1, v1, i2, v2 ->
            // rearrange so indexes are first, to illustrate what values are returned
            listOf(i1, i2, v1, v2)
        }
        assertThat(p).containsExactlyInAnyOrder(
            listOf(0, 1, 1, 2), listOf(0, 2, 1, 3), listOf(0, 3, 1, 4),
            listOf(1, 2, 2, 3), listOf(1, 3, 2, 4),
            listOf(2, 3, 3, 4)
        )
    }
}