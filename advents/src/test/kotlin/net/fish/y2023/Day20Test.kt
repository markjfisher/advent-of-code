package net.fish.y2023

import net.fish.resourcePath
import net.fish.y2023.Day20.Conjunction
import net.fish.y2023.Day20.FlipFlop
import net.fish.y2023.Day20.ModuleState.OFF
import net.fish.y2023.Day20.Pulse.LOW
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {
    @Test
    fun `can parse input to PulseCoordinator`() {
        val data = resourcePath("/2023/day20-test.txt")
        val pulseCoordinator = Day20.createCoordinator(data)
        assertThat(pulseCoordinator.modules).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "broadcaster" to Day20.Broadcast(listOf("a", "b", "c")),
                "a" to FlipFlop("a", listOf("b"), OFF),
                "b" to FlipFlop("b", listOf("c"), OFF),
                "c" to FlipFlop("c", listOf("inv"), OFF),
                "inv" to Conjunction("inv", listOf("a"), mutableMapOf("c" to LOW))
            )
        )
    }

    @Test
    fun `can parse input to PulseCoordinator with untyped`() {
        val data = resourcePath("/2023/day20-test2.txt")
        val pulseCoordinator = Day20.createCoordinator(data)
        assertThat(pulseCoordinator.modules).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "broadcaster" to Day20.Broadcast(listOf("a")),
                "a" to FlipFlop("a", listOf("inv", "con"), OFF),
                "b" to FlipFlop("b", listOf("con"), OFF),
                "inv" to Conjunction("inv", listOf("b"), mutableMapOf("a" to LOW)),
                "con" to Conjunction("con", listOf("output"), mutableMapOf("a" to LOW, "b" to LOW)),
                "output" to Day20.Untyped("output")
            )
        )
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day20-test.txt")
        val v = Day20.doPart1(data)
        assertThat(v).isEqualTo(32000000L)
    }

    @Test
    fun `can do part 1b`() {
        val data = resourcePath("/2023/day20-test2.txt")
        val v = Day20.doPart1(data)
        assertThat(v).isEqualTo(11687500L)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day20-test.txt")
        val v = Day20.doPart2(data)
        assertThat(v).isEqualTo(0)
    }
}