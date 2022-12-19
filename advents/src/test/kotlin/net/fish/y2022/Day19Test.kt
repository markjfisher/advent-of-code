package net.fish.y2022

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class Day19Test {
    @Test
    fun `can read blueprints`() {
        assertThat(Day19.toBlueprints(resourcePath("/2022/day19-test.txt"))).containsExactly(
            Day19.Blueprint(1, 4, 2, Day19.ObsidianRobotCost(3, 14), Day19.GeodeRobotCost(2, 7)),
            Day19.Blueprint(2, 2, 3, Day19.ObsidianRobotCost(3, 8), Day19.GeodeRobotCost(3, 12))
        )
    }

    @Test
    fun `score blueprints`() {
        val blueprints = Day19.toBlueprints(resourcePath("/2022/day19-test.txt"))
        assertThat(Day19.score(blueprints[0], 24)).isEqualTo(9)
        assertThat(Day19.score(blueprints[1], 24)).isEqualTo(12)
    }

    @Test
    fun `can do part 1`() {
        val blueprints = Day19.toBlueprints(resourcePath("/2022/day19-test.txt"))
        assertThat(Day19.doPart1(blueprints)).isEqualTo(33)
    }

    @Disabled("Takes too long to run")
    @Test
    fun `can do part 2`() {
        val blueprints = Day19.toBlueprints(resourcePath("/2022/day19-test.txt"))
        assertThat(Day19.doPart2(blueprints)).isEqualTo(56 * 62)
    }

}