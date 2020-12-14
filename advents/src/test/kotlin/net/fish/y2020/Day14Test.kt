package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day14Test {
    private val program1 = resourcePath("/2020/day14-test-01.txt")
    private val program2 = resourcePath("/2020/day14-test-02.txt")

    @Test
    fun `can decode bit mask`() {
        assertThat(Mask.from("0").bits).containsExactlyEntriesOf(mapOf(0 to Bit.OFF))
        assertThat(Mask.from("1").bits).containsExactlyEntriesOf(mapOf(0 to Bit.ON))
        assertThat(Mask.from("X").bits).containsExactlyEntriesOf(mapOf(0 to Bit.X))
        assertThat(Mask.from("00").bits).containsExactlyEntriesOf(mapOf(0 to Bit.OFF, 1 to Bit.OFF))
        assertThat(Mask.from("01").bits).containsExactlyEntriesOf(mapOf(0 to Bit.ON, 1 to Bit.OFF))
        assertThat(Mask.from("10").bits).containsExactlyEntriesOf(mapOf(0 to Bit.OFF, 1 to Bit.ON))
        assertThat(Mask.from("1X0").bits).containsExactlyEntriesOf(mapOf(0 to Bit.OFF, 1 to Bit.X, 2 to Bit.ON))
    }

    @Test
    fun `can convert bits to number`() {
        assertThat(Bit.toLong(emptyMap())).isEqualTo(0)
        assertThat(Bit.toLong(mapOf(0 to Bit.ON))).isEqualTo(1)
        assertThat(Bit.toLong(mapOf(1 to Bit.ON))).isEqualTo(2)
        assertThat(Bit.toLong(mapOf(0 to Bit.OFF, 1 to Bit.ON))).isEqualTo(2)
        assertThat(Bit.toLong(mapOf(0 to Bit.ON, 1 to Bit.ON))).isEqualTo(3)
        assertThat(Bit.toLong(mapOf(5 to Bit.ON, 1 to Bit.ON))).isEqualTo(34)
    }

    @Test
    fun `docking computer functions for p1`() {
        val computer = DockingComputer(program1)
        assertThat(computer.mem.memory).isEmpty()
        assertThat(computer.mask.bits).isEmpty()
        assertThat(computer.pc).isEqualTo(0)

        // mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
        computer.step(computer::changeValues)
        assertThat(computer.mem.memory).isEmpty()
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(1 to Bit.OFF, 6 to Bit.ON))
        assertThat(computer.pc).isEqualTo(1)

        // mem[8] = 11, but bitmask forces it to 73
        computer.step(computer::changeValues)
        assertThat(computer.mem.memory).containsExactlyEntriesOf(mapOf(8L to 73L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(1 to Bit.OFF, 6 to Bit.ON))
        assertThat(computer.pc).isEqualTo(2)

        // mem[7] = 101, no change from bitmask
        computer.step(computer::changeValues)
        assertThat(computer.mem.memory).containsExactlyEntriesOf(mapOf(8L to 73L, 7L to 101L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(1 to Bit.OFF, 6 to Bit.ON))
        assertThat(computer.pc).isEqualTo(3)

        // mem[8] = 0, bitmask changes it to 64
        computer.step(computer::changeValues)
        assertThat(computer.mem.memory).containsExactlyEntriesOf(mapOf(8L to 64L, 7L to 101L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(1 to Bit.OFF, 6 to Bit.ON))
        assertThat(computer.pc).isEqualTo(4)
    }

    @Test
    fun `run computer p1`() {
        val computer = DockingComputer(program1)
        computer.run(computer::changeValues)
        assertThat(computer.mem.memory).containsAllEntriesOf(mapOf(8L to 64L, 7L to 101L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(1 to Bit.OFF, 6 to Bit.ON))
        assertThat(computer.pc).isEqualTo(4)
    }

    @Test
    fun `substituting Xs gives all permutations`() {
        assertThat(Bit.toLongs(mapOf(0 to Bit.ON, 1 to Bit.X))).containsExactlyInAnyOrder(1, 3)
        assertThat(Bit.toLongs(mapOf(0 to Bit.X, 1 to Bit.ON))).containsExactlyInAnyOrder(2, 3)
        assertThat(Bit.toLongs(mapOf(0 to Bit.X, 1 to Bit.X))).containsExactlyInAnyOrder(0, 1, 2, 3)
    }

    @Test
    fun `docking computer functions for p2`() {
        val computer = DockingComputer(program2)
        assertThat(computer.mem.memory).isEmpty()
        assertThat(computer.mask.bits).isEmpty()
        assertThat(computer.pc).isEqualTo(0)

        // mask = 000000000000000000000000000000X1001X
        computer.step(computer::changeAddresses)
        assertThat(computer.mem.memory).isEmpty()
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(0 to Bit.X, 1 to Bit.ON, 4 to Bit.ON, 5 to Bit.X))
        assertThat(computer.pc).isEqualTo(1)

        // mem[42] = 100, bitmask causes writes of 100 to 26, 27, 58, 59
        computer.step(computer::changeAddresses)
        assertThat(computer.mem.memory).containsAllEntriesOf(mapOf(26L to 100L, 27L to 100L, 58L to 100L, 59L to 100L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(0 to Bit.X, 1 to Bit.ON, 4 to Bit.ON, 5 to Bit.X))
        assertThat(computer.pc).isEqualTo(2)

        // mask = 00000000000000000000000000000000X0XX
        computer.step(computer::changeAddresses)
        assertThat(computer.mem.memory).containsAllEntriesOf(mapOf(26L to 100L, 27L to 100L, 58L to 100L, 59L to 100L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(0 to Bit.X, 1 to Bit.X, 3 to Bit.X))
        assertThat(computer.pc).isEqualTo(3)

        // mem[26] = 1, bitmask causes writes to 16, 17, 18, 19, 24, 25, 26, 27 (overwriting some)
        computer.step(computer::changeAddresses)
        assertThat(computer.mem.memory).containsAllEntriesOf(mapOf(16L to 1L, 17L to 1L, 18L to 1L, 19L to 1L, 24L to 1L, 25L to 1L, 26L to 1L, 27L to 1L, 58L to 100L, 59L to 100L))
        assertThat(computer.mask.bits).containsAllEntriesOf(mapOf(0 to Bit.X, 1 to Bit.X, 3 to Bit.X))
        assertThat(computer.pc).isEqualTo(4)
    }
}