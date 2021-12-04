package net.fish

import java.io.InputStream
import java.util.stream.Collectors

internal object Resources

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

