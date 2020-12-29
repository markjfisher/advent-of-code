package net.fish.geometry

import kotlin.math.abs

typealias PathPositions = List<Pair<Int, Int>>

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