package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings

object Day11 : Day {
    override fun part1() = doPart1(toSimbiants(resourceStrings(2022, 11)))
    override fun part2() = doPart2(toSimbiants(resourceStrings(2022, 11)))

    fun doPart1(data: Map<Int, Simbiant>): Long {
        val simulator = SimbiantSimulator(data)
        simulator.round(20) { level, _ -> level / 3L }
        return simulator.monkeyBusiness()
    }

    fun doPart2(data: Map<Int, Simbiant>): Long {
        val simulator = SimbiantSimulator(data)
        simulator.round(10_000) { level, modulus -> level % modulus }
        return simulator.monkeyBusiness()
    }

    sealed class SimbOp {
        abstract fun inspect(old: Long): Long
    }

    data class MultSimbOp(val factor: Int) : SimbOp() {
        override fun inspect(old: Long): Long = old * factor
    }

    data class AddSimbOp(val factor: Int) : SimbOp() {
        override fun inspect(old: Long): Long = old + factor
    }

    object SqSimbOp : SimbOp() {
        override fun inspect(old: Long): Long = old * old
    }

    data class Simbiant(
        val levels: MutableList<Long>,
        val op: SimbOp,
        val div: Int,
        val t: Int,
        val f: Int,
        var inspectedItems: Long = 0L
    )

    fun toSimbiants(data: List<String>): Map<Int, Simbiant> {
        return data.foldIndexed(mutableMapOf()) { index, acc, s ->
            val simbiantData = s.split("\n")
            val levels = Regex("Starting items: (.*)")
                .find(simbiantData[1])!!.destructured
                .let { (ds) -> ds }.split(",")
                .map { it.trim().toLong() }
                .toMutableList()
            val op = Regex("Operation: new = old (.*) (.*)").find(simbiantData[2])!!.destructured.let { (f, v) ->
                when (v) {
                    "old" -> SqSimbOp
                    else -> if (f == "+") AddSimbOp(v.toInt()) else MultSimbOp(v.toInt())
                }
            }
            val div = Regex("Test: divisible by (\\d+)").find(simbiantData[3])!!.destructured.let { (f) -> f.toInt() }
            val t = Regex("monkey (\\d+)").find((simbiantData[4]))!!.destructured.let { (t) -> t.toInt() }
            val f = Regex("monkey (\\d+)").find((simbiantData[5]))!!.destructured.let { (t) -> t.toInt() }
            acc[index] = Simbiant(levels, op, div, t, f)
            acc
        }
    }

    data class SimbiantSimulator(val simbiants: Map<Int, Simbiant>) {
        private val modulus = simbiants.values.map { it.div }.reduce(Int::times)

        fun round(n: Int = 1, worry: (Long, Int) -> Long) {
            (1..n).forEach { _ ->
                simbiants.entries.sortedBy { it.key }.forEach { (_, s) ->
                    s.levels.forEach { l ->
                        val newLevel = worry(s.op.inspect(l), modulus)
                        val sTo = if (newLevel % s.div == 0L) s.t else s.f
                        simbiants[sTo]!!.levels += newLevel
                    }
                    s.inspectedItems += s.levels.size
                    s.levels.clear()
                }
            }
        }

        fun monkeyBusiness(): Long {
            return simbiants.entries
                .sortedByDescending { it.value.inspectedItems }
                .take(2)
                .map { it.value.inspectedItems }
                .reduce(Long::times)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}