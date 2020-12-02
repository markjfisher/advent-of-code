package net.fish.y2019

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {
    val data = resourcePath("/2019/orbit-data.txt")
    val comObj = Body("COM")
    val bObj = Body("B")
    val cObj = Body("C")
    val dObj = Body("D")
    val eObj = Body("E")
    val fObj = Body("F")
    val gObj = Body("G")
    val hObj = Body("H")
    val iObj = Body("I")
    val jObj = Body("J")
    val kObj = Body("K")
    val lObj = Body("L")

    val graph = Day06.createGraph(data)

    @Test
    fun `graph creation`() {
        assertThat(graph.objects).containsExactlyInAnyOrder(comObj, bObj, cObj, dObj, eObj, fObj, gObj, hObj, iObj, jObj, kObj, lObj)
        assertThat(graph.orbits).containsExactlyInAnyOrder(
            Orbit(body = bObj, parent = comObj),
            Orbit(body = gObj, parent = bObj),
            Orbit(body = hObj, parent = gObj),
            Orbit(body = cObj, parent = bObj),
            Orbit(body = dObj, parent = cObj),
            Orbit(body = iObj, parent = dObj),
            Orbit(body = eObj, parent = dObj),
            Orbit(body = jObj, parent = eObj),
            Orbit(body = kObj, parent = jObj),
            Orbit(body = lObj, parent = kObj),
            Orbit(body = fObj, parent = eObj)
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