package net.fish.collections

fun <T> Sequence<T>.cycle() = sequence { while (true) yieldAll(this@cycle) }