package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2021.Day22.CutPlane.X
import net.fish.y2021.Day22.CutPlane.Y
import net.fish.y2021.Day22.CutPlane.Z
import org.joml.Vector3i

object Day22 : Day {
    private val lineExtractor = Regex("""(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""")
    private val lines: List<CuboidLine> by lazy { toCuboidLines(resourceLines(2021, 22)) }

    override fun part1() = doPart1(lines)
    override fun part2() = doPart2(lines)

    fun doPart1(cuboidLines: List<CuboidLine>): Long {
        val coercedLines = cuboidLines.mapNotNull { (isOn, cuboid) ->
            val lower = Vector3i(cuboid.lower.x.coerceAtLeast(-50), cuboid.lower.y.coerceAtLeast(-50), cuboid.lower.z.coerceAtLeast(-50))
            val upper = Vector3i(cuboid.upper.x.coerceAtMost(50), cuboid.upper.y.coerceAtMost(50), cuboid.upper.z.coerceAtMost(50))
            val coercedCuboid = Cuboid(lower, upper)
            if (coercedCuboid.valid()) CuboidLine(isOn, coercedCuboid) else null
        }
        return solve(coercedLines)
    }

    fun doPart2(cuboidLines: List<CuboidLine>): Long {
        return solve(cuboidLines)
    }

    fun solve(cuboidLines: List<CuboidLine>): Long {
        var cuboids = listOf<Cuboid>()
        cuboidLines.forEach { (isOn, cuboid) ->
            val cuboidsToAdd = mutableListOf<Cuboid>()

            // break every cuboid so far into multiple parts, made up of subtracting the new cuboid
            cuboids.forEach {
                cuboidsToAdd.addAll(it - cuboid)
            }
            // This is either adding to whole, or was used to subtract from everything else so far.
            if (isOn) cuboidsToAdd.add(cuboid)

            // set our cuboids to the newly broken up cuboids
            cuboids = cuboidsToAdd
        }

        // Now we have all the cuboid definitions, sum their volumes
        return cuboids.sumOf { it.volume() }
    }

    fun toCuboidLines(lines: List<String>): List<CuboidLine> {
        return lines.map { line ->
            lineExtractor.find(line)?.destructured!!.let { (onOff, x1, x2, y1, y2, z1, z2) ->
                val lower = Vector3i(x1.toInt(), y1.toInt(), z1.toInt())
                val upper = Vector3i(x2.toInt() + 1, y2.toInt() + 1, z2.toInt() + 1)
                CuboidLine(onOff == "on", Cuboid(lower, upper))
            }
        }
    }

    data class Cuboid(
        val lower: Vector3i,
        val upper: Vector3i
    ) {
        fun valid(): Boolean {
            return upper.x > lower.x && upper.y > lower.y && upper.z > lower.z
        }

        fun volume(): Long = (upper.x.toLong() - lower.x.toLong()) * (upper.y.toLong() - lower.y.toLong()) * (upper.z.toLong() - lower.z.toLong())

        fun intersect(other: Cuboid): Cuboid? {
            return Cuboid(
                Vector3i(maxOf(lower.x, other.lower.x), maxOf(lower.y, other.lower.y), maxOf(lower.z, other.lower.z)),
                Vector3i(minOf(upper.x, other.upper.x), minOf(upper.y, other.upper.y), minOf(upper.z, other.upper.z)),
            ).takeIf { it.valid() }
        }

        fun cut(plane: CutPlane, at: Int): Pair<Cuboid?, Cuboid?> {
            val cp = cuboidPlane(plane)
            return when {
                at >= cp.second -> Pair(this, null)
                at <= cp.first -> Pair(null, this)
                else -> {
                    when (plane) {
                        X -> Pair(
                            Cuboid(Vector3i(lower.x, lower.y, lower.z), Vector3i(at, upper.y, upper.z)),
                            Cuboid(Vector3i(at, lower.y, lower.z), Vector3i(upper.x, upper.y, upper.z))
                        )
                        Y -> Pair(
                            Cuboid(Vector3i(lower.x, lower.y, lower.z), Vector3i(upper.x, at, upper.z)),
                            Cuboid(Vector3i(lower.x, at, lower.z), Vector3i(upper.x, upper.y, upper.z))
                        )
                        Z -> Pair(
                            Cuboid(Vector3i(lower.x, lower.y, lower.z), Vector3i(upper.x, upper.y, at)),
                            Cuboid(Vector3i(lower.x, lower.y, at), Vector3i(upper.x, upper.y, upper.z))
                        )
                    }
                }
            }
        }

        operator fun minus(other: Cuboid): List<Cuboid> {
            if (this.intersect(other) == null) return listOf(this)

            val parts = mutableListOf<Cuboid>()
            var currentlyCutting = this

            // There are 6 cuts to perform at worst, at extremes of each plane.
            // To do this we can cut at lower then upper, but cutting at the upper gives us the cuboids opposite way around
            for (cutPlane in listOf(X, Y, Z)) {
                val planeBounds = other.cuboidPlane(cutPlane)
                val (a, b) = currentlyCutting.cut(cutPlane, planeBounds.first)
                if (a != null) parts.add(a)
                if (b == null) return parts

                currentlyCutting = b
                val (c, d) = currentlyCutting.cut(cutPlane, planeBounds.second)
                if (d != null) parts.add(d)
                if (c == null) return parts

                currentlyCutting = c
            }

            return parts
        }

        // Return the lower/upper values of the cuboid's requested plane for cutting other cuboids by
        fun cuboidPlane(plane: CutPlane): Pair<Int, Int> {
            return when (plane) {
                X -> Pair(lower.x, upper.x)
                Y -> Pair(lower.y, upper.y)
                Z -> Pair(lower.z, upper.z)
            }
        }

        override fun toString(): String {
            return String.format("Cuboid(%d, %d, %d - %d, %d, %d)", lower.x, lower.y, lower.z, upper.x, upper.y, upper.z)
        }
    }

    enum class CutPlane {
        X, Y, Z;
    }

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