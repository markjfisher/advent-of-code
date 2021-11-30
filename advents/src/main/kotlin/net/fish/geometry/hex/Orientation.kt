package net.fish.geometry.hex

import java.lang.Exception
import kotlin.math.sqrt

data class Orientation(
    val f0: Double,
    val f1: Double,
    val f2: Double,
    val f3: Double,
    val b0: Double,
    val b1: Double,
    val b2: Double,
    val b3: Double,
    val start_angle: Double
) {
    // Difference to site. I have made this part of the Orientation class, so they can be used in HexGrid
    companion object {
        var pointy = Orientation(sqrt(3.0), sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0, sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5)
        var flat = Orientation(3.0 / 2.0, 0.0, sqrt(3.0) / 2.0, sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, sqrt(3.0) / 3.0, 0.0)
    }

    enum class ORIENTATION(val o: Orientation) {
        FLAT(flat), POINTY(pointy);
        companion object {
            fun from(o: String): ORIENTATION? {
                return values().firstOrNull { it.name.equals(o, ignoreCase = true) }
            }
        }
    }
}