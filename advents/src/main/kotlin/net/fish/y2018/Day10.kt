package net.fish.y2018

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.area
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day10 : Day {
    // e.g. position=< 20168,  40187> velocity=<-2, -4>
    private val pointExtractor by lazy { Regex("""<\s*(-?\d+),\s*(-?\d+)> velocity=<\s*(-?\d+),\s*(-?\d+)>""") }
    private val data by lazy { toStars(resourceLines(2018, 10)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(stars: List<Pair<Point, Point>>): String {
        val (minimum, t) = findMinimum(stars)
        return displayStars(minimum) + "\n$t"
    }
    fun doPart2(data: List<Pair<Point, Point>>): Int = 0

    fun toStars(data: List<String>): List<Pair<Point, Point>> = data.map { line ->
        pointExtractor.find(line)?.destructured!!.let { (x, y, dx, dy) ->
            Pair(Point(x.toInt(), y.toInt()), Point(dx.toInt(), dy.toInt()))
        }
    }

    fun moveStars(stars: List<Pair<Point, Point>>, moves: Int = 1): List<Pair<Point, Point>> {
        return stars.fold(listOf()) { acc, pair ->
            acc + Pair(Point(pair.first.x + pair.second.x * moves, pair.first.y + pair.second.y * moves), pair.second)
        }
    }

    fun boudaryArea(stars: List<Pair<Point, Point>>): Long = stars.map { it.first }.bounds().area()

    fun displayStars(stars: List<Pair<Point, Point>>): String {
        val starPoints = stars.map { it.first }.toSet()
        var result = ""
        val boundary = stars.map { it.first }.bounds()
        for (y in boundary.first.y .. boundary.second.y) {
            for (x in boundary.first.x .. boundary.second.x) {
                result += if (starPoints.contains(Point(x, y))) "▓" else "░"
            }
            result += "\n"
        }
        return result
    }

    fun findMinimum(stars: List<Pair<Point, Point>>): Pair<List<Pair<Point, Point>>, Int> {
        var ss = stars
        var lastSS = stars
        var lastBoundaryArea = boudaryArea(stars)
        var foundMinimum = false
        var time = 0
        while (!foundMinimum) {
            ss = moveStars(ss)
            val newBoundaryArea = boudaryArea(ss)
            if (newBoundaryArea > lastBoundaryArea) {
                foundMinimum = true
            } else {
                time++
                lastBoundaryArea = newBoundaryArea
                lastSS = ss
            }
        }
        return Pair(lastSS, time)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}