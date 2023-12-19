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

    fun doPart2(data: List<String>): Long {
        val system = Day19.generatePartSystem(data)
        return system.countCombinations()
    }

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

        fun decreaseUpper(variable: Char, maxValue: Int, ranges: XmasRanges): XmasRanges {
            when (variable) {
                'x' -> ranges.x = Pair(ranges.x.first, minOf(ranges.x.second, maxValue))
                'm' -> ranges.m = Pair(ranges.m.first, minOf(ranges.m.second, maxValue))
                'a' -> ranges.a = Pair(ranges.a.first, minOf(ranges.a.second, maxValue))
                's' -> ranges.s = Pair(ranges.s.first, minOf(ranges.s.second, maxValue))
                else -> throw Exception("Unknown variable: $variable in rule")
            }
            return ranges
        }

        fun increaseLower(variable: Char, minValue: Int, ranges: XmasRanges): XmasRanges {
            when (variable) {
                'x' -> ranges.x = Pair(maxOf(ranges.x.first, minValue), ranges.x.second)
                'm' -> ranges.m = Pair(maxOf(ranges.m.first, minValue), ranges.m.second)
                'a' -> ranges.a = Pair(maxOf(ranges.a.first, minValue), ranges.a.second)
                's' -> ranges.s = Pair(maxOf(ranges.s.first, minValue), ranges.s.second)
                else -> throw Exception("Unknown variable: $variable in rule")
            }
            return ranges
        }

        abstract fun applyCondition(range: XmasRanges): XmasRanges
    }

    data class LTRule(val variable: Char, val value: Int, override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = partValue(variable, part) < value
        override fun applyCondition(range: XmasRanges): XmasRanges {
            // in order for this to be true, we have to reduce upper range of appropriate variable to value-1
            return decreaseUpper(variable, value - 1, range)
        }
    }

    data class GTRule(val variable: Char, val value: Int, override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = partValue(variable, part) > value
        override fun applyCondition(range: XmasRanges): XmasRanges {
            // in order for this to be true, we have to increase lower range of appropriate variable to value+1
            return increaseLower(variable, value + 1, range)
        }
    }

    data class Always(override val operation: RuleOperation) : Rule(operation) {
        override fun check(part: Part): Boolean = true
        override fun applyCondition(range: XmasRanges): XmasRanges = range
    }

    // P2 processing, an "always" turns into this for previous rules
    data class NotAnyOf(val rules: List<Rule>): Rule(Accept) {
        override fun check(part: Part): Boolean = true // not used
        override fun applyCondition(range: XmasRanges): XmasRanges {
            // we have a list of rules that we need to do opposite of
            // so "a < 1000" becomes "a > 999"
            //    "a > 1000" becomes "a < 1001"
            // , then we can apply them as usual

            return rules.fold(range) { r, rule ->
                val inverted = when (rule) {
                    is GTRule -> LTRule(rule.variable, rule.value + 1, rule.operation)
                    is LTRule -> GTRule(rule.variable, rule.value - 1, rule.operation)
                    else -> throw Exception("rule was not GT or LT when inverting: $rule")
                }
                inverted.applyCondition(r)
            }
        }
    }

    // P2 processing, anything after first rule in list has to negate the previous rules in the list
    data class And(val rules: List<Rule>): Rule(Accept) {
        override fun check(part: Part): Boolean = true
        override fun applyCondition(range: XmasRanges): XmasRanges {
            // apply all the rules in the And clause together
            return rules.fold(range) { r, rule -> rule.applyCondition(r) }
        }
    }

    data class WorkFlow(val name: String, val rules: List<Rule>)
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun total() = x + m + a + s
    }

    data class XmasRanges(var x: Pair<Int, Int>, var m: Pair<Int, Int>, var a: Pair<Int, Int>, var s: Pair<Int, Int>)

    data class PartSystem(val workflows: Map<String, WorkFlow>, val parts: List<Part>) {
        private val pass: Unit = Unit
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

        fun findAcceptPaths(currentWorkflow: String = "in", currentPath: List<Rule> = emptyList()): List<List<Rule>> {
            val workflow = workflows[currentWorkflow] ?: throw Exception("Workflow $currentWorkflow not found")
            val paths = mutableListOf<List<Rule>>()

            workflow.rules.forEachIndexed { ruleIndex, rule ->
                // we need to look back at previous rules in the list to negate their conditions
                val convertedRule = when (rule) {
                    is Always -> NotAnyOf(workflow.rules.dropLast(1))
                    else -> {
                        // negate all the previous rules if index > 0.
                        // This will never hit an Always, as that's the final entry, and already catered for
                        if (ruleIndex > 0) {
                            val previousRules = buildList {
                                (0 until ruleIndex).forEach { i ->
                                    add(workflow.rules[i])
                                }
                            }
                            And(listOf(NotAnyOf(previousRules), rule))
                        } else {
                            rule
                        }
                    }
                }
                when (val operation = rule.operation) {
                    is Accept -> paths.add(currentPath + convertedRule)
                    is MoveOperation -> paths.addAll(findAcceptPaths(operation.destination, currentPath + convertedRule))
                    is Reject -> pass
                }
            }
            return paths
        }

        fun findRanges(): List<XmasRanges> {
            fun limit(rules: List<Rule>): XmasRanges {
                return rules.fold(XmasRanges(Pair(1, 4000), Pair(1, 4000), Pair(1, 4000), Pair(1, 4000))) { range, rule ->
                    rule.applyCondition(range)
                }
            }

            val paths = findAcceptPaths()
            return paths.map { path -> limit(path) }
        }

        fun countCombinations(): Long {
            val ranges = findRanges()
            val summed = ranges.fold(0L) { total, range ->
                total + (range.x.second - range.x.first + 1L) * (range.m.second - range.m.first + 1L) * (range.a.second - range.a.first + 1L) * (range.s.second - range.s.first + 1L)
            }
            return summed
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