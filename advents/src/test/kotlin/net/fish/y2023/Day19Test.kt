package net.fish.y2023

import net.fish.resourceStrings
import net.fish.y2023.Day19.Part
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day19Test {
    @Test
    fun `can parse system`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val system = Day19.generatePartSystem(data)
        assertThat(system.parts).containsExactly(
            Part(x = 787, m = 2655, a = 1222, s = 2876),
            Part(x = 1679, m = 44, a = 2067, s = 496),
            Part(x = 2036, m = 264, a = 79, s = 2244),
            Part(x = 2461, m = 1339, a = 466, s = 291),
            Part(x = 2127, m = 1623, a = 2188, s = 1013)
        )
        assertThat(system.workflows["px"]).isEqualTo(
            Day19.WorkFlow(
                name = "px", rules = listOf(
                    Day19.LTRule(variable = 'a', value = 2006, operation = Day19.MoveOperation(destination = "qkq")),
                    Day19.GTRule(variable = 'm', value = 2090, operation = Day19.Accept),
                    Day19.Always(operation = Day19.MoveOperation(destination = "rfg"))
                )
            )
        )
        assertThat(system.workflows["pv"]).isEqualTo(
            Day19.WorkFlow(
                name = "pv", rules = listOf(
                    Day19.GTRule(variable = 'a', value = 1716, operation = Day19.Reject),
                    Day19.Always(operation = Day19.Accept)
                )
            )
        )
        assertThat(system.workflows["crn"]).isEqualTo(
            Day19.WorkFlow(
                name = "crn", rules = listOf(
                    Day19.GTRule(variable = 'x', value = 2662, operation = Day19.Accept),
                    Day19.Always(operation = Day19.Reject)
                )
            )
        )
    }

    @Test
    fun `can process rules`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val system = Day19.generatePartSystem(data)
        assertThat(system.processParts()).containsExactlyEntriesOf(
            mapOf(
                Part(x = 787, m = 2655, a = 1222, s = 2876) to Day19.Accept,
                Part(x = 1679, m = 44, a = 2067, s = 496) to Day19.Reject,
                Part(x = 2036, m = 264, a = 79, s = 2244) to Day19.Accept,
                Part(x = 2461, m = 1339, a = 466, s = 291) to Day19.Reject,
                Part(x = 2127, m = 1623, a = 2188, s = 1013) to Day19.Accept
            )
        )
    }

    @Test
    fun `can do part 1`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val v = Day19.doPart1(data)
        assertThat(v).isEqualTo(19114)
    }

    @Test
    fun `can do part 2`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val v = Day19.doPart2(data)
        assertThat(v).isEqualTo(167409079868000)
    }

}