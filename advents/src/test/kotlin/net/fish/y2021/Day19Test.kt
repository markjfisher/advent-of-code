package net.fish.y2021

import net.fish.resourcePath
import net.fish.y2021.Day19.mRot90x
import net.fish.y2021.Day19.mRot90y
import net.fish.y2021.Day19.mRot90z
import net.fish.y2021.Day19.rot24
import org.assertj.core.api.Assertions.assertThat
import org.joml.Matrix4f
import org.joml.Vector4f
import org.junit.jupiter.api.Test

internal class Day19Test {
    private val testData by lazy { resourcePath("/2021/day19-test.txt") }

    @Test
    fun `part 1 on test data`() {
        val scanners = Day19.parseScanners(testData)
        val count = Day19.doPart1(scanners)
        assertThat(count).isEqualTo(Pair(79, 3621))
    }

    @Test
    fun `can parse input`() {
        val scanners = Day19.parseScanners(testData)
        assertThat(scanners).hasSize(5)

        assertThat(scanners[0].beacons).hasSize(25)
        assertThat(scanners[1].beacons).hasSize(25)
        assertThat(scanners[2].beacons).hasSize(26)
        assertThat(scanners[3].beacons).hasSize(25)
        assertThat(scanners[4].beacons).hasSize(26)

        assertThat(scanners[0].index).isEqualTo(0)
        assertThat(scanners[1].index).isEqualTo(1)
        assertThat(scanners[2].index).isEqualTo(2)
        assertThat(scanners[3].index).isEqualTo(3)
        assertThat(scanners[4].index).isEqualTo(4)

        assertThat(scanners[0].beacons[0]).isEqualTo(Vector4f(404f, -588f, -901f, 1f))
        assertThat(scanners[1].beacons[0]).isEqualTo(Vector4f(686f, 422f, 578f, 1f))
        assertThat(scanners[2].beacons[0]).isEqualTo(Vector4f(649f, 640f, 665f, 1f))
        assertThat(scanners[3].beacons[0]).isEqualTo(Vector4f(-589f, 542f, 597f, 1f))
        assertThat(scanners[4].beacons[0]).isEqualTo(Vector4f(727f, 592f, 562f, 1f))
    }

    @Test
    fun `set distances`() {
        // points are from a 3/4/5 triangle to make it easy on numbers
        val simpleData = """
            --- scanner 0 ---
            1,1,10
            1,4,10
            5,1,10
        """.trimIndent().lines()
        val scanners = Day19.parseScanners(simpleData)
        assertThat(scanners).hasSize(1)
        assertThat(scanners[0].beacons).hasSize(3)
        assertThat(scanners[0].distances).containsExactlyEntriesOf(
            mapOf(9 to Pair(0, 1), 16 to Pair(0, 2), 25 to Pair(1, 2))
        )
    }

    @Test
    fun `can create rotation matricies`() {
        assertThat(mRot90x).isEqualTo(
            Matrix4f(
                1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, -1f, 0f, 0f,
                0f, 0f, 0f, 1f
            )
        )

        assertThat(mRot90y).isEqualTo(
            Matrix4f(
                0f, 0f, -1f, 0f,
                0f, 1f, 0f, 0f,
                1f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f
            )
        )

        assertThat(mRot90z).isEqualTo(
            Matrix4f(
                0f, 1f, 0f, 0f,
                -1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        )
    }

    @Test
    fun `can create 24 rotations`() {
        assertThat(rot24).hasSize(24)
        assertThat(rot24).containsExactly(
            Matrix4f(0f, 1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(-1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, -1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, -1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, 1f, 0f, 0f, -1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, -1f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(-1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, -1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 1f, 0f, 0f, 0f, 0f, -1f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, -1f, 0f, 0f, 0f, 0f, 1f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, 1f, 0f, -1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(-1f, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, -1f, 0f, 1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(-1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(1f, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(0f, 0f, -1f, 0f, -1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f),
            Matrix4f(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        )
    }

}
