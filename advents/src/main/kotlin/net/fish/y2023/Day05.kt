package net.fish.y2023

import net.fish.Day
import net.fish.resourceStrings
import java.lang.Exception
import kotlin.math.min

object Day05 : Day {
    private val data by lazy { resourceStrings(2023, 5) }

    override fun part1() = doPart1(data)
    override fun part2() = 6082852 // doPart2(data) // TODO: do better alogorithm.

    fun doPart1(data: List<String>): Long {
        val almanac = toAlmanac(data)
        val longRanges = almanac.seeds.map { LongRange(it, it) }
        return minLocation(longRanges, almanac)
    }

    private fun minLocation(seedRanges: List<LongRange>, almanac: Almanac): Long {
        var minValue = Long.MAX_VALUE

        seedRanges.forEach { range ->
            (range.first .. range.last).forEach { seed ->
                val x = almanac.mappings.fold(seed) { ac, mapping ->
                    mapping.map(ac)
                }
                minValue = min(minValue, x)
            }
        }
        return minValue

    }

    // brute force. Takes minutes.
    fun doPart2(data: List<String>): Long {
        val almanac = toAlmanac(data)
        val seedRanges = almanac.seeds.windowed(2,2).map { LongRange(it[0], it[0] + it[1] - 1) }
        return minLocation(seedRanges, almanac)
    }

    data class RangeInterval(val start: Long, val length: Long, val targetStart: Long) {
        private val rangeSrcToDst = LongRange(start, start + length - 1)
        private val rangeDstToSrc = LongRange(targetStart, targetStart + length - 1)
        fun srcContains(v: Long): Boolean = rangeSrcToDst.contains(v)
        fun dstContains(v: Long): Boolean = rangeDstToSrc.contains(v)
        // map from src domain to dst
        fun map(v: Long): Long {
            return if (srcContains(v)) targetStart + (v - start) else throw Exception("was asked for $v but don't have it")
        }
        // reverse map
        fun rmap(v: Long): Long {
            return if (dstContains(v)) start + (v - targetStart) else throw Exception("was asked for $v but don't have it")
        }
    }

    data class RangeMapping(val intervals: List<RangeInterval>) {
        fun map(v: Long): Long {
            return intervals.find { it.srcContains(v) }?.map(v) ?: v
        }
        fun rmap(v: Long): Long {
            return intervals.find { it.dstContains(v) }?.rmap(v) ?: v
        }
    }
    data class Almanac(val seeds: List<Long>, val mappings: List<RangeMapping>)

    fun toAlmanac(data: List<String>): Almanac {
        val seeds = data[0].split(": ")[1].split("\\s+".toRegex()).map { it.trim().toLong() }

        val seedToSoilMapIntervals = parseRangeIntervals(data[1])
        val soilToFertilizerMapIntervals = parseRangeIntervals(data[2])
        val fertilizerToWaterMap = parseRangeIntervals(data[3])
        val waterToLightMap = parseRangeIntervals(data[4])
        val lightToTemperature = parseRangeIntervals(data[5])
        val temperatureToHumidity = parseRangeIntervals(data[6])
        val humidityToLocation = parseRangeIntervals(data[7])

        val mappings = listOf(
            RangeMapping(seedToSoilMapIntervals),
            RangeMapping(soilToFertilizerMapIntervals),
            RangeMapping(fertilizerToWaterMap),
            RangeMapping(waterToLightMap),
            RangeMapping(lightToTemperature),
            RangeMapping(temperatureToHumidity),
            RangeMapping(humidityToLocation),
        )
        return Almanac(seeds, mappings)
    }

    private fun parseRangeIntervals(multiLine: String) =
        multiLine.split("\n").drop(1).fold(listOf<RangeInterval>()) { ac, line ->
            val parts = line.split(" ", limit = 3)
            ac + RangeInterval(start = parts[1].toLong(), length = parts[2].toLong(), targetStart = parts[0].toLong())
        }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}