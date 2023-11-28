package net.fish.y2018

import net.fish.Day
import net.fish.resourceLines
import java.lang.Exception

object Day04 : Day {
    private val bs by lazy { Regex("""\[\d{4}-\d{2}-\d{2} \d{2}:\d{2}] Guard #(\d+) begins shift""") }
    private val fa by lazy { Regex("""\[\d{4}-\d{2}-\d{2} \d{2}:(\d{2})] falls asleep""") }
    private val wu by lazy { Regex("""\[\d{4}-\d{2}-\d{2} \d{2}:(\d{2})] wakes up""") }
    private val data by lazy { parseInstructions(resourceLines(2018, 4)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(guardMap: Map<Int, Guard>): Int {
        val longestSleepingGuard = guardMap.maxBy { it.value.totalSleep() }.value
        return longestSleepingGuard.id * longestSleepingGuard.mostAsleepAt()
    }

    fun doPart2(guardMap: Map<Int, Guard>): Int {
        // look at all the guards, find the one with the largest asleep time in a particular minute
        val longestSleepingGuardOnSingleMinute = guardMap.maxBy { entry -> entry.value.sleeps.entries.maxOf { sleepsEntry -> sleepsEntry.value } }.value
        return longestSleepingGuardOnSingleMinute.id * longestSleepingGuardOnSingleMinute.mostAsleepAt()
    }

    data class Guard(val id: Int, val sleeps: MutableMap<Int, Int>) {
        fun totalSleep(): Int = sleeps.values.sum()
        fun mostAsleepAt(): Int = sleeps.entries.maxBy { it.value }.key
    }

    fun parseInstructions(data: List<String>): Map<Int, Guard> {
        val dataMap: MutableMap<Int, Guard> = mutableMapOf()
        // sort the data so we get correct processing order. it's already in good format by date to simply sort by natural text order
        val sortedData = data.sorted()
        var isSleeping = false
        var asleepStart = -1
        var currentGuard = 0
        // Data is strictly in the order of:
        //  - Guard
        //  - N x pairs of Sleep/Awake
        sortedData.forEach {  line ->
            // println("parsing: $line")
            if (line.contains("Guard")) {
                val match = bs.find(line) ?: throw Exception("Failed to parse line for guard data: $line")
                match.destructured.let { (gId) -> currentGuard = gId.toInt() }
            } else if (!isSleeping) {
                val match = fa.find(line) ?: throw Exception("Failed to parse line for sleep data: $line")
                match.destructured.let { (t) -> asleepStart = t.toInt() }
                isSleeping = true
            } else {
                val match = wu.find(line) ?: throw Exception("Failed to parse line for waking up: $line")
                match.destructured.let {(awakeMinute) ->
                    isSleeping = false
                    // now save this guard's sleep times
                    val guard = dataMap.getOrPut(currentGuard) { Guard(currentGuard, mutableMapOf()) }
                    (asleepStart until awakeMinute.toInt()).forEach { minute ->
                        val currentSleepCountForMinute = guard.sleeps.getOrPut(minute) { 0 }
                        guard.sleeps[minute] = currentSleepCountForMinute + 1
                    }
                }

            }
        }
        return dataMap
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}