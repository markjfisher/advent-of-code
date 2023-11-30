package net.fish.y2018

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day08Test {

    @Test
    fun `can parse tree`() {
        val data = resourcePath("/2018/day08-test.txt").first().split(" ").map { it.toInt() }
        val nA = Day08.toLicenceTree(data)
        assertThat(nA.childNodes).hasSize(2)

        val nB = nA.childNodes[0]
        val nC = nA.childNodes[1]
        val nD = nC.childNodes[0]

        assertThat(nA.metaData).containsExactly(1, 1, 2)
        assertThat(nB.metaData).containsExactly(10, 11, 12)
        assertThat(nC.metaData).containsExactly(2)
        assertThat(nD.metaData).containsExactly(99)

        println(nA)
        assertThat(nA.sumAllMetaData()).isEqualTo(138)
        assertThat(nA.metaDataPart2()).isEqualTo(66)
    }
}