package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.assertj.core.api.Assertions.assertThat
import org.joml.Math.toRadians
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

class WrappingHexGridTest {
    private val pointyLayout = Layout(POINTY)
    private val flatLayout = Layout(FLAT)
    private val pointyGrid = WrappingHexGrid(8, 4, pointyLayout)
    private val flatGrid = WrappingHexGrid(8, 4, flatLayout)

    @Test
    fun `can get hexes out of pointy grid in correct sequence`() {
        val hexes = listOf(
            Hex(0, 0, 0), Hex(1, 0, -1), Hex(2, 0, -2),
            Hex(1, -1, 0), Hex(2, -1, -1), Hex(3, -1, -2),
            Hex(1, -2, 1), Hex(2, -2, 0), Hex(3, -2, -1),
            Hex(2, -3, 1), Hex(3, -3, 0), Hex(4, -3, -1)
        )
        assertThat(WrappingHexGrid(3, 4, pointyLayout).hexes()).containsExactlyElementsOf(hexes)
    }

    @Test
    fun `can get hexes out of flat grid in correct sequence`() {
        val hexes = listOf(
            Hex(0, 0, 0), Hex(1, -1, 0), Hex(2, -1, -1), Hex(3, -2, -1),
            Hex(0, -1, 1), Hex(1, -2, 1), Hex(2, -2, 0), Hex(3, -3, 0),
            Hex(0, -2, 2), Hex(1, -3, 2), Hex(2, -3, 1), Hex(3, -4, 1)
        )
        assertThat(WrappingHexGrid(4, 3, flatLayout).hexes()).containsExactlyElementsOf(hexes)
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

    @Test
    fun `getting mesh coordinates and indexes from 1x2 pointy grid`() {
        val grid = WrappingHexGrid(1, 2, pointyLayout)
        val mesh = grid.mesh()
        assertThat(mesh.points).hasSize(6) // lots of duplicates in this tiny grid
        assertThat(mesh.indices).hasSize(36)
        assertThat(mesh.indices).containsExactly(
            0, 1, 4,
            1, 2, 4,
            2, 1, 4,
            1, 0, 4,
            0, 3, 4,
            3, 0, 4,

            2, 3, 5,
            3, 0, 5,
            0, 3, 5,
            3, 2, 5,
            2, 1, 5,
            1, 2, 5
        )
    }

    @Test
    fun `getting mesh coordinates and indexes from 2x2 pointy grid`() {
        val grid = WrappingHexGrid(2, 2, pointyLayout)
        val mesh = grid.mesh()
        assertThat(mesh.points).hasSize(12) // lots of duplicates in this tiny grid
        assertThat(mesh.indices).hasSize(72)
        assertThat(mesh.indices).containsExactly(
            0, 1, 6,
            1, 2, 6,
            2, 3, 6,
            3, 4, 6,
            4, 5, 6,
            5, 0, 6,

            4, 3, 9,
            3, 7, 9,
            7, 1, 9,
            1, 0, 9,
            0, 8, 9,
            8, 4, 9,

            7, 8, 10,
            8, 0, 10,
            0, 5, 10,
            5, 2, 10,
            2, 1, 10,
            1, 7, 10,

            2, 5, 11,
            5, 4, 11,
            4, 8, 11,
            8, 7, 11,
            7, 3, 11,
            3, 2, 11
        )
    }

    @Test
    fun `getting pointy hex centres`() {
        val grid = WrappingHexGrid(m = 2, n = 2, layout = pointyLayout, r1 = 1.0, r2 = 2.0)
        val centres = grid.centres()
        // freaky shape!
        assertThat((centres[0] - Point3D(3.0, 0.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[1] - Point3D(-3.0, 0.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[2] - Point3D(0.0, 1.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[3] - Point3D(0.0, -1.0, 0.0)).length()).isLessThan(0.0001)
    }

    @Test
    fun `getting flat hex centres`() {
        val grid = WrappingHexGrid(m = 2, n = 2, layout = flatLayout, r1 = 1.0, r2 = 2.0)
        val centres = grid.centres()
        // freaky shape!
        assertThat((centres[0] - Point3D(3.0, 0.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[1] - Point3D(-2.0, 0.0, 1.0)).length()).isLessThan(0.0001)
        assertThat((centres[2] - Point3D(1.0, 0.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[3] - Point3D(-2.0, 0.0, -1.0)).length()).isLessThan(0.0001)
    }

    @Test
    fun `getting axes of flat hexes`() {
        val grid = WrappingHexGrid(m = 4, n = 4, layout = flatLayout, r1 = 1.0, r2 = 2.0)
        val axes = grid.hexAxes()
//        println("=====================================================================")
//        grid.hexes().forEachIndexed { i, hex ->
//            println("hex: $hex")
//            println("axes:\n${axes[i]}")
//            println("-------------------")
//        }

        checkHexAxes(axes[0], Vector3f(3f, 0f, 0f), Matrix3f(0f, -1f, 0f, 0f, 0f, -1f, 1f, 0f, 0f))
        checkHexAxes(axes[1], Vector3f(0f, 2.7071f, 0.7071f), Matrix3f(1f, 0f, 0f, 0f, 0.6547f, -0.7559f, 0f, 0.7559f, 0.6547f))
        checkHexAxes(axes[2], Vector3f(-3f, 0f, 0f), Matrix3f(0f, 1f, 0f, 0f, 0f, -1f, -1f, 0f, 0f))
        checkHexAxes(axes[3], Vector3f(0f, -2.7071f, 0.7071f), Matrix3f(-1f, 0f, 0f, 0f, -0.6547f, -0.7559f, 0f, -0.7559f, 0.6547f))

        checkHexAxes(axes[4], Vector3f(2f, 0f, 1f), Matrix3f(0f, -1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f))
        checkHexAxes(axes[5], Vector3f(0f, 1.293f, 0.7071f), Matrix3f(1f, 0f, 0f, 0f, 0.6547f, 0.7559f, 0f, -0.7559f, 0.6547f))
        checkHexAxes(axes[6], Vector3f(-2f, 0f, 1f), Matrix3f(0f, 1f, 0f, -1f, 0f, 0f, 0f, 0f, 1f))
        checkHexAxes(axes[7], Vector3f(0f, -1.293f, 0.7071f), Matrix3f(-1f, 0f, 0f, 0f, -0.6547f, 0.7559f, 0f, 0.7559f, 0.6547f))

        checkHexAxes(axes[8], Vector3f(1f, 0f, 0f), Matrix3f(0f, -1f, 0f, 0f, 0f, 1f, -1f, 0f, 0f))
        checkHexAxes(axes[9], Vector3f(0f, 1.293f, -0.7071f), Matrix3f(1f, 0f, 0f, 0f, -0.6547f, 0.7559f, 0f, -0.7559f, -0.6547f))
        checkHexAxes(axes[10], Vector3f(-1f, 0f, 0f), Matrix3f(0f, 1f, 0f, 0f, 0f, 1f, 1f, 0f, 0f))
        checkHexAxes(axes[11], Vector3f(0f, -1.293f, -0.7071f), Matrix3f(-1f, 0f, 0f, 0f, 0.6547f, 0.7559f, 0f, 0.7559f, -0.6547f))

        checkHexAxes(axes[12], Vector3f(2f, 0f, -1f), Matrix3f(0f, -1f, 0f, -1f, 0f, 0f, 0f, 0f, -1f))
        checkHexAxes(axes[13], Vector3f(0f, 2.7071f, -0.7071f), Matrix3f(1f, 0f, 0f, 0f, -0.6547f, -0.7559f, 0f, 0.7559f, -0.6547f))
        checkHexAxes(axes[14], Vector3f(-2f, 0f, -1f), Matrix3f(0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, -1f))
        checkHexAxes(axes[15], Vector3f(0f, -2.7071f, -0.7071f), Matrix3f(-1f, 0f, 0f, 0f, 0.6547f, -0.7559f, 0f, -0.7559f, -0.6547f))

    }

    @Test
    fun `getting axes of pointy hexes`() {
        val grid = WrappingHexGrid(m = 4, n = 4, layout = pointyLayout, r1 = 1.0, r2 = 2.0)
        val axes = grid.hexAxes()
//        println("=====================================================================")
//        grid.hexes().forEachIndexed { i, hex ->
//            println("hex: $hex")
//            println("axes:\n${axes[i]}")
//            println("-------------------")
//        }

        checkHexAxes(axes[0], Vector3f(3f, 0f, 0f), Matrix3f(0f, -1f, 0f, 0f, 0f, -1f, 1f, 0f, 0f))
        checkHexAxes(axes[1], Vector3f(0f, 3f, 0f), Matrix3f(1f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f))
        checkHexAxes(axes[2], Vector3f(-3f, 0f, 0f), Matrix3f(0f, 1f, 0f, 0f, 0f, -1f, -1f, 0f, 0f))
        checkHexAxes(axes[3], Vector3f(0f, -3f, 0f), Matrix3f(-1f, 0f, 0f, 0f, -0f, -1f, 0f, -1f, 0f))

        checkHexAxes(axes[4], Vector3f(1.4142f, 1.4142f, 1f), Matrix3f(0.7071f, -0.7071f, 0f, 0.7071f, 0.7071f, 0f, 0f, 0f, 1f))
        checkHexAxes(axes[5], Vector3f(-1.4142f, 1.4142f, 1f), Matrix3f(0.7071f, 0.7071f, 0f, -0.7071f, 0.7071f, 0f, 0f, 0f, 1f))
        checkHexAxes(axes[6], Vector3f(-1.4142f, -1.4142f, 1f), Matrix3f(-0.7071f, 0.7071f, 0f, -0.7071f, -0.7071f, 0f, 0f, 0f, 1f))
        checkHexAxes(axes[7], Vector3f(1.4142f, -1.4142f, 1f), Matrix3f(-0.7071f, -0.7071f, 0f, 0.7071f, -0.7071f, 0f, 0f, 0f, 1f))

        checkHexAxes(axes[8], Vector3f(1f, 0f, 0f), Matrix3f(0f, -1f, 0f, 0f, 0f, 1f, -1f, 0f, 0f))
        checkHexAxes(axes[9], Vector3f(0f, 1f, 0f), Matrix3f(1f, 0f, 0f, 0f, 0f, 1f, 0f, -1f, 0f))
        checkHexAxes(axes[10], Vector3f(-1f, 0f, 0f), Matrix3f(0f, 1f, 0f, 0f, 0f, 1f, 1f, 0f, 0f))
        checkHexAxes(axes[11], Vector3f(0f, -1f, 0f), Matrix3f(-1f, 0f, 0f, 0f, -0f, 1f, 0f, 1f, 0f))

        checkHexAxes(axes[12], Vector3f(1.4142f, 1.4142f, -1f), Matrix3f(0.7071f, -0.7071f, 0f, -0.7071f, -0.7071f, 0f, 0f, 0f, -1f))
        checkHexAxes(axes[13], Vector3f(-1.4142f, 1.4142f, -1f), Matrix3f(0.7071f, 0.7071f, 0f, 0.7071f, -0.7071f, 0f, 0f, 0f, -1f))
        checkHexAxes(axes[14], Vector3f(-1.4142f, -1.4142f, -1f), Matrix3f(-0.7071f, 0.7071f, 0f, 0.7071f, 0.7071f, 0f, 0f, 0f, -1f))
        checkHexAxes(axes[15], Vector3f(1.4142f, -1.4142f, -1f), Matrix3f(-0.7071f, -0.7071f, 0f, -0.7071f, 0.7071f, 0f, 0f, 0f, -1f))

    }

    @Test
    fun `validate normals again`() {
        val grid = WrappingHexGrid(m = 12, n = 12, layout = pointyLayout, r1 = 1.0, r2 = 2.0)
        val axes = grid.hexAxes()
        val hexes = grid.hexes().toList()

        println("=====================================================================")
        listOf(0, 12, 24, 36, 48, 60, 72, 84, 96, 108, 120, 132).forEach { i ->
            val hex = hexes[i]
            println("hex: $hex")
            println("axes:\n${axes[i]}")
            println("-------------------")
        }

    }

    private fun checkHexAxes(hexAxis: HexAxis, expectedLoaction: Vector3f, expectedAxis: Matrix3f) {
        val v3 = Vector3f() // a holder for writing stuff into
        val m3 = Matrix3f()
        assertThat(hexAxis.location.sub(expectedLoaction).length()).isLessThan(0.001f)

        println("--- expecte matrix:")
        println(expectedAxis)

        println("--- before subtracting expected:")
        println(hexAxis.axes)

        hexAxis.axes.sub(expectedAxis, m3)
        println("--- after subtracting expected:")
        println(hexAxis.axes)

        m3.getScale(v3)
        assertThat(v3.length()).isLessThan(0.001f)
    }

    @Test
    fun `rotation testing`() {
        val r45x = Vector3f(toRadians(45f), 0f, 0f)
        val rm45x = Vector3f(toRadians(-45f), 0f, 0f)
        val a45x = Matrix3f().identity().rotateXYZ(r45x)
        val am45x = Matrix3f().identity().rotateXYZ(rm45x)
        println(" 45° x:\n$a45x")
        println("-45° x:\n$am45x")

        val c1 = Vector3f(0f, 1f, 0f).mul(a45x)
        println(" A45x x [0,1,0] = $c1")

        val c2 = Vector3f(0f, 1f, 0f).mul(am45x)
        println("Am45x x [0,1,0] = $c2")

        val m1 = a45x.mul(am45x, Matrix3f())
        println("a x am = \n$m1")
    }

    @Test
    fun `euler angles`() {
        val rotAngles = Vector3f(toRadians(45f), toRadians(45f), toRadians(45f))
        val matrixOfRotation = Matrix3f().rotateZYX(rotAngles)
        val e = matrixOfRotation.getEulerAnglesZYX(Vector3f())
        println("matrix of rotation:\n$matrixOfRotation")
        println("rotAngles: $rotAngles")
        println("    euler: $e")

        val m2 = Matrix3f().rotateZYX(rotAngles)
        val p1 = Vector3f(1f, 1f, 1f).mul(m2)
        println(" m2 x [1,1,1] = $p1")

        val p2 = Vector3f(1f, 1f, 1f).rotateX(toRadians(45f))
        println("[1,1,1] rot by 45x = $p2")
        val p3 = p2.rotateY(toRadians(45f), Vector3f())
        println("... rot by 45y = $p3")
        val p4 = p3.rotateZ(toRadians(45f), Vector3f())
        println("... rot by 45z = $p4")

    }

    @Test
    fun `checking camera matrix rotations`() {
        //val rotAngles = Vector3f(toRadians(10.0f), toRadians(0.0), toRadians(0.0))
        val cameraLocation = Vector3f(0f, 0f, 1f)
        val cameraRotation = Vector3f(0f, 0f, 0f)
        val cameraDirectionAxes = Matrix3f().rotateZYX(cameraRotation)
        println("cameraDirs:\n$cameraDirectionAxes")
        val rotAngles = Vector3f(toRadians(45.0f), toRadians(0.0f), toRadians(0.0f))
        val matrixOfRotation = Matrix3f().rotateZYX(rotAngles)
        println("matrix of rotation:\n$matrixOfRotation")

        cameraDirectionAxes.mul(matrixOfRotation)
        println("new cameraRotation 1:\n$cameraDirectionAxes")
        cameraDirectionAxes.mul(matrixOfRotation)
        println("new cameraRotation 2:\n$cameraDirectionAxes")

        cameraLocation.mul(cameraDirectionAxes)
        println("new camera location1: $cameraLocation")


        // now start at 0, sqrt(2)/2, sqrt(2)/2 pointing at 0,0,0, so camera direction = -45, 0, 0
        val cameraDirection2 = Matrix3f().rotateZYX(Vector3f(-0.785398163f, 0f, 0f))
        println("cd:\n$cameraDirection2")
        val r2o2 = sqrt(2f)/2f
        cameraLocation.set(0f, r2o2, r2o2)
        cameraDirection2.mul(matrixOfRotation)
        println("cd:\n$cameraDirection2") // should be along z axis - the matrix is now I! This isn't the one to now apply a rotation with, but the camera's normals.
        cameraLocation.mul(cameraDirection2)
        println("new camera location2: $cameraLocation")

    }

    @Test
    fun `looking at`() {
        // 22.5 degrees above the horizon
        val cameraLocation = Vector3f(0f, 0.382683432f, 0.923879533f)
        val worldCentre = Vector3f(0f, 0f, 0f)
        val dirVec = worldCentre.sub(cameraLocation, Vector3f())
        var lookAlong = Matrix3f().lookAlong(dirVec, Vector3f(0f, 1f, 0f))

        println("lookAlong with camera at 1 unit, 22.5 deg above horizon: $cameraLocation\n$lookAlong")
        // lookAlong with camera at 1 unit, 22.5 deg above horizon: ( 0.000E+0  3.827E-1  9.239E-1)
        // 1.000E+0  0.000E+0  0.000E+0
        // 0.000E+0  9.239E-1 -3.827E-1
        // 0.000E+0  3.827E-1  9.239E-1
        // NOTE: first row = X axis, second row = Y axis (note -ve z), third row = Z axis - positive z. seems wrong

        dirVec.mul(-1f)
        lookAlong = Matrix3f().lookAlong(dirVec, Vector3f(0f, 1f, 0f))

        println("lookAlong with camera at 1 unit, 22.5 deg above horizon: $cameraLocation\n$lookAlong")



//        var eulerAngles = lookAlong.getEulerAnglesZYX(Vector3f()).mul(180f / PI.toFloat())
//        println("lookAlong:\n$lookAlong\nangles: $eulerAngles")

//        cameraLocation.set(0f, 0.99f, -0.01f)
//        lookAlong = Matrix3f().lookAlong(worldCentre.sub(cameraLocation, Vector3f()), Vector3f(0f, -1f, 0f))
//        eulerAngles = lookAlong.getEulerAnglesZYX(Vector3f()).mul(180f / PI.toFloat())
//        println("lookAlong:\n$lookAlong\nangles: $eulerAngles")

    }

    @Test
    fun `euler angles checking`() {
        val cameraAngles = vectorToRadians(Vector3f(5f, 5f, 0f))
        val yaw = cameraAngles.y
        val pitch = cameraAngles.x
        val manuallyCalculatedEuler = Vector3f(
            cos(yaw) * cos(pitch),
            sin(pitch),
            sin(yaw) * cos(pitch)
        ).normalize()
        val matrixCalculatedEuler = Matrix3f().rotateXYZ(cameraAngles).getEulerAnglesZYX(Vector3f())

        println("manual:\n$manuallyCalculatedEuler")
        println("matrix:\n$matrixCalculatedEuler")

    }

    @Test
    fun `quaternion tests vs matrix - setting full direction vector works but multiplication is not as expected`() {
        val q = Quaternionf().setFromNormalized(Matrix3f().identity())

        // Check matrix rotation and quaternion rotation do the same thing to a point
        val r45x = Vector3f(toRadians(45f), 0f, 0f)
        val a45x = Matrix3f().identity().rotateXYZ(r45x)
        val r15x = Vector3f(toRadians(15f), 0f, 0f)
        val a15x = Matrix3f().identity().rotateXYZ(r15x)
        q.setFromNormalized(a45x)

        // rotate 0,0,1 by 45 degrees x 3 - THIS WORKS as it's SIMPLE in one plane
        println("rotating 0,0,1 by 45 deg around X, 3 times")
        val p1 = Vector3f(0f, 0f, 1f)
        val c1 = p1.mul(a45x, Vector3f()) // this is ( 0.000E+0  7.071E-1  7.071E-1), i.e. 0,1,0 rotated by x axis by 45 deg
        val c2 = p1.rotate(q, Vector3f()) // this is the same as c1, i.e. a rotation about x axis of 45 deg
        println("c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")
        c1.mul(a45x)
        c2.rotate(q)
        println("c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")
        c1.mul(a45x)
        c2.rotate(q)
        println("c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")

        // Check another point
        println("\nRotating 1,1,1 by 45 deg X axis")
        p1.set(1f, 1f, 1f)
        p1.mul(a45x, c1)
        p1.rotate(q, c2)
        println("c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")

        // Check another point, 15 degrees, when we're going over the top of X axis (crossing z negative to positive)
        println("\nrotate point from -ve z into +ve z:")
        q.setFromNormalized(a15x)
        p1.set(0f, 1.95f, -0.1f)
        p1.mul(a15x, c1)
        p1.rotate(q, c2)
        println("c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")

        // check xz rotation - THIS DOESN'T WORK FOR MATRIX AS THE INITIAL SETUP OF THE MATRIX IS NOT SIMULTANEOUS
        println("\nrotate point on unit sphere at $p1 by 45 deg in x/z, twice to get to 0,1,0")
        // This matrix does not do the correct rotation - not doing it simultaneously
        val r45xz = Vector3f(toRadians(45f), 0f, toRadians(45f))
        val a45xz = Matrix3f().identity().rotateXYZ(r45xz)

        // We can get a quaternion from direction vector - THIS WORKS BECAUSE WE SPECIFY THE EXACT VECTOR TO ROTATE AROUND INITIALLY!
        val sqrt2div2 = sqrt(2f) / 2f
        // create a unit vector to rotate around, this will be either the (local) "X" for pitch, "Y" for yaw, "Z" for roll
        q.setAngleAxis(toRadians(45f), sqrt2div2, 0f, sqrt2div2)
        println("Q THAT IS X/Z 45 DEGREES: $q\nas matrix:\n${q.get(Matrix3f())}")
        p1.set(0.7071f, 0f, -0.7071f)
        p1.mul(a45xz, c1)
        p1.rotate(q, c2)
        // Only c2 is correct...
        println("xz1: c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")
        c1.mul(a45xz)
        c2.rotate(q)
        // Only c2 is correct...
        println("xz2: c1: $c1\nc2: $c2\ndiff: ${c1.sub(c2, Vector3f()).length()}")

        // Rotate another point by our magic q:
        val p2 = Vector3f(0f, 1f, 0f)
        p2.rotate(q, c1)
        c1.rotate(q, c2)
        println("\nMagic Q on $p2 -> $c1 -> $c2")

        // We can also simply use v3.rotateAxis, THIS WORKS!
        println("\nTrying with v3.rotateAxis")
        p1.rotateAxis(toRadians(45f), sqrt2div2, 0f, sqrt2div2, c1)
        c1.rotateAxis(toRadians(45f), sqrt2div2, 0f, sqrt2div2, c2)
        println("p1 -> $c1 -> $c2")

        // can we combine 2 quaternion rotations to get same result - using multiply? doesn't work
        println("\nTrying to build quat up from 2 rotations using MULTIPLY (p1: $p1)")
        val qx = Quaternionf().setAngleAxis(toRadians(45f), 1f, 0f, 0f)
        val qy = Quaternionf().setAngleAxis(toRadians(45f), 0f, 1f, 0f)
        val qBoth1 = qx.mul(qy, Quaternionf())
        val qBoth2 = qy.mul(qx, Quaternionf())

        // This gets the original q value back. see https://gamedev.stackexchange.com/a/188332/146339
        val qThing = qy.invert(Quaternionf()).mul(qx, Quaternionf()).mul(qy, Quaternionf())

        p1.rotate(qBoth1, c1)
        c1.rotate(qBoth1, c2)
        println("both1 $qBoth1: p1 -> $c1 -> $c2")

        p1.rotate(qBoth2, c1)
        c1.rotate(qBoth2, c2)
        println("both2 $qBoth2: p1 -> $c1 -> $c2")

        p1.rotate(qThing, c1)
        c1.rotate(qThing, c2)
        println("thing! $qThing: p1 -> $c1 -> $c2")
        c1.set(sqrt2div2, 0f, sqrt2div2)
        c1.rotate(qThing, c2)
        println("Thing should convert $c1 into itself, as rotating a point on the line doesn't move:")
        println("$c1 -> $c2")


        // /* This is for outputting points to plot in https://www.monroecc.edu/faculty/paulseeburger/calcnsf/CalcPlot3D/
        // loop around doing 5 degree rotations
        println("# X  # Y # Z")
        q.setAngleAxis(toRadians(5f), 1f, 0f, 0f)
        val q2 = Quaternionf().setAngleAxis(toRadians(-5f), 0f, 1f, 0f)
        q.mul(q2)

        q.setAngleAxis(toRadians(5f), sqrt2div2, 0f, sqrt2div2)
        c1.set(0f, 0f, 1f)
        for (it in 0..(360/5)) {
            c1.rotate(q)
            val cx = if (abs(c1.x) < 0.0001) 0f else c1.x
            val cy = if (abs(c1.y) < 0.0001) 0f else c1.y
            val cz = if (abs(c1.z) < 0.0001) 0f else c1.z
            println("""
                <point>
                    point="($cx, $cy, $cz)"
                    color="rgb(0,0,0)"
                    size="4"
                    visible="true"
                </point>
            """.trimIndent())
        }
        // */


        // Converting from q to normals - THIS WORKS
        q.setAngleAxis(toRadians(45f), sqrt2div2, 0f, sqrt2div2) // 45 degree around XZ plane
        val m1 = q.get(Matrix3f())
        println("\n45 deg XZ matrix from q:\n$m1")
        p1.mul(m1, c1)
        c1.mul(m1, c2)
        println("p1 -> $c1 -> $c2")

        // ALSO WORKS - just X rotation
        q.setAngleAxis(toRadians(45f), 1f, 0f, 0f) // 45 degree around XZ plane
        q.get(m1)
        println("\n45 deg X matrix from q:\n$m1")
        p1.mul(m1, c1)
        c1.mul(m1, c2)
        println("p1 -> $c1 -> $c2")

        // Convert quaternion to Simple Angles
        q.getEulerAnglesXYZ(c1)

    }

    @Test
    fun `quaternion to rotation about one axis and back again`() {
        // so what happens if I have an orientation, apply the rotation in one of its "axes" (e.g. local x axis after converting to matrix)
        // and then convert back to quaternion to keep its orientation

        // the camera orientation, pointing in x/z direction
        val cameraOrientation = Quaternionf().fromAxisAngleDeg(Vector3f(1f, 0f, 1f).normalize(), 45f)

        // cameraOrientation.rotateLocalX()

    }

    @Test
    fun `what u does`() {
        val sqrt2div2 = sqrt(2f) / 2f
        val sqrt3div3 = sqrt(3f) / 3f
        val worldCentre = Vector3f(-0.2f, 0.4f, 0.3f)
        // val cameraPosition = Vector3f(sqrt2div2, 0f, sqrt2div2)
        val cameraPosition = Vector3f(sqrt3div3, -sqrt3div3, sqrt3div3)

        val rotationFromLookAt = Quaternionf().lookAlong(worldCentre.sub(cameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize().conjugate()

//        val rotationFromZYX = Quaternionf().rotationZYX(0f, toRadians(45f), 0f)

        println("""
            <!--
rotationFromLookAt: $rotationFromLookAt
${rotationFromLookAt.get(Matrix3f())}
            -->
        """.trimIndent())

        val cameraRotation = Quaternionf(rotationFromLookAt)

        plotHeader(1)


        println("<step 1>")
        for(x in (0 .. 355/5)) {
            val cameraOrientationMatrix = cameraRotation.get(Matrix3f())
            val worldToCameraVector = cameraPosition.sub(worldCentre, Vector3f())
            // this should stay the same as we only rotate about it
//            val rotationVector = cameraOrientationMatrix.getColumn(0, Vector3f()).normalize()
//            val currentUp = cameraOrientationMatrix.getColumn(1, Vector3f()).normalize()
//            val currentDir = cameraOrientationMatrix.getColumn(2, Vector3f()).normalize()

            val inverseCameraRotation = cameraRotation.conjugate(Quaternionf())
            val rotationVector = inverseCameraRotation.positiveX(Vector3f())
            val currentUp = inverseCameraRotation.positiveY(Vector3f())
            val currentDir = inverseCameraRotation.positiveZ(Vector3f())

            val newLocation = worldToCameraVector.rotateAxis(toRadians(5f), rotationVector.x, rotationVector.y, rotationVector.z, Vector3f()).add(worldCentre)

            val dirVec = newLocation.sub(worldCentre, Vector3f())
            val unitDirVec = dirVec.normalize(Vector3f())
            val newUpVector = unitDirVec.cross(rotationVector, Vector3f())
            val newRotationMatrix = Matrix3f().setColumn(0, rotationVector).setColumn(1, newUpVector).setColumn(2, unitDirVec)
            val determinant = newRotationMatrix.determinant()
            val newRotation = Quaternionf().setFromNormalized(newRotationMatrix)

            // calculate a point, distance from camera in opposite direction of the camera dir to prove it's pointing at world centre
            val distFromWorldCentre = cameraPosition.sub(currentDir.mul(dirVec.length(), Vector3f()), Vector3f()).sub(worldCentre).length()
            println("<!-- should be 0: $distFromWorldCentre -->")

            val cx = if (abs(cameraPosition.x) < 0.0001) 0f else cameraPosition.x
            val cy = if (abs(cameraPosition.y) < 0.0001) 0f else cameraPosition.y
            val cz = if (abs(cameraPosition.z) < 0.0001) 0f else cameraPosition.z

            val grey = round((0.1f + x * 0.8f / 72f) * 255f).toInt()

            // println("<step ${x+1}>")
            println("""
                <!-- point number $x -->
                <!--
                x/y/z: $rotationVector, $currentUp, $currentDir
                camera rotation matrix:
$cameraOrientationMatrix
                determ: ${cameraOrientationMatrix.determinant()}

                new direction: $dirVec
                new up: $newUpVector
                new rotation matrix determ: $determinant
                new rot matrix:
$newRotationMatrix
                -->

            """.trimIndent())
            println("""
                <point>
                    point="($cx, $cy, $cz)"
                    color="rgb($grey,$grey,$grey)"
                    size="4"
                    visible="true"
                </point>
            """.trimIndent())

            cameraPosition.set(newLocation)
            cameraRotation.set(newRotation)
        }
        plotFooter()
    }

    @Test
    fun `what j does`() {
        val worldCentre = Vector3f(0.5f, 1f, 0.25f)
        val cameraPosition = Vector3f(-0.5f, -0.5f, 1.75f)
        val rotationFromLookAt = Quaternionf().lookAlong(worldCentre.sub(cameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize().conjugate()
        val cameraRotation = Quaternionf(rotationFromLookAt)

        val camera = Camera(rotation = cameraRotation, position = cameraPosition)

        val projectBack = cameraPosition.sub(cameraPosition.normalize().mul(cameraPosition.sub(worldCentre, Vector3f()).length(), Vector3f()), Vector3f()).sub(worldCentre).length()
        println("<!-- initial check: $projectBack -->")

        plotHeader(1)

        println("<step 1>")
        for(x in (0 .. 355/5)) {
            val rotAngles = Vector3f(toRadians(5f), toRadians(5f), 0f)

            // do left/right first so we don't affect the up vector
            val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())

            val cameraX = inverseCameraRotation.positiveX(Vector3f())
            val cameraY = inverseCameraRotation.positiveY(Vector3f())
            val cameraZ = inverseCameraRotation.positiveZ(Vector3f())
            val globalY = Vector3f(0f, 1f, 0f)

            // calculate the new camera direction (relative to world centre) after rotation by global Y first
            val newCameraVector = camera.position.sub(worldCentre, Vector3f())
            if (rotAngles.x != 0f) {
                newCameraVector.rotateAxis(rotAngles.x, globalY.x, globalY.y, globalY.z)
            }

            // calculate camera's new rotation vector before we rotate about its local X for any up/down movement
            val newX = cameraY.cross(newCameraVector, Vector3f()).normalize()
            // val newY = newCameraVector.cross(newX, Vector3f()).normalize()
            println("""<!-- XXXXXXXXXXX 
                |cameraX: $cameraX, newX: $newX 
                |from Y: $cameraY cross Z: $newCameraVector)
                |-->""".trimMargin())

            // now rotate about any up/down
            if (rotAngles.y != 0f) {
                newCameraVector.rotateAxis(rotAngles.y, newX.x, newX.y, newX.z)
            }

            // calculate the new up vector from the direction vector and the unchanged (for this part of the rotation) X vector
            val newZ = newCameraVector.normalize(Vector3f())
            val newY = newZ.cross(newX, Vector3f())

            val newRotationMatrix = Matrix3f().setColumn(0, newX).setColumn(1, newY).setColumn(2, newZ)
            val newRotation = Quaternionf().setFromNormalized(newRotationMatrix)

            newCameraVector.add(worldCentre)

            // DEBUG BIT IN MIDDLE
            val cameraOrientationMatrix = camera.rotation.get(Matrix3f())
            val determinant = newRotationMatrix.determinant()
            val distFromWorldCentre = newCameraVector.sub(newZ.mul(newCameraVector.sub(worldCentre, Vector3f()).length(), Vector3f()), Vector3f()).sub(worldCentre).length()
            println("<!-- should be 0: $distFromWorldCentre -->")

            val cx = if (abs(newCameraVector.x) < 0.0001) 0f else newCameraVector.x
            val cy = if (abs(newCameraVector.y) < 0.0001) 0f else newCameraVector.y
            val cz = if (abs(newCameraVector.z) < 0.0001) 0f else newCameraVector.z

            val grey = round((0.1f + x * 0.8f / 72f) * 255f).toInt()

            // println("<step ${x+1}>")
            println("""
                <!-- point number $x -->
                <!--
                x/y/z: $cameraX, $cameraY, $cameraZ
                camera rotation matrix:
$cameraOrientationMatrix
                determ: ${cameraOrientationMatrix.determinant()}

                new up: $newY
                new direction: $newZ
                new rotation matrix determ: $determinant
                new rot matrix:
$newRotationMatrix
                -->

            """.trimIndent())
            println("""
                <point>
                    point="($cx, $cy, $cz)"
                    color="rgb($grey,$grey,$grey)"
                    size="4"
                    visible="true"
                </point>
            """.trimIndent())
            // END DEBUG BIT

            camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
            camera.setRotation(newRotation.x, newRotation.y, newRotation.z, newRotation.w)

        }
        plotFooter()
    }

    data class Camera(
        val rotation: Quaternionf = Quaternionf(),
        val position: Vector3f = Vector3f()
    ) {
        fun setRotation(x: Float, y: Float, z: Float, w: Float) {
            rotation.set(x, y, z, w)
        }

        fun setPosition(x: Float, y: Float, z: Float) {
            position.set(x, y, z)
        }
    }

    fun vectorToRadians(v: Vector3f): Vector3f {
        return v.mul((PI / 180.0).toFloat(), Vector3f())
    }

    fun plotHeader(points: Int) {
        println("""
            <init steps="$points">
        """.trimIndent())
    }

    fun plotFooter() {
        println("""
    <window>
		hsrmode="3"
		nomidpts="true"
		anaglyph="-1"
		transparent="false"
		alpha="140"
		twoViews="false"
		unlinkViews="false"
		axisExtension="0.7"
		xaxislabel="x"
		yaxislabel="y"
		zaxislabel="z"
		xmin="-2"
		xmax="2"
		xscale="1"
		xscalefactor="1"
		ymin="-2"
		ymax="2"
		yscale="1"
		yscalefactor="1"
		zmin="-2"
		zmax="2"
		zscale="1"
		zscalefactor="1"
		zminClip="-4"
		zmaxClip="4"
		zoom="1.265333"
		edgesOn="true"
		facesOn="true"
		showBox="true"
		showAxes="true"
		showTicks="true"
		perspective="true"
		centerxpercent="0.5"
		centerypercent="0.5"
		rotationsteps="30"
		autospin="true"
		xygrid="false"
		yzgrid="false"
		xzgrid="false"
		gridsOnBox="true"
		gridPlanes="false"
		gridColor="rgb(128,128,128)"
	</window>
	<viewpoint>
		center="1.5,2,4,1"
		focus="0,0,0,1"
		up="0,2,0,1"
	</viewpoint>
</step>
        """.trimIndent())
    }

}