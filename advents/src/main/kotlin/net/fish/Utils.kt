package net.fish

import java.io.InputStream
import java.time.Duration
import java.util.stream.Collectors

internal object Resources

typealias PathsType = List<Pair<Int, Int>>

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


fun formatDuration(ms: Long): String {
    val d = Duration.ofMillis(ms)
    return when {
        ms > 60000 -> String.format("%s m %s s", d.toMinutes(), d.minusMinutes(d.toMinutes()).seconds)
        ms > 10000 -> String.format("%s s", d.seconds)
        ms > 1000 -> String.format("%.2f s", ms / 1000.0)
        else -> String.format("%s ms", ms)
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

fun Collection<Point>.minX() = this.map { it.x }.min()
fun Collection<Point>.minY() = this.map { it.y }.min()
fun Collection<Point>.maxX() = this.map { it.x }.max()
fun Collection<Point>.maxY() = this.map { it.y }.max()

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
