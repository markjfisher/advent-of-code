package net.fish.collections

// kotlin.collections

inline fun <T> Array<out T>.takeWhileInclusive(
    predicate: (T) -> Boolean
): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun ByteArray.takeWhileInclusive(
    predicate: (Byte) -> Boolean
): List<Byte> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun ShortArray.takeWhileInclusive(
    predicate: (Short) -> Boolean
): List<Short> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun IntArray.takeWhileInclusive(
    predicate: (Int) -> Boolean
): List<Int> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun LongArray.takeWhileInclusive(
    predicate: (Long) -> Boolean
): List<Long> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun FloatArray.takeWhileInclusive(
    predicate: (Float) -> Boolean
): List<Float> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun DoubleArray.takeWhileInclusive(
    predicate: (Double) -> Boolean
): List<Double> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun BooleanArray.takeWhileInclusive(
    predicate: (Boolean) -> Boolean
): List<Boolean> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun CharArray.takeWhileInclusive(
    predicate: (Char) -> Boolean
): List<Char> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun <T> Iterable<T>.takeWhileInclusive(
    predicate: (T) -> Boolean
): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

// kotlin.sequences

fun <T> Sequence<T>.takeWhileInclusive(
    predicate: (T) -> Boolean
): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

// kotlin.text

inline fun CharSequence.takeWhileInclusive(
    predicate: (Char) -> Boolean
): CharSequence {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

inline fun String.takeWhileInclusive(
    predicate: (Char) -> Boolean
): String {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}