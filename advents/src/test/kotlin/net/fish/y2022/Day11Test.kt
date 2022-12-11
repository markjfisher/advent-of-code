package net.fish.y2022

import net.fish.resourceStrings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day11Test {
    @Test
    fun `can parse simbiant data`() {
        assertThat(Day11.toSimbiants(resourceStrings("/2022/day11-test.txt"))).containsExactlyEntriesOf(mapOf(
            0 to Day11.Simbiant(mutableListOf(79, 98), Day11.MultSimbOp(19), 23, 2, 3, 0),
            1 to Day11.Simbiant(mutableListOf(54, 65, 75, 74), Day11.AddSimbOp(6), 19, 2, 0, 0),
            2 to Day11.Simbiant(mutableListOf(79, 60, 97), Day11.SqSimbOp, 13, 1, 3, 0),
            3 to Day11.Simbiant(mutableListOf(74), Day11.AddSimbOp(3), 17, 0, 1, 0)
        ))
    }

    @Test
    fun `can run simulator 1 rounds`() {
        val simulator = Day11.SimbiantSimulator(Day11.toSimbiants(resourceStrings("/2022/day11-test.txt")))
        simulator.round(1) { level, _ -> level / 3L }
        assertThat(simulator.simbiants).containsExactlyEntriesOf(mapOf(
            0 to Day11.Simbiant(mutableListOf(20, 23, 27, 26), Day11.MultSimbOp(19), 23, 2, 3, 2),
            1 to Day11.Simbiant(mutableListOf(2080, 25, 167, 207, 401, 1046), Day11.AddSimbOp(6), 19, 2, 0, 4),
            2 to Day11.Simbiant(mutableListOf(), Day11.SqSimbOp, 13, 1, 3, 3),
            3 to Day11.Simbiant(mutableListOf(), Day11.AddSimbOp(3), 17, 0, 1, 5)
        ))
    }

    @Test
    fun `can run simulator 20 rounds and do part 1`() {
        val simulator = Day11.SimbiantSimulator(Day11.toSimbiants(resourceStrings("/2022/day11-test.txt")))
        simulator.round(20) { level, _ -> level / 3L }
        assertThat(simulator.simbiants).containsExactlyEntriesOf(mapOf(
            0 to Day11.Simbiant(mutableListOf(10, 12, 14, 26, 34), Day11.MultSimbOp(19), 23, 2, 3, 101),
            1 to Day11.Simbiant(mutableListOf(245, 93, 53, 199, 115), Day11.AddSimbOp(6), 19, 2, 0, 95),
            2 to Day11.Simbiant(mutableListOf(), Day11.SqSimbOp, 13, 1, 3, 7),
            3 to Day11.Simbiant(mutableListOf(), Day11.AddSimbOp(3), 17, 0, 1, 105)
        ))
        assertThat(simulator.monkeyBusiness()).isEqualTo(10605L)
    }

    @Test
    fun `can run simulator for part 2`() {
        val simulator = Day11.SimbiantSimulator(Day11.toSimbiants(resourceStrings("/2022/day11-test.txt")))
        val p2Worry: (Long, Int) -> Long = { level, modulus -> level % modulus }

        simulator.round(1, p2Worry)
        assertThat(simulator.simbiants.entries.sortedBy { it.key }.map { it.value.inspectedItems }).containsExactly(2, 4, 3, 6)
        simulator.round(19, p2Worry)
        assertThat(simulator.simbiants.entries.sortedBy { it.key }.map { it.value.inspectedItems }).containsExactly(99, 97, 8, 103)
        simulator.round(1000 - 20, p2Worry)
        assertThat(simulator.simbiants.entries.sortedBy { it.key }.map { it.value.inspectedItems }).containsExactly(5204, 4792, 199, 5192)
        simulator.round(10_000 - 1_000, p2Worry)
        assertThat(simulator.monkeyBusiness()).isEqualTo(2713310158L)
    }

}