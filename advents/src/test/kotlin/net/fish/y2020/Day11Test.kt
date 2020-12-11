package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day11.Location.EMPTY
import net.fish.y2020.Day11.Location.FLOOR
import net.fish.y2020.Day11.Location.OCCUPIED
import net.fish.y2020.Day11.Location.OUTSIDE
import net.fish.y2020.Day11.columns
import net.fish.y2020.Day11.identical
import net.fish.y2020.Day11.rows
import net.fish.y2020.Day11.toGrid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day11Test {
    private val testSeatPlan = Day11.toSeatPlan(resourcePath("/2020/day11-test-start.txt"))
    private val testSeatPlan1Iteration1 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p1-i1.txt"))
    private val testSeatPlan1Iteration2 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p1-i2.txt"))
    private val testSeatPlan1IterationFinal = Day11.toSeatPlan(resourcePath("/2020/day11-test-p1-final.txt"))
    private val testSeatPlan2Iteration1 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p2-i1.txt"))
    private val testSeatPlan2Iteration2 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p2-i2.txt"))
    private val testSeatPlan2IterationFinal = Day11.toSeatPlan(resourcePath("/2020/day11-test-p2-final.txt"))

    private val testSeatPlan2CanSee1 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p2-cansee1.txt"))
    private val testSeatPlan2CanSee2 = Day11.toSeatPlan(resourcePath("/2020/day11-test-p2-cansee2.txt"))

    @Test
    fun `convert input to double array`() {
        val seatPlan = Day11.toSeatPlan(listOf("#.", "L#"))
        assertThat(seatPlan[0][0]).isEqualTo(OCCUPIED)
        assertThat(seatPlan[0][1]).isEqualTo(FLOOR)
        assertThat(seatPlan[1][0]).isEqualTo(EMPTY)
        assertThat(seatPlan[1][1]).isEqualTo(OCCUPIED)
    }

    @Test
    fun `locations in seatplan`() {
        val seatPlan = Day11.toSeatPlan(listOf("#.", "L#"))
        assertThat(Day11.locationAt(0, 0, seatPlan)).isEqualTo(OCCUPIED)
        assertThat(Day11.locationAt(0, 1, seatPlan)).isEqualTo(EMPTY)
        assertThat(Day11.locationAt(1, 0, seatPlan)).isEqualTo(FLOOR)
        assertThat(Day11.locationAt(1, 1, seatPlan)).isEqualTo(OCCUPIED)
        assertThat(Day11.locationAt(2, 0, seatPlan)).isEqualTo(OUTSIDE)
        assertThat(Day11.locationAt(-1, 0, seatPlan)).isEqualTo(OUTSIDE)
        assertThat(Day11.locationAt(0, 2, seatPlan)).isEqualTo(OUTSIDE)
        assertThat(Day11.locationAt(0, -1, seatPlan)).isEqualTo(OUTSIDE)
    }

    @Test
    fun `get seats around a position`() {
        assertThat(Day11.around(0, 0, testSeatPlan)).containsExactly(
            OUTSIDE, OUTSIDE, OUTSIDE,
            OUTSIDE,          FLOOR,
            OUTSIDE, EMPTY,   EMPTY
        )

        assertThat(Day11.around(1, 1, testSeatPlan)).containsExactly(
            EMPTY, FLOOR, EMPTY,
            EMPTY,        EMPTY,
            EMPTY, FLOOR, EMPTY
        )

        assertThat(Day11.around(9, 9, testSeatPlan)).containsExactly(
            FLOOR,   EMPTY,   OUTSIDE,
            EMPTY,            OUTSIDE,
            OUTSIDE, OUTSIDE, OUTSIDE
        )
    }

    @Test
    fun `can get rows and columns`() {
        val seatPlan = Day11.toSeatPlan(listOf("#.#", "L#."))
        assertThat(seatPlan.columns()).isEqualTo(3)
        assertThat(seatPlan.rows()).isEqualTo(2)
    }

    @Test
    fun `can compare`() {
        assertThat(Day11.toSeatPlan(listOf("#.#", "L#."))).isEqualTo(Day11.toSeatPlan(listOf("#.#", "L#.")))
        assertThat(Day11.toSeatPlan(listOf("#.#", "L#."))).isNotEqualTo(Day11.toSeatPlan(listOf("#.L", "L#.")))
    }

    @Test
    fun `iterating over initial plan1`() {
        val i1 = Day11.processPlanP1(testSeatPlan)
        testSeatPlan.toGrid()
        i1.toGrid()
        assertThat(testSeatPlan1Iteration1).isEqualTo(i1)

        val i2 = Day11.processPlanP1(i1)
        i2.toGrid()
        assertThat(testSeatPlan1Iteration2).isEqualTo(i2)

    }

    @Test
    fun `stabalizing plan1`() {
        val final = Day11.simulatePeopleP1(testSeatPlan)
        println("--------------")
        final.toGrid()
        testSeatPlan1IterationFinal.toGrid()
        assertThat(testSeatPlan1IterationFinal).isEqualTo(final)
    }
    
    @Test
    fun `iterate p2`() {
        val i1 = Day11.processPlanP2(testSeatPlan)
        assertThat(testSeatPlan2Iteration1).isEqualTo(i1)
        val i2 = Day11.processPlanP2(i1)
        i2.toGrid()
        testSeatPlan2Iteration2.toGrid()
        assertThat(testSeatPlan2Iteration2.identical(i2)).isTrue

        val final = Day11.simulatePeopleP2(testSeatPlan)
        assertThat(final.identical(testSeatPlan2IterationFinal))
    }

    @Test
    fun `can see p2`() {
        val locationsSanSee1 = Day11.canSee(3, 4, testSeatPlan2CanSee1)
        assertThat(locationsSanSee1).containsExactly(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED
        )

        val locationsSanSee2 = Day11.canSee(1, 1, testSeatPlan2CanSee2)
        assertThat(locationsSanSee2).containsExactly(
            EMPTY
        )


    }
}