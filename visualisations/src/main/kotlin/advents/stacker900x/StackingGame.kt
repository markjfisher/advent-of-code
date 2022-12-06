package advents.stacker900x

import com.github.ajalt.mordant.terminal.Terminal
import net.fish.resourceStrings
import net.fish.y2022.Day05

class StackingGame(private val stacks: Day05.Stacks) {
    private fun animate() {
        val t = Terminal()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val stacks = Day05.toStacks(resourceStrings(year = 2022, day = 5, trim = false))
            StackingGame(stacks).animate()
        }
    }
}