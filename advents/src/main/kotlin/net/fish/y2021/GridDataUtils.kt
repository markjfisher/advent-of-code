package net.fish.y2021

import net.fish.geometry.Point

object GridDataUtils {
    fun mapIntPointsFromLines(input: List<String>): MutableMap<Point, Int> {
        val gridMap = input.foldIndexed(mutableMapOf<Point, Int>()) { row, m, line ->
            val valuesForLine = line.windowed(1, 1).map { it.toInt() }
            valuesForLine.forEachIndexed { lineIndex, gridValue ->
                m[Point(lineIndex, row)] = gridValue
            }
            m
        }
        return gridMap
    }

    fun mapCharPointsFromLines(input: List<String>): MutableMap<Point, Char> {
        val gridMap = input.foldIndexed(mutableMapOf<Point, Char>()) { row, m, line ->
            val valuesForLine = line.windowed(1, 1).map { it.toCharArray()[0] }
            valuesForLine.forEachIndexed { lineIndex, gridValue ->
                m[Point(lineIndex, row)] = gridValue
            }
            m
        }
        return gridMap
    }

}