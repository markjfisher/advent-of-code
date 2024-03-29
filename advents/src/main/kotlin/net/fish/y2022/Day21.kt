package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day21 : Day {
    private val data by lazy { resourceLines(2022, 21) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)
    // override fun part2() = doPart2WithSolver(data)

    fun doPart1(data: List<String>): Long {
        val monkeys = parseMonkeys(data).toMutableMap()
        solve("root", monkeys)
        return (monkeys["root"] as ValueMonkey).value.toLong()
    }

    // Use binary search to find answer. First time this ran was rather long to get a decent boundary
    fun doPart2(data: List<String>, lb: Long = 3000000000000L, ub: Long = 4000000000000L): Long {
        // replace root's operation with SUB, and solve with humn values until root is 0
        val monkeysStart = parseMonkeys(data).toMutableMap().let {
            val root = it["root"] as MathsMonkey
            it["root"] = MathsMonkey(root.name, root.left, root.right, MathsOperation.SUB)
            it
        }.toMap()

        var lower = lb
        var upper = ub

        var oldMid = 0L
        while(true) {
            val monkeys = monkeysStart.toMutableMap()
            val mid: Long = (lower + upper) / 2
            if (oldMid == mid) throw Exception("solution not within bounds: $lb, $ub")
            oldMid = mid
            monkeys["humn"] = ValueMonkey("humn", mid.toDouble())
            val solved = solve("root", monkeys)
            if (!solved) throw Exception("can't solve") // TODO not needed yet
            val r = (monkeys["root"] as ValueMonkey).value.toLong()
            if (r == 0L) return mid
            if (r > 0L) lower = mid else upper = mid
        }
    }

    // WIP
    fun doPart2WithSolver(data: List<String>): Long {
        val monkeys = parseMonkeys(data).toMutableMap()
        monkeys.remove("humn")

        val root = monkeys["root"] as MathsMonkey
        val left = root.left
        val right = root.right

        val canSolveLeft = solve(left, monkeys)
        val canSolveRight = solve(right, monkeys)
        if (!canSolveLeft && !canSolveRight) throw Exception("Harder than thought")

        val monkeyToSolve = if(canSolveLeft) right else left
        val monkeySolved = if(canSolveLeft) left else right
        val targetValue = (monkeys[monkeySolved] as ValueMonkey).value

        // we have some formula of type

        return 0L
    }

    fun parseMonkeys(data: List<String>): Map<String, Monkey> {
        return data.associate { line ->
            val parts = line.split(" ")
            val name = parts[0].substringBefore(':')
            val monkey = if (parts.size == 2) {
                ValueMonkey(name, parts[1].toDouble())
            } else {
                val op = when (parts[2]) {
                    "+" -> MathsOperation.ADD
                    "-" -> MathsOperation.SUB
                    "/" -> MathsOperation.DIV
                    "*" -> MathsOperation.MUL
                    else -> throw Exception("Unknown op: $parts[2]")
                }
                MathsMonkey(name, parts[1], parts[3], op)
            }
            name to monkey
        }
    }

    fun solve(name: String, monkeys: MutableMap<String, Monkey>): Boolean {
        var solved = false
        var canSolve = true
        while(!solved && canSolve) {
            val mathsMonkeyCount = monkeys.count { it.value is MathsMonkey }
            monkeys.forEach { (name, monkey) ->
                if (monkey is MathsMonkey) {
                    if (monkeys[monkey.left] is ValueMonkey && monkeys[monkey.right] is ValueMonkey) {
                        val m1 = monkeys[monkey.left] as ValueMonkey
                        val m2 = monkeys[monkey.right] as ValueMonkey
                        // repace maths monkey with value
                        val value = when(monkey.op) {
                            MathsOperation.ADD -> m1.value + m2.value
                            MathsOperation.SUB -> m1.value - m2.value
                            MathsOperation.MUL -> m1.value * m2.value
                            MathsOperation.DIV -> m1.value / m2.value
                        }
                        monkeys[name] = ValueMonkey(name, value)
                    }
                }
            }
            if (mathsMonkeyCount == monkeys.count { it.value is MathsMonkey }) canSolve = false
            solved = monkeys[name] is ValueMonkey
        }
        return solved
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}

enum class MathsOperation { ADD, MUL, DIV, SUB }
sealed class Monkey
// Double required for P2 as the division of Longs leads to multiple results in binary search.
data class ValueMonkey(val name: String, val value: Double): Monkey()
data class MathsMonkey(val name: String, val left: String, val right: String, val op: MathsOperation): Monkey()