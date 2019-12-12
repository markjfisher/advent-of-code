package net.fish

import java.lang.Exception

data class Day2Machine (
    var memory: MutableList<Int>,
    var instructionPointer: Int,
    var running: Boolean = true
) {
    private fun doAdd() {
        val a = memory[memory[instructionPointer + 1]]
        val b = memory[memory[instructionPointer + 2]]
        memory[memory[instructionPointer + 3]] = a + b
        instructionPointer += 4
    }

    private fun doMult() {
        val a = memory[memory[instructionPointer + 1]]
        val b = memory[memory[instructionPointer + 2]]
        memory[memory[instructionPointer + 3]] = a * b
        instructionPointer += 4
    }

    fun runProgram(): Day2Machine {
        while(running) {
            when (val instruction = memory[instructionPointer]) {
                1 -> doAdd()
                2 -> doMult()
                99 -> running = false
                else -> throw BadMachine("Got instruction: $instruction in machine: $this")
            }
        }
        return this
    }

    class BadMachine(message: String): Exception(message)
}