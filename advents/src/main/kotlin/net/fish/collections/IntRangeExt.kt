package net.fish.collections

fun IntRange.fullyContains(t: IntRange): Boolean = this.first <= t.first && this.last >= t.last

fun IntRange.overlaps(t: IntRange): Boolean {
    return (this.first in t.first..t.last) || (this.last in t.first..t.last) ||
            (t.first in this.first..this.last) || (t.last in this.first..this.last)
}