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
    private val pointyGrid = WrappingHexGrid(8, 4, pointyLayout)
    private val flatGrid = WrappingHexGrid(8, 4, flatLayout)

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
        assertThat(abs(pointyGrid.width() - 8 * sqrt(3.0))).isLessThan(0.0001)
        assertThat(pointyGrid.height()).isEqualTo(6.0)

        assertThat(flatGrid.width()).isEqualTo(12.0)
        assertThat(abs(flatGrid.height() - 4 * sqrt(3.0))).isLessThan(0.0001)
    }

    @Test
    fun `torus coordinates are 7 points with centre as last coordinate`() {
        val hex1 = pointyGrid.hex(0, 0, 0)
        val c1 = pointyGrid.toroidCoordinates(hex1)
        assertThat(c1).hasSize(7)
        // The centre point of 0,0,0 is at [60, 0, 0], x = r1 + r2
        assertThat((c1[6] - Point3D(60.0, 0.0, 0.0)).length()).isLessThan(0.001)
    }

    @Test
    fun `pointy hex corner and torus coordinates`() {
        // In theory...
        // h1[0] == h2[2] (x same, y out by grid height)
        // h1[5] == h2[3] (x same, y out by grid height)
        // h1[4] == h4[0] (x out by grid width, y same)
        // h1[3] == h4[1] (x out by grid width, y same)
        // h1[5] == h3[1] (x out by grid width, y out by grid height)
        // h1[4] == h3[2] (x out by grid width, y out by grid height)
        //
        // h2[4] == h3[0] (x out by grid width, y same)
        // h2[3] == h3[1] (x out by grid width, y same)
        //
        // h3[3] == h4[5] (x same, y out by grid height)
        // h3[2] == h4[0] (x same, y out by grid height)
        val hex1 = pointyGrid.hex(0, 0, 0)
        val hex2 = pointyGrid.hex(2, -3, 1)
        val hex3 = pointyGrid.hex(9, -3, -6)
        val hex4 = pointyGrid.hex(7, 0, -7)
        val corners1 = pointyLayout.polygonCorners(hex1)
        val corners2 = pointyLayout.polygonCorners(hex2)
        val corners3 = pointyLayout.polygonCorners(hex3)
        val corners4 = pointyLayout.polygonCorners(hex4)

        // verify h1 and h2 points that touch
        var diff = corners1[0] - corners2[2]
        assertThat(abs(diff.x)).isLessThan(0.001)
        assertThat(abs(diff.y - pointyGrid.height())).isLessThan(0.001)
        diff = corners1[5] - corners2[3]
        assertThat(abs(diff.x)).isLessThan(0.001)
        assertThat(abs(diff.y - pointyGrid.height())).isLessThan(0.001)

        // verify h1 and h4 points that touch
        diff = corners1[4] - corners4[0]
        assertThat(abs(diff.x + pointyGrid.width())).isLessThan(0.001)
        assertThat(abs(diff.y)).isLessThan(0.001)
        diff = corners1[3] - corners4[1]
        assertThat(abs(diff.x + pointyGrid.width())).isLessThan(0.001)
        assertThat(abs(diff.y)).isLessThan(0.001)

        // verify h1 and h3 points that touch
        diff = corners1[5] - corners3[1]
        assertThat(abs(diff.x + pointyGrid.width())).isLessThan(0.001)
        assertThat(abs(diff.y - pointyGrid.height())).isLessThan(0.001)
        diff = corners1[4] - corners3[2]
        assertThat(abs(diff.x + pointyGrid.width())).isLessThan(0.001)
        assertThat(abs(diff.y - pointyGrid.height())).isLessThan(0.001)

        // Convert to torus coordinates
        val c1 = pointyGrid.toroidCoordinates(hex1)
        val c2 = pointyGrid.toroidCoordinates(hex2)
        val c3 = pointyGrid.toroidCoordinates(hex3)
        val c4 = pointyGrid.toroidCoordinates(hex4)

        // check coordinates are same by taking length of their difference
        assertThat((c1[0] - c2[2]).length()).isLessThan(0.001)
        assertThat((c1[5] - c2[3]).length()).isLessThan(0.001)
        assertThat((c1[4] - c4[0]).length()).isLessThan(0.001)
        assertThat((c1[3] - c4[1]).length()).isLessThan(0.001)
        assertThat((c1[5] - c3[1]).length()).isLessThan(0.001)
        assertThat((c1[4] - c3[2]).length()).isLessThan(0.001)

        assertThat((c2[4] - c3[0]).length()).isLessThan(0.001)
        assertThat((c2[3] - c3[1]).length()).isLessThan(0.001)
        assertThat((c3[3] - c4[5]).length()).isLessThan(0.001)
        assertThat((c3[2] - c4[0]).length()).isLessThan(0.001)

    }

    @Test
    fun `flat hex coordinates on torus`() {
        val hex1 = flatGrid.hex(0, 0, 0)
        val hex2 = flatGrid.hex(0, -3, 3)
        val hex3 = flatGrid.hex(7, -7, 0)
        val hex4 = flatGrid.hex(7, -4, -3)
        val c1 = flatGrid.toroidCoordinates(hex1)
        val c2 = flatGrid.toroidCoordinates(hex2)
        val c3 = flatGrid.toroidCoordinates(hex3)
        val c4 = flatGrid.toroidCoordinates(hex4)

        // check coordinates are same by taking length of their difference
        assertThat((c1[4] - c2[2]).length()).isLessThan(0.001)
        assertThat((c1[5] - c2[1]).length()).isLessThan(0.001)
        assertThat((c1[3] - c4[5]).length()).isLessThan(0.001)
        assertThat((c1[2] - c4[0]).length()).isLessThan(0.001)
        assertThat((c1[4] - c3[0]).length()).isLessThan(0.001)
        assertThat((c1[3] - c3[1]).length()).isLessThan(0.001)

        assertThat((c2[3] - c3[5]).length()).isLessThan(0.001)
        assertThat((c2[2] - c3[0]).length()).isLessThan(0.001)
        assertThat((c3[2] - c4[4]).length()).isLessThan(0.001)
        assertThat((c3[1] - c4[5]).length()).isLessThan(0.001)

    }
}