package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings

object Day11 : Day {
    private val data by lazy { toSimbiants(resourceStrings(2022, 11)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: Map<Int,Simbiant>): Long {
        val simulator = SimbiantSimulator(data)
        simulator.round(20)
        return simulator.monkeyBusiness()
    }
    fun doPart2(data: Map<Int,Simbiant>): Long = 0

    sealed class SimbOp {
        abstract fun inspect(old: Long): Long
    }
    data class MultSimbOp(val factor: Int): SimbOp() {
        override fun inspect(old: Long): Long {
            return old * factor
        }
    }

    data class AddSimbOp(val factor: Int): SimbOp() {
        override fun inspect(old: Long): Long {
            return old + factor
        }
    }

    object SqSimbOp: SimbOp() {
        override fun inspect(old: Long): Long {
            return old * old
        }
    }

    data class Simbiant(val levels: MutableList<Long>, val op: SimbOp, val div: Int, val t: Int, val f: Int, var inspectedItems: Long = 0L)

    fun toSimbiants(data: List<String>) : Map<Int,Simbiant>{
        return data.foldIndexed(mutableMapOf()) { index, acc, s ->
            val simbiantData = s.split("\n")
            val levels = Regex("Starting items: (.*)").find(simbiantData[1])!!.destructured.let { (ds) -> ds }.split(",").map { it.trim().toLong() }.toMutableList()
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
        fun round(n: Int = 1, worryDivisor: Long = 3L) {
            (1 .. n).forEach { _ ->
                simbiants.entries.sortedBy { it.key }.forEach { (_, s) ->
                    s.levels.forEach { l ->
                        var newLevel = s.op.inspect(l)
                        if (s.op is MultSimbOp) {
                            println("old, new -> $l $newLevel")
                        }
                        if (worryDivisor != 1L) {
                            newLevel /= worryDivisor
                        }
                        val sTo = if (newLevel % s.div == 0L) s.t else s.f
                        simbiants[sTo]!!.levels += newLevel
                    }
                    s.inspectedItems += s.levels.size
                    s.levels.clear()
                }
            }
        }

        fun monkeyBusiness(): Long {
            return simbiants.entries.sortedByDescending { it.value.inspectedItems }.take(2).fold(1L) { ac, m -> ac * m.value.inspectedItems }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}