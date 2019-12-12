package net.fish

import net.fish.ParamMode.*
import java.lang.Exception

data class AdventComputer (
    var memory: MutableList<Long>,
    var inputs: List<Long> = emptyList()
) {
    private var instructionPointer: Int = 0
    private var relativeBase: Int = 0
    private var currentInput: Long = 0
    private var waitingInput: Boolean = false
    private lateinit var currentInstruction: Instruction

    var outputs = mutableListOf<Long>()
    var running: Boolean = true

    fun takeOutput(): Long {
        return outputs.removeAt(0)
    }

    private fun add() {
        memory[memory[instructionPointer + 3].toInt()] = param1Value() + param2Value()
        instructionPointer += 4
    }

    private fun mult() {
        memory[memory[instructionPointer + 3].toInt()] = param1Value() * param2Value()
        instructionPointer += 4
    }

    private fun output() {
        val output = param1Value()
        outputs.add(output)
        instructionPointer += 2
    }

    private fun input() {
        if (inputs.isEmpty()) {
            waitingInput = true
            return
        }
        waitingInput = false
        currentInput = inputs.first()
        inputs = inputs.drop(1)
        memory[memory[instructionPointer + 1].toInt()] = currentInput
        instructionPointer += 2
    }

    private fun jumpIfTrue() {
        if (param1Value() != 0L) {
            instructionPointer = param2Value().toInt()
        } else {
            instructionPointer += 3
        }
    }

    private fun jumpIfFalse() {
        if (param1Value() == 0L) {
            instructionPointer = param2Value().toInt()
        } else {
            instructionPointer += 3
        }
    }

    private fun isLessThan() {
        memory[memory[instructionPointer + 3].toInt()] = if (param1Value() < param2Value()) 1 else 0
        instructionPointer += 4
    }

    private fun doEquals() {
        memory[memory[instructionPointer + 3].toInt()] = if (param1Value() == param2Value()) 1 else 0
        instructionPointer += 4
    }

    private fun param1Value(): Long {
        return paramAt(0)
    }

    private fun param2Value(): Long {
        return paramAt(1)
    }

    private fun paramAt(i: Int): Long {
        // 0 based index, param1 = 0, param2 = 1, ...
        val pos = instructionPointer + i + 1
        return when(currentInstruction.modes[i]) {
            POSITIONAL -> memory[memory[pos].toInt()]
            IMMEDIATE -> memory[pos]
            RELATIVE -> memory[pos + relativeBase]
        }
    }

    fun runProgram(): AdventComputer {
        if (waitingInput) input()
        while(running && !waitingInput) {
            currentInstruction = Instruction.from(memory[instructionPointer].toInt())
            when (val opCode = currentInstruction.opCode) {
                1 -> add()
                2 -> mult()
                3 -> input()
                4 -> output()
                5 -> jumpIfTrue()
                6 -> jumpIfFalse()
                7 -> isLessThan()
                8 -> doEquals()
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
            val digits = convertNumberToListOf5Digits(instruction)
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