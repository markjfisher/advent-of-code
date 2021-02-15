package net.fish.geometry.square

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class WrappingSquareGridTest {
    private val wrapper = WrappingSquareGrid(4, 3)

    @Test
    fun `modulus negative maths`() {
        assertThat(5 % 4).isEqualTo(1)
        assertThat(6 % 4).isEqualTo(2)
        assertThat(-5 % 4).isEqualTo(-1)
        assertThat(-6 % 4).isEqualTo(-2)
    }

    @Test
    fun `constrain squares`() {
        assertThat(wrapper.constrain(Square(0, 3))).isEqualTo(Square(0, 0))
        assertThat(wrapper.constrain(Square(1, 3))).isEqualTo(Square(1, 0))
        assertThat(wrapper.constrain(Square(2, 3))).isEqualTo(Square(2, 0))
        assertThat(wrapper.constrain(Square(3, 3))).isEqualTo(Square(3, 0))
        assertThat(wrapper.constrain(Square(4, 3))).isEqualTo(Square(0, 0))
        assertThat(wrapper.constrain(Square(5, 3))).isEqualTo(Square(1, 0))
        assertThat(wrapper.constrain(Square(6, 3))).isEqualTo(Square(2, 0))
        assertThat(wrapper.constrain(Square(7, 3))).isEqualTo(Square(3, 0))
        assertThat(wrapper.constrain(Square(8, 3))).isEqualTo(Square(0, 0))
        assertThat(wrapper.constrain(Square(9, 3))).isEqualTo(Square(1, 0))

        assertThat(wrapper.constrain(Square(-1, -1))).isEqualTo(Square(3, 2))
        assertThat(wrapper.constrain(Square(-4, -4))).isEqualTo(Square(0, 2))
        assertThat(wrapper.constrain(Square(-8, -8))).isEqualTo(Square(0, 1))
        assertThat(wrapper.constrain(Square(-12, -12))).isEqualTo(Square(0, 0))

        assertThat(wrapper.constrain(Square(4, -1))).isEqualTo(Square(0, 2))
    }

    @Test
    fun `square grid neighbours are constrained`() {
        assertThat(wrapper.square(0, 0).neighbours()).containsExactly(
            Square(1, 0), Square(1, 1), Square(0, 1), Square(3, 1), Square(3, 0), Square(3, 2), Square(0, 2), Square(1, 2)
        )
        // Equivalent of 0,0
        assertThat(wrapper.square(-12, -12).neighbours()).containsExactly(
            Square(1, 0), Square(1, 1), Square(0, 1), Square(3, 1), Square(3, 0), Square(3, 2), Square(0, 2), Square(1, 2)
        )
        assertThat(wrapper.square(3, 0).neighbours()).containsExactly(
            Square(0, 0), Square(0, 1), Square(3, 1), Square(2, 1), Square(2, 0), Square(2, 2), Square(3, 2), Square(0, 2)
        )
        assertThat(wrapper.square(3, 2).neighbours()).containsExactly(
            Square(0, 2), Square(0, 0), Square(3, 0), Square(2, 0), Square(2, 2), Square(2, 1), Square(3, 1), Square(0, 1)
        )
    }

    @Test
    fun `all items are returned`() {
        assertThat(wrapper.items()).containsExactly(
            Square(0, 0), Square(1, 0), Square(2, 0), Square(3, 0),
            Square(0, 1), Square(1, 1), Square(2, 1), Square(3, 1),
            Square(0, 2), Square(1, 2), Square(2, 2), Square(3, 2)
        )
    }
}