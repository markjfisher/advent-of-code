package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {

    @Test
    fun `can parse caves`() {
        val t1data = resourcePath("/2021/day12-1-test.txt")
        val caves = Day12.toCaves(t1data)
        assertThat(caves.keys).containsExactlyInAnyOrder("start", "A", "b", "c", "d", "end")

        assertThat(caves["start"]!!.connected).containsExactlyInAnyOrder("A", "b")
        assertThat(caves["A"]!!.connected).containsExactlyInAnyOrder("start", "b", "c", "end")
        assertThat(caves["b"]!!.connected).containsExactlyInAnyOrder("start", "A", "d", "end")
        assertThat(caves["c"]!!.connected).containsExactlyInAnyOrder("A")
        assertThat(caves["d"]!!.connected).containsExactlyInAnyOrder("b")
        assertThat(caves["end"]!!.connected).containsExactlyInAnyOrder("A", "b")
    }

    @Test
    fun `can traverse test sample 1 caves part 1`() {
        val t1data = resourcePath("/2021/day12-1-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverse("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(10)
    }
    @Test
    fun `can traverse test sample 1 caves part 2`() {
        val t1data = resourcePath("/2021/day12-1-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverseWithRevist("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(36)
    }

    @Test
    fun `can traverse test sample 2 caves part 1`() {
        val t1data = resourcePath("/2021/day12-2-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverse("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(19)
    }

    @Test
    fun `can traverse test sample 2 caves part 2`() {
        val t1data = resourcePath("/2021/day12-2-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverseWithRevist("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(103)
    }

    @Test
    fun `can traverse test sample 3 caves part 1`() {
        val t1data = resourcePath("/2021/day12-3-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverse("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(226)
    }

    @Test
    fun `can traverse test sample 3 caves part 2`() {
        val t1data = resourcePath("/2021/day12-3-test.txt")
        val caves = Day12.toCaves(t1data)
        val allPaths = Day12.traverseWithRevist("start", "end", caves)
        assertThat(allPaths.size).isEqualTo(3509)
    }

}