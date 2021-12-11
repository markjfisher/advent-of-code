package net.fish.y2021

import net.fish.geometry.Point

object GridDataUtils {
    fun mapPointsFromLines(input: List<String>): MutableMap<Point, Int> {
        val gridMap = input.foldIndexed(mutableMapOf<Point, Int>()) { row, m, line ->
            val valuesForLine = line.windowed(1, 1).map { it.toInt() }
            valuesForLine.forEachIndexed { lineIndex, gridValue ->
                m[Point(lineIndex, row)] = gridValue
            }

            m
        }
        return gridMap
    }


}