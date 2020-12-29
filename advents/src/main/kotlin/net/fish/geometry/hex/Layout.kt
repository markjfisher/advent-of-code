package net.fish.geometry.hex

import kotlin.math.cos
import kotlin.math.sin

data class Point2D(
    val x: Double,
    val y: Double
)

data class Layout(
    val orientation: Orientation,
    val size: Point2D,
    val origin: Point2D
) {
    fun hexToPixel(h: Hex): Point2D {
        val (f0, f1, f2, f3) = orientation
        val x = (f0 * h.q + f1 * h.r) * size.x
        val y = (f2 * h.q + f3 * h.r) * size.y
        return Point2D(x + origin.x, y + origin.y)
    }

    fun pixelToHex(p: Point2D): FractionalHex {
        val (x, y) = Point2D((p.x - origin.x) / size.x, (p.y - origin.y) / size.y)
        val q = orientation.b0 * x + orientation.b1 * y
        val r = orientation.b2 * x + orientation.b3 * y
        return FractionalHex(q, r, -q - r)
    }

    fun hexCornerOffset(corner: Int): Point2D {
        val angle = 2.0 * Math.PI * (orientation.start_angle - corner) / 6.0
        return Point2D(size.x * cos(angle), size.y * sin(angle))
    }

    fun polygonCorners(h: Hex): List<Point2D> {
        val corners = mutableListOf<Point2D>()
        val (x, y) = hexToPixel(h)
        for (i in 0..5) {
            val (x1, y1) = hexCornerOffset(i)
            corners.add(Point2D(x + x1, y + y1))
        }
        return corners
    }

}