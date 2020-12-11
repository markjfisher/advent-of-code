package net.fish.y2020

import net.fish.resourcePath
import net.fish.y2020.Day11.Location.EMPTY
import net.fish.y2020.Day11.Location.FLOOR
import net.fish.y2020.Day11.Location.OCCUPIED
import net.fish.y2020.Day11.Location.OUTSIDE
import net.fish.y2020.Day11.around
import net.fish.y2020.Day11.canSee
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
        assertThat(seatPlan.locationAt(0, 0)).isEqualTo(OCCUPIED)
        assertThat(seatPlan.locationAt(0, 1)).isEqualTo(EMPTY)
        assertThat(seatPlan.locationAt(1, 0)).isEqualTo(FLOOR)
        assertThat(seatPlan.locationAt(1, 1)).isEqualTo(OCCUPIED)
        assertThat(seatPlan.locationAt(2, 0)).isEqualTo(OUTSIDE)
        assertThat(seatPlan.locationAt(-1, 0)).isEqualTo(OUTSIDE)
        assertThat(seatPlan.locationAt(0, 2)).isEqualTo(OUTSIDE)
        assertThat(seatPlan.locationAt(0, -1)).isEqualTo(OUTSIDE)
    }

    @Test
    fun `get seats around a position`() {
        assertThat(around(0, 0, testSeatPlan)).containsExactly(
            OUTSIDE, OUTSIDE, OUTSIDE,
            OUTSIDE,          FLOOR,
            OUTSIDE, EMPTY,   EMPTY
        )

        assertThat(around(1, 1, testSeatPlan)).containsExactly(
            EMPTY, FLOOR, EMPTY,
            EMPTY,        EMPTY,
            EMPTY, FLOOR, EMPTY
        )

        assertThat(around(9, 9, testSeatPlan)).containsExactly(
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
        val i1 = Day11.iteratePlan(testSeatPlan, 4, ::around)
        testSeatPlan.printPlan()
        i1.printPlan()
        assertThat(testSeatPlan1Iteration1).isEqualTo(i1)

        val i2 = Day11.iteratePlan(i1, 4, ::around)
        i2.printPlan()
        assertThat(testSeatPlan1Iteration2).isEqualTo(i2)

    }

    @Test
    fun `stabalizing plan1`() {
        val final = Day11.simulatePassengers(testSeatPlan, 4, ::around)
        assertThat(testSeatPlan1IterationFinal).isEqualTo(final)
    }
    
    @Test
    fun `iterate and stabalize p2`() {
        val i1 = Day11.iteratePlan(testSeatPlan, 5, ::canSee)
        assertThat(testSeatPlan2Iteration1).isEqualTo(i1)
        val i2 = Day11.iteratePlan(i1, 5, ::canSee)
        testSeatPlan2Iteration2.printPlan()
        assertThat(testSeatPlan2Iteration2.identical(i2)).isTrue

        val final = Day11.simulatePassengers(testSeatPlan, 5, ::canSee)
        assertThat(final.identical(testSeatPlan2IterationFinal))
    }

    @Test
    fun `can see p2`() {
        val locationsSanSee1 = canSee(3, 4, testSeatPlan2CanSee1)
        assertThat(locationsSanSee1).containsExactly(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED
        )

        val locationsSanSee2 = canSee(1, 1, testSeatPlan2CanSee2)
        assertThat(locationsSanSee2).containsExactly(
            EMPTY
        )


    }
}