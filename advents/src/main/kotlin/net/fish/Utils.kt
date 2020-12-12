package net.fish

import net.fish.Direction.EAST
import net.fish.Direction.NORTH
import net.fish.Direction.SOUTH
import net.fish.Direction.WEST
import java.io.InputStream
import java.time.Duration
import java.util.stream.Collectors
import kotlin.math.abs

internal object Resources

typealias PathPositions = List<Pair<Int, Int>>

fun resourceStream(name: String): InputStream {
    return Resources.javaClass.getResourceAsStream(name)
}

fun resource(year: Int, day: Int): InputStream {
    val name = String.format("/%d/day%02d.txt", year, day)
    return resourceStream(name)
}

fun resourceLines(year: Int, day: Int): List<String> {
    return resource(year, day).bufferedReader().lines().collect(Collectors.toList())
}

fun resourceString(year: Int, day: Int): String {
    return resource(year, day).bufferedReader().use { it.readText() }
}

fun resourcePath(path: String): List<String> {
    return resourceStream(path).bufferedReader().lines().collect(Collectors.toList())
}


fun formatDuration(nanos: Long): String {
    val d = Duration.ofNanos(nanos)
    val ms = nanos / 1_000_000.0
    return when {
        ms > 60000 -> String.format("%s m %s s", d.toMinutes(), d.minusMinutes(d.toMinutes()).seconds)
        ms > 10000 -> String.format("%s s", d.seconds)
        ms > 1000 -> String.format("%.2f s", ms / 1000.0)
        else -> String.format("%.2f ms", ms)
    }
}

fun lcm(x: Long, y: Long): Long {
    var a = x
    var b = y
    while (a != 0L) {
        a = (b % a).also { b = a }
    }
    return x / b * y
}

fun Collection<Point>.minX() = this.map { it.x }.minOrNull()
fun Collection<Point>.minY() = this.map { it.y }.minOrNull()
fun Collection<Point>.maxX() = this.map { it.x }.maxOrNull()
fun Collection<Point>.maxY() = this.map { it.y }.maxOrNull()

fun Collection<Point>.bounds() =
    this.fold(listOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)) { list, point ->
        listOf(
            kotlin.math.min(list[0], point.x),
            kotlin.math.min(list[1], point.y),
            kotlin.math.max(list[2], point.x),
            kotlin.math.max(list[3], point.y)
        )
    }.let { (minX, minY, maxX, maxY) ->
        Point(minX, minY) to Point(maxX, maxY)
    }

fun wireManhattanDistance(path1: PathPositions, path2: PathPositions): Int {
    return findIntersections(path1, path2)
        .map { manhattenDistance(it) }
        .filter { it > 0 }
        .minOrNull() ?: 0
}

fun manhattenDistance(coordinates: Pair<Int, Int>): Int = abs(coordinates.first) + abs(coordinates.second)

fun convertWirePathsToCoordinates(directions: List<String>): PathPositions {
    // Starting at 0,0 accumulate the coordinates that the directions touch in an infinite grid
    val coordinates = mutableListOf<Pair<Int, Int>>()
    var currentPosition = Pair(0, 0)
    directions.forEach { direction ->
        val count = direction.substring(1).toInt()
        currentPosition = when (direction[0]) {
            'D' -> addDown(currentPosition, coordinates, count)
            'U' -> addUp(currentPosition, coordinates, count)
            'L' -> addLeft(currentPosition, coordinates, count)
            'R' -> addRight(currentPosition, coordinates, count)
            else -> throw Exception("Unknown direction: $direction")
        }
    }
    return coordinates.toList()
}

fun addUp(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
    val range = (from.second + 1) until (from.second + length + 1)
    range.forEach { coordinates.add(Pair(from.first, it)) }
    return Pair(from.first, from.second + length)
}

fun addDown(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
    val progression = (from.second - 1) downTo (from.second - length)
    progression.forEach { coordinates.add(Pair(from.first, it)) }
    return Pair(from.first, from.second - length)
}

fun addRight(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
    val range = (from.first + 1)..(from.first + length)
    range.forEach { coordinates.add(Pair(it, from.second)) }
    return Pair(from.first + length, from.second)
}

fun addLeft(from: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>, length: Int): Pair<Int, Int> {
    val range = (from.first - 1) downTo from.first - length
    range.forEach { coordinates.add(Pair(it, from.second)) }
    return Pair(from.first - length, from.second)
}

fun stepsTo(intersection: Pair<Int, Int>, points: PathPositions): Int {
    return points.indexOfFirst { it == intersection } + 1
}

fun findIntersections(points1: PathPositions, points2: PathPositions): Set<Pair<Int, Int>> {
    return points1.intersect(points2)
}
