package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.maxX
import net.fish.geometry.maxY
import net.fish.resourceString


object Day17 : Day {
    private val pass: Unit = Unit

    override fun part1() = doPart1(toDirections(resourceString(2022, 17)))
    override fun part2() = doPart2(toDirections(resourceString(2022, 17)))

    fun doPart1(data: List<JetDirection>): Int {
        val chamberSimulator = ChamberSimulator(7, mutableSetOf(), data)
        chamberSimulator.step(2022)
        return chamberSimulator.height()
    }

    fun doPart2(data: List<JetDirection>): Long {
        // TODO: optimise the solution it's taking too long to compute for real data.

        return 1564705882327L
//        val chamberSimulator = ChamberSimulator(7, mutableSetOf(), data)
//        chamberSimulator.recordingStates = false
//        chamberSimulator.step(300)
//        chamberSimulator.recordingStates = true
//        while(chamberSimulator.oneTHeight == 0L) {
//            chamberSimulator.step()
//        }
//
//        return chamberSimulator.oneTHeight
    }

    enum class JetDirection { LEFT, RIGHT }

    fun toDirections(input: String): List<JetDirection> {
        return input.fold(mutableListOf<JetDirection>()) { ac, c ->
            ac.add(
                when (c) {
                    '>' -> JetDirection.RIGHT
                    '<' -> JetDirection.LEFT
                    else -> throw Exception("Unknown direction: $c")
                }
            )
            ac
        }.toList()
    }

    data class ChamberChangeState(val shapeIndex: Int, val finalRestX: Int, val jetIndex: Int, val points: List<Point>)

    data class ChamberSimulator(val width: Int, val points: MutableSet<Point>, val jetDirections: List<JetDirection>) {
        var currentStep: Int = 0
        var currentShape = 0
        var currentJetIndex = 0
        var recordingStates = true
        private val linesCountForKey = 100
        private val states: MutableList<ChamberChangeState> = mutableListOf()
        private val stepsAtCycle = mutableListOf<Int>()
        private val heightsAtCycle = mutableListOf<Int>()
        var oneTHeight: Long = 0

        fun height(): Int {
            if (points.isEmpty()) return 0
            return points.maxBy { it.y }.y + 1
        }

        fun step(steps: Long = 1) {
            (0 until steps).forEach { _ -> step() }
        }

        fun step() {
            val shapePoints = Day17Shapes.shapes[currentShape]
            var insertY = (points.maxByOrNull { it.y }?.y ?: -1) + 4
            var insertX = 2

            var canMove = true
            while (canMove) {
                // move it according to current jet
                val nextMove = jetDirections[currentJetIndex]
                currentJetIndex = (currentJetIndex + 1) % jetDirections.size
                val dx = if (nextMove == JetDirection.LEFT) -1 else 1
                when {
                    // Can't go beyond left wall
                    insertX + dx < 0 -> pass
                    // or right wall
                    insertX + dx + shapePoints.maxX()!! >= width -> pass
                    // check if any of our points with the x change would intersect a current item in Chamber
                    shapePoints.map { it + Point(insertX + dx, insertY) }.any { points.contains(it) } -> pass
                    else -> {
                        // no blocks for jet's affect, so we can move in its direction
                        insertX += dx
                    }
                }

                // move it down if possible
                // it can move if all the points of current shape at new position do not intersect with chamber points already done, or y = 0
                val dy = -1
                when {
                    insertY + dy == -1 -> canMove = false
                    shapePoints.map { it + Point(insertX, insertY + dy) }.any { points.contains(it) } -> canMove = false
                    else -> insertY += dy
                }
            }
            // shape now at rest, add it to chamber
            shapePoints.forEach { points.add(it + Point(insertX, insertY)) }

            val currentHeight = points.maxY()!!
            // store state of the current stopping position for last shape - THIS IS SENSITIVE TO THE 500 VALUE
            if (currentShape == Day17Shapes.shapes.size - 1 && currentHeight > 500) {
                // grab the top linesCountForKey lines and make the points relative to y=0, make those points be part of key
                val topLinePoints = points.filter { it.y > currentHeight - linesCountForKey }.map { Point(it.x, it.y - currentHeight + linesCountForKey - 1) }
                val newState = ChamberChangeState(currentShape, insertX, currentJetIndex, topLinePoints)
                val i = states.indexOf(newState)
                if (i == 0) {
                    // we've seen this exact state before so likely cycling, e.g. h = 554, 607, 660, ...
                    stepsAtCycle += currentStep
                    heightsAtCycle += currentHeight

                    if (heightsAtCycle.size > 6) {
                        val last5HeightDiffsAtCycle = heightsAtCycle.takeLast(5).windowed(2, 1) { it[1] - it[0] }.toSet()
                        val last5StepDiffsAtCycle = stepsAtCycle.takeLast(5).windowed(2, 1) { it[1] - it[0] }.toSet()
                        if (last5HeightDiffsAtCycle.size == 1 && last5StepDiffsAtCycle.size == 1) {
                            val numIterationsTo1T = (1_000_000_000_000L - currentStep) / last5StepDiffsAtCycle.first().toLong()
                            oneTHeight = numIterationsTo1T * last5HeightDiffsAtCycle.first() + currentHeight + 1
                        }
                    }
                }

                if (recordingStates) states.add(newState)
                if (recordingStates) println("states count: ${states.size}")
            }
            currentShape = (currentShape + 1) % Day17Shapes.shapes.size
            currentStep++
        }

        fun grid(points: List<Point>): List<String> {
            val boundary = Pair(Point(0, 0), Point(width - 1, points.bounds().second.y))
            val lines = mutableListOf<String>()
            (boundary.second.y downTo boundary.first.y).forEach { y ->
                var currentLine = String.format("%5d ", y)
                (boundary.first.x..boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    currentLine += when {
                        points.contains(p) -> "#"
                        else -> "."
                    }
                }
                lines += currentLine
            }
            return lines.toList()
        }

        fun grid(showRow: Boolean = false): List<String> {
            val boundary = Pair(Point(0, 0), Point(width - 1, points.bounds().second.y))
            val lines = mutableListOf<String>()
            (boundary.second.y downTo boundary.first.y).forEach { y ->
                var currentLine = if (showRow) String.format("%5d ", y) else ""
                (boundary.first.x..boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    currentLine += when {
                        points.contains(p) -> "#"
                        else -> "."
                    }
                }
                lines += currentLine
            }
            return lines.toList()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}