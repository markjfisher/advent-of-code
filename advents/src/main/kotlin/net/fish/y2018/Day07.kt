package net.fish.y2018

import net.fish.Day
import net.fish.collections.SortedMutableList
import net.fish.collections.sortedMutableListOf
import net.fish.resourceLines
import java.util.LinkedList

object Day07 : Day {
    private val stepExtractor by lazy { Regex("""Step (.) must be finished before step (.) can begin""") }
    // The fields of Step are mutable and are changed by part 1
    // private val data by lazy { toSteps(resourceLines(2018, 7)) }

    override fun part1() = doPart1(toSteps(resourceLines(2018, 7)))
    override fun part2() = doPart2(toSteps(resourceLines(2018, 7)))

    fun doPart1(steps: Map<Char, Step>): String = generateStepOrder(steps)
    fun doPart2(steps: Map<Char, Step>): Int = parallelWorkers(steps)

    data class Step(val name: Char, val requires: SortedMutableList<Char>, val requiredBy: SortedMutableList<Char>) {
        fun available() = requires.size == 0
    }

    fun toSteps(data: List<String>): Map<Char, Step> {
        return data.fold(mutableMapOf<Char, Step>()) { ac, line ->
            stepExtractor.find(line)?.destructured!!.let { (n, r) ->
                val name = n.first()
                val requires = r.first()
                val theStep = ac.getOrPut(name) { Step(name, sortedMutableListOf(), sortedMutableListOf())}
                val requiresStep = ac.getOrPut(requires) { Step(requires, sortedMutableListOf(), sortedMutableListOf())}
                theStep.requiredBy.add(requiresStep.name) // += is a reassignment, so doesn't work here, as the variable is a val. also it's not implemented
                requiresStep.requires.add(theStep.name)
            }
            ac
        }.toMap()
    }

    fun generateStepOrder(steps: Map<Char, Step>): String {
        fun process(steps: MutableMap<Char, Step>, currentOrder: String): String {
            if (steps.isEmpty()) return currentOrder
            // Look for available steps, sort them by name and pick first
            val availableStep = steps.filterValues { it.available() }.keys.minOf { it }.let { steps[it] ?: throw Exception("No available step found") }

            // remove the available step from the map, and any steps that require it
            steps.remove(availableStep.name)
            steps.values.forEach { s ->
                s.requires.remove(availableStep.name)
            }
            // recurse away until the steps are done
            return process(steps, currentOrder + availableStep.name)
        }
        return process(steps.toMutableMap(), "")
    }

    data class Worker(val name: String, var availableAt: Int, var workingOn: Char)

    fun parallelWorkers(steps: Map<Char, Step>, maxWorkers: Int = 5, processDelay: Int = 60): Int {
        // steps.values.forEach { println(it) }
        fun timeToProcess(step: Step): Int = processDelay + (step.name - 'A' + 1)
        fun availableSteps(steps: Map<Char, Step>, count: Int): List<Step> {
            return steps.filterValues { it.available() }.toSortedMap().values.take(count)
        }
        val availableWorkers = LinkedList<Worker>().apply {
            (0 until maxWorkers).forEach {
                add(Worker("w${it + 1}", 0, '!'))
            }
        }
        val working = LinkedList<Worker>()
        var currentMinute = 0
        val newSteps = steps.toMutableMap()
        while (newSteps.isNotEmpty() || working.isNotEmpty()) {
            // are there any workers in the working set that have finished a step, and are now available?
            val finishedWorkers = working.filter { it.availableAt == currentMinute }
            if (finishedWorkers.isNotEmpty()) {
                working.removeAll(finishedWorkers.toSet())
                availableWorkers.addAll(finishedWorkers)
                finishedWorkers.forEach { worker ->
                    // println("worker $worker finishing at $currentMinute")
                    // remove the worker's step from the steps, so it unlocks other steps at the right time
                    newSteps.values.forEach { s ->
                        s.requires.remove(worker.workingOn)
                    }
                }
            }

            // get the available steps, distribute to workers
            val availableSteps = availableSteps(newSteps, availableWorkers.size)
            if (availableSteps.isNotEmpty()) {
                // println("available steps: ${availableSteps.map { it.name }.joinToString(", ")}")
                availableSteps.forEach { step ->
                    val worker = availableWorkers.remove()
                    worker.availableAt = currentMinute + timeToProcess(step)
                    worker.workingOn = step.name
                    working.add(worker)
                    newSteps.remove(step.name)
                    // println("worker $worker, working on $step at $currentMinute until ${worker.availableAt}")
                    // we don't remove the step from anyone depending on it until the worker is done with it
                }
            }
            // increment the time
            currentMinute++
        }

        // we advanced 1 before we tested the loop, so remove it
        return currentMinute - 1
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}