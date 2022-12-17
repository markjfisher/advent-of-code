package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.maxX
import net.fish.resourceString
import kotlin.math.min

object Day17 : Day {
    private val data by lazy { toDirections(resourceString(2022, 17)) }
    private val pass: Unit = Unit

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: ArrayDeque<JetDirection>): Int {
        val chamberSimulator = ChamberSimulator(7, mutableSetOf(), data)
        chamberSimulator.step(2022)
        return chamberSimulator.height()
    }

    fun doPart2(data: ArrayDeque<JetDirection>): Int = data.size

    enum class JetDirection { LEFT, RIGHT }
    fun toDirections(input: String): ArrayDeque<JetDirection> {
        return input.fold(ArrayDeque()) { ac, c ->
            ac.addLast(when (c) {
                '>' -> JetDirection.RIGHT
                '<' -> JetDirection.LEFT
                else -> throw Exception("Unknown direction: $c")
            })
            ac
        }
    }

    data class ChamberSimulator(val width: Int, val points: MutableSet<Point>, val jetDirections: ArrayDeque<JetDirection>) {
        var currentShape = 0

        fun height(): Int {
            if (points.isEmpty()) return 0
            return points.maxBy { it.y }.y + 1
        }

        fun step(steps: Long = 1) {
            (0 until steps).forEach { _ -> step() }
        }

        fun step() {
            val shapePoints = Day17Shapes.shapes[currentShape]
            currentShape = (currentShape + 1) % Day17Shapes.shapes.size
            var insertY = (points.maxByOrNull { it.y }?.y ?: -1) + 4
            var insertX = 2

            var canMove = true
            while (canMove) {
                // move it according to current jet
                val nextMove = jetDirections.removeFirst()
                // push it back on the end so the queue repeats
                jetDirections.addLast(nextMove)
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
        }

        fun grid(): List<String> {
            val boundary = Pair(Point(0, 0), Point(width - 1, points.bounds().second.y))
            val lines = mutableListOf<String>()
            (boundary.second.y downTo  boundary.first.y).forEach { y ->
                var currentLine = ""
                (boundary.first.x .. boundary.second.x).forEach { x ->
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