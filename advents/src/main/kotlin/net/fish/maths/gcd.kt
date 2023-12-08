package net.fish.maths

fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}