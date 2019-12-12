package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day6BaseTest {
    val data = Helpers.loadOrbitData("/orbit-data.txt")
    val comObj = Day6Object("COM")
    val bObj = Day6Object("B")
    val cObj = Day6Object("C")
    val dObj = Day6Object("D")
    val eObj = Day6Object("E")
    val fObj = Day6Object("F")
    val gObj = Day6Object("G")
    val hObj = Day6Object("H")
    val iObj = Day6Object("I")
    val jObj = Day6Object("J")
    val kObj = Day6Object("K")
    val lObj = Day6Object("L")

    val graph = Day6Base().createGraph(data)

    @Test
    fun `graph creation`() {
        assertThat(graph.objects).containsExactlyInAnyOrder(comObj, bObj, cObj, dObj, eObj, fObj, gObj, hObj, iObj, jObj, kObj, lObj)
        assertThat(graph.orbits).containsExactlyInAnyOrder(
            Day6Orbit(body = bObj, parent = comObj),
            Day6Orbit(body = gObj, parent = bObj),
            Day6Orbit(body = hObj, parent = gObj),
            Day6Orbit(body = cObj, parent = bObj),
            Day6Orbit(body = dObj, parent = cObj),
            Day6Orbit(body = iObj, parent = dObj),
            Day6Orbit(body = eObj, parent = dObj),
            Day6Orbit(body = jObj, parent = eObj),
            Day6Orbit(body = kObj, parent = jObj),
            Day6Orbit(body = lObj, parent = kObj),
            Day6Orbit(body = fObj, parent = eObj)
        )
    }

    @Test
    fun `orbit counts`() {
        assertThat(graph.parentCount(comObj)).isEqualTo(0)
        assertThat(graph.parentCount(bObj)).isEqualTo(1)
        assertThat(graph.parentCount(cObj)).isEqualTo(2)
        assertThat(graph.parentCount(dObj)).isEqualTo(3)
        assertThat(graph.parentCount(eObj)).isEqualTo(4)
        assertThat(graph.parentCount(fObj)).isEqualTo(5)
        assertThat(graph.parentCount(gObj)).isEqualTo(2)
        assertThat(graph.parentCount(hObj)).isEqualTo(3)
        assertThat(graph.parentCount(iObj)).isEqualTo(4)
        assertThat(graph.parentCount(jObj)).isEqualTo(5)
        assertThat(graph.parentCount(kObj)).isEqualTo(6)
        assertThat(graph.parentCount(lObj)).isEqualTo(7)
        assertThat(graph.orbitCounts()).isEqualTo(42)
    }

    @Test
    fun `count nodes between`() {
        assertThat(graph.countNodesBetween("I", "C")).isEqualTo(2)
        assertThat(graph.countNodesBetween("K", "B")).isEqualTo(5)
        assertThat(graph.countNodesBetween("K", "H")).isEqualTo(0)
        assertThat(graph.countNodesBetween("X", "H")).isEqualTo(0)
        assertThat(graph.countNodesBetween("K", "X")).isEqualTo(0)
    }

    @Test
    fun `common parent`() {
        assertThat(graph.commonParent("K", "I")?.name).isEqualTo("D")
        assertThat(graph.commonParent("L", "H")?.name).isEqualTo("B")
        assertThat(graph.commonParent("X", "H")).isNull()
        assertThat(graph.commonParent("H", "X")).isNull()
    }

    @Test
    fun `traverse nodes test`() {
        assertThat(graph.traverseCount("H", "L")).isEqualTo(6)
        assertThat(graph.traverseCount("L", "H")).isEqualTo(6)
        assertThat(graph.traverseCount("F", "G")).isEqualTo(3)
        assertThat(graph.traverseCount("G", "F")).isEqualTo(3)
        assertThat(graph.traverseCount("L", "COM")).isEqualTo(0)
        assertThat(graph.traverseCount("COM", "L")).isEqualTo(0)
        assertThat(graph.traverseCount("L", "L")).isEqualTo(0)
    }
}