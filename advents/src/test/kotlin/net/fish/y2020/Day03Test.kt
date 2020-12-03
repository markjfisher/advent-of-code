package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day03Test {
    private val forestData = resourcePath("/2020/day03-test.txt")

    @Test
    fun `traversing forest gives expected sequence`() {
        assertThat(Day03.generateForestValueSequence(forestData, 3, 1).toList()).containsExactly('.', '.', '#', '.', '#', '#', '.', '#', '#', '#', '#')
        assertThat(Day03.generateForestValueSequence(forestData, 1, 2).toList()).containsExactly('.', '#', '.', '#', '.', '.')
    }

    @Test
    fun `generating location sequences`() {
        assertThat(Day03.generateLocationSequence(3, 1, 11, 11).toList()).containsExactly(
            Pair(0, 0), Pair(3, 1), Pair(6, 2), Pair(9, 3), Pair(1, 4), Pair(4, 5), Pair(7, 6), Pair(10, 7), Pair(2, 8), Pair(5, 9), Pair(8, 10)
        )
        assertThat(Day03.generateLocationSequence(1, 2, 11, 11).toList()).containsExactly(
            Pair(0, 0), Pair(1, 2), Pair(2, 4), Pair(3, 6), Pair(4, 8), Pair(5, 10)
        )
    }

    @Test
    fun `correct results are matched`() {
        assertThat(Day03.part1()).isEqualTo(289)
        assertThat(Day03.part2()).isEqualTo(5522401584L)
    }

    @Test
    fun `test data results match`() {
        assertThat(Day03.traverseForest(forestData, Day03.part1Runs)).isEqualTo(7)
        assertThat(Day03.traverseForest(forestData, Day03.part2Runs)).isEqualTo(336L)
    }
}