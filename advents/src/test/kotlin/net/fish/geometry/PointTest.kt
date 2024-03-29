package net.fish.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

internal class PointTest {
    @Test
    fun `when comparing points y is considered first then x`() {
        val p1 = Point(0, 1)
        val p2 = Point(1, 0)
        val p3 = Point(1, 1)
        assertThat(p1.compareTo(p2)).isEqualTo(1)
        assertThat(p2.compareTo(p1)).isEqualTo(-1)
        assertThat(p1.compareTo(p3)).isEqualTo(-1)
        assertThat(p3.compareTo(p1)).isEqualTo(1)
        assertThat(p2.compareTo(p3)).isEqualTo(-1)
        assertThat(p3.compareTo(p2)).isEqualTo(1)
        assertThat(p1.compareTo(p1)).isEqualTo(0)
        assertThat(p2.compareTo(p2)).isEqualTo(0)
        assertThat(p3.compareTo(p3)).isEqualTo(0)
    }

    @Test
    fun `adding points`() {
        assertThat(Point(0, 1) + Point(2, 3)).isEqualTo(Point(2, 4))
        assertThat(Point(-1, 1) + Point(1, -1)).isEqualTo(Point(0, 0))
    }

    @Test
    fun `adding to a point`() {
        assertThat(Point(5, 5).plus(-2, -2)).isEqualTo(Point(3,3))
    }

    @Test
    fun `adding pair to point`() {
        assertThat(Point(2, 3).plus(Pair(1, 2))).isEqualTo(Point(3, 5))
    }

    @Test
    fun `angles between points are same coordinates as maths circle`() {
        assertThat(Point(0, 0).angle(Point(0, 1))).isEqualTo(Math.PI / 2.0)
        assertThat(Point(0, 0).angle(Point(-1, 0))).isEqualTo(Math.PI)
        assertThat(Point(0, 0).angle(Point(-1, 1))).isEqualTo(3.0 * Math.PI / 4.0)
        assertThat(Point(0, 0).angle(Point(0, -1))).isEqualTo(-Math.PI / 2.0)

        assertThat(Point(0, 0).angle(Point(1, 1))).isEqualTo(Math.PI / 4.0)
        assertThat(Point(0, 0).angle(Point(1, -1))).isEqualTo(-Math.PI / 4.0)

        assertThat(Point(0, 0).angle(Point(-1, 1))).isEqualTo(3.0 * Math.PI / 4.0)
        assertThat(Point(0, 0).angle(Point(-1, -1))).isEqualTo(-3.0 * Math.PI / 4.0)
    }

    @Test
    fun `distance between points`() {
        assertThat(Point(0, 0).distance(Point(0, 0))).isEqualTo(0.0)
        assertThat(Point(1, 1).distance(Point(1, 1))).isEqualTo(0.0)
        assertThat(Point(-1, -1).distance(Point(-1, -1))).isEqualTo(0.0)

        assertThat(Point(0, 0).distance(Point(1, 0))).isEqualTo(1.0)
        assertThat(Point(0, 0).distance(Point(0, 1))).isEqualTo(1.0)
        assertThat(Point(0, 1).distance(Point(0, 0))).isEqualTo(1.0)
        assertThat(Point(1, 0).distance(Point(0, 0))).isEqualTo(1.0)

        assertThat(Point(-1, -1).distance(Point(1, 1))).isEqualTo(2.0 * sqrt(2.0))
        assertThat(Point(1, 1).distance(Point(-1, -1))).isEqualTo(2.0 * sqrt(2.0))
    }

    @Test
    fun `manhatten distance between points`() {
        assertThat(Point(0, 0).manhattenDistance(Point(0, 0))).isEqualTo(0)
        assertThat(Point(1, 1).manhattenDistance(Point(1, 1))).isEqualTo(0)

        assertThat(Point(0, 0).manhattenDistance(Point(5, 3))).isEqualTo(8)
        assertThat(Point(0, 0).manhattenDistance(Point(3, 5))).isEqualTo(8)
        assertThat(Point(5, 3).manhattenDistance(Point(0, 0))).isEqualTo(8)
        assertThat(Point(3, 5).manhattenDistance(Point(0, 0))).isEqualTo(8)

        assertThat(Point(1, 1).manhattenDistance(Point(-1, -1))).isEqualTo(4)
    }

    @Test
    fun `edge points of boundry`() {
        val boundary = listOf(Point(1, 1), Point(4,3)).bounds()
        val edgePoints = boundary.edgePoints().toList()
        assertThat(edgePoints).containsExactly(
            Point(1,1),
            Point(2,1),
            Point(3,1),
            Point(4,1),
            Point(4,2),
            Point(4,3),
            Point(3,3),
            Point(2,3),
            Point(1,3),
            Point(1,2),
        )
    }

    @Test
    fun `sequence of all points in boundary`() {
        val boundary = listOf(Point(1, 1), Point(2,3)).bounds()
        val allPoints = boundary.points()
        assertThat(allPoints.toList()).containsExactly(
            Point(1,1),
            Point(2,1),
            Point(1,2),
            Point(2,2),
            Point(1,3),
            Point(2,3),
        )
    }

    @Test
    fun `area of boundary`() {
        val area = listOf(Point(1, 1), Point(5, 4)).bounds().area()
        assertThat(area).isEqualTo(12)
    }

    @Test
    fun `can find rows`() {
        val rows = listOf(Point(1, 1), Point(3, 2)).bounds().rows().toList()
        assertThat(rows).containsExactly(
            listOf(Point(x=1, y=1), Point(x=2, y=1), Point(x=3, y=1)),
            listOf(Point(x=1, y=2), Point(x=2, y=2), Point(x=3, y=2))
        )
    }

    @Test
    fun `can find columns`() {
        val columns = listOf(Point(1, 1), Point(3, 2)).bounds().columns().toList()
        assertThat(columns).containsExactly(
            listOf(Point(x=1, y=1), Point(x=1, y=2)),
            listOf(Point(x=2, y=1), Point(x=2, y=2)),
            listOf(Point(x=3, y=1), Point(x=3, y=2))
        )
    }
}