package net.fish.maths

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MovingEntriesCircularArrayTest {
    @Test
    fun `can create linked list`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, -3, 3, -2, 0, 4))

        assertThat(l.at(0)).isEqualTo(1)
        assertThat(l.at(1)).isEqualTo(2)
        assertThat(l.at(2)).isEqualTo(-3)
        assertThat(l.at(3)).isEqualTo(3)
        assertThat(l.at(4)).isEqualTo(-2)
        assertThat(l.at(5)).isEqualTo(0)
        assertThat(l.at(6)).isEqualTo(4)

        assertThat(l.at(7)).isEqualTo(1)
        assertThat(l.at(14)).isEqualTo(1)
        assertThat(l.at(-1)).isEqualTo(4)
        assertThat(l.at(-8)).isEqualTo(4)
        assertThat(l.at(-15)).isEqualTo(4)
    }

    @Test
    fun `can move entries`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, -3, 3, -2, 0, 4))
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 1, -3, 3, -2, 0, 4)
        l.move(LongCircArrayEntry(2, 1), 2)
        assertThat(l.values()).containsExactly(1, -3, 2, 3, -2, 0, 4)

    }

    @Test
    fun `can cycle entries right`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, 3, 4, 0, 5, 6))
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 1, 3, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 1, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 4, 1, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 1, 5, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 5, 1, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 5, 6, 1)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 1, 3, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), 1)
        assertThat(l.values()).containsExactly(2, 3, 1, 4, 0, 5, 6)

    }

    @Test
    fun `can cycle entries left`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, 3, 4, 0, 5, 6))
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 5, 1, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 1, 5, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 3, 4, 1, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 3, 1, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 1, 3, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(1, 2, 3, 4, 0, 5, 6)
        l.move(LongCircArrayEntry(1, 0), -1)
        assertThat(l.values()).containsExactly(2, 3, 4, 0, 5, 1, 6)
    }

    @Test
    fun `can jump longer than list size forwards`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, 3, 4, 5, 6, 7))
        l.move(LongCircArrayEntry(1, 0), 6)
        // effectively no change
        assertThat(l.values()).containsExactly(1, 2, 3, 4, 5, 6, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), 7)
        // effectively move 1
        assertThat(l.values()).containsExactly(2, 1, 3, 4, 5, 6, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), 8)
        assertThat(l.values()).containsExactly(2, 3, 1, 4, 5, 6, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), 9)
        assertThat(l.values()).containsExactly(2, 3, 4, 1, 5, 6, 7)
    }

    @Test
    fun `can jump longer than list size backwards`() {
        val l = MovingEntriesCircularArray(listOf(1, 2, 3, 4, 5, 6, 7))
        l.move(LongCircArrayEntry(1, 0), -6)
        // effectively no change
        assertThat(l.values()).containsExactly(1, 2, 3, 4, 5, 6, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), -7)
        // effectively move 1 left
        assertThat(l.values()).containsExactly(2, 3, 4, 5, 6, 1, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), -8)
        assertThat(l.values()).containsExactly(2, 3, 4, 5, 1, 6, 7)

        l.init()
        l.move(LongCircArrayEntry(1, 0), -9)
        assertThat(l.values()).containsExactly(2, 3, 4, 1, 5, 6, 7)
    }
}