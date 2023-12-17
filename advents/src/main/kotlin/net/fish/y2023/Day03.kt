package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.edgePoints
import net.fish.geometry.rows
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day03 : Day {
    private val data by lazy { resourceLines(2023, 3) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = findUnattachedNumbers(toNumberGrid(data)).sum()
    fun doPart2(data: List<String>): Int = findGears(toNumberGrid(data)).sum()

    data class GridNumber(val position: Point, val value: Int)
    data class GridSymbol(val position: Point, val type: Char)

    data class NumberGrid(val numbers: MutableList<GridNumber>, val symbols: MutableList<GridSymbol>) {
        fun print() {
            numbers.forEach { println(it) }
            symbols.forEach { println(it) }
        }
    }
    fun toInt(ints: List<Int>): Int {
        return ints.fold(0) { ac, i ->
            ac * 10 + i
        }
    }

    private fun numLength(i: Int): Int {
        return "$i".length
    }

    private fun findUnattachedNumbers(grid: NumberGrid): List<Int> {
        val allSymbolPositions = grid.symbols.map { it.position }.toSet()
        // find all the points around the numbers, and search for a symbol in its location
        return grid.numbers.fold(listOf()) { ac, gridNumber ->
            // find the points along the box surrounding the number's position
            val edgePoints = Pair(gridNumber.position - Point(1, 1), Point(gridNumber.position.x + numLength(gridNumber.value) - 1, gridNumber.position.y) + Point(1, 1)).edgePoints()
            val isAttached = edgePoints.any { allSymbolPositions.contains(it) }
            if (isAttached) ac + gridNumber.value else ac
        }
    }

    fun findGears(grid: NumberGrid): List<Int> {
        val stars = grid.symbols.filter { it.type == '*' }
        // find all positions that have a number in them
        val pointToNumber: MutableMap<Point, GridNumber> = mutableMapOf()
        grid.numbers.forEach { gn ->
            // add all the points for a particular grid number to our map
            var currentNumPoint = gn.position
            pointToNumber[currentNumPoint] = gn
            for (i in 0 until numLength(gn.value) - 1) {
                currentNumPoint += Point(1,0)
                pointToNumber[currentNumPoint] = gn
            }
        }
        // find a bounding box around each *, find the numbers that touch that
        val starsToAttachedNumbers = stars.map { s ->
            val edgePoints = Pair(s.position - Point(1, 1), s.position + Point(1, 1)).edgePoints()
            val attachedNumbers = edgePoints.mapNotNull { pointToNumber[it] }.toSet()
            attachedNumbers
        }
        val validGears = starsToAttachedNumbers.filter { it.size == 2 }.map {
            val x = it.toList()
            x[0].value * x[1].value
        }

        return validGears
    }

    fun toNumberGrid(data: List<String>): NumberGrid {
        val grid = GridDataUtils.mapCharPointsFromLines(data)
        val bounds = grid.map { it.key }.bounds()
        val sequence = bounds.rows()
        val iterator = sequence.iterator()
        val numberGrid = NumberGrid(mutableListOf(), mutableListOf())

        fun checkForSymbol(c: Char, p: Point) {
            if (c != '.') {
                numberGrid.symbols += GridSymbol(p, c)
            }
        }

        while (iterator.hasNext()) {
            val line = iterator.next()
            var parsingNumber = false
            val nextNumberList = mutableListOf<Int>()
            var currentLineIndex = 0
            var numberStartPosition = Point(-1, -1)
            while (currentLineIndex < line.size) {
                val currentPoint = line[currentLineIndex]
                val nextChar = grid[currentPoint]!!
                if (parsingNumber) {
                    if (nextChar.isDigit()) {
                        nextNumberList += nextChar.digitToInt()
                    } else {
                        // end of a number sequence, don't forget to also check if it's a symbol
                        numberGrid.numbers += GridNumber(position = numberStartPosition, value = toInt(nextNumberList))
                        nextNumberList.clear()
                        checkForSymbol(nextChar, currentPoint)
                        parsingNumber = false
                    }
                } else {
                    if (nextChar.isDigit()) {
                        // started a new number
                        nextNumberList += nextChar.digitToInt()
                        parsingNumber = true
                        numberStartPosition = currentPoint
                    } else {
                        // not parsing a number, and this isn't a digit, is it a symbol?
                        checkForSymbol(nextChar, currentPoint)
                    }
                }
                currentLineIndex++
            }
            // cater for numbers on the ends!
            if (parsingNumber) numberGrid.numbers += GridNumber(position = numberStartPosition, value = toInt(nextNumberList))
        }
        // numberGrid.print()
        return numberGrid
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}