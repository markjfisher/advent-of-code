package net.fish.maths

    fun lcm(x: Long, y: Long): Long {
        var a = x
        var b = y
        while (a != 0L) {
            a = (b % a).also { b = a }
        }
        return x / b * y
    }

    fun Iterable<Long>.lcm(): Long = reduce { t, l -> lcm(t, l) }