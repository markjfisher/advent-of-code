package net.fish.y2022

object DistressSignalProcessor {
    fun parseData(data: List<List<String>>): List<DSPPair> {
        return data.map { pair -> DSPPair(left = parseLine(pair[0]), right = parseLine(pair[1])) }
    }

    private fun parseLine(line: String): DSPList {
        var currentInputPointer = 0
        fun parse(current: DSPList? = null): DSPList {
            when (val c = line[currentInputPointer]) {
                '[' -> {
                    // start a new list
                    currentInputPointer++
                    val newList = DSPList(parent = current)
                    if (current != null) current.children += newList
                    return parse(newList)
                }
                ']' -> {
                    // close the current list, skip back to parent list unless ending
                    currentInputPointer++
                    if (current!!.parent == null) return current
                    return parse(current.parent!!)
                }
                ',' -> {
                    // list separator, just keep parsing
                    currentInputPointer++
                    return parse(current)
                }
                else -> {
                    // accumulate digits
                    currentInputPointer++
                    val chars = mutableListOf(c)
                    var nextChar = line[currentInputPointer]
                    while (nextChar != '[' && nextChar != ']' && nextChar != ',') {
                        chars += nextChar
                        currentInputPointer++
                        nextChar = line[currentInputPointer]
                    }
                    current!!.children += DSPValue(Integer.parseInt(chars.joinToString("")))
                    return parse(current)
                }
            }
        }

        return parse()
    }
}

data class DSPPair(val left: DSPList, val right: DSPList)

sealed class DistressSignalPacket
data class DSPValue(var value: Int): DistressSignalPacket()

class DSPList(
    var children: MutableList<DistressSignalPacket> = mutableListOf(),
    var parent: DSPList? = null
): DistressSignalPacket() {
    operator fun compareTo(other: DSPList): Int {
        children.forEachIndexed { i, dsp ->
            // right side out of elements, left is greater
            if (other.children.size < i + 1) return 1

            val otherValue = other.children[i]

            // both simple integers
            if (dsp is DSPValue && otherValue is DSPValue) {
                if (dsp.value != otherValue.value) return dsp.value - otherValue.value
            }

            // both are lists
            if (dsp is DSPList && otherValue is DSPList) {
                val listCompare = dsp.compareTo(otherValue)
                if (listCompare != 0) return listCompare
            }

            // they are different types
            if (dsp is DSPList && otherValue is DSPValue) {
                val listCompare = dsp.compareTo(DSPList(mutableListOf(otherValue)))
                if (listCompare != 0) return listCompare
            }
            if (dsp is DSPValue && otherValue is DSPList) {
                val listCompare = DSPList(mutableListOf(dsp)).compareTo(otherValue)
                if (listCompare != 0) return listCompare
            }
        }
        // do we still have elements in other? if so, left < right
        if (other.children.size > children.size) return -1

        // all tests done, must be same
        return 0
    }

    override fun toString(): String {
        return "[${children.joinToString(", ")}]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DSPList
        if (children != other.children) return false
        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }

    companion object : Comparator<DSPList> {
        override fun compare(o1: DSPList, o2: DSPList): Int {
            return o1.compareTo(o2)
        }
    }
}
