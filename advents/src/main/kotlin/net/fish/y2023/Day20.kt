package net.fish.y2023

import com.marcinmoskala.math.product
import net.fish.Day
import net.fish.maths.lcm
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

    fun doPart2(data: List<String>): Long {
        val pulseCoordinator = createCoordinator(data)
        // need to find the cycle time of all the rx inputs when they go HIGH, so they can send a low pulse.
        // Inspecting the data, we have:
        //  &bm, &cl, &tn, &dr -> &vr -> rx
        // so we look at rx's parent's inputs, find the HIGH cycle times of those, and then LCD them.
        // thus we can keep looping until we have cycle times for ALL Conjuctions (except vr), and break out, then find the right ones for rx (or vr)
        val rxInput = (pulseCoordinator.modules["rx"] as Untyped).input
        val conjuctionsToMonitor = (pulseCoordinator.modules[rxInput]!! as Conjunction).inputs.keys // e.g. setOf("bm", "cl", ...)
        // println("looking for conjuctions of $conjuctionsToMonitor")

        while (true) {
            pulseCoordinator.start(rxInput)
            val overallCycleTimes = conjuctionsToMonitor.map { (pulseCoordinator.modules[it]!! as Conjunction).cycleTime }
            if (overallCycleTimes.all { it != 0L }) {
                return overallCycleTimes.reduce { ac, f -> lcm(ac, f) }
            }
        }
    }

    sealed class Module(open val name: String, open val destinations: List<String>) {
        abstract fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>>
    }

    enum class ModuleState {
        OFF, ON;
        operator fun not(): ModuleState = if (this == OFF) ON else OFF
    }

    enum class Pulse {
        LOW, HIGH;
        operator fun not(): Pulse = if (this == LOW) HIGH else LOW
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

    data class Conjunction(override val name: String, override val destinations: List<String>, val inputs: MutableMap<String, Pulse>, var cycleTime: Long = 0L) : Module(name, destinations) {
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

    data class Untyped(override val name: String, val input: String): Module(name, mutableListOf()) {
        override fun process(pulse: Pulse, from: String): List<Pair<String, Pulse>> = emptyList()
    }

    // <from, to, pulse>
    data class PulseCoordinator(val queue: ArrayDeque<Triple<String, String, Pulse>> = ArrayDeque(), val modules: Map<String, Module>) {
        var lowPulseCount: Long = 0L
        var highPulseCount: Long = 0L
        var currentCycle = 0L

        fun clear() {
            lowPulseCount = 0L
            highPulseCount = 0L
        }

        fun start(target: String = "") {
            queue += Triple("button", "broadcaster", LOW)
            currentCycle++
            while (queue.isNotEmpty()) {
                val triple = queue.removeFirst()
                when (triple.third) {
                    LOW -> lowPulseCount++
                    HIGH -> highPulseCount++
                }
                val module = modules[triple.second] ?: throw Exception("Unknown module in pair: $triple")
                if (triple.second == target && triple.third == HIGH) {
                    // println("triple: $triple")
                    val fromModule = modules[triple.first]!! as Conjunction
                    if (fromModule.cycleTime == 0L) {
                        fromModule.cycleTime = currentCycle
                        // println("set cycle time for $fromModule")
                    }
                }
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
        untypedNames.forEach { name ->
            val modulesWithThisDestination = moduleMap.values.filter { it.destinations.contains(name) }.map { it.name }
            if (modulesWithThisDestination.size > 1) throw Exception("Only expecting untyped to be from 1 module")
            moduleMap[name] = Untyped(name, modulesWithThisDestination.first())
        }

        return PulseCoordinator(modules = moduleMap.toMap())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}