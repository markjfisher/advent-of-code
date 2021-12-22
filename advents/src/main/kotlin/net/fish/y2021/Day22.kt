package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines
import org.joml.Vector3i

object Day22 : Day {
    val lineExtractor = Regex("""(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""")
    private val lines: List<CuboidLine> by lazy { toCuboidLines(resourceLines(2021, 22)) }

    override fun part1() = doPart1(lines)
    override fun part2() = doPart2(lines)

    fun doPart1(cuboidLines: List<CuboidLine>): Int {
        val points = applyCuboidLinesInArea(cuboidLines, 50)
        return points.size
    }

    fun doPart2(data: List<CuboidLine>): Int = 0

    fun toCuboidLines(lines: List<String>): List<CuboidLine> {
        return lines.map { line ->
            lineExtractor.find(line)?.destructured!!.let { (onOff, x1, x2, y1, y2, z1, z2) ->
                val lower = Vector3i(x1.toInt(), y1.toInt(), z1.toInt())
                val upper = Vector3i(x2.toInt(), y2.toInt(), z2.toInt())
                CuboidLine(onOff == "on", Cuboid(lower, upper))
            }
        }
    }

    fun applyCuboidLinesInArea(lines: List<CuboidLine>, distance: Int): Set<Vector3i> {
        // for each line, work out what intersects with our distance
        val distanceBoundedCuboidLines = lines.mapNotNull { l ->
            // is this anywhere within our bounds

            when {
                l.cuboid.upper.x < -distance || l.cuboid.lower.x > distance -> null
                l.cuboid.upper.y < -distance || l.cuboid.lower.y > distance -> null
                l.cuboid.upper.z < -distance || l.cuboid.lower.z > distance -> null
                else -> CuboidLine(
                    l.isOn,
                    Cuboid(
                        Vector3i(maxOf(l.cuboid.lower.x, -distance), maxOf(l.cuboid.lower.y, -distance), maxOf(l.cuboid.lower.z, -distance)),
                        Vector3i(minOf(l.cuboid.upper.x, distance), minOf(l.cuboid.upper.y, distance), minOf(l.cuboid.upper.z, distance))
                    )
                )
            }
        }
        // Now take all these new instructions and convert to real cuboids
        return process(distanceBoundedCuboidLines)
    }

    fun process(cuboidLines: List<CuboidLine>): Set<Vector3i> {
        val points = mutableSetOf<Vector3i>()
        cuboidLines.forEach { line ->
            for (x in line.cuboid.lower.x..line.cuboid.upper.x) {
                for (y in line.cuboid.lower.y..line.cuboid.upper.y) {
                    for (z in line.cuboid.lower.z..line.cuboid.upper.z) {
                        val point = Vector3i(x, y, z)
                        if (line.isOn) points.add(point) else points.remove(point)
                    }
                }
            }
        }
        return points
    }

    data class Cuboid(
        val lower: Vector3i,
        val upper: Vector3i
    )

    data class CuboidLine(
        val isOn: Boolean,
        val cuboid: Cuboid
    )

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}