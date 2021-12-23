package net.fish.y2021

import net.fish.geometry.square.Square
import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DumboOctopusSimulatorTest {
    private val testData = resourcePath("/2021/day11-test.txt")
    @Test
    fun `can create simulator`() {
        val simulator = DumboOctopusSimulator(testData)
        assertThat(simulator.grid.width).isEqualTo(10)
        assertThat(simulator.grid.height).isEqualTo(10)

        val gridValues = simulator.engine.gridValues()
        assertThat(gridValues).containsExactly(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
        )
    }

    @Test
    fun `flashing iteration is correct`() {
        val simulator = DumboOctopusSimulator(testData)
        var flashing = simulator.engine.step()
        var gridValues = simulator.engine.gridValues()
        assertThat(gridValues).containsExactly(
            "6594254334",
            "3856965822",
            "6375667284",
            "7252447257",
            "7468496589",
            "5278635756",
            "3287952832",
            "7993992245",
            "5957959665",
            "6394862637"
        )
        assertThat(flashing.size).isEqualTo(0)

        flashing = simulator.engine.step()
        gridValues = simulator.engine.gridValues()
        assertThat(gridValues).containsExactly(
            "8807476555",
            "5089087054",
            "8597889608",
            "8485769600",
            "8700908800",
            "6600088989",
            "6800005943",
            "0000007456",
            "9000000876",
            "8700006848"
        )
        assertThat(flashing.size).isEqualTo(35)
        val remapped = flashing.map { it.iterationStarted to Pair((it.item as Square).x, (it.item as Square).y) }
        // check that the iteration to (x, y) values are correct, the iterationStarted tracks how long before this flash should actually start.
        // i.e. any value at 9 will become flashing in round 0, any 8 that was touching this will first become 9 (its step), then become flashing at round 1
        // creating a ripple effect of flashing
        assertThat(remapped).containsExactlyInAnyOrder(
            Pair(0, Pair(2, 0)), Pair(0, Pair(4, 1)), Pair(0, Pair(5, 4)), Pair(0, Pair(9, 4)), Pair(0, Pair(4, 6)),
            Pair(0, Pair(1, 7)), Pair(0, Pair(2, 7)), Pair(0, Pair(4, 7)), Pair(0, Pair(5, 7)), Pair(0, Pair(1, 8)),
            Pair(0, Pair(4, 8)), Pair(0, Pair(6, 8)), Pair(0, Pair(2, 9)), Pair(1, Pair(1, 1)), Pair(1, Pair(8, 4)),
            Pair(1, Pair(3, 5)), Pair(1, Pair(2, 6)), Pair(1, Pair(3, 6)), Pair(1, Pair(0, 7)), Pair(1, Pair(2, 8)),
            Pair(1, Pair(3, 8)), Pair(1, Pair(5, 8)), Pair(1, Pair(4, 9)), Pair(2, Pair(9, 3)), Pair(2, Pair(3, 4)),
            Pair(2, Pair(2, 5)), Pair(2, Pair(4, 5)), Pair(2, Pair(3, 7)), Pair(2, Pair(3, 9)), Pair(2, Pair(5, 9)),
            Pair(3, Pair(8, 2)), Pair(3, Pair(2, 4)), Pair(3, Pair(5, 6)), Pair(4, Pair(7, 1)), Pair(4, Pair(8, 3))
        )
    }

    @Test
    fun `can step simulator`() {
        val simpleData = listOf(
            "11111",
            "19991",
            "19191",
            "19991",
            "11111"
        )
        val simulator = DumboOctopusSimulator(simpleData)
        assertThat(simulator.engine.gridValues()).containsExactly(
            "11111",
            "19991",
            "19191",
            "19991",
            "11111"
        )

        var flashing = simulator.engine.step()
        var score = flashing.size
        assertThat(score).isEqualTo(9)

        var gridValues = simulator.engine.gridValues()
        assertThat(gridValues).containsExactly(
            "34543",
            "40004",
            "50005",
            "40004",
            "34543"
        )

        flashing = simulator.engine.step()
        score = flashing.size
        assertThat(score).isEqualTo(0)

        gridValues = simulator.engine.gridValues()
        assertThat(gridValues).containsExactly(
            "45654",
            "51115",
            "61116",
            "51115",
            "45654"
        )
    }
}