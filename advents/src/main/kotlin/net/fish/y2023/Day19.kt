package net.fish.y2023

import net.fish.Day
import net.fish.resourceStrings

object Day19 : Day {
    private val partExtractor by lazy { Regex("""x=(\d+),m=(\d+),a=(\d+),s=(\d+)""") }
    private val data by lazy { resourceStrings(2023, 19) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val system = generatePartSystem(data)
        val states = system.processParts()
        return states.filter { it.value == Accept }.keys.sumOf { it.total() }
    }

    fun doPart2(data: List<String>): Long = 0L

    sealed class RuleOperation
    object Accept : RuleOperation() {
        override fun toString() = "Accept"
    }
    object Reject : RuleOperation() {
        override fun toString() = "Reject"
    }
    data class MoveOperation(val destination: String) : RuleOperation()

    sealed class Rule(open val operation: RuleOperation) {
        abstract fun check(part: Part): Boolean
        fun partValue(variable: Char, part: Part): Int = when (variable) {
            'x' -> part.x
            'm' -> part.m
            'a' -> part.a
            's' -> part.s
            else -> throw Exception("Unknown variable: $variable in rule")
        }
    }

    data class LTRule(val variable: Char, val value: Int, override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = partValue(variable, part) < value
    }

    data class GTRule(val variable: Char, val value: Int, override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = partValue(variable, part) > value
    }

    data class Always(override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = true
    }

    data class WorkFlow(val name: String, val rules: List<Rule>)
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun total() = x + m + a + s
    }

    data class PartSystem(val workflows: Map<String, WorkFlow>, val parts: List<Part>) {
        fun processParts(): Map<Part, RuleOperation> {
            fun process(part: Part, workflowName: String, visited: List<String>): RuleOperation {
                if (visited.contains(workflowName)) throw Exception("Circular movement. Part $part has already visited $workflowName, ${visited.joinToString("-> ")}")
                val workflow = workflows[workflowName] ?: throw Exception("Couldn't find workflow named $workflowName")
                val rule = workflow.rules.first { it.check(part) }
                return when (rule.operation) {
                    is MoveOperation -> process(part, (rule.operation as MoveOperation).destination, visited + workflowName)
                    else -> rule.operation
                }
            }

            return parts.associateWith { process(it, "in", emptyList()) }
        }

        fun findAcceptPaths(): List<List<RuleOperation>> {

            return emptyList()
        }
    }

    fun generatePartSystem(data: List<String>): PartSystem {
        val workflowStrings = data[0].split("\n")
        val workflows = workflowStrings.associate { line ->
            val nameRulesSplit = line.split("{")
            val workflowName = nameRulesSplit[0]
            val rulesString = nameRulesSplit[1].dropLast(1)
            val rulesSplit = rulesString.split(",")
            val rules = rulesSplit.map { rulePart ->
                if (rulePart.contains(":")) {
                    val conditionActionSplit = rulePart.split(":")
                    val action = when (val actionPart = conditionActionSplit[1]) {
                        "A" -> Accept
                        "R" -> Reject
                        else -> MoveOperation(actionPart)
                    }

                    val conditionPart = conditionActionSplit[0]
                    val rule = if (conditionPart.contains("<")) {
                        LTRule(variable = conditionPart.first(), value = conditionPart.substring(2).toInt(), operation = action)
                    } else if (conditionPart.contains(">")) {
                        GTRule(variable = conditionPart.first(), value = conditionPart.substring(2).toInt(), operation = action)
                    } else {
                        throw Exception("Bad data at $rulePart, contains ':' but no < or >")
                    }
                    rule
                } else {
                    val operation = when (rulePart) {
                        "A" -> Accept
                        "R" -> Reject
                        else -> MoveOperation(rulePart)
                    }
                    Always(operation)
                }
            }
            workflowName to WorkFlow(workflowName, rules)
        }

        val partStrings = data[1].split("\n")
        val parts = partStrings.map { line ->
            partExtractor.find(line)?.destructured!!.let { (x, m, a, s) ->
                Part(x = x.toInt(), m = m.toInt(), a = a.toInt(), s = s.toInt())
            }
        }

        return PartSystem(workflows, parts)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}