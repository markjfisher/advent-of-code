package net.fish.collections

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SequencesTest {
    @Test
    fun `can cycle string`() {
        val dirs = "ABC"
        val infSeq = dirs.asSequence().cycle()
        Assertions.assertThat(infSeq.take(10).joinToString(":")).isEqualTo("A:B:C:A:B:C:A:B:C:A")
    }
}