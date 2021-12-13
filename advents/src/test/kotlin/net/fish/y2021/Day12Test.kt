package net.fish.y2021

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day12Test {

//    @Test
    fun `can parse caves`() {
        val t1data = resourcePath("/2021/day12-1-test.txt")
        val start = Day12.parseCaves(t1data)
        assertThat(start.connected).hasSize(2)

//        assertThat(start["start"]!!.connected).containsExactlyInAnyOrder("A", "b")
//        assertThat(start["A"]!!.connected).containsExactlyInAnyOrder("start", "b", "c", "end")
//        assertThat(start["b"]!!.connected).containsExactlyInAnyOrder("start", "A", "d", "end")
//        assertThat(start["c"]!!.connected).containsExactlyInAnyOrder("A")
//        assertThat(start["d"]!!.connected).containsExactlyInAnyOrder("b")
//        assertThat(start["end"]!!.connected).containsExactlyInAnyOrder("A", "b")
    }

//    @Test
    fun `can traverse test sample 1 caves part 1`() {
        val t1data = resourcePath("/2021/day12-1-test.txt")
        val start = Day12.parseCaves(t1data)
        val allPaths = Day12.traverse(start, Day12::singleVisitSmallCaves)
        assertThat(allPaths.size).isEqualTo(10)
    }

//    @Test
    fun `can traverse test sample 2 caves part 1`() {
        val t1data = resourcePath("/2021/day12-2-test.txt")
        val start = Day12.parseCaves(t1data)
        val allPaths = Day12.traverse(start, Day12::singleVisitSmallCaves)
        assertThat(allPaths.size).isEqualTo(19)
    }

//    @Test
    fun `can traverse test sample 3 caves part 1`() {
        val t1data = resourcePath("/2021/day12-3-test.txt")
        val start = Day12.parseCaves(t1data)
        val allPaths = Day12.traverse(start, Day12::singleVisitSmallCaves)
        assertThat(allPaths.size).isEqualTo(226)
    }

}