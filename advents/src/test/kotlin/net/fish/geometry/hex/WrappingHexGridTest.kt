package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.sqrt

class WrappingHexGridTest {
    private val pointyLayout = Layout(POINTY)
    private val flatLayout = Layout(FLAT)
    private val pointyGrid = WrappingHexGrid(m = 8, n = 4, layout = pointyLayout)
    private val flatGrid = WrappingHexGrid(m = 8, n = 4, layout = flatLayout)

    @Test
    fun `can get hexes out of pointy grid in correct sequence`() {
        val hexes = listOf(
            Hex(0, 0, 0), Hex(1, 0, -1), Hex(2, 0, -2),
            Hex(1, -1, 0), Hex(2, -1, -1), Hex(3, -1, -2),
            Hex(1, -2, 1), Hex(2, -2, 0), Hex(3, -2, -1),
            Hex(2, -3, 1), Hex(3, -3, 0), Hex(4, -3, -1)
        )
        assertThat(WrappingHexGrid(3, 4, pointyLayout).items()).containsExactlyElementsOf(hexes)
    }

    @Test
    fun `can get hexes out of flat grid in correct sequence`() {
        val hexes = listOf(
            Hex(0, 0, 0), Hex(1, -1, 0), Hex(2, -1, -1), Hex(3, -2, -1),
            Hex(0, -1, 1), Hex(1, -2, 1), Hex(2, -2, 0), Hex(3, -3, 0),
            Hex(0, -2, 2), Hex(1, -3, 2), Hex(2, -3, 1), Hex(3, -4, 1)
        )
        assertThat(WrappingHexGrid(4, 3, flatLayout).items()).containsExactlyElementsOf(hexes)
    }

    @Test
    fun `flat wrapping hex grid constrains hexagons to its grid size`() {
        // Top to bottom
        assertThat(flatGrid.constrain(Hex(0, 1, -1))).isEqualTo(Hex(0, -3, 3))
        assertThat(flatGrid.constrain(Hex(1, 0, -1))).isEqualTo(Hex(1, -4, 3))
        assertThat(flatGrid.constrain(Hex(0, -4, 4))).isEqualTo(Hex(0, 0, 0))
        assertThat(flatGrid.constrain(Hex(1, -5, 4))).isEqualTo(Hex(1, -1, 0))
        assertThat(flatGrid.constrain(Hex(7, -8, 1))).isEqualTo(Hex(7, -4, -3))
        assertThat(flatGrid.constrain(Hex(6, -7, 1))).isEqualTo(Hex(6, -3, -3))
        assertThat(flatGrid.constrain(Hex(6, -2, -4))).isEqualTo(Hex(6, -6, 0))

        // Left to right
        assertThat(flatGrid.constrain(Hex(-1, -3, 4))).isEqualTo(Hex(7, -7, 0))
        assertThat(flatGrid.constrain(Hex(-1, -2, 3))).isEqualTo(Hex(7, -6, -1))
        assertThat(flatGrid.constrain(Hex(-1, 0, 1))).isEqualTo(Hex(7, -4, -3))
        assertThat(flatGrid.constrain(Hex(-1, 1, 0))).isEqualTo(Hex(7, -7, 0))

        assertThat(flatGrid.constrain(Hex(8, -8, 0))).isEqualTo(Hex(0, 0, 0))
        assertThat(flatGrid.constrain(Hex(8, -7, -1))).isEqualTo(Hex(0, -3, 3))
        assertThat(flatGrid.constrain(Hex(8, -5, -3))).isEqualTo(Hex(0, -1, 1))
        assertThat(flatGrid.constrain(Hex(8, -4, -4))).isEqualTo(Hex(0, 0, 0))
    }

    @Test
    fun `pointy wrapping hex grid constrains hexagons to its grid size`() {
        // Top to bottom
        assertThat(pointyGrid.constrain(Hex(-1, 1, 0))).isEqualTo(Hex(9, -3, -6))
        assertThat(pointyGrid.constrain(Hex(0, 1, -1))).isEqualTo(Hex(2, -3, 1))
        assertThat(pointyGrid.constrain(Hex(2, -4, 2))).isEqualTo(Hex(0, 0, 0))
        assertThat(pointyGrid.constrain(Hex(3, -4, 1))).isEqualTo(Hex(1, 0, -1))
        assertThat(pointyGrid.constrain(Hex(6, 1, -7))).isEqualTo(Hex(8, -3, -5))
        assertThat(pointyGrid.constrain(Hex(7, 1, -8))).isEqualTo(Hex(9, -3, -6))

        // Left to right
        assertThat(pointyGrid.constrain(Hex(-1, 0, 1))).isEqualTo(Hex(7, 0, -7))
        assertThat(pointyGrid.constrain(Hex(8, 0, -8))).isEqualTo(Hex(0, 0, 0))
        assertThat(pointyGrid.constrain(Hex(0, -1, 1))).isEqualTo(Hex(8, -1, -7))
        assertThat(pointyGrid.constrain(Hex(9, -1, -8))).isEqualTo(Hex(1, -1, 0))
        assertThat(pointyGrid.constrain(Hex(1, -3, 2))).isEqualTo(Hex(9, -3, -6))
        assertThat(pointyGrid.constrain(Hex(10, -3, -7))).isEqualTo(Hex(2, -3, 1))
    }

    @Test
    fun `pointy hex grid neighbours are constrained`() {
        assertThat(pointyGrid.hex(0, 0, 0).neighbours()).containsExactly(
            Hex(1, 0, -1), Hex(1, -1, 0), Hex(8, -1, -7), Hex(7, 0, -7), Hex(9, -3, -6), Hex(2, -3, 1)
        )
        assertThat(pointyGrid.hex(1, -1, 0).neighbours()).containsExactly(
            Hex(2, -1, -1), Hex(2, -2, 0), Hex(1, -2, 1), Hex(8, -1, -7), Hex(0, 0, 0), Hex(1, 0, -1)
        )
        assertThat(pointyGrid.hex(9, -3, -6).neighbours()).containsExactly(
            Hex(2, -3, 1), Hex(0, 0, 0), Hex(7, 0, -7), Hex(8, -3, -5), Hex(8, -2, -6), Hex(1, -2, 1)
        )
        assertThat(pointyGrid.hex(7, 0, -7).neighbours()).containsExactly(
            Hex(0, 0, 0), Hex(8, -1, -7), Hex(7, -1, -6), Hex(6, 0, -6), Hex(8, -3, -5), Hex(9, -3, -6)
        )
        // a version of 0,0,0 that isn't yet constrained
        assertThat(pointyGrid.hex(10, -4, -6).neighbours()).containsExactly(
            Hex(1, 0, -1), Hex(1, -1, 0), Hex(8, -1, -7), Hex(7, 0, -7), Hex(9, -3, -6), Hex(2, -3, 1)
        )
    }

    @Test
    fun `flat hex grid neighbours are constrained`() {
        assertThat(flatGrid.hex(0, 0, 0).neighbours()).containsExactly(
            Hex(1, -4, 3), Hex(1, -1, 0), Hex(0, -1, 1), Hex(7, -4, -3), Hex(7, -7, 0), Hex(0, -3, 3)
        )
        assertThat(flatGrid.hex(1, -1, 0).neighbours()).containsExactly(
            Hex(2, -1, -1), Hex(2, -2, 0), Hex(1, -2, 1), Hex(0, -1, 1), Hex(0, 0, 0), Hex(1, -4, 3)
        )
        assertThat(flatGrid.hex(7, -7, 0).neighbours()).containsExactly(
            Hex(0, -3, 3), Hex(0, 0, 0), Hex(7, -4, -3), Hex(6, -3, -3), Hex(6, -6, -0), Hex(7, -6, -1)
        )
        assertThat(flatGrid.hex(7, -4, -3).neighbours()).containsExactly(
            Hex(0, 0, 0), Hex(0, -1, 1), Hex(7, -5, -2), Hex(6, -4, -2), Hex(6, -3, -3), Hex(7, -7, 0)
        )
        // a version of 0,0,0 that isn't yet constrained
        assertThat(flatGrid.hex(8, -4, -4).neighbours()).containsExactly(
            Hex(1, -4, 3), Hex(1, -1, 0), Hex(0, -1, 1), Hex(7, -4, -3), Hex(7, -7, 0), Hex(0, -3, 3)
        )
    }

    @Test
    fun `hex grid diagonals are constrained`() {
        assertThat(pointyGrid.hex(1, -1, 0).diagonals()).containsExactly(
            Hex(3, -2, -1), Hex(2, -3, 1), Hex(8, -2, -6), Hex(7, 0, -7), Hex(2, -3, 1), Hex(2, 0, -2)
        )
        assertThat(flatGrid.hex(7, -4, -3).diagonals()).containsExactly(
            Hex(1, -1, 0), Hex(0, -2, 2), Hex(6, -5, -1), Hex(5, -3, -2), Hex(6, -6, 0), Hex(0, -3, 3)
        )
    }

    @Test
    fun `pointy rotation about a point`() {
        // Simple
        assertThat(pointyGrid.hex(1, -1, 0).rotateLeft(Hex(2, -1, -1))).isEqualTo(Hex(2, -3, 1))
        assertThat(pointyGrid.hex(1, -1, 0).rotateRight(Hex(2, -1, -1))).isEqualTo(Hex(2, 0, -2))

        // Boundary
        assertThat(pointyGrid.hex(7, 0, -7).rotateLeft(Hex(2, -1, -1))).isEqualTo(Hex(8, -2, -6))
        assertThat(pointyGrid.hex(7, 0, -7).rotateRight(Hex(2, -1, -1))).isEqualTo(Hex(2, -3, 1))

        // Long
        assertThat(pointyGrid.hex(0, 0, 0).rotateLeft(Hex(9, -3, -6))).isEqualTo(Hex(2, -1, -1))
        assertThat(pointyGrid.hex(0, 0, 0).rotateRight(Hex(9, -3, -6))).isEqualTo(Hex(7, -2, -5))
    }

    @Test
    fun `flat rotation about a point`() {
        // Simple
        assertThat(flatGrid.hex(1, -1, 0).rotateLeft(Hex(2, -1, -1))).isEqualTo(Hex(2, -3, 1))
        assertThat(flatGrid.hex(1, -1, 0).rotateRight(Hex(2, -1, -1))).isEqualTo(Hex(2, -4, 2))

        // Boundary
        assertThat(flatGrid.hex(7, -4, -3).rotateLeft(Hex(2, -1, -1))).isEqualTo(Hex(0, -2, 2))
        assertThat(flatGrid.hex(7, -4, -3).rotateRight(Hex(2, -1, -1))).isEqualTo(Hex(0, -3, 3))

        // Long
        assertThat(flatGrid.hex(0, 0, 0).rotateLeft(Hex(7, -7, 0))).isEqualTo(Hex(0, -3, 3))
        assertThat(flatGrid.hex(0, 0, 0).rotateRight(Hex(7, -7, 0))).isEqualTo(Hex(7, -4, -3))
    }

    @Test
    fun `grid width and height`() {
        assertThat(abs(pointyGrid.hexWidth() - 8 * sqrt(3.0))).isLessThan(0.0001)
        assertThat(pointyGrid.hexHeight()).isEqualTo(6.0)

        assertThat(flatGrid.hexWidth()).isEqualTo(12.0)
        assertThat(abs(flatGrid.hexHeight() - 4 * sqrt(3.0))).isLessThan(0.0001)
    }


}