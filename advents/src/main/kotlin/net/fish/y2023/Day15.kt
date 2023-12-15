package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day15 : Day {
    private val data by lazy { resourceLines(2023, 15) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        return readPuzzle(data).toHash()
    }
    fun doPart2(data: List<String>): Long {
        val p = readPuzzle(data)
        val boxes = p.processCommands()
        return p.score(boxes)
    }

    fun nameHash(s: String): Int = s.fold(0) { ac, c -> ((ac + c.code) * 17) % 256 }

    data class InitSequence(val commands: List<String>) {
        fun processCommands(): List<List<Pair<String, Int>>> {
            val boxes = List(256) { mutableListOf<Pair<String, Int>>() }
            commands.forEach { cmd ->
                if (cmd.contains("=")) {
                    val parts = cmd.split("=", limit = 2)
                    val box = nameHash(parts[0])
                    val v = parts[1].toInt()
                    val entry = boxes[box]
                    var replacedPair = false
                    // check if it needs to be reassigned, rather than added.
                    for (i in entry.indices) {
                        val pair = entry[i]
                        if (pair.first == parts[0]) {
                            entry[i] = Pair(pair.first, v)
                            replacedPair = true
                        }
                    }
                    if (!replacedPair) {
                        boxes[box] += Pair(parts[0], v)
                    }
                } else if (cmd.contains("-")) {
                    val parts = cmd.split("-")
                    val box = nameHash(parts[0])
                    val entry = boxes[box]
                    entry.removeIf { it.first == parts[0] }
                }
            }
            // println(boxes)
            return boxes
        }

        fun score(boxes: List<List<Pair<String, Int>>>): Long {
            return boxes.foldIndexed(0L) { boxNum, ac, box ->
                ac + box.foldIndexed(0L) { slotNum, t, pair ->
                    val r = (boxNum + 1) * (slotNum + 1) * pair.second
                    t + r
                }
            }
        }

        fun toHash(): Long {
            return commands.fold(0L) { ac, cmd ->
                ac + nameHash(cmd)
            }
        }
    }

    fun readPuzzle(data: List<String>): InitSequence {
        return InitSequence(data[0].split(","))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}