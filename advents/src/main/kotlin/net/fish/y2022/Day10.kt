package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines
import net.fish.resourcePath
import kotlin.reflect.typeOf

object Day10 : Day {
    private val data by lazy { toInstructions(resourceLines(2022, 10)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    sealed class Day10Instruction (open val occursAt: Int)
    data class AddX(val value: Int, override val occursAt: Int): Day10Instruction(occursAt)
    data class Noop(override val occursAt: Int): Day10Instruction(occursAt)

    data class CRTComputer(val instructions: List<Day10Instruction>) {
        private var currentCycle: Int = 0
        private val instructionAt = instructions.groupBy { it.occursAt }
        private val pass: Unit = Unit

        var x = 1

        fun step(steps: Int) {
            repeat((1 .. steps).count()) {
                currentCycle++
                val i = instructionAt[currentCycle]?.first()
                if (i != null) {
                    when (i) {
                        is AddX -> x += i.value
                        is Noop -> pass
                    }
                }
            }
        }

        fun signalStrength(): Long {
            return x.toLong() * currentCycle.toLong()
        }

        fun signalStrengthsAt(times: List<Int>): List<Long> {
            // reset computer
            x = 1
            currentCycle = 0
            return times.map { t ->
                step(t - currentCycle)
                signalStrength()
            }
        }
    }

    fun toInstructions(data: List<String>): List<Day10Instruction> {
        var t = 1
        return data.fold(mutableListOf()) { ac, line ->
            ac += when (line) {
                "noop" -> {
                    t += 1
                    Noop(t)
                }
                else -> {
                    val xVal = line.split(" ", limit = 2)[1].toInt()
                    t += 2
                    AddX(xVal, t)
                }
            }
            ac
        }
    }

    fun doPart1(data: List<Day10Instruction>): Long {
        val computer = CRTComputer(data)
        return computer.signalStrengthsAt(listOf(20, 60, 100, 140, 180, 220)).sum()
    }
    fun doPart2(data: List<Day10Instruction>): Int = data.size

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}