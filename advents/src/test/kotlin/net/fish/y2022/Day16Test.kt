package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day16Test {
    @Test
    fun `can do part 1`() {
        val valves = Day16.createNetwork(resourcePath("/2022/day16-test.txt"))
        assertThat(Day16.dfs(valves, 0, "AA", emptySet(), 0, mutableSetOf(0), 30, false)).isEqualTo(1651)
    }

    @Test
    fun `can do part 2`() {
        val valves = Day16.createNetwork(resourcePath("/2022/day16-test.txt"))
        assertThat(Day16.dfs(valves, 0, "AA", emptySet(), 0, mutableSetOf(0), 26, true)).isEqualTo(1707)
    }

    @Test
    fun `can read input`() {
        assertThat(Day16.toValves(resourcePath("/2022/day16-test.txt"))).containsExactlyEntriesOf(mapOf(
            "AA" to Day16.Valve("AA", 0, listOf("DD", "II", "BB")),
            "BB" to Day16.Valve("BB", 13, listOf("CC", "AA")),
            "CC" to Day16.Valve("CC", 2, listOf("DD", "BB")),
            "DD" to Day16.Valve("DD", 20, listOf("CC", "AA", "EE")),
            "EE" to Day16.Valve("EE", 3, listOf("FF", "DD")),
            "FF" to Day16.Valve("FF", 0, listOf("EE", "GG")),
            "GG" to Day16.Valve("GG", 0, listOf("FF", "HH")),
            "HH" to Day16.Valve("HH", 22, listOf("GG")),
            "II" to Day16.Valve("II", 0, listOf("AA", "JJ")),
            "JJ" to Day16.Valve("JJ", 21, listOf("II")),
        ))
    }

    @Test
    fun `can get distances between valves`() {
        val valves = Day16.toValves(resourcePath("/2022/day16-test.txt"))
        Day16.calculateDistances(valves)
        // show full map output
        assertThat(valves["AA"]!!.distances).containsExactlyEntriesOf(mapOf("AA" to 2, "BB" to 1, "CC" to 2, "DD" to 1, "EE" to 2, "FF" to 3, "GG" to 4, "HH" to 5, "II" to 1, "JJ" to 2))
        // save some typing
        assertThat(valves["BB"]!!.distances.toSortedMap().values).containsExactly(1, 2, 1, 2, 3, 4, 5, 6, 2, 3)
        assertThat(valves["CC"]!!.distances.toSortedMap().values).containsExactly(2, 1, 2, 1, 2, 3, 4, 5, 3, 4)
        assertThat(valves["DD"]!!.distances.toSortedMap().values).containsExactly(1, 2, 1, 2, 1, 2, 3, 4, 2, 3)
        assertThat(valves["EE"]!!.distances.toSortedMap().values).containsExactly(2, 3, 2, 1, 2, 1, 2, 3, 3, 4)
        assertThat(valves["FF"]!!.distances.toSortedMap().values).containsExactly(3, 4, 3, 2, 1, 2, 1, 2, 4, 5)
        assertThat(valves["GG"]!!.distances.toSortedMap().values).containsExactly(4, 5, 4, 3, 2, 1, 2, 1, 5, 6)
        assertThat(valves["HH"]!!.distances.toSortedMap().values).containsExactly(5, 6, 5, 4, 3, 2, 1, 2, 6, 7)
        assertThat(valves["II"]!!.distances.toSortedMap().values).containsExactly(1, 2, 3, 2, 3, 4, 5, 6, 2, 1)
        assertThat(valves["JJ"]!!.distances.toSortedMap().values).containsExactly(2, 3, 4, 3, 4, 5, 6, 7, 1, 2)
    }

    @Test
    fun `can remove 0 flow distances from valves`() {
        val valves = Day16.toValves(resourcePath("/2022/day16-test.txt"))
        Day16.calculateDistances(valves)
        Day16.removeZeroFlowValves(valves)
        // show full map output
        assertThat(valves["AA"]!!.distances).containsExactlyEntriesOf(mapOf("BB" to 1, "CC" to 2, "DD" to 1, "EE" to 2, "HH" to 5, "JJ" to 2))
        // save some typing
        assertThat(valves["BB"]!!.distances.toSortedMap().values).containsExactly(2, 1, 2, 3, 6, 3)
        assertThat(valves["CC"]!!.distances.toSortedMap().values).containsExactly(1, 2, 1, 2, 5, 4)
        assertThat(valves["DD"]!!.distances.toSortedMap().values).containsExactly(2, 1, 2, 1, 4, 3)
        assertThat(valves["EE"]!!.distances.toSortedMap().values).containsExactly(3, 2, 1, 2, 3, 4)
        assertThat(valves["FF"]!!.distances.toSortedMap().values).containsExactly(4, 3, 2, 1, 2, 5)
        assertThat(valves["GG"]!!.distances.toSortedMap().values).containsExactly(5, 4, 3, 2, 1, 6)
        assertThat(valves["HH"]!!.distances.toSortedMap().values).containsExactly(6, 5, 4, 3, 2, 7)
        assertThat(valves["II"]!!.distances.toSortedMap().values).containsExactly(2, 3, 2, 3, 6, 1)
        assertThat(valves["JJ"]!!.distances.toSortedMap().values).containsExactly(3, 4, 3, 4, 7, 2)
    }
}