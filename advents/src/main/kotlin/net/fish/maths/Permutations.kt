package net.fish.maths

// Useful combinations/permutations implementations from
// https://medium.com/@voddan/a-handful-of-kotlin-permutations-7659c555d421

import java.util.*

interface Circular<T> : Iterable<T> {
    fun state(): T
    fun inc()
    fun isZero(): Boolean   // this is true when at the initial state, e.g. 0 in 0..n-1
    fun hasNext(): Boolean  // this is false if the next state is back to the beginning, e.g. n-1 in 0..n-1

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var started = false

            override fun next(): T {
                if (started) inc() else started = true
                return state()
            }

            override fun hasNext() = this@Circular.hasNext()
        }
    }
}

class Ring(val size: Int, private val offset: Int = 0) : Circular<Int> {
    private var state = 0

    // offset allows change of usual "0..n-1" to "offset .. (n-1) + offset"
    // eg. ranges from "-1 to 1" instead of "0 to 2"
    override fun state() = state + offset
    override fun inc() {
        state = (1 + state) % size
    }

    override fun isZero() = (state == 0)
    override fun hasNext() = (state != size - 1)

    // check user isn't insane
    init { assert(size > 0) }
}

abstract class CircularList<E, H : Circular<E>>(val size: Int) : Circular<List<E>> {
    protected abstract val state: List<H>

    override fun inc() {
        state.forEach {
            it.inc()
            if (!it.isZero()) return
        }
    }

    override fun isZero() = state.all { it.isZero() }
    override fun hasNext() = state.any { it.hasNext() }
}

abstract class IntCombinations(size: Int) : CircularList<Int, Ring>(size)

class BinaryBits(N: Int) : IntCombinations(N) {
    override val state = Array(N) { Ring(2) }.toList()
    override fun state() = state.map { it.state() }.reversed()
}

class D6(N: Int) : IntCombinations(N) {
    override val state = Array(N) { Ring(6, 1) }.toList()
    override fun state() = state.map { it.state() }.reversed()
}

class AroundSpace(N: Int) : IntCombinations(N) {
    override val state = Array(N) { Ring(3, -1) }.toList()
    override fun state() = state.map { it.state() }.reversed()
}

class Permutations(N: Int) : IntCombinations(N) {
    override val state = mutableListOf<Ring>()

    init { for (i in N downTo 1) state += Ring(i) }

    override fun state(): List<Int> {
        val items = (0 until size).toCollection(LinkedList())
        return state.map { ring -> items.removeAt(ring.state()) }
    }
}

fun main() {
    val n = 100
    val sumNumbers = Ring(n).sum()
    println("In theory, the sum of numbers 0..${n - 1} is ${n * (n - 1) / 2}.")
    println("Which is consistent with a practical result of $sumNumbers.\n")

    println("binary bits 5, sum = 2")
    BinaryBits(5).asSequence().filter { it.sum() == 2 }.take(5).forEach { println(it) }

    println("binary bits 2")
    BinaryBits(2).toList().forEach { println(it) }

    println("around 3d")
    val as3 = AroundSpace(3).toList()
    println("as3 size: ${as3.size}")
    as3.forEach { println(it) }

    println("around 4d")
    val as4 = AroundSpace(4).toList()
    println("as4 size: ${as4.size}")
    as4.forEach { println(it) }

    println("around 5d")
    val as5 = AroundSpace(5).toList()
    println("as5 size: ${as5.size}")

    println("around 6d")
    val as6 = AroundSpace(6).toList()
    println("as6 size: ${as6.size}")

    println("\npermutations of 3 elements:")
    for (configuration in Permutations(3)) {
        println(configuration)
    }

    println("d6 combinations")
    for (d6 in D6(2)) {
        println(d6)
    }
    val d6x3 = D6(3).filter { dice -> dice.count { it >= 5 } >= 2 }
    println("d6x3: ${d6x3.count()} : $d6x3")

}

// Faster iterations.
fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> =
    sequence {
        val pool = this@combinations as? List<T> ?: toList()
        val n = pool.size
        if (length > n) return@sequence
        val indices = IntArray(length) { it }
        while (true) {
            yield(indices.map { pool[it] })
            var i = length
            do {
                i--
                if (i == -1) return@sequence
            } while (indices[i] == i + n - length)
            indices[i]++
            for (j in i + 1 until length) indices[j] = indices[j - 1] + 1
        }
    }

// Faster permutations.
fun <T> Iterable<T>.permutations(length: Int? = null): Sequence<List<T>> =
    sequence {
        val pool = this@permutations as? List<T> ?: toList()
        val n = pool.size
        val r = length ?: n
        if (r > n) return@sequence
        val indices = IntArray(n) { it }
        val cycles = IntArray(r) { n - it }
        yield(List(r) { pool[indices[it]] })
        if (n == 0) return@sequence
        cyc@ while (true) {
            for (i in r - 1 downTo 0) {
                cycles[i]--
                if (cycles[i] == 0) {
                    val temp = indices[i]
                    for (j in i until n - 1) indices[j] = indices[j + 1]
                    indices[n - 1] = temp
                    cycles[i] = n - i
                } else {
                    val j = n - cycles[i]
                    indices[i] = indices[j].also { indices[j] = indices[i] }
                    yield(List(r) { pool[indices[it]] })
                    continue@cyc
                }
            }
            return@sequence
        }
    }