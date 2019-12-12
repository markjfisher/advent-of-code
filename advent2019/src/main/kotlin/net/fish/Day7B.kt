package net.fish

import java.lang.Long.max

/*
It's no good - in this configuration, the amplifiers can't generate a large enough output signal to produce the thrust
you'll need. The Elves quickly talk you through rewiring the amplifiers into a feedback loop:

      O-------O  O-------O  O-------O  O-------O  O-------O
0 -+->| Amp A |->| Amp B |->| Amp C |->| Amp D |->| Amp E |-.
   |  O-------O  O-------O  O-------O  O-------O  O-------O |
   |                                                        |
   '--------------------------------------------------------+
                                                            |
                                                            v
                                                     (to thrusters)
Most of the amplifiers are connected as they were before; amplifier A's output is connected to amplifier B's input,
and so on. However, the output from amplifier E is now connected into amplifier A's input. This creates the feedback
loop: the signal will be sent through the amplifiers many times.

In feedback loop mode, the amplifiers need totally different phase settings: integers from 5 to 9, again each used
exactly once. These settings will cause the Amplifier Controller Software to repeatedly take input and produce output
many times before halting. Provide each amplifier its phase setting at its first input instruction; all further
input/output instructions are for signals.

Don't restart the Amplifier Controller Software on any amplifier during this process. Each one should continue
receiving and sending signals until it halts.

All signals sent or received in this process will be between pairs of amplifiers except the very first signal and the
very last signal. To start the process, a 0 signal is sent to amplifier A's input exactly once.

Eventually, the software on the amplifiers will halt after they have processed the final loop. When this happens, the
last output signal from amplifier E is sent to the thrusters. Your job is to find the largest output signal that can
be sent to the thrusters using the new phase settings and feedback loop arrangement.

Here are some example programs:

Max thruster signal 139629729 (from phase setting sequence 9,8,7,6,5):
3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5

Max thruster signal 18216 (from phase setting sequence 9,7,8,5,6):
3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,
-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,
53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10

Try every combination of the new phase settings on the amplifier feedback loop. What is the highest signal that can be sent to the thrusters?
*/

class Day7B {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val day7 = Day7B()
            val memoryInitialState = Helpers.loadResourceCSVAsListOfLongs("/day7-input.txt")
            val phaseSequences = permute(listOf(5L, 6L, 7L, 8L, 9L))
            val highestThrust = phaseSequences.fold(0L) { highestThrust, sequence ->
                val thrust = day7.calculateThrust(memoryInitialState, sequence)
                max(highestThrust, thrust)
            }
            println("Highest thrust: $highestThrust") // 70602018
        }
    }

    fun calculateThrust(memoryInitialState: List<Long>, sequence: List<Long>): Long {
        var machineA = AdventComputer(memory = memoryInitialState.toMutableList(), inputs = listOf(sequence[0], 0)).runProgram()
        var machineB = AdventComputer(memory = memoryInitialState.toMutableList(), inputs = listOf(sequence[1], machineA.takeOutput())).runProgram()
        var machineC = AdventComputer(memory = memoryInitialState.toMutableList(), inputs = listOf(sequence[2], machineB.takeOutput())).runProgram()
        var machineD = AdventComputer(memory = memoryInitialState.toMutableList(), inputs = listOf(sequence[3], machineC.takeOutput())).runProgram()
        var machineE = AdventComputer(memory = memoryInitialState.toMutableList(), inputs = listOf(sequence[4], machineD.takeOutput())).runProgram()
        var stillRunning = listOf(machineA, machineB, machineC, machineD, machineE).any { it.running }
        while(stillRunning) {
            machineA.inputs = listOf(machineE.takeOutput())
            machineA = machineA.runProgram()

            machineB.inputs = listOf(machineA.takeOutput())
            machineB = machineB.runProgram()

            machineC.inputs = listOf(machineB.takeOutput())
            machineC = machineC.runProgram()

            machineD.inputs = listOf(machineC.takeOutput())
            machineD = machineD.runProgram()

            machineE.inputs = listOf(machineD.takeOutput())
            machineE = machineE.runProgram()

            stillRunning = listOf(machineA, machineB, machineC, machineD, machineE).any { it.running }
        }
        return machineE.outputs.last()
    }
}