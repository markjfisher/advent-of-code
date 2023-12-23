package net.fish.y2023

import net.fish.geometry.Point3D
import net.fish.resourcePath
import net.fish.y2023.Day22.SandBlock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day22Test {
    @Test
    fun `can parse sand grid`() {
        val data = resourcePath("/2023/day22-test.txt")
        val sandGrid = Day22.generateSandGrid(data)
        assertThat(sandGrid.blocks).containsExactly(
            SandBlock(id = 0, start = Point3D(x = 1, y = 0, z = 1), end = Point3D(x = 1, y = 2, z = 1)),
            SandBlock(id = 1, start = Point3D(x = 0, y = 0, z = 2), end = Point3D(x = 2, y = 0, z = 2)),
            SandBlock(id = 2, start = Point3D(x = 0, y = 2, z = 3), end = Point3D(x = 2, y = 2, z = 3)),
            SandBlock(id = 3, start = Point3D(x = 0, y = 0, z = 4), end = Point3D(x = 0, y = 2, z = 4)),
            SandBlock(id = 4, start = Point3D(x = 2, y = 0, z = 5), end = Point3D(x = 2, y = 2, z = 5)),
            SandBlock(id = 5, start = Point3D(x = 0, y = 1, z = 6), end = Point3D(x = 2, y = 1, z = 6)),
            SandBlock(id = 6, start = Point3D(x = 1, y = 1, z = 8), end = Point3D(x = 1, y = 1, z = 9))
        )
    }

    @Test
    fun `can drop blocks`() {
        val data = resourcePath("/2023/day22-test.txt")
        val sandGrid = Day22.generateSandGrid(data)
        val sb0 = SandBlock(id = 0, start = Point3D(x = 1, y = 0, z = 1), end = Point3D(x = 1, y = 2, z = 1))
        val sb1 = SandBlock(id = 1, start = Point3D(x = 0, y = 0, z = 2), end = Point3D(x = 2, y = 0, z = 2))
        val sb2 = SandBlock(id = 2, start = Point3D(x = 0, y = 2, z = 2), end = Point3D(x = 2, y = 2, z = 2))
        val sb3 = SandBlock(id = 3, start = Point3D(x = 0, y = 0, z = 3), end = Point3D(x = 0, y = 2, z = 3))
        val sb4 = SandBlock(id = 4, start = Point3D(x = 2, y = 0, z = 3), end = Point3D(x = 2, y = 2, z = 3))
        val sb5 = SandBlock(id = 5, start = Point3D(x = 0, y = 1, z = 4), end = Point3D(x = 2, y = 1, z = 4))
        val sb6 = SandBlock(id = 6, start = Point3D(x = 1, y = 1, z = 5), end = Point3D(x = 1, y = 1, z = 6))
        assertThat(sandGrid.settledBlocks).containsExactly(sb0, sb1, sb2, sb3, sb4, sb5, sb6)

        assertThat(sandGrid.supporting).containsExactlyEntriesOf(mapOf(
            sb0 to emptySet(),
            sb1 to setOf(sb0),
            sb2 to setOf(sb0),
            sb3 to setOf(sb1, sb2),
            sb4 to setOf(sb1, sb2),
            sb5 to setOf(sb3, sb4),
            sb6 to setOf(sb5)
        ))


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
        assertThat(v).isEqualTo(7)
    }

}