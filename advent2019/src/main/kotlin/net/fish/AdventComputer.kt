package net.fish

import java.lang.Exception

data class AdventComputer (
    var memory: MutableList<Int>,
    var inputs: List<Int> = emptyList()
) {
    private var instructionPointer: Int = 0
    private var currentInput: Int = 0
    private var waitingInput: Boolean = false
    private lateinit var currentInstruction: Instruction

    var outputs = mutableListOf<Int>()
    var running: Boolean = true

    fun takeOutput(): Int {
        return outputs.removeAt(0)
    }

    private fun add() {
        memory[memory[instructionPointer + 3]] = param1Value() + param2Value()
        instructionPointer += 4
    }

    private fun mult() {
        memory[memory[instructionPointer + 3]] = param1Value() * param2Value()
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
        memory[memory[instructionPointer + 1]] = currentInput
        instructionPointer += 2
    }

    private fun jumpIfTrue() {
        if (param1Value() != 0) {
            instructionPointer = param2Value()
        } else {
            instructionPointer += 3
        }
    }

    private fun jumpIfFalse() {
        if (param1Value() == 0) {
            instructionPointer = param2Value()
        } else {
            instructionPointer += 3
        }
    }

    private fun isLessThan() {
        memory[memory[instructionPointer + 3]] = if (param1Value() < param2Value()) 1 else 0
        instructionPointer += 4
    }

    private fun doEquals() {
        memory[memory[instructionPointer + 3]] = if (param1Value() == param2Value()) 1 else 0
        instructionPointer += 4
    }

    private fun param1Value(): Int {
        return if (currentInstruction.param1IsImmediate) memory[instructionPointer + 1] else memory[memory[instructionPointer + 1]]
    }

    private fun param2Value(): Int {
        return if (currentInstruction.param2IsImmediate) memory[instructionPointer + 2] else memory[memory[instructionPointer + 2]]
    }

    private fun param3Value(): Int {
        return if (currentInstruction.param3IsImmediate) memory[instructionPointer + 3] else memory[memory[instructionPointer + 3]]
    }

    fun runProgram(): AdventComputer {
        if (waitingInput) input()
        while(running && !waitingInput) {
            currentInstruction = Instruction.from(memory[instructionPointer])
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

class Instruction (
    val param1IsImmediate: Boolean,
    val param2IsImmediate: Boolean,
    val param3IsImmediate: Boolean,
    val opCode: Int
){
    companion object {
        fun from(instruction: Int): Instruction {
            val digits = convertNumberToListOf5Digits(instruction)
            return Instruction(
                param1IsImmediate = digits[2] == 1,
                param2IsImmediate = digits[1] == 1,
                param3IsImmediate = digits[0] == 1,
                opCode = instruction % 100
            )
        }

        private fun convertNumberToListOf5Digits(number: Int): List<Int> {
            return number.toString().padStart(5, '0').map { it - '0' }
        }

    }
}