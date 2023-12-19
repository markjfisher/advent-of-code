package net.fish.y2023

import net.fish.resourceStrings
import net.fish.y2023.Day19.Accept
import net.fish.y2023.Day19.Always
import net.fish.y2023.Day19.And
import net.fish.y2023.Day19.GTRule
import net.fish.y2023.Day19.LTRule
import net.fish.y2023.Day19.MoveOperation
import net.fish.y2023.Day19.NotAnyOf
import net.fish.y2023.Day19.Part
import net.fish.y2023.Day19.Reject
import net.fish.y2023.Day19.XmasRanges
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
                    LTRule(variable = 'a', value = 2006, operation = MoveOperation(destination = "qkq")),
                    GTRule(variable = 'm', value = 2090, operation = Accept),
                    Always(operation = MoveOperation(destination = "rfg"))
                )
            )
        )
        assertThat(system.workflows["pv"]).isEqualTo(
            Day19.WorkFlow(
                name = "pv", rules = listOf(
                    GTRule(variable = 'a', value = 1716, operation = Reject),
                    Always(operation = Accept)
                )
            )
        )
        assertThat(system.workflows["crn"]).isEqualTo(
            Day19.WorkFlow(
                name = "crn", rules = listOf(
                    GTRule(variable = 'x', value = 2662, operation = Accept),
                    Always(operation = Reject)
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
                Part(x = 787, m = 2655, a = 1222, s = 2876) to Accept,
                Part(x = 1679, m = 44, a = 2067, s = 496) to Reject,
                Part(x = 2036, m = 264, a = 79, s = 2244) to Accept,
                Part(x = 2461, m = 1339, a = 466, s = 291) to Reject,
                Part(x = 2127, m = 1623, a = 2188, s = 1013) to Accept
            )
        )
    }

    @Test
    fun `can find accept paths`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val system = Day19.generatePartSystem(data)
        val acceptPaths = system.findAcceptPaths()
        assertThat(acceptPaths).hasSize(9)
        assertThat(acceptPaths[0]).containsExactly(
            LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            LTRule(variable = 'a', value = 2006, operation = MoveOperation(destination = "qkq")),
            LTRule(variable = 'x', value = 1416, operation = Accept)
        )
        assertThat(acceptPaths[1]).containsExactly(
            LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            LTRule(variable = 'a', value = 2006, operation = MoveOperation(destination = "qkq")),
            NotAnyOf(rules = listOf(LTRule(variable = 'x', value = 1416, operation = Accept))),
            GTRule(variable = 'x', value = 2662, operation = Accept)
        )
        assertThat(acceptPaths[2]).containsExactly(
            LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            And(listOf(
                NotAnyOf(listOf(
                    LTRule(variable = 'a', value = 2006, operation=MoveOperation(destination = "qkq"))
                )),
                GTRule(variable = 'm', value = 2090, operation = Accept)
            ))
        )
        assertThat(acceptPaths[3]).containsExactly(
            LTRule(variable = 's', value = 1351, operation=MoveOperation(destination = "px")),
            NotAnyOf(listOf(
                LTRule(variable = 'a', value = 2006, operation = MoveOperation(destination = "qkq")),
                GTRule(variable = 'm', value = 2090, operation = Accept))
            ),
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 537, operation = MoveOperation(destination = "gd")),
                GTRule(variable = 'x', value = 2440, operation = Reject))
            ),
        )
        // 4,5,6 don't change the range at all in this example, as everything leads to Accept
        assertThat(acceptPaths[4]).containsExactly(
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            )),
            GTRule(variable = 's', value = 2770, operation = MoveOperation(destination = "qs")),
            GTRule(variable = 's', value = 3448, operation = Accept)
        )
        // 5 and 6 could be combined, but I doubt it will make much difference
        assertThat(acceptPaths[5]).containsExactly(
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            )),
            GTRule(variable = 's', value = 2770, operation = MoveOperation(destination = "qs")),
            NotAnyOf(listOf(
                GTRule(variable = 's', value = 3448, operation = Accept),
            )),
            GTRule(variable = 'm', value = 1548, operation = Accept)
        )
        assertThat(acceptPaths[6]).containsExactly(
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            )),
            GTRule(variable = 's', value = 2770, operation = MoveOperation(destination = "qs")),
            NotAnyOf(listOf(
                GTRule(variable = 's', value = 3448, operation = Accept),
            )),
            NotAnyOf(listOf(
                GTRule(variable = 'm', value = 1548, operation = Accept),
            ))
        )
        assertThat(acceptPaths[7]).containsExactly(
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            )),
            And(listOf(
                NotAnyOf(listOf(
                    GTRule(variable = 's', value = 2770, operation = MoveOperation(destination = "qs")),
                )),
                LTRule(variable = 'm', value = 1801, operation = MoveOperation(destination = "hdj")),
            )),
            GTRule(variable = 'm', value = 838, operation = Accept)
        )
        assertThat(acceptPaths[8]).containsExactly(
            NotAnyOf(listOf(
                LTRule(variable = 's', value = 1351, operation = MoveOperation(destination = "px")),
            )),
            And(listOf(
                NotAnyOf(listOf(
                    GTRule(variable = 's', value = 2770, operation = MoveOperation(destination = "qs")),
                )),
                LTRule(variable = 'm', value = 1801, operation = MoveOperation(destination = "hdj")),
            )),
            NotAnyOf(listOf(
                GTRule(variable = 'm', value = 838, operation = Accept)
            )),
            NotAnyOf(listOf(
                GTRule(variable = 'a', value = 1716, operation = Reject)
            )),
        )

    }

    @Test
    fun `can find min max ranges`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val system = Day19.generatePartSystem(data)
        val ranges = system.findRanges()
        assertThat(ranges).containsExactly(
            XmasRanges(Pair(1, 1415), Pair(1, 4000), Pair(1, 2005), Pair(1, 1350)),
            XmasRanges(Pair(2663, 4000), Pair(1, 4000), Pair(1, 2005), Pair(1, 1350)),
            XmasRanges(Pair(1, 4000), Pair(2091, 4000), Pair(2006, 4000), Pair(1, 1350)),
            XmasRanges(Pair(1, 2440), Pair(1, 2090), Pair(2006, 4000), Pair(537, 1350)),
            XmasRanges(Pair(1, 4000), Pair(1, 4000), Pair(1, 4000), Pair(3449, 4000)),
            XmasRanges(Pair(1, 4000), Pair(1549, 4000), Pair(1, 4000), Pair(2771, 3448)),
            XmasRanges(Pair(1, 4000), Pair(1, 1548), Pair(1, 4000), Pair(2771, 3448)),
            XmasRanges(Pair(1, 4000), Pair(839, 1800), Pair(1, 4000), Pair(1351, 2770)),
            XmasRanges(Pair(1, 4000), Pair(1, 838), Pair(1, 1716), Pair(1351, 2770))
        )
    }

    @Test
    fun `can count all combinations`() {
        val data = resourceStrings("/2023/day19-test.txt")
        val system = Day19.generatePartSystem(data)
        val combinations = system.countCombinations()
        assertThat(combinations).isEqualTo(167409079868000L)
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
        assertThat(v).isEqualTo(167409079868000L)
    }

}