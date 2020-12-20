package net.fish

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

fun resourceStrings(year: Int, day: Int, delimiter: String = "\n\n"): List<String> = resourceString(year, day).split(delimiter).map { it.trim() }
fun resourceStrings(path: String, delimiter: String = "\n\n"): List<String> = resourceStream(path).bufferedReader().use { it.readText() }.split(delimiter).map { it.trim() }

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

fun move(from: Pair<Int, Int>, times: Int, direction: Pair<Int, Int>, history: MutableList<Pair<Int, Int>>): Pair<Int, Int> {
    var tracking = from
    (0 until times).forEach {
        tracking = Pair(tracking.first + direction.first, tracking.second + direction.second)
        history.add(tracking)
    }
    return history.last()
}

fun stepsTo(intersection: Pair<Int, Int>, points: PathPositions): Int {
    return points.indexOfFirst { it == intersection } + 1
}

fun findIntersections(points1: PathPositions, points2: PathPositions): Set<Pair<Int, Int>> {
    return points1.intersect(points2)
}

inline fun <reified R> rotateMatrix(mat: Array<Array<R>>): Array<Array<R>> {
    // only allow nxn matrix
    check(mat.size == mat[0].size)

    var matCopy = copyMatrix(mat)

    val size = mat.size

//    println("BEFORE ROTATION -------------------------------")
//    for (i in 0 until size) {
//        for (j in 0 until size) print(matCopy[i][j].toString() + " ")
//        print("\n")
//    }
//    println("-------------------------------")

    for (layer in (0 until (size / 2))) {
        val last = size - layer - 1
        for (i in (layer until last)) {
            val offset = i - layer
            val lastMinusOffset = last - offset
            val top = matCopy[layer][i]
            matCopy[layer][i] = matCopy[lastMinusOffset][layer]
            matCopy[lastMinusOffset][layer] = matCopy[last][lastMinusOffset]
            matCopy[last][lastMinusOffset] = matCopy[i][last]
            matCopy[i][last] = top
        }
    }

    // Print rotated matrix
//    println("AFTER ROTATION -------------------------------")
    // val size = mat.size
//    for (i in 0 until size) {
//        for (j in 0 until size) print(matCopy[i][j].toString() + " ")
//        print("\n")
//    }
//    println("-------------------------------")
    return matCopy
}

inline fun <reified R> copyMatrix(matrix: Array<Array<R>>): Array<Array<R>> {
    var copy = arrayOf<Array<R>>()
    for (j in matrix.indices) {
        var row = arrayOf<R>()
        for (i in matrix.indices) {
            row += matrix[j][i]
        }
        copy += row
    }
    return copy
}

inline fun <reified R> flipMatrixByVertical(mat: Array<Array<R>>): Array<Array<R>> {
    // only allow nxn matrix
    check(mat.size == mat[0].size)

    val matCopy = copyMatrix(mat)
    for (r in matCopy.indices) {
        matCopy[r].reverse()
    }

    return matCopy
}

inline fun <reified T> Pair<Int, Int>.createArray(initialValue: T) = Array(this.first) { Array(this.second) { initialValue } }
