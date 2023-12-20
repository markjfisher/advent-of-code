package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines
import net.fish.y2023.Day20.ModuleState.OFF
import net.fish.y2023.Day20.ModuleState.ON
import net.fish.y2023.Day20.Pulse.HIGH
import net.fish.y2023.Day20.Pulse.LOW

object Day20 : Day {
    private val data by lazy { resourceLines(2023, 20) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        val pulseCoordinator = createCoordinator(data)
        repeat((0 until 1000).count()) { pulseCoordinator.start() }
        return pulseCoordinator.lowPulseCount * pulseCoordinator.highPulseCount
    }

    fun doPart2(data: List<String>): Int = data.size

    sealed class Module(open val name: String, open val destinations: List<String>) {
        abstract fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>>
    }

    enum class ModuleState {
        OFF, ON;

        operator fun not(): ModuleState {
            return when (this) {
                OFF -> ON
                ON -> OFF
            }
        }
    }

    enum class Pulse {
        LOW, HIGH;

        operator fun not(): Pulse {
            return when (this) {
                LOW -> HIGH
                HIGH -> LOW
            }
        }
    }

    data class FlipFlop(override val name: String, override val destinations: List<String>, var state: ModuleState = OFF) : Module(name, destinations) {
        override fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>> {
            val toProcess = mutableListOf<Pair<String, Pulse>>()
            if (pulse == LOW) {
                state = !state
                toProcess += when (state) {
                    ON -> destinations.map { Pair(it, HIGH) }
                    OFF -> destinations.map { Pair(it, LOW) }
                }
            }
            return toProcess
        }
    }

    data class Conjunction(override val name: String, override val destinations: List<String>, val inputs: MutableMap<String, Pulse>) : Module(name, destinations) {
        override fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>> {
            val toProcess = mutableListOf<Pair<String, Pulse>>()
            inputs[from] = pulse
            toProcess += when (inputs.values.all { it == HIGH }) {
                true -> destinations.map { Pair(it, LOW) }
                false -> destinations.map { Pair(it, HIGH) }
            }
            return toProcess
        }
    }

    data class Broadcast(override val destinations: List<String>) : Module("broadcaster", destinations) {
        override fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>> {
            // send the pulse to every destination
            return destinations.map { Pair(it, pulse) }
        }
    }

    data class Untyped(override val name: String): Module(name, mutableListOf()) {
        override fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>> {
            return emptyList()
        }
    }

    // <from, to, pulse>
    data class PulseCoordinator(val queue: ArrayDeque<Triple<String, String, Pulse>> = ArrayDeque(), val modules: Map<String, Module>) {
        var lowPulseCount: Long = 0L
        var highPulseCount: Long = 0L
        val allPulsesSent = mutableListOf<Triple<String, String, Pulse>>()

        fun clear() {
            lowPulseCount = 0L
            highPulseCount = 0L
            allPulsesSent.clear()
        }

        fun start() {
            queue += Triple("button", "broadcaster", LOW)
            while (queue.isNotEmpty()) {
                val triple = queue.removeFirst()
                allPulsesSent += triple
                when (triple.third) {
                    LOW -> lowPulseCount++
                    HIGH -> highPulseCount++
                }
                val module = modules[triple.second] ?: throw Exception("Unknown module in pair: $triple")
                val nextPulsePairs = module.process(triple.third, triple.first)
                // the module we just spoke to gave us some pulses to process, they need to go on top of the queue in reverse order so that when they are removed from stack, we get correct order
                nextPulsePairs.reversed().forEach { pair ->
                    queue += Triple(module.name, pair.first, pair.second)
                }
            }
        }
    }

    fun createCoordinator(data: List<String>): PulseCoordinator {
        val allDestinations = mutableSetOf<String>()
        val allModuleNames = mutableSetOf<String>()
        val moduleMap = data.associate { line ->
            val assignSplit = line.split(" -> ")
            val moduleNameAndType = assignSplit[0]
            val moduleType = moduleNameAndType.first()
            val moduleName = if (moduleNameAndType.startsWith('b')) "broadcaster" else moduleNameAndType.drop(1)
            allModuleNames.add(moduleName)
            val destinationNames = assignSplit[1].split(", ")
            allDestinations.addAll(destinationNames)
            moduleName to when (moduleType) {
                'b' -> Broadcast(destinationNames)
                '%' -> FlipFlop(moduleName, destinationNames)
                '&' -> Conjunction(moduleName, destinationNames, mutableMapOf())
                else -> throw Exception("Unknown module name: $moduleName")
            }
        }.toMutableMap()
        // add inputs to every Conjunction
        val conjunctionNames = moduleMap.values.filterIsInstance<Conjunction>().map { it.name }
        conjunctionNames.forEach { cname ->
            val modulesWithThisConjuctionAsDestination = moduleMap.values.filter { it.destinations.contains(cname) }.map { it.name }
            val conjunction = moduleMap[cname] as Conjunction
            conjunction.inputs += modulesWithThisConjuctionAsDestination.associateWith { _ -> LOW }
        }

        // add any untyped (destinations that are not in the current modules)
        val untypedNames = allDestinations - allModuleNames
        untypedNames.forEach { name ->  moduleMap[name] = Untyped(name) }

        return PulseCoordinator(modules = moduleMap.toMap())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}