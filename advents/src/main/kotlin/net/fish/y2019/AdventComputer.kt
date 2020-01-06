package net.fish.y2019

import net.fish.y2019.ParamMode.*
import java.lang.Exception

data class AdventComputer (
    var program: List<Long>,
    var initialInput: List<Long> = emptyList()
) {
    private var mem: Memory = Memory(program.mapIndexed { i, v -> i to v }.toMap().toMutableMap())
    private var inputs: MutableList<Long> = initialInput.toMutableList()
    private var pc: Int = 0
    private var base: Int = 0
    private var currentInput: Long = 0
    private var waitingInput: Boolean = false
    private lateinit var instruction: Instruction

    var outputs = mutableListOf<Long>()
    var running: Boolean = true

    data class Memory(val memory: MutableMap<Int, Long>) {
        fun get(address: Int) = memory.computeIfAbsent(address) { 0 }
        fun set(address: Int, value: Long) {
            memory[address] = value
        }
    }

    fun memoryAt(location: Int): Long = mem.get(location)

    fun out(): Long {
        return outputs.removeAt(0)
    }

    fun clearOutput() {
        outputs.clear()
    }

    fun addInput(input: Long) {
        inputs.add(input)
    }

    private fun add() {
        set(get(1) + get(2), 3)
        pc += 4
    }

    private fun mult() {
        set(get(1) * get(2), 3)
        pc += 4
    }

    private fun output() {
        outputs.add(get(1))
        pc += 2
    }

    private fun input() {
        if (inputs.isEmpty()) {
            waitingInput = true
            return
        }
        waitingInput = false
        currentInput = inputs.first()
        inputs = inputs.drop(1).toMutableList()
        set(currentInput, 1)
        pc += 2
    }

    private fun jumpIfTrue() {
        if (get(1) != 0L) {
            pc = get(2).toInt()
        } else {
            pc += 3
        }
    }

    private fun jumpIfFalse() {
        if (get(1) == 0L) {
            pc = get(2).toInt()
        } else {
            pc += 3
        }
    }

    private fun isLessThan() {
        set(if (get(1) < get(2)) 1 else 0, 3)
        pc += 4
    }

    private fun doEquals() {
        set(if (get(1) == get(2)) 1 else 0, 3)
        pc += 4
    }

    private fun changeRelativeBase() {
        base += get(1).toInt()
        pc += 2
    }

    private fun get(offset: Int): Long {
        val pos = pc + offset
        return when(instruction.modes[offset - 1]) {
            POSITIONAL -> mem.get(mem.get(pos).toInt())
            IMMEDIATE -> mem.get(pos)
            RELATIVE -> mem.get(mem.get(pos).toInt() + base)
        }
    }

    private fun set(v: Long, offset: Int) {
        when(instruction.modes[offset - 1]) {
            RELATIVE -> mem.set(base + mem.get(pc + offset).toInt(), v)
            else -> mem.set(mem.get(pc + offset).toInt(), v)
        }
    }

    fun run(): AdventComputer {
        if (waitingInput) input()
        while(running && !waitingInput) {
            instruction = Instruction.from(mem.get(pc).toInt())
            when (val opCode = instruction.opCode) {
                1 -> add()
                2 -> mult()
                3 -> input()
                4 -> output()
                5 -> jumpIfTrue()
                6 -> jumpIfFalse()
                7 -> isLessThan()
                8 -> doEquals()
                9 -> changeRelativeBase()
                99 -> running = false
                else -> throw BadMachine("Got opCode: $opCode in machine: $this")
            }
        }
        return this
    }

    class BadMachine(message: String): Exception(message)
}

enum class ParamMode(val value: Int) {
    POSITIONAL(0), IMMEDIATE(1), RELATIVE(2);

    companion object {
        fun from(v: Int) = values().find { it.value == v } ?: throw Exception("Unknown value $v")
    }
}

class Instruction (
    val modes: List<ParamMode>,
    val opCode: Int
){
    companion object {
        fun from(instruction: Int): Instruction {
            val digits =
                convertNumberToListOf5Digits(instruction)
            return Instruction(
                modes = listOf(ParamMode.from(digits[2]), ParamMode.from(digits[1]), ParamMode.from(digits[0])),
                opCode = instruction % 100
            )
        }

        private fun convertNumberToListOf5Digits(number: Int): List<Int> {
            return number.toString().padStart(5, '0').map { it - '0' }
        }

    }
}