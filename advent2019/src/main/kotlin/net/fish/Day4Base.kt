package net.fish

open class Day4Base {
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