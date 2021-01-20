package net.fish.geometry.hex

import net.fish.geometry.Point3D
import net.fish.maths.normalFromPoints
import org.assertj.core.api.Assertions.assertThat
import org.joml.Math
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

internal class TorusMappedWrappingHexGridTest {
    private val pointyLayout = Layout(Orientation.ORIENTATION.POINTY)
    private val flatLayout = Layout(Orientation.ORIENTATION.FLAT)
    private val pointyGrid = WrappingHexGrid(m = 8, n = 4, layout = pointyLayout)
    private val flatGrid = WrappingHexGrid(m = 8, n = 4, layout = flatLayout)

    private val pointyTorus = TorusMappedWrappingHexGrid(pointyGrid, 1.0, 2.0)
    private val flatTorus = TorusMappedWrappingHexGrid(flatGrid, 1.0, 2.0)

    @Test
    fun `torus coordinates are 7 points with centre as last coordinate`() {
        val hex1 = pointyGrid.hex(0, 0, 0)
        val c1 = pointyTorus.toroidCoordinates(hex1)
        assertThat(c1).hasSize(7)
        println("c1 -----------------------------")
        c1.forEach { p ->
            println(String.format("%.6f, %6f, %6f", p.x, p.y, p.z))
        }
        // The centre point of 0,0,0 is at [-(r2+r1), 0, 0]
        assertThat((c1[6] - Point3D(0.0, 0.0, -3.0)).length()).isLessThan(0.001)

        val hex2 = pointyGrid.hex(1, -2, 1)
        val c2 = pointyTorus.toroidCoordinates(hex2)
        println("c2 -----------------------------")
        c2.forEach { p ->
            println(String.format("%.6f, %6f, %6f", p.x, p.y, p.z))
        }

        // The opposite of centre point is at [-(r2-r1), 0, 0]
        assertThat((c2[6] - Point3D(0.0, 0.0, -1.0)).length()).isLessThan(0.001)
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
        val c1 = pointyTorus.toroidCoordinates(hex1)
        val c2 = pointyTorus.toroidCoordinates(hex2)
        val c3 = pointyTorus.toroidCoordinates(hex3)
        val c4 = pointyTorus.toroidCoordinates(hex4)

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
        val c1 = flatTorus.toroidCoordinates(hex1)
        val c2 = flatTorus.toroidCoordinates(hex2)
        val c3 = flatTorus.toroidCoordinates(hex3)
        val c4 = flatTorus.toroidCoordinates(hex4)

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
    fun `getting pointy hex centres`() {
        val grid = WrappingHexGrid(m = 2, n = 2, layout = pointyLayout)
        val torus = TorusMappedWrappingHexGrid(grid, 1.0, 2.0)
        val centres = torus.centres()
        // freaky shape!
        assertThat((centres[0] - Point3D(0.0, 0.0, -3.0)).length()).isLessThan(0.0001)
        assertThat((centres[1] - Point3D(0.0, 0.0, 3.0)).length()).isLessThan(0.0001)
        assertThat((centres[2] - Point3D(1.0, 0.0, 0.0)).length()).isLessThan(0.0001)
        assertThat((centres[3] - Point3D(-1.0, 0.0, 0.0)).length()).isLessThan(0.0001)
    }

    @Test
    fun `getting flat hex centres`() {
        val grid = WrappingHexGrid(m = 2, n = 2, layout = flatLayout)
        val torus = TorusMappedWrappingHexGrid(grid, 1.0, 2.0)
        val centres = torus.centres()
        // freaky shape!
        assertThat((centres[0] - Point3D(0.0, 0.0, -3.0)).length()).isLessThan(0.0001)
        assertThat((centres[1] - Point3D(0.0, 1.0, 2.0)).length()).isLessThan(0.0001)
        assertThat((centres[2] - Point3D(0.0, 0.0, -1.0)).length()).isLessThan(0.0001)
        assertThat((centres[3] - Point3D(0.0, -1.0, 2.0)).length()).isLessThan(0.0001)
    }

    @Test
    fun `can calculate normal from 3 points in blender output order`() {
        // From a hexagon with lifted points these are the 6 points output:
        val sqrt3div2 = sqrt(3f) / 2f
        val p1 = Vector3f(0f, 0.1f, -1f)
        val p2 = Vector3f(-sqrt3div2, 0f, -0.5f)
        val p3 = Vector3f(-sqrt3div2, 0f, 0.5f)
        val p4 = Vector3f(0f, 0.1f, 1f)
        val p5 = Vector3f(sqrt3div2, 0f, 0.5f)
        val p6 = Vector3f(sqrt3div2, 0f, -0.5f)

        // Now verify the given faces with points in given order generate the normals as output also by blender
        assertHasNormal(Vector3f(0.0574f, 0.9934f, 0.0993f), normalFromPoints(p6, p1, p3))
        assertHasNormal(Vector3f(-0.1147f, 0.9934f, 0.0000f), normalFromPoints(p1, p2, p3))
        assertHasNormal(Vector3f(0f, 0.9806f, -0.1961f), normalFromPoints(p3, p4, p5))
        assertHasNormal(Vector3f(0f, 1f, 0f), normalFromPoints(p5, p6, p3))
    }

    @Test
    fun `can create obj from hex`() {
        val grid = WrappingHexGrid(m = 12, n = 6, layout = pointyLayout)
        val torus = TorusMappedWrappingHexGrid(grid, 1.0, 5.0)

        grid.hexes().forEachIndexed { i, hex ->
            val obj = torus.toroidalHexObj(hex)
            File(String.format("/home/markf/Documents/blender/h%04d.obj", i)).writeText(obj.joinToString("\n"))
        }

    }

    private fun assertHasNormal(expectedNormal: Vector3f, normal: Vector3f) {
        assertThat(abs(expectedNormal.sub(normal).length())).isLessThan(0.0001f)
    }

    fun roundClose(v: Float): Float {
        return when {
            abs(v) < 0.0001 -> 0f
            abs(v - 0.5f) < 0.0001 -> 0.5f
            abs(v + 0.5f) < 0.0001 -> -0.5f
            abs(v - 1f) < 0.0001 -> 1f
            abs(v + 1f) < 0.0001 -> -1f
            else -> v
        }
    }

    @Test
    fun `getting axes of flat hexes`() {
        val grid = WrappingHexGrid(m = 4, n = 4, layout = flatLayout)
        val torus = TorusMappedWrappingHexGrid(grid, 1.0, 2.0)
        val axes = torus.hexAxes()

/*
        grid.hexes().forEachIndexed { i, hex ->
            val vx = roundClose(axes[i].location.x)
            val vy = roundClose(axes[i].location.y)
            val vz = roundClose(axes[i].location.z)
            val m = axes[i].axes
            val m00 = roundClose(m.m00)
            val m01 = roundClose(m.m01)
            val m02 = roundClose(m.m02)
            val m10 = roundClose(m.m10)
            val m11 = roundClose(m.m11)
            val m12 = roundClose(m.m12)
            val m20 = roundClose(m.m20)
            val m21 = roundClose(m.m21)
            val m22 = roundClose(m.m22)
            println("""
                checkHexAxes(axes[$i], Vector3f(${vx}f, ${vy}f, ${vz}f), Matrix3f(${m00}f, ${m01}f, ${m02}f, ${m10}f, ${m11}f, ${m12}f, ${m20}f, ${m21}f, ${m22}f))
            """.trimIndent())
        }
*/

        checkHexAxes(axes[0], Vector3f(0.0f, 0.0f, -3.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f))
        checkHexAxes(axes[1], Vector3f(2.7071068f, 0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, -0.6546537f, 0.755929f, 0.0f, -0.755929f, -0.6546537f, 0.0f))
        checkHexAxes(axes[2], Vector3f(0.0f, 0.0f, 3.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f))
        checkHexAxes(axes[3], Vector3f(-2.7071068f, 0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, 0.6546537f, 0.755929f, 0.0f, 0.755929f, -0.6546537f, 0.0f))

        checkHexAxes(axes[4], Vector3f(0.0f, 1.0f, -2.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f))
        checkHexAxes(axes[5], Vector3f(1.2928932f, 0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, -0.6546537f, -0.755929f, 0.0f, 0.755929f, -0.6546537f, 0.0f))
        checkHexAxes(axes[6], Vector3f(0.0f, 1.0f, 2.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f))
        checkHexAxes(axes[7], Vector3f(-1.2928932f, 0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, 0.6546537f, -0.755929f, 0.0f, -0.755929f, -0.6546537f, 0.0f))

        checkHexAxes(axes[8], Vector3f(0.0f, 0.0f, -1.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f))
        checkHexAxes(axes[9], Vector3f(1.2928932f, -0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, 0.6546537f, -0.755929f, 0.0f, 0.755929f, 0.6546537f, 0.0f))
        checkHexAxes(axes[10], Vector3f(0.0f, 0.0f, 1.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f))
        checkHexAxes(axes[11], Vector3f(-1.2928932f, -0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, -0.6546537f, -0.755929f, 0.0f, -0.755929f, 0.6546537f, 0.0f))

        checkHexAxes(axes[12], Vector3f(0.0f, -1.0f, -2.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f))
        checkHexAxes(axes[13], Vector3f(2.7071068f, -0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, 0.6546537f, 0.755929f, 0.0f, -0.755929f, 0.6546537f, 0.0f))
        checkHexAxes(axes[14], Vector3f(0.0f, -1.0f, 2.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f))
        checkHexAxes(axes[15], Vector3f(-2.7071068f, -0.70710677f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, -0.6546537f, 0.755929f, 0.0f, 0.755929f, 0.6546537f, 0.0f))

    }

    @Test
    fun `getting axes of pointy hexes`() {
        val grid = WrappingHexGrid(m = 4, n = 4, layout = pointyLayout)
        val torus = TorusMappedWrappingHexGrid(grid, 1.0, 2.0)
        val axes = torus.hexAxes()

        checkHexAxes(axes[0], Vector3f(0.0f, 0.0f, -3.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f))
        checkHexAxes(axes[1], Vector3f(3.0f, 0.0f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f))
        checkHexAxes(axes[2], Vector3f(0.0f, 0.0f, 3.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f))
        checkHexAxes(axes[3], Vector3f(-3.0f, 0.0f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f))

        checkHexAxes(axes[4], Vector3f(1.4142135f, 1.0f, -1.4142135f), Matrix3f(0.70710677f, 0.0f, 0.70710677f, -0.70710677f, 0.0f, 0.70710677f, 0.0f, -1.0f, 0.0f))
        checkHexAxes(axes[5], Vector3f(1.4142135f, 1.0f, 1.4142135f), Matrix3f(-0.70710677f, 0.0f, 0.70710677f, -0.70710677f, 0.0f, -0.70710677f, 0.0f, -1.0f, 0.0f))
        checkHexAxes(axes[6], Vector3f(-1.4142135f, 1.0f, 1.4142135f), Matrix3f(-0.70710677f, 0.0f, -0.70710677f, 0.70710677f, 0.0f, -0.70710677f, 0.0f, -1.0f, 0.0f))
        checkHexAxes(axes[7], Vector3f(-1.4142135f, 1.0f, -1.4142135f), Matrix3f(0.70710677f, 0.0f, -0.70710677f, 0.70710677f, 0.0f, 0.70710677f, 0.0f, -1.0f, 0.0f))

        checkHexAxes(axes[8], Vector3f(0.0f, 0.0f, -1.0f), Matrix3f(1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f))
        checkHexAxes(axes[9], Vector3f(1.0f, 0.0f, 0.0f), Matrix3f(0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f))
        checkHexAxes(axes[10], Vector3f(0.0f, 0.0f, 1.0f), Matrix3f(-1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f))
        checkHexAxes(axes[11], Vector3f(-1.0f, 0.0f, 0.0f), Matrix3f(0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f))

        checkHexAxes(axes[12], Vector3f(1.4142135f, -1.0f, -1.4142135f), Matrix3f(0.70710677f, 0.0f, 0.70710677f, 0.70710677f, 0.0f, -0.70710677f, 0.0f, 1.0f, 0.0f))
        checkHexAxes(axes[13], Vector3f(1.4142135f, -1.0f, 1.4142135f), Matrix3f(-0.70710677f, 0.0f, 0.70710677f, 0.70710677f, 0.0f, 0.70710677f, 0.0f, 1.0f, 0.0f))
        checkHexAxes(axes[14], Vector3f(-1.4142135f, -1.0f, 1.4142135f), Matrix3f(-0.70710677f, 0.0f, -0.70710677f, -0.70710677f, 0.0f, 0.70710677f, 0.0f, 1.0f, 0.0f))
        checkHexAxes(axes[15], Vector3f(-1.4142135f, -1.0f, -1.4142135f), Matrix3f(0.70710677f, 0.0f, -0.70710677f, -0.70710677f, 0.0f, -0.70710677f, 0.0f, 1.0f, 0.0f))

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
    fun `quaternion tests vs matrix - setting full direction vector works but multiplication is not as expected`() {
        val q = Quaternionf().setFromNormalized(Matrix3f().identity())

        // Check matrix rotation and quaternion rotation do the same thing to a point
        val r45x = Vector3f(Math.toRadians(45f), 0f, 0f)
        val a45x = Matrix3f().identity().rotateXYZ(r45x)
        val r15x = Vector3f(Math.toRadians(15f), 0f, 0f)
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
        val r45xz = Vector3f(Math.toRadians(45f), 0f, Math.toRadians(45f))
        val a45xz = Matrix3f().identity().rotateXYZ(r45xz)

        // We can get a quaternion from direction vector - THIS WORKS BECAUSE WE SPECIFY THE EXACT VECTOR TO ROTATE AROUND INITIALLY!
        val sqrt2div2 = sqrt(2f) / 2f
        // create a unit vector to rotate around, this will be either the (local) "X" for pitch, "Y" for yaw, "Z" for roll
        q.setAngleAxis(Math.toRadians(45f), sqrt2div2, 0f, sqrt2div2)
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
        p1.rotateAxis(Math.toRadians(45f), sqrt2div2, 0f, sqrt2div2, c1)
        c1.rotateAxis(Math.toRadians(45f), sqrt2div2, 0f, sqrt2div2, c2)
        println("p1 -> $c1 -> $c2")

        // can we combine 2 quaternion rotations to get same result - using multiply? doesn't work
        println("\nTrying to build quat up from 2 rotations using MULTIPLY (p1: $p1)")
        val qx = Quaternionf().setAngleAxis(Math.toRadians(45f), 1f, 0f, 0f)
        val qy = Quaternionf().setAngleAxis(Math.toRadians(45f), 0f, 1f, 0f)
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
        q.setAngleAxis(Math.toRadians(5f), 1f, 0f, 0f)
        val q2 = Quaternionf().setAngleAxis(Math.toRadians(-5f), 0f, 1f, 0f)
        q.mul(q2)

        q.setAngleAxis(Math.toRadians(5f), sqrt2div2, 0f, sqrt2div2)
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
        q.setAngleAxis(Math.toRadians(45f), sqrt2div2, 0f, sqrt2div2) // 45 degree around XZ plane
        val m1 = q.get(Matrix3f())
        println("\n45 deg XZ matrix from q:\n$m1")
        p1.mul(m1, c1)
        c1.mul(m1, c2)
        println("p1 -> $c1 -> $c2")

        // ALSO WORKS - just X rotation
        q.setAngleAxis(Math.toRadians(45f), 1f, 0f, 0f) // 45 degree around XZ plane
        q.get(m1)
        println("\n45 deg X matrix from q:\n$m1")
        p1.mul(m1, c1)
        c1.mul(m1, c2)
        println("p1 -> $c1 -> $c2")

        // Convert quaternion to Simple Angles
        q.getEulerAnglesXYZ(c1)

    }

    @Test
    fun `camera rotation testing`() {
        val worldCentre = Vector3f(0f, 0.1f, 0f)
        val cameraPosition = Vector3f(1.2f, 0.2f, 1.2f)
        val rotationFromLookAt = Quaternionf().lookAlong(worldCentre.sub(cameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize().conjugate()
        val cameraRotation = Quaternionf(rotationFromLookAt)

        val camera = Camera(rotation = cameraRotation, position = cameraPosition)

        val projectBack = cameraPosition.sub(cameraPosition.normalize().mul(cameraPosition.sub(worldCentre, Vector3f()).length(), Vector3f()), Vector3f()).sub(worldCentre).length()
        println("<!-- initial check: $projectBack -->")

        plotHeader(1)

        println("<step 1>")
        for(x in (0 .. 355/5)) {
            val rotAngles = Vector3f(Math.toRadians(5f), Math.toRadians(0f), 0f)

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