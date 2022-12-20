package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.maths.Parametrics2D
import net.fish.resourceString
import kotlin.math.abs
import kotlin.math.floor

object Day17 : Day {
    private val targetExtractor by lazy { Regex("""target area: x=(\d+)\.\.(\d+), y=(-\d+)\.\.(-\d+)""") }
    private val target: Target by lazy { toTarget(resourceString(2021, 17)) }

    fun toTarget(data: String): Target {
        return targetExtractor.find(data)?.destructured!!.let { (x1, x2, y1, y2) -> Target(x1.toInt(), x2.toInt(), y1.toInt(), y2.toInt()) }
    }

    override fun part1() = doPart1(target)
    override fun part2() = doPart2(target)

    fun doPart1(data: Target): Int {
        // for a given t, we can find the min/max values of vy (initial y velocity) where it will be inside the box
        // because y(t) = t/2 ( 2 * vy + 1 - t)
        // and we can always construct a vx that just reaches the box, so it plays no part in this calculation
        // so rearranging:
        // vy >= Ymin / t + (t - 1)/2
        // vy <= Ymax / t + (t - 1)/2
        // so loop through t, finding all the vy that are valid ranges for that t, and then find the highest.
        // When do we stop t though?
        // it turns out it's from t = 2*|yMin| to 2*|yMax| - TODO: WHY!!?? spreadsheet proof isn't good enough!
        // Actually, it turns out there's a local max at t = 2*abs(minY)

        val t = 2 * abs(data.minY)
        val maxVyAtT = data.maxY / t.toDouble() + (t - 1) / 2.0
        val maxVy = floor(maxVyAtT).toInt()

        // the max value of y for a vy = vy * (vy + 1) / 2
        return maxVy * (maxVy + 1) / 2

    }

    private fun createParametrics(vx: Int, vy: Int): Parametrics2D {
        return Parametrics2D(
            fx = { t ->
                val vxp1 = vx + 1
                if (t >= vxp1) 0.5 * vx * vxp1 else 0.5 * t * (2 * vx + 1 - t)
            },
            fy = { t -> 0.5 * t * (2 * vy + 1 - t) }
        )
    }

    fun doPart2(data: Target): Int {
        // vy range yMin to abs(yMin)
        // vx range 1 .. xMax
        val bounds = listOf(Point(data.minX, data.minY), Point(data.maxX, data.maxY)).bounds()

        val hitTarget = mutableSetOf<Point>()
        for (vy in data.minY..abs(data.minY)) {
            for (vx in 1..data.maxX) {
                val params = createParametrics(vx, vy)
                var t = 0
                var hasHit = false
                var hasGonePast = false
                while (!hasHit && !hasGonePast) {
                    val xp = params.fx.eval(t.toDouble())
                    val yp = params.fy.eval(t.toDouble())
                    val p = Point(xp.toInt(), yp.toInt())
                    hasHit = p.within(bounds)
                    hasGonePast = xp > data.maxX || yp < data.minY
                    t++
                }
                if (hasHit) hitTarget += Point(vx, vy)
            }
        }

        return hitTarget.count()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Target(
        val minX: Int,
        val maxX: Int,
        val minY: Int,
        val maxY: Int,
    )
}