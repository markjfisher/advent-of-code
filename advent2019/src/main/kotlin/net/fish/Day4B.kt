package net.fish

/*
An Elf just remembered one more important detail: the two adjacent matching digits are not part of a larger group
of matching digits.

Given this additional criterion, but still ignoring the range rule, the following are now true:

112233 meets these criteria because the digits never decrease and all repeated digits are exactly two digits long.
123444 no longer meets the criteria (the repeated 44 is part of a larger group of 444).
111122 meets the criteria (even though 1 is repeated more than twice, it still contains a double 22).
How many different passwords within the range given in your puzzle input meet all of the criteria?

Your puzzle input is 234208-765869.
*/

class Day4B: Day4Base() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val passwords: List<Int> = Day4B().findPasswords(234208, 765869)
            println(passwords.size) // 814
        }
    }

    fun findPasswords(lower: Int, upper: Int): List<Int> {
        return (lower..upper)
            .filter { hasAdjacentDigits(it) }
            .filter { hasAtLeastOnePairOfAdjacentDigits(it) }
            .filter { allNumbersMonotonicallyIncrease(it) }
    }

}