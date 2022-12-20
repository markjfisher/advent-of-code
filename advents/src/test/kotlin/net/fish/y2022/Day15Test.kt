package net.fish.y2022

import net.fish.geometry.Point
import net.fish.resourcePath
import net.fish.y2022.Day15.SensorBeacon
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class Day15Test {
    @Test
    fun `can convert input to sensor beacons`() {
        val sensorBeacons = Day15.toSensorBeacons(resourcePath("/2022/day15-test.txt"))
        assertThat(sensorBeacons).containsExactly(
            SensorBeacon(sensor = Point(x = 2, y = 18), beacon = Point(x = -2, y = 15), md = 7),
            SensorBeacon(sensor=Point(x=9, y=16), beacon=Point(x=10, y=16), md=1),
            SensorBeacon(sensor=Point(x=13, y=2), beacon=Point(x=15, y=3), md=3),
            SensorBeacon(sensor=Point(x=12, y=14), beacon=Point(x=10, y=16), md=4),
            SensorBeacon(sensor=Point(x=10, y=20), beacon=Point(x=10, y=16), md=4),
            SensorBeacon(sensor=Point(x=14, y=17), beacon=Point(x=10, y=16), md=5),
            SensorBeacon(sensor=Point(x=8, y=7), beacon=Point(x=2, y=10), md=9),
            SensorBeacon(sensor=Point(x=2, y=0), beacon=Point(x=2, y=10), md=10),
            SensorBeacon(sensor=Point(x=0, y=11), beacon=Point(x=2, y=10), md=3),
            SensorBeacon(sensor=Point(x=20, y=14), beacon=Point(x=25, y=17), md=8),
            SensorBeacon(sensor=Point(x=17, y=20), beacon=Point(x=21, y=22), md=6),
            SensorBeacon(sensor=Point(x=16, y=7), beacon=Point(x=15, y=3), md=5),
            SensorBeacon(sensor=Point(x=14, y=3), beacon=Point(x=15, y=3), md=1),
            SensorBeacon(sensor=Point(x=20, y=1), beacon=Point(x=15, y=3), md=7)
        )
    }

    @Test
    fun `can get intervals crossing a row`() {
        // 1 crossing
        var sbs = listOf(SensorBeacon(Point(3,6), Point(5,7)))
        var intervals = Day15.intervalsAtRow(8, sbs)
        assertThat(intervals).containsExactly(IntRange(2, 4))

        // no crossing
        sbs = listOf(SensorBeacon(Point(2,15), Point(3,14)))
        intervals = Day15.intervalsAtRow(8, sbs)
        assertThat(intervals).isEmpty()

        // moving line to get different intersections
        sbs = listOf(
            SensorBeacon(Point(3,6), Point(5,7)),
            SensorBeacon(Point(9,10), Point(6,12))
        )
        intervals = Day15.intervalsAtRow(8, sbs)
        assertThat(intervals).containsExactly(IntRange(2, 4), IntRange(6, 12))
        intervals = Day15.intervalsAtRow(9, sbs)
        assertThat(intervals).containsExactly(IntRange(3, 3), IntRange(5, 13))
        intervals = Day15.intervalsAtRow(10, sbs)
        assertThat(intervals).containsExactly(IntRange(4, 14))

    }

    @Test
    fun `can do part 1`() {
        assertThat(Day15.coverageCount(10, Day15.toSensorBeacons(resourcePath("/2022/day15-test.txt")))).isEqualTo(26)
    }

    @Disabled("Needs work")
    @Test
    fun `can do part 2`() {
        // first version
        assertThat(Day15.tuningFrequency(Day15.toSensorBeacons(resourcePath("/2022/day15-test.txt")), IntRange(0, 20))).isEqualTo(56_000_011L)

        // second version
        assertThat(Day15.tuningFrequency(Day15.toSensorBeacons(resourcePath("/2022/day15-test.txt")), Pair(Point(0, 0), Point(20, 20)))).isEqualTo(56_000_011L)
    }

    @Test
    fun `can find isolated point`() {
        val sensorBeacons = Day15.toSensorBeacons(resourcePath("/2022/day15-test.txt"))
        assertThat(Day15.isolatedPoint(sensorBeacons, Pair(Point(0, 0), Point(20, 20)))).isEqualTo(Point(14,11))
    }

    @Test
    fun `can combine ranges`() {
        val r1 = IntRange(0, 5)
        val r2 = IntRange(1, 6)
        val r3 = IntRange(8, 10)
        val r4 = IntRange(4, 9)
        assertThat(Day15.combinedRanges(setOf(r1, r2))).containsExactly(IntRange(0, 6))
        assertThat(Day15.combinedRanges(setOf(r1, r3))).containsExactly(IntRange(0, 5), IntRange(8, 10))
        assertThat(Day15.combinedRanges(setOf(r1, r3, r4))).containsExactly(IntRange(0, 10))
    }

    @Test
    fun `can clip ranges`() {
        val r1 = IntRange(-5, -3)
        val r2 = IntRange(-1, 25)
        val r3 = IntRange(28, 30)
        // no clipping
        assertThat(Day15.clipRanges(setOf(r1, r2, r3), IntRange(-20, 35))).containsExactly(r1, r2, r3)
        // removes r1 and r3, clips r2
        assertThat(Day15.clipRanges(setOf(r1, r2, r3), IntRange(0, 20))).containsExactly(IntRange(0, 20))
        assertThat(Day15.clipRanges(setOf(r1, r2, r3), IntRange(-4, 29))).containsExactly(IntRange(-4, -3), r2, IntRange(28, 29))
    }

    @Test
    fun `can get points outside sensorBeacon`() {
        val sb1 = SensorBeacon(Point(0,0), Point(2,0))
        assertThat(Day15.pointsOutside(sb1, Pair(Point(-10, -10), Point(10, 10)))).containsExactly(
            Point(x=-3, y=0),
            Point(x=-2, y=-1),
            Point(x=-2, y=1),
            Point(x=-1, y=-2),
            Point(x=-1, y=2),
            Point(x=0, y=-3),
            Point(x=0, y=3),
            Point(x=1, y=-2),
            Point(x=1, y=2),
            Point(x=2, y=-1),
            Point(x=2, y=1),
            Point(x=3, y=0)
        )
        assertThat(Day15.pointsOutside(sb1, Pair(Point(0, 0), Point(10, 10)))).containsExactly(
            Point(x=0, y=3),
            Point(x=1, y=2),
            Point(x=2, y=1),
            Point(x=3, y=0)
        )
    }
}