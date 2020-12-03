package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day03.Delta
import net.fish.y2020.Day03.Location
import net.fish.y2020.Day03.Square.GAP
import net.fish.y2020.Day03.Square.TREE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day03Test {
    private val forestData = resourcePath("/2020/day03-test.txt")

    @Test
    fun `traversing test forest gives expected sequence given in puzzle description`() {
        assertThat(Day03.skiSequence(forestData, Delta(3, 1)).toList()).containsExactly(GAP, GAP, TREE, GAP, TREE, TREE, GAP, TREE, TREE, TREE, TREE)
        assertThat(Day03.skiSequence(forestData, Delta(1, 2)).toList()).containsExactly(GAP, TREE, GAP, TREE, GAP, GAP)
    }

    @Test
    fun `generating location sequences`() {
        assertThat(Day03.generateLocationSequence(Delta(3, 1), 11, 11).toList()).containsExactly(
            Location(0, 0), Location(3, 1), Location(6, 2), Location(9, 3), Location(1, 4), Location(4, 5), Location(7, 6), Location(10, 7), Location(2, 8), Location(5, 9), Location(8, 10)
        )
        assertThat(Day03.generateLocationSequence(Delta(1, 2), 11, 11).toList()).containsExactly(
            Location(0, 0), Location(1, 2), Location(2, 4), Location(3, 6), Location(4, 8), Location(5, 10)
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