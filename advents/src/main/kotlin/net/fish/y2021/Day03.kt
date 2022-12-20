package net.fish.y2021

import com.marcinmoskala.math.pow
import net.fish.Day
import net.fish.resourceLines

object Day03 : Day {
    private val data by lazy { resourceLines(2021, 3) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val gamma = calculateGamma(data)
        val epsilon = calculateEpsilon(gamma, data[0].length)
        return gamma * epsilon
    }

    fun calculateGamma(data: List<String>): Int {
        // count the 1s in each column from the whole data, then convert to a binary string if the counts are over half the total in each position.
        return data.fold(Array(data[0].length) { 0 }) { counts, d ->
            d.forEachIndexed { i, c -> if (c == '1') counts[i] += 1 }
            counts
        }.fold("") { s, c ->
            s + if (c > data.size / 2) "1" else "0"
        }.toInt(2)
    }

    fun calculateEpsilon(gamma: Int, n: Int): Int {
        return 2.pow(n) - gamma - 1
    }

    fun doPart2(data: List<String>): Int {
        return calculateCarb(data) * calculateOxygen(data)
    }

    private fun calculateCarb(data: List<String>): Int {
        return findDataMatchingBitFnAsDecimal(data, ::lsb)
    }

    private fun calculateOxygen(data: List<String>): Int {
        return findDataMatchingBitFnAsDecimal(data, ::msb)
    }

    private fun findDataMatchingBitFnAsDecimal(data: List<String>, bitFn: (List<String>, Int) -> Int): Int {
        val mutData = data.toMutableList()
        var currentDigit = 0
        while (mutData.size > 1 && currentDigit < data[0].length) {
            val significantBitValue = bitFn(mutData, currentDigit)
            mutData.removeIf { it[currentDigit] != ('0' + significantBitValue) }
            currentDigit++
        }

        return mutData[0].toInt(2)
    }

    fun msb(data: List<String>, n: Int): Int {
        val (onesCount, zerosCount) = digitCounts(data, n)
        return if (onesCount >= zerosCount) 1 else 0
    }

    fun lsb(data: List<String>, n: Int): Int {
        val (onesCount, zerosCount) = digitCounts(data, n)
        return if (onesCount < zerosCount) 1 else 0
    }

    private fun digitCounts(data: List<String>, n: Int): Pair<Int, Int> {
        val onesCount = data.fold(0) { v, line ->
            v + if (line[n] == '1') 1 else 0
        }
        val zerosCount = data.size - onesCount
        return Pair(onesCount, zerosCount)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}