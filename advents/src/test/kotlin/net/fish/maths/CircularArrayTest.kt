package net.fish.maths

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CircularArrayTest {
    private fun createTestArray() = CircularArray(listOf(1, 2, 3))

    @Test
    fun `can create get and set values`() {
        val ca = CircularArray(Array(5) { 0L }.toList())
        assertThat(ca[0]).isEqualTo(0)

        ca[0] = 1
        assertThat(ca[0]).isEqualTo(1)
    }

    @Test
    fun `can rotate single item`() {
        val ca = CircularArray(listOf(0))
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(0)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(0)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(0)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(0)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(0)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(0)

        ca.add(0, 1)
        assertThat(ca.toList()).containsExactly(1, 0)
    }

    @Test
    fun `can shift values left`() {
        val ca = createTestArray()
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateLeft(2)
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateLeft(2)
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateLeft(2)
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateLeft(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateLeft(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateLeft(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateLeft(4)
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateLeft(4)
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateLeft(4)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
    }

    @Test
    fun `can shift values right`() {
        val ca = createTestArray()
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateRight(2)
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight(2)
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight(2)
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateRight(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight(3)
        assertThat(ca.toList()).containsExactly(1, 2, 3)

        ca.rotateRight(4)
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight(4)
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight(4)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
    }

    @Test
    fun `can add values at start and rotate left`() {
        val ca = createTestArray()
        ca.add(0, 4)
        assertThat(ca.toList()).containsExactly(4, 1, 2, 3)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(1, 2, 3, 4)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(2, 3, 4, 1)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(3, 4, 1, 2)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(4, 1, 2, 3)
        ca.rotateLeft()
        assertThat(ca.toList()).containsExactly(1, 2, 3, 4)
    }

    @Test
    fun `can add values at start and rotate right`() {
        val ca = createTestArray()
        ca.add(0, 4)
        assertThat(ca.toList()).containsExactly(4, 1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 4, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 4, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 3, 4)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(4, 1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 4, 1, 2)
    }

    @Test
    fun `can add values to end without index`() {
        var ca = createTestArray()
        ca.add(4)
        assertThat(ca.toList()).containsExactly(1, 2, 3, 4)

        ca = createTestArray()
        ca.rotateLeft()
        ca.add(4)
        assertThat(ca.toList()).containsExactly(2, 3, 1, 4)

        ca = createTestArray()
        ca.rotateRight()
        ca.add(4)
        assertThat(ca.toList()).containsExactly(3, 1, 2, 4)
    }

    @Test
    fun `can add values inbetween`() {
        var ca = createTestArray()
        ca.add(1, 4)
        assertThat(ca.toList()).containsExactly(1, 4, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 4, 2)

        ca = createTestArray()
        ca.rotateLeft()
        ca.add(1, 4)
        assertThat(ca.toList()).containsExactly(2, 4, 3, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 4, 3)

        ca = createTestArray()
        ca.rotateRight()
        ca.add(1, 4)
        assertThat(ca.toList()).containsExactly(3, 4, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 4, 1)
    }

    @Test
    fun `can remove values at start`() {
        val ca = CircularArray(listOf(1, 2, 3, 4))
        val v = ca.remove(0)
        assertThat(v).isEqualTo(1L)
        assertThat(ca.toList()).containsExactly(2, 3, 4)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(4, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 4, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 4)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(4, 2, 3)
    }

    @Test
    fun `can remove values at end`() {
        val ca = CircularArray(listOf(1, 2, 3, 4))
        val v = ca.remove(3)
        assertThat(v).isEqualTo(4L)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
    }

    @Test
    fun `can remove values with wrap`() {
        val ca = CircularArray(listOf(1, 2, 3, 4))
        val v = ca.remove(5)
        assertThat(v).isEqualTo(2L)
        assertThat(ca.toList()).containsExactly(1, 3, 4)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(4, 1, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 4, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 3, 4)
    }

    @Test
    fun `can remove values after rotate left`() {
        // remove after first rotating left
        val ca = CircularArray(listOf(1, 2, 3, 4))
        ca.rotateLeft()
        val v = ca.remove(0)
        assertThat(v).isEqualTo(2L)
        assertThat(ca.toList()).containsExactly(3, 4, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 3, 4)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(4, 1, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 4, 1)
    }

    @Test
    fun `can remove values after rotate right`() {
        // remove after first rotating right
        val ca = CircularArray(listOf(1, 2, 3, 4))
        ca.rotateRight()
        val v = ca.remove(0)
        assertThat(v).isEqualTo(4L)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 3)
    }

    @Test
    fun `can remove first entry after rotate right`() {
        // remove after first rotating right
        val ca = CircularArray(listOf(1, 2, 3, 4))
        ca.rotateRight()
        val v = ca.remove()
        assertThat(v).isEqualTo(4L)
        assertThat(ca.toList()).containsExactly(1, 2, 3)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(3, 1, 2)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(2, 3, 1)
        ca.rotateRight()
        assertThat(ca.toList()).containsExactly(1, 2, 3)
    }

    @Test
    fun `can sum lists`() {
        val caInt = CircularArray(listOf(1, 2, 3, 4))
        assertThat(caInt.sum()).isEqualTo(10L)
    }

    @Test
    fun `can access index which wraps`() {
        val ca = CircularArray(listOf(1, 2, 3, 4))
        assertThat(ca[4]).isEqualTo(1)
        assertThat(ca[5]).isEqualTo(2)
        assertThat(ca[6]).isEqualTo(3)
        assertThat(ca[7]).isEqualTo(4)
        assertThat(ca[8]).isEqualTo(1)

        assertThat(ca[-1]).isEqualTo(4)
        assertThat(ca[-2]).isEqualTo(3)
        assertThat(ca[-3]).isEqualTo(2)
        assertThat(ca[-4]).isEqualTo(1)
        assertThat(ca[-5]).isEqualTo(4)
    }
}