package net.fish.geometry

import net.fish.geometry.Orientation.ORIENTATION.FLAT
import net.fish.geometry.Orientation.ORIENTATION.POINTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WrappingHexGridTest {
    private val pointyGrid = WrappingHexGrid(8, 4, POINTY)
    private val flatGrid = WrappingHexGrid(8, 4, FLAT)

    @Test
    fun `flat wrapping hex grid constrains hexagons to its grid size`() {
        // Top to bottom
        assertThat(flatGrid.constrainToGrid(Hex(0, 1, -1))).isEqualTo(Hex(0, -3, 3))
        assertThat(flatGrid.constrainToGrid(Hex(1, 0, -1))).isEqualTo(Hex(1, -4, 3))
        assertThat(flatGrid.constrainToGrid(Hex(0, -4, 4))).isEqualTo(Hex(0, 0, 0))
        assertThat(flatGrid.constrainToGrid(Hex(1, -5, 4))).isEqualTo(Hex(1, -1, 0))
        assertThat(flatGrid.constrainToGrid(Hex(7, -8, 1))).isEqualTo(Hex(7, -4, -3))
        assertThat(flatGrid.constrainToGrid(Hex(6, -7, 1))).isEqualTo(Hex(6, -3, -3))
        assertThat(flatGrid.constrainToGrid(Hex(6, -2, -4))).isEqualTo(Hex(6, -6, 0))

        // Left to right
        assertThat(flatGrid.constrainToGrid(Hex(-1, -3, 4))).isEqualTo(Hex(7, -7, 0))
        assertThat(flatGrid.constrainToGrid(Hex(-1, -2, 3))).isEqualTo(Hex(7, -6, -1))
        assertThat(flatGrid.constrainToGrid(Hex(-1, 0, 1))).isEqualTo(Hex(7, -4, -3))
        assertThat(flatGrid.constrainToGrid(Hex(-1, 1, 0))).isEqualTo(Hex(7, -7, 0))

        assertThat(flatGrid.constrainToGrid(Hex(8, -8, 0))).isEqualTo(Hex(0, 0, 0))
        assertThat(flatGrid.constrainToGrid(Hex(8, -7, -1))).isEqualTo(Hex(0, -3, 3))
        assertThat(flatGrid.constrainToGrid(Hex(8, -5, -3))).isEqualTo(Hex(0, -1, 1))
        assertThat(flatGrid.constrainToGrid(Hex(8, -4, -4))).isEqualTo(Hex(0, 0, 0))
    }

    @Test
    fun `pointy wrapping hex grid constrains hexagons to its grid size`() {
        // Top to bottom
        assertThat(pointyGrid.constrainToGrid(Hex(-1, 1, 0))).isEqualTo(Hex(9, -3, -6))
        assertThat(pointyGrid.constrainToGrid(Hex(0, 1, -1))).isEqualTo(Hex(2, -3, 1))
        assertThat(pointyGrid.constrainToGrid(Hex(2, -4, 2))).isEqualTo(Hex(0, 0, 0))
        assertThat(pointyGrid.constrainToGrid(Hex(3, -4, 1))).isEqualTo(Hex(1, 0, -1))
        assertThat(pointyGrid.constrainToGrid(Hex(6, 1, -7))).isEqualTo(Hex(8, -3, -5))
        assertThat(pointyGrid.constrainToGrid(Hex(7, 1, -8))).isEqualTo(Hex(9, -3, -6))

        // Left to right
        assertThat(pointyGrid.constrainToGrid(Hex(-1, 0, 1))).isEqualTo(Hex(7, 0, -7))
        assertThat(pointyGrid.constrainToGrid(Hex(8, 0, -8))).isEqualTo(Hex(0, 0, 0))
        assertThat(pointyGrid.constrainToGrid(Hex(0, -1, 1))).isEqualTo(Hex(8, -1, -7))
        assertThat(pointyGrid.constrainToGrid(Hex(9, -1, -8))).isEqualTo(Hex(1, -1, 0))
        assertThat(pointyGrid.constrainToGrid(Hex(1, -3, 2))).isEqualTo(Hex(9, -3, -6))
        assertThat(pointyGrid.constrainToGrid(Hex(10, -3, -7))).isEqualTo(Hex(2, -3, 1))
    }

    @Test
    fun `pointy hex grid neighbours are constrained`() {
        assertThat(pointyGrid.neighbours(Hex(0, 0, 0))).containsExactly(
            Hex(1, 0, -1), Hex(1, -1, 0), Hex(8, -1, -7), Hex(7, 0, -7), Hex(9, -3, -6), Hex(2, -3, 1)
        )
        assertThat(pointyGrid.neighbours(Hex(1, -1, 0))).containsExactly(
            Hex(2, -1, -1), Hex(2, -2, 0), Hex(1, -2, 1), Hex(8, -1, -7), Hex(0, 0, 0), Hex(1, 0, -1)
        )
        assertThat(pointyGrid.neighbours(Hex(9, -3, -6))).containsExactly(
            Hex(2, -3, 1), Hex(0, 0, 0), Hex(7, 0, -7), Hex(8, -3, -5), Hex(8, -2, -6), Hex(1, -2, 1)
        )
        assertThat(pointyGrid.neighbours(Hex(7, 0, -7))).containsExactly(
            Hex(0, 0, 0), Hex(8, -1, -7), Hex(7, -1, -6), Hex(6, 0, -6), Hex(8, -3, -5), Hex(9, -3, -6)
        )
        // a version of 0,0,0 that isn't yet constrained
        assertThat(pointyGrid.neighbours(Hex(10, -4, -6))).containsExactly(
            Hex(1, 0, -1), Hex(1, -1, 0), Hex(8, -1, -7), Hex(7, 0, -7), Hex(9, -3, -6), Hex(2, -3, 1)
        )
    }

    @Test
    fun `flat hex grid neighbours are constrained`() {
        assertThat(flatGrid.neighbours(Hex(0, 0, 0))).containsExactly(
            Hex(1, -4, 3), Hex(1, -1, 0), Hex(0, -1, 1), Hex(7, -4, -3), Hex(7, -7, 0), Hex(0, -3, 3)
        )
        assertThat(flatGrid.neighbours(Hex(1, -1, 0))).containsExactly(
            Hex(2, -1, -1), Hex(2, -2, 0), Hex(1, -2, 1), Hex(0, -1, 1), Hex(0, 0, 0), Hex(1, -4, 3)
        )
        assertThat(flatGrid.neighbours(Hex(7, -7, 0))).containsExactly(
            Hex(0, -3, 3), Hex(0, 0, 0), Hex(7, -4, -3), Hex(6, -3, -3), Hex(6, -6, -0), Hex(7, -6, -1)
        )
        assertThat(flatGrid.neighbours(Hex(7, -4, -3))).containsExactly(
            Hex(0, 0, 0), Hex(0, -1, 1), Hex(7, -5, -2), Hex(6, -4, -2), Hex(6, -3, -3), Hex(7, -7, 0)
        )
        // a version of 0,0,0 that isn't yet constrained
        assertThat(flatGrid.neighbours(Hex(8, -4, -4))).containsExactly(
            Hex(1, -4, 3), Hex(1, -1, 0), Hex(0, -1, 1), Hex(7, -4, -3), Hex(7, -7, 0), Hex(0, -3, 3)
        )
    }

    @Test
    fun `hex grid diagonals are constrained`() {
        assertThat(pointyGrid.diagonals(Hex(1, -1, 0))).containsExactly(
            Hex(3, -2, -1), Hex(2, -3, 1), Hex(8, -2, -6), Hex(7, 0, -7), Hex(2, -3, 1), Hex(2, 0, -2)
        )
        assertThat(flatGrid.diagonals(Hex(7, -4, -3))).containsExactly(
            Hex(1, -1, 0), Hex(0, -2, 2), Hex(6, -5, -1), Hex(5, -3, -2), Hex(6, -6, 0), Hex(0, -3, 3)
        )
    }
}