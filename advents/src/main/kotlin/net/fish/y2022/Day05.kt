package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings
import net.fish.y2022.Day05.MoverModel.M9000
import net.fish.y2022.Day05.MoverModel.M9001

object Day05 : Day {
    private val movementExtractor = Regex("""move (\d+) from (\d+) to (\d+)""")

    fun doPart1(stacks: Stacks): String {
        stacks.processAllInstructions(M9000)
        return stacks.tops()
    }
    fun doPart2(stacks: Stacks): String {
        stacks.processAllInstructions(M9001)
        return stacks.tops()
    }

    data class Stacks(val columns: Map<Int, ArrayDeque<Char>>, val instructions: List<Instruction>) {
        private var currentInstruction = 0

        fun processAllInstructions(model: MoverModel) {
            do { val instruction = move(model) } while (instruction != NoMoreInstructions)
        }

        fun move(model: MoverModel): MoverInstruction {
            return if (currentInstruction < instructions.size) {
                val instruction = instructions[currentInstruction++]
                val taken = (0 until instruction.count).map { _ -> columns[instruction.from - 1]!!.removeLast() }
                (if (model == M9000) taken else taken.reversed()).forEach { c -> columns[instruction.to - 1]!!.addLast(c) }
                instruction
            } else NoMoreInstructions
        }

        fun tops(): String = columns.entries.sortedBy { it.key }.map { (_, v) -> v.last() }.joinToString("")
    }

    fun toStacks(data: List<String>): Stacks {
        val layout = data[0]
        val moves = data[1]

        val columns = layout.split("\n").reversed().drop(1).fold(mutableMapOf<Int, ArrayDeque<Char>>()) { ac, row ->
            row.filterIndexed { i, _ -> (i - 1) % 4 == 0 }.mapIndexed { i, c ->
                if (c != ' ') { ac.getOrDefault(i, ArrayDeque()).let { it.addLast(c); ac[i]= it } }
            }
            ac
        }

        val instructions = moves.split("\n").map { line ->
            movementExtractor.find(line)?.destructured!!.let { (c, a, b) ->
                Instruction(count = c.toInt(), from = a.toInt(), to = b.toInt())
            }
        }
        return Stacks(columns = columns, instructions = instructions)
    }

    enum class MoverModel { M9000, M9001 }
    sealed class MoverInstruction
    data class Instruction(val count: Int, val from: Int, val to: Int): MoverInstruction()
    object NoMoreInstructions: MoverInstruction()

    override fun part1() = doPart1(toStacks(resourceStrings(year = 2022, day = 5, trim = false)))
    override fun part2() = doPart2(toStacks(resourceStrings(year = 2022, day = 5, trim = false)))

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}