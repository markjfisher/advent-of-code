package net.fish

// Useful combinations/permutations implementations from
// https://medium.com/@voddan/a-handful-of-kotlin-permutations-7659c555d421

import java.util.*

interface Circular<T> : Iterable<T> {
    fun state(): T
    fun inc()
    fun isZero(): Boolean   // `true` in exactly one state
    fun hasNext(): Boolean  // `false` if the next state `isZero()`

    override fun iterator() : Iterator<T> {
        return object : Iterator<T> {
            var started = false

            override fun next(): T {
                if(started) {
                    inc()
                } else {
                    started = true
                }

                return state()
            }

            override fun hasNext() = this@Circular.hasNext()
        }
    }
}

class Ring(val size: Int) : Circular<Int> {
    private var state = 0

    override fun state() = state
    override fun inc() {state = (1 + state) % size}
    override fun isZero() = (state == 0)
    override fun hasNext() = (state != size - 1)

    init {
        assert(size > 0)
    }
}

abstract class CircularList<E, H: Circular<E>>(val size: Int) : Circular<List<E>> {
    protected abstract val state: List<H>  // state.size == size

    override fun inc() {
        state.forEach {
            it.inc()
            if(! it.isZero()) return
        }
    }

    override fun isZero() = state.all {it.isZero()}
    override fun hasNext() = state.any {it.hasNext()}
}

abstract class IntCombinations(size: Int) : CircularList<Int, Ring>(size)

class BinaryBits(N: Int) : IntCombinations(N) {
    override val state = Array(N) { Ring(2) }.toList()
    override fun state() = state.map {it.state()}.reversed()
}

class CombinationsOf(m: Int, n: Int) : IntCombinations(n) {
    override val state = Array(n) { Ring(m) }.toList()
    override fun state() = state.map {it.state()}.reversed()
}

class Permutations(N: Int) : IntCombinations(N) {
    override val state = mutableListOf<Ring>()

    init {
        for(i in N downTo 1) {
            state += Ring(i)
        }
    }

    override fun state(): List<Int> {
        val items = (0 until size).toCollection(LinkedList())
        return state.map {ring -> items.removeAt(ring.state())}
    }
}

fun main(args: Array<String>) {
    val n = 100
    val sumNumbers = Ring(n).sum()
    println("In theory, the sum of numbers 0..${n - 1} is ${n * (n - 1) / 2}.")
    println("Which is consistent with a practical result of $sumNumbers.\n")

    BinaryBits(5).asSequence().filter {it.sum() == 2}.take(5).forEach { println(it) }
    CombinationsOf(5, 2).asSequence().forEach { println(it) }

    println("\npermutations of 3 elements:")
    for(configuration in Permutations(3)) {
        println(configuration)
    }
}