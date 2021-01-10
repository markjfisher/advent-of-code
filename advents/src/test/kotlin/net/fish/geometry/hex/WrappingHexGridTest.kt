package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.assertj.core.api.Assertions.assertThat
import org.joml.Math.toRadians
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
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
    fun `4f matrix tests for rotation`() {
        val cameraLocation = Vector3f(0f, 0.382683432f, 0.923879533f)
        val worldCentre = Vector3f(0f, 0f, 0f)
        val distanceFromWorldCentre = worldCentre.sub(cameraLocation, Vector3f()).length()
        val view = Matrix4f().translation(0f, 0f, -distanceFromWorldCentre)
            .rotateX(toRadians(5f))
            .translate(-worldCentre.x, -worldCentre.y, -worldCentre.z)

    }

    fun vectorToRadians(v: Vector3f): Vector3f {
        return v.mul((PI / 180.0).toFloat(), Vector3f())
    }

}