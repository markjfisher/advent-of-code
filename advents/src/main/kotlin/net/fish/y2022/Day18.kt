package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import org.joml.Vector3i

object Day18 : Day {
    private val data by lazy { resourceLines(2022, 18) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Pair<Int, Int> = solve(data)
    fun doPart2(data: List<String>): Int = 0

    fun solve(data: List<String>): Pair<Int, Int> {
        val sides = setOf(
            Vector3i(0, 0, 1),
            Vector3i(0, 0, -1),
            Vector3i(0, 1, 0),
            Vector3i(0, -1, 0),
            Vector3i(1, 0, 0),
            Vector3i(-1, 0, 0)
        )
        val inputCubes = data.fold(setOf<Vector3i>()) { vs, coords ->
            val v = coords.split(",", limit = 3).map { it.toInt() }.toTypedArray()
            vs + Vector3i(v[0], v[1], v[2])
        }
        val combined = inputCubes.fold(listOf<Vector3i>()) { combined, v ->
            combined + sides.map { val r = Vector3i(); v.add(it, r); r }
        }
        val r1 = (combined - inputCubes).size

        val xs = inputCubes.map { it.x }
        val ys = inputCubes.map { it.y }
        val zs = inputCubes.map { it.z }

        val lower = Vector3i(xs.min() - 1, ys.min() - 1, zs.min() - 1)
        val upper = Vector3i(xs.max() + 1, ys.max() + 1, zs.max() + 1)

        val seen = mutableSetOf<Vector3i>()
        val processStack = ArrayDeque<Vector3i>()
        processStack.add(lower)
        while (processStack.isNotEmpty()) {
            val cube = processStack.removeLast()
            if (!seen.contains(cube) && !inputCubes.contains(cube) && within(cube, lower, upper)) {
                seen += cube;
                sides.forEach { side ->
                    val r = Vector3i()
                    cube.add(side, r)
                    processStack.addLast(r)
                }
            }
        }
        val r2 = combined.fold(0) { ac, c -> if (seen.contains(c)) ac + 1 else ac }

        return Pair(r1, r2)
    }

    private fun within(p: Vector3i, lb: Vector3i, ub: Vector3i): Boolean {
        return (lb.x <= p.x && p.x <= ub.x) && (lb.y <= p.y && p.y <= ub.y) && (lb.z <= p.z && p.z <= ub.z)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}