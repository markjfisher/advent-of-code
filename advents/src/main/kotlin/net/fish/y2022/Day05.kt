package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings
import net.fish.y2022.Day05.MoverModel.M9000
import net.fish.y2022.Day05.MoverModel.M9001

object Day05 : Day {
    private val movementExtractor = Regex("""move (\d+) from (\d+) to (\d+)""")
    fun toStacks(data: List<String>): Stacks {
        val layout = data[0]
        val moves = data[1]

        // calculate the columns
        val stacksByLine = layout.split("\n")
        val reversedToAdd = stacksByLine.reversed().drop(1)
        val columns = mutableMapOf<Int, ArrayDeque<Char>>()
        reversedToAdd.forEach { row ->
            val numEntries = (row.length + 1) / 4
            for (i in 0 until numEntries) {
                val c = row[i * 4 + 1]
                if (c != ' ') {
                    if (!columns.containsKey(i)) columns[i] = ArrayDeque()
                    columns[i]!!.addLast(c)
                }
            }
        }

        // Now add movement information
        val instructions = moves.split("\n").map { line ->
            movementExtractor.find(line)?.destructured!!.let { (c, a , b) ->
                Instruction(count = c.toInt(), from = a.toInt(), to = b.toInt())
            }
        }
        return Stacks(columns = columns, instructions = instructions)
    }

    enum class MoverModel { M9000, M9001 }

    sealed class MoverInstruction

    data class Instruction(val count: Int, val from: Int, val to: Int): MoverInstruction()
    object NoMoreInstructions: MoverInstruction()

    data class Stacks(val columns: Map<Int, ArrayDeque<Char>>, val instructions: List<Instruction>) {
        private var currentInstruction = 0
        fun processAllInstructions(model: MoverModel) {
            do {
                val instruction = move(model)
            } while (instruction != NoMoreInstructions)
        }

        fun tops(): String {
            return columns.entries.sortedBy { it.key }.map { (_, v) -> v.last() }.joinToString("")
        }

        fun move(model: MoverModel): MoverInstruction {
            return if (currentInstruction < instructions.size) {
                val instruction = instructions[currentInstruction++]
                val taken = (0 until instruction.count).map { _ -> columns[instruction.from - 1]!!.removeLast() }
                (if (model == M9000) taken else taken.reversed()).forEach { c -> columns[instruction.to - 1]!!.addLast(c) }
                instruction
            } else NoMoreInstructions
        }
    }

    override fun part1() = doPart1(toStacks(resourceStrings(year = 2022, day = 5, trim = false)))
    override fun part2() = doPart2(toStacks(resourceStrings(year = 2022, day = 5, trim = false)))

    fun doPart1(stacks: Stacks): String {
        stacks.processAllInstructions(M9000)
        return stacks.tops()
    }
    fun doPart2(stacks: Stacks): String {
        stacks.processAllInstructions(M9001)
        return stacks.tops()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}