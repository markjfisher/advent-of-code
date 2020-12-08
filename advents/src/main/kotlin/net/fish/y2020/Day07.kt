package net.fish.y2020

import net.fish.Day
import net.fish.resourceLines

object Day07 : Day {
    private val data = resourceLines(2020, 7)
    private val rules = toRules(data)

    override fun part1(): Int = doPart1("shiny gold", rules)
    override fun part2(): Int = doPart2("shiny gold", rules)

    fun toRules(data: List<String>): List<Rule> {
        return data.map { bagRule ->
            // Note to future self: Split the rules up for parts you want to find, this wasn't doable in single regex and you lost 2 hours of your life trying.
            // EDIT: this rule would work, but using 2 is easier to process.
            val fullExtractor = Regex("""([\p{Alpha} ]+) bags contain|(\d+) ([\p{Alpha} ]+) bag""")
            val bagExtractor = Regex("""^([\p{Alpha} ]+) bags contain""")
            val rulesExtractor = Regex("""([\d]+) ([\p{Alpha} ]+) bag""")

            val bag = bagExtractor.find(bagRule)?.destructured!!.let { (n) -> Bag(name = n) }
            val contains = rulesExtractor.findAll(bagRule).map { it.destructured.let { (num, bagName) -> Contain(num = num.toInt(), bag = Bag(bagName)) } }.toList()
            Rule(bag = bag, contains = contains)
        }
    }

    fun doPart1(bagName: String, rules: List<Rule>) = allBagsThatContain(Bag(bagName), rules).size

    private fun allBagsThatContain(bag: Bag, rules: List<Rule>): Set<Bag> {
        val wrappingBags = rulesThatContainBag(bag, rules).map { it.bag }.toSet()
        if (wrappingBags.isEmpty()) return emptySet()
        return wrappingBags + wrappingBags.flatMap { allBagsThatContain(it, rules) }.toSet()
    }

    private fun rulesThatContainBag(bag: Bag, rules: List<Rule>): List<Rule> {
        return rules.filter { rule -> rule.contains.any { it.hasBag(bag) } }
    }

    fun doPart2(bagName: String, rules: List<Rule>): Int = containedInBag(Bag(name = bagName), rules)

    private fun containedInBag(bag: Bag, rules: List<Rule>): Int {
        val rule = findRuleForBag(bag, rules)
        if (rule.contains.isEmpty()) return 0
        return rule.contains.sumBy {
            it.num * (1 + containedInBag(it.bag, rules))
        }
    }

    private fun findRuleForBag(bag: Bag, rules: List<Rule>): Rule {
        return rules.first { it.bag.name == bag.name }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Bag(val name: String)
    data class Rule(val bag: Bag, val contains: List<Contain>)
    data class Contain(val num: Int, val bag: Bag) {
        fun hasBag(bag: Bag) = this.bag == bag
    }
}


/*
You land at the regional airport in time for your next flight. In fact, it looks like you'll even have time to grab some food:
all flights are currently delayed due to issues in luggage processing.

Due to recent aviation regulations, many rules (your puzzle input) are being enforced about bags and their contents; bags must
be color-coded and must contain specific quantities of other color-coded bags. Apparently, nobody responsible for these regulations
considered how long they would take to enforce!

For example, consider the following rules:

light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.

These rules specify the required contents for 9 bag types. In this example, every faded blue bag is empty, every
vibrant plum bag contains 11 bags (5 faded blue and 6 dotted black), and so on.

You have a shiny gold bag. If you wanted to carry it in at least one other bag, how many different bag colors
would be valid for the outermost bag? (In other words: how many colors can, eventually, contain at least one shiny gold bag?)

In the above rules, the following options would be available to you:

A bright white bag, which can hold your shiny gold bag directly.
A muted yellow bag, which can hold your shiny gold bag directly, plus some other bags.
A dark orange bag, which can hold bright white and muted yellow bags, either of which could then hold your shiny gold bag.
A light red bag, which can hold bright white and muted yellow bags, either of which could then hold your shiny gold bag.
So, in this example, the number of bag colors that can eventually contain at least one shiny gold bag is 4.

How many bag colors can eventually contain at least one shiny gold bag? (The list of rules is quite long; make sure you get all of it.)

 */

/*
It's getting pretty expensive to fly these days - not because of ticket prices, but because of the ridiculous number of bags you need to buy!

Consider again your shiny gold bag and the rules from the above example:

faded blue bags contain 0 other bags.
dotted black bags contain 0 other bags.
vibrant plum bags contain 11 other bags: 5 faded blue bags and 6 dotted black bags.
dark olive bags contain 7 other bags: 3 faded blue bags and 4 dotted black bags.

So, a single shiny gold bag must contain 1 dark olive bag (and the 7 bags within it) plus 2 vibrant plum bags
(and the 11 bags within each of those): 1 + 1*7 + 2 + 2*11 = 32 bags!

Of course, the actual rules have a small chance of going several levels deeper than this example; be sure to count all of the bags,
even if the nesting becomes topologically impractical!

Here's another example:

shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags.

In this example, a single shiny gold bag must contain 126 other bags.

How many individual bags are required inside your single shiny gold bag?
 */