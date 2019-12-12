package net.fish

import java.io.InputStream

internal object Resources

fun resourceStream(name: String): InputStream {
    return Resources.javaClass.getResourceAsStream(name)
}

fun <T> permute(input: List<T>): List<List<T>> {
    if (input.size == 1) return listOf(input)
    val perms = mutableListOf<List<T>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}

object Helpers {
    fun loadResourceLinesAsLongs(path: String): List<Long> {
        return String(resourceStream(path).readBytes()).lines().map { it.toLong() }
    }

    fun loadResourceLinesAsInts(path: String): List<Int> {
        return String(resourceStream(path).readBytes()).lines().map { it.toInt() }
    }

    fun loadResourceCSVAsListOfLongs(path: String): List<Long> {
        return String(resourceStream(path).readBytes()).split(",").map { it.toLong() }
    }

    fun loadResourceCSVAsListOfInts(path: String): List<Int> {
        return String(resourceStream(path).readBytes()).split(",").map { it.toInt() }
    }

    fun loadWireData(path: String): Pair<List<String>, List<String>> {
        val lines = String(resourceStream(path).readBytes()).lines()
        return Pair(lines[0].split(","), lines[1].split(","))
    }

    fun loadOrbitData(path: String): List<String> {
        return String(resourceStream(path).readBytes()).lines()
    }

    fun loadResourceAsIntList(path: String): List<Int> {
        return String(resourceStream(path).readBytes()).map { it - '0' }
    }
}