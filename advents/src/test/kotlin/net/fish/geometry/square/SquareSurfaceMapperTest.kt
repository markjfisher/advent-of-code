package net.fish.geometry.square

import net.fish.geometry.knots.Knots
import net.fish.geometry.paths.PathCreator
import net.fish.geometry.paths.PathData
import net.fish.maths.Parametrics
import org.assertj.core.api.Assertions.assertThat
import org.joml.Vector3f
import org.junit.jupiter.api.Test
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal class SquareSurfaceMapperTest {
    private val wrapper = WrappingSquareGrid(8, 4)
    @Test
    fun `calculate centres`() {
        val sqrt2tp6 = (sqrt(2.0) * 0.6).toFloat()
        val sqrt2d2 = (sqrt(2.0) * 0.5).toFloat()
        val sqrt2tp4 = (sqrt(2.0) * 0.4).toFloat()

        val pathCreator = TestPathCreator()
        val mapper = SquareSurfaceMapper(grid = wrapper, pathCreator = pathCreator, 0.2f)

        val centres = mapper.calculateSquareCentres()

        val sq00centres = centres[Square(0, 0, wrapper)]!!
        val sq10centres = centres[Square(1, 0, wrapper)]!!
        val sq20centres = centres[Square(2, 0, wrapper)]!!
        val sq30centres = centres[Square(3, 0, wrapper)]!!
        val sq40centres = centres[Square(4, 0, wrapper)]!!
        val sq50centres = centres[Square(5, 0, wrapper)]!!
        val sq60centres = centres[Square(6, 0, wrapper)]!!
        val sq70centres = centres[Square(7, 0, wrapper)]!!

        assertThat(sq00centres.distance(Vector3f(1.2f, 0f, 0f))).isLessThan(0.0001f)
        assertThat(sq10centres.distance(Vector3f(sqrt2tp6, 0f, sqrt2tp6))).isLessThan(0.0001f)
        assertThat(sq20centres.distance(Vector3f(0f, 0f, 1.2f))).isLessThan(0.0001f)
        assertThat(sq30centres.distance(Vector3f(-sqrt2tp6, 0f, sqrt2tp6))).isLessThan(0.0001f)
        assertThat(sq40centres.distance(Vector3f(-1.2f, 0f, 0f))).isLessThan(0.0001f)
        assertThat(sq50centres.distance(Vector3f(-sqrt2tp6, 0f, -sqrt2tp6))).isLessThan(0.0001f)
        assertThat(sq60centres.distance(Vector3f(0f, 0f, -1.2f))).isLessThan(0.0001f)
        assertThat(sq70centres.distance(Vector3f(sqrt2tp6, 0f, -sqrt2tp6))).isLessThan(0.0001f)

        val sq01centres = centres[Square(0, 1, wrapper)]!!
        val sq11centres = centres[Square(1, 1, wrapper)]!!
        val sq21centres = centres[Square(2, 1, wrapper)]!!
        val sq31centres = centres[Square(3, 1, wrapper)]!!
        val sq41centres = centres[Square(4, 1, wrapper)]!!
        val sq51centres = centres[Square(5, 1, wrapper)]!!
        val sq61centres = centres[Square(6, 1, wrapper)]!!
        val sq71centres = centres[Square(7, 1, wrapper)]!!

        assertThat(sq01centres.distance(Vector3f(1.0f, 0.2f, 0f))).isLessThan(0.0001f)
        assertThat(sq11centres.distance(Vector3f(sqrt2d2, 0.2f, sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq21centres.distance(Vector3f(0f, 0.2f, 1.0f))).isLessThan(0.0001f)
        assertThat(sq31centres.distance(Vector3f(-sqrt2d2, 0.2f, sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq41centres.distance(Vector3f(-1.0f, 0.2f, 0f))).isLessThan(0.0001f)
        assertThat(sq51centres.distance(Vector3f(-sqrt2d2, 0.2f, -sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq61centres.distance(Vector3f(0f, 0.2f, -1.0f))).isLessThan(0.0001f)
        assertThat(sq71centres.distance(Vector3f(sqrt2d2, 0.2f, -sqrt2d2))).isLessThan(0.0001f)

        val sq02centres = centres[Square(0, 2, wrapper)]!!
        val sq12centres = centres[Square(1, 2, wrapper)]!!
        val sq22centres = centres[Square(2, 2, wrapper)]!!
        val sq32centres = centres[Square(3, 2, wrapper)]!!
        val sq42centres = centres[Square(4, 2, wrapper)]!!
        val sq52centres = centres[Square(5, 2, wrapper)]!!
        val sq62centres = centres[Square(6, 2, wrapper)]!!
        val sq72centres = centres[Square(7, 2, wrapper)]!!

        assertThat(sq02centres.distance(Vector3f(0.8f, 0f, 0f))).isLessThan(0.0001f)
        assertThat(sq12centres.distance(Vector3f(sqrt2tp4, 0f, sqrt2tp4))).isLessThan(0.0001f)
        assertThat(sq22centres.distance(Vector3f(0f, 0f, 0.8f))).isLessThan(0.0001f)
        assertThat(sq32centres.distance(Vector3f(-sqrt2tp4, 0f, sqrt2tp4))).isLessThan(0.0001f)
        assertThat(sq42centres.distance(Vector3f(-0.8f, 0f, 0f))).isLessThan(0.0001f)
        assertThat(sq52centres.distance(Vector3f(-sqrt2tp4, 0f, -sqrt2tp4))).isLessThan(0.0001f)
        assertThat(sq62centres.distance(Vector3f(0f, 0f, -0.8f))).isLessThan(0.0001f)
        assertThat(sq72centres.distance(Vector3f(sqrt2tp4, 0f, -sqrt2tp4))).isLessThan(0.0001f)

        val sq03centres = centres[Square(0, 3, wrapper)]!!
        val sq13centres = centres[Square(1, 3, wrapper)]!!
        val sq23centres = centres[Square(2, 3, wrapper)]!!
        val sq33centres = centres[Square(3, 3, wrapper)]!!
        val sq43centres = centres[Square(4, 3, wrapper)]!!
        val sq53centres = centres[Square(5, 3, wrapper)]!!
        val sq63centres = centres[Square(6, 3, wrapper)]!!
        val sq73centres = centres[Square(7, 3, wrapper)]!!

        assertThat(sq03centres.distance(Vector3f(1.0f, -0.2f, 0f))).isLessThan(0.0001f)
        assertThat(sq13centres.distance(Vector3f(sqrt2d2, -0.2f, sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq23centres.distance(Vector3f(0f, -0.2f, 1.0f))).isLessThan(0.0001f)
        assertThat(sq33centres.distance(Vector3f(-sqrt2d2, -0.2f, sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq43centres.distance(Vector3f(-1.0f, -0.2f, 0f))).isLessThan(0.0001f)
        assertThat(sq53centres.distance(Vector3f(-sqrt2d2, -0.2f, -sqrt2d2))).isLessThan(0.0001f)
        assertThat(sq63centres.distance(Vector3f(0f, -0.2f, -1.0f))).isLessThan(0.0001f)
        assertThat(sq73centres.distance(Vector3f(sqrt2d2, -0.2f, -sqrt2d2))).isLessThan(0.0001f)
    }

}

class TestPathCreator: PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        val fns = Parametrics(
            { x -> cos(x) },
            { x -> sin(x) },
            { 0.0 }
        )
        return Knots.createPath(segments, fns)
    }

}