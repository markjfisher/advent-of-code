package net.fish.y2023

import net.fish.resourcePath
import net.fish.y2023.Day22.SandBlock
import org.assertj.core.api.Assertions.assertThat
import org.joml.Vector3i
import org.junit.jupiter.api.Test

class Day22Test {
    @Test
    fun `can parse sand grid`() {
        val data = resourcePath("/2023/day22-test.txt")
        val sandGrid = Day22.generateSandGrid(data)
        assertThat(sandGrid.blocks).containsExactly(
            SandBlock(0, listOf(Vector3i(1,0,1), Vector3i(1,1,1), Vector3i(1,2,1))),
            SandBlock(1, listOf(Vector3i(0,0,2), Vector3i(1,0,2), Vector3i(2,0,2))),
            SandBlock(2, listOf(Vector3i(0,2,3), Vector3i(1,2,3), Vector3i(2,2,3))),
            SandBlock(3, listOf(Vector3i(0,0,4), Vector3i(0,1,4), Vector3i(0,2,4))),
            SandBlock(4, listOf(Vector3i(2,0,5), Vector3i(2,1,5), Vector3i(2,2,5))),
            SandBlock(5, listOf(Vector3i(0,1,6), Vector3i(1,1,6), Vector3i(2,1,6))),
            SandBlock(6, listOf(Vector3i(1,1,8), Vector3i(1,1,9))),
        )
    }

    @Test
    fun `can drop blocks`() {
        val data = resourcePath("/2023/day22-test.txt")
        val sandGrid = Day22.generateSandGrid(data)
        sandGrid.drop()
        assertThat(sandGrid.blocks).containsExactly(
            SandBlock(0, listOf(Vector3i(1,0,1), Vector3i(1,1,1), Vector3i(1,2,1))),
            SandBlock(1, listOf(Vector3i(0,0,2), Vector3i(1,0,2), Vector3i(2,0,2))),
            SandBlock(2, listOf(Vector3i(0,2,2), Vector3i(1,2,2), Vector3i(2,2,2))),
            SandBlock(3, listOf(Vector3i(0,0,3), Vector3i(0,1,3), Vector3i(0,2,3))),
            SandBlock(4, listOf(Vector3i(2,0,3), Vector3i(2,1,3), Vector3i(2,2,3))),
            SandBlock(5, listOf(Vector3i(0,1,4), Vector3i(1,1,4), Vector3i(2,1,4))),
            SandBlock(6, listOf(Vector3i(1,1,5), Vector3i(1,1,6))),
        )
    }

    @Test
    fun `can do part 1`() {
        val data = resourcePath("/2023/day22-test.txt")
        val v = Day22.doPart1(data)
        assertThat(v).isEqualTo(5)
    }

    @Test
    fun `can do part 2`() {
        val data = resourcePath("/2023/day22-test.txt")
        val v = Day22.doPart2(data)
        assertThat(v).isEqualTo(0)
    }

}