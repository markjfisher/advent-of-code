package net.fish.y2019

import net.fish.Day

object Day04: Day {

    override fun part1() = findPasswords(234208, 765869).size

    override fun part2() = findPasswords(234208, 765869)
        .filter { hasAtLeastOnePairOfAdjacentDigits(it) }
        .size

    private fun findPasswords(lower: Int, upper: Int): List<Int> {
        return (lower..upper)
            .filter { hasAdjacentDigits(it) }
            .filter { allNumbersMonotonicallyIncrease(it) }
    }

    fun allNumbersMonotonicallyIncrease(number: Int): Boolean {
        val asList: List<Int> = convertNumberToListOfDigits(number)
        return isMonotonicIncreasing(asList)
    }

    private fun isMonotonicIncreasing(list: List<Int>): Boolean {
        return list.foldIndexed(true) { index, isMonotonic, item ->
            if (!isMonotonic) false
            else {
                if (index > 0) item >= list[index - 1] else true
            }
        }
    }

    fun hasAdjacentDigits(number: Int): Boolean {
        val asList: List<Int> = convertNumberToListOfDigits(number)
        return runLengths(asList).any { it > 1 }
    }

    fun hasAtLeastOnePairOfAdjacentDigits(number: Int): Boolean {
        val asList: List<Int> = convertNumberToListOfDigits(number)
        return runLengths(asList).filter { it == 2 }.count() >= 1
    }

    fun convertNumberToListOfDigits(number: Int): List<Int> {
        return number.toString().map { it - '0' }
    }

    fun runLengths(list: List<Int>): List<Int> = runLengths(list, mutableListOf())

    private fun runLengths(list: List<Int>, counts: MutableList<Int>): List<Int> {
        if (list.isEmpty()) return counts.toList()
        val count = list.takeWhile { it == list[0] }.count()
        counts.add(count)
        return runLengths(list.drop(count), counts)
    }

}

/*
You arrive at the Venus fuel depot only to discover it's protected by a password.
The Elves had written the password on a sticky note, but someone threw it out.

However, they do remember a few key facts about the password:

It is a six-digit number.
The value is within the range given in your puzzle input.
Two adjacent digits are the same (like 22 in 122345).
Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123 or 135679).
Other than the range rule, the following are true:

111111 meets these criteria (double 11, never decreases).
223450 does not meet these criteria (decreasing pair of digits 50).
123789 does not meet these criteria (no double).
How many different passwords within the range given in your puzzle input meet these criteria?

Your puzzle input is 234208-765869.
*/

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
