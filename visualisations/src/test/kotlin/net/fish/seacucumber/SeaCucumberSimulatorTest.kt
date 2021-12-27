package net.fish.seacucumber

import net.fish.geometry.square.WrappingSquareGrid
import net.fish.seacucumber.SeaCucumberFloorValue.E
import net.fish.seacucumber.SeaCucumberFloorValue.S
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SeaCucumberSimulatorTest {
    @Test
    fun `can create simulator`() {
        val input = """
            ..........
            .>v....v..
            .......>..
            ..........
        """.trimIndent().lines()

        val simulator = SeaCucumberSimulator(input)
        assertThat(simulator.engine.grid.width).isEqualTo(10)
        assertThat(simulator.engine.grid.height).isEqualTo(4)
        val wsg = simulator.engine.grid as WrappingSquareGrid
        val sc1 = wsg.square(1, 1)
        val sc2 = wsg.square(2, 1)
        val sc3 = wsg.square(7, 1)
        val sc4 = wsg.square(7, 2)
        assertThat(simulator.engine.storage.getData(sc1)!!.value).isEqualTo(E)
        assertThat(simulator.engine.storage.getData(sc2)!!.value).isEqualTo(S)
        assertThat(simulator.engine.storage.getData(sc3)!!.value).isEqualTo(S)
        assertThat(simulator.engine.storage.getData(sc4)!!.value).isEqualTo(E)
        assertThat(simulator.engine.storage.data.toList()).hasSize(40)
    }

    @Test
    fun `can print grid`() {
        val input = """
            ..........
            .>v....v..
            .......>..
            ..........
        """.trimIndent().lines()
        val simulator = SeaCucumberSimulator(input)
        val gridString = simulator.engine.gridValues()
        assertThat(gridString).containsExactly(
            "..........",
            ".>v....v..",
            ".......>..",
            ".........."
        )
    }

    @Test
    fun `can step grid`() {
        val input = """
            ..........
            .>v....v..
            .......>..
            ..........
        """.trimIndent().lines()
        val simulator = SeaCucumberSimulator(input)
        val movedCount = simulator.engine.step()
        assertThat(movedCount).isEqualTo(3)
        assertThat(simulator.engine.gridValues()).containsExactly(
            "..........",
            ".>........",
            "..v....v>.",
            ".........."
        )
    }

    @Test
    fun `can find blocking step on test data`() {
        val input = """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>
        """.trimIndent().lines()
        val simulator = SeaCucumberSimulator(input)
        val blockCount = simulator.findBlockStep()
        assertThat(blockCount).isEqualTo(58)
        assertThat(simulator.engine.gridValues()).containsExactly(
            "..>>v>vv..",
            "..v.>>vv..",
            "..>>v>>vv.",
            "..>>>>>vv.",
            "v......>vv",
            "v>v....>>v",
            "vvv.....>>",
            ">vv......>",
            ".>v.vv.v.."
        )
    }
}