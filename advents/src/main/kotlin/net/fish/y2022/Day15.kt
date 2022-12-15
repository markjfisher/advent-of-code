package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Point
import net.fish.resourceLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day15 : Day {
    override val warmUps = 0

    private val sensorBeaconExtractor = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
    private val data by lazy { toSensorBeacons(resourceLines(2022, 15)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<SensorBeacon>): Int = coverageCount(2_000_000, data)
    // about 5s to do this version
    fun doPart2(data: List<SensorBeacon>): Long = tuningFrequency(data, IntRange(0, 4_000_000))
    // This one needs some work yet...
    //fun doPart2(data: List<SensorBeacon>): Long = tuningFrequency(data, Pair(Point(0, 0), Point(4_000_000, 4_000_000)))

    fun tuningFrequency(sensorBeacons: List<SensorBeacon>, range: IntRange): Long {
        val (row, ranges) = rowWithGap(sensorBeacons, range)
        return row.toLong() + 4_000_000L * (ranges[0].last + 1)
    }

    fun tuningFrequency(sensorBeacons: List<SensorBeacon>, boundary: Pair<Point, Point>): Long {
        val isolatedPoint = isolatedPoint(sensorBeacons, boundary)
        return isolatedPoint.y.toLong() + 4_000_000L * isolatedPoint.x.toLong()
    }

    private fun rowWithGap(sensorBeacons: List<SensorBeacon>, range: IntRange): Pair<Int, List<IntRange>> {
        lateinit var clipped: Set<IntRange>
        val row = (range.first .. range.last).first { row ->
            clipped = clipRanges(combinedRanges(intervalsAtRow(row, sensorBeacons)), range)
            clipped.size > 1
        }
        println("row: $row, clipped: $clipped")
        return Pair(row, clipped.sortedBy { it.first })
    }

    data class SensorBeacon(val sensor: Point, val beacon: Point, val md: Int = sensor.manhattenDistance(beacon))

    fun toSensorBeacons(data: List<String>): List<SensorBeacon> = data.map {
        sensorBeaconExtractor.find(it)?.destructured!!.let { (sx, sy, bx, by) ->
            SensorBeacon(Point(sx.toInt(), sy.toInt()), Point(bx.toInt(), by.toInt()))
        }
    }

    fun intervalsAtRow(row: Int, sensorBeacons: List<SensorBeacon>): Set<IntRange> {
        return sensorBeacons.fold(setOf()) { ac, sb ->
            // return those whose sensor's y coordinate +- manhatten distance contains the row, converted to ranges in x
            val distToRow = abs(sb.sensor.y - row)

            if (distToRow <= sb.md) {
                // our area of sensor crosses the row, and its width in the x direction is twice the distance
                val xr = sb.md - distToRow
                ac + setOf(IntRange(sb.sensor.x - xr, sb.sensor.x + xr))
            } else ac
        }
    }

    fun coverageCount(row: Int, sensorBeacons: List<SensorBeacon>): Int {
        val intervals = intervalsAtRow(row, sensorBeacons)
        val covered = combinedRanges(intervals).sumOf { r -> r.last - r.first + 1 }

        // need to remove any beacons/sensors on the row
        val sensorsInRowCount = sensorBeacons.filter { it.sensor.y == row }.size
        // The beacons are not unique!
        val beaconsInRowCount = sensorBeacons.filter { it.beacon.y == row }.map { it.beacon }.toSet().size
        return covered - sensorsInRowCount - beaconsInRowCount
    }

    fun combinedRanges(ranges: Set<IntRange>): Set<IntRange> {
        // combine overlapping ranges into distinct ranges
        val orderedRanges = ranges.sortedWith(compareBy({ it.first }, { it.last }))
        return orderedRanges.fold(emptyList<IntRange>()) { ac, r ->
            if (ac.isEmpty()) listOf(r)
            else {
                // consider following:
                // [(0,3), (5,7)], (9,10)
                //            a     b
                // when b > a+1 add r to end of list

                // Two cases where they overlap
                // [(0,3), (5,7)],  (5, 9)
                // [(0,3), (5,15)], (6, 9)
                //            a      b
                //          c           d
                // when b <= a+1 replace last with (c, max(a,d))

                val a = ac.last().last
                val b = r.first
                if (b > a + 1) {
                    ac + listOf(r)
                } else {
                    val c = ac.last().first
                    val d = max(r.last, a)
                    ac.dropLast(1) + listOf(IntRange(c, d))
                }
            }
        }.toSet()
    }

    fun clipRanges(ranges: Set<IntRange>, clip: IntRange): Set<IntRange> {
        return ranges.fold(setOf()) { ac, r ->
            if (r.first > clip.last) ac // skip if lower bound outside upper range
            else if (r.last < clip.first) ac // skip if lower range entirely before lower bound
            else {
                ac + listOf(IntRange(max(clip.first, r.first), min(clip.last, r.last)))
            }
        }
    }

    fun pointsOutside(sensorBeacon: SensorBeacon, boundary: Pair<Point, Point>): Set<Point> {
        // from the sensor pick all points its MD distance + 1 away
        // TODO: optimise this, it's taking forever and looping over values we don't use. Works for test data pretty quick, but not large scale
        val lower = max(boundary.first.x, sensorBeacon.sensor.x - (sensorBeacon.md + 1))
        val upper = min(boundary.second.x, sensorBeacon.sensor.x + (sensorBeacon.md + 1))
        return (-(sensorBeacon.md + 1) .. (sensorBeacon.md + 1)).fold(setOf()) { ac, i ->
            println("doing i: $i")
            val x = sensorBeacon.sensor.x + i
            val dy = abs(i) - (sensorBeacon.md + 1)
            val p1 = Point(x, sensorBeacon.sensor.y + dy)
            val p2 = Point(x, sensorBeacon.sensor.y - dy)
            val additionalPoints = mutableSetOf<Point>()
            if (p1.within(boundary)) additionalPoints += p1
            if (p2.within(boundary)) additionalPoints += p2
            if (additionalPoints.isNotEmpty()) ac + additionalPoints else ac
        }
    }

    fun isolatedPoint(sensorBeacons: List<SensorBeacon>, boundary: Pair<Point, Point>): Point {
        println("sensorBeacons size: " + sensorBeacons.size)

        for(sb in sensorBeacons) {
            val outsidePoints = pointsOutside(sb, boundary)
            println("testing points count: " + outsidePoints.size)
            val notInAnyBeacon = findOutsideAny(sensorBeacons - sb, outsidePoints)
            println("... found: $notInAnyBeacon")
            if (notInAnyBeacon != null) return notInAnyBeacon
        }

        throw Exception("No isolated point found")
    }

    private fun findOutsideAny(sensorBeacons: List<SensorBeacon>, outsidePoints: Set<Point>): Point? {
        // check MD of points in all beacons
        return outsidePoints.firstOrNull { p ->
            sensorBeacons.all { sb -> p.manhattenDistance(sb.sensor) > sb.md }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}