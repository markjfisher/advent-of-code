package net.fish.y2021

object SnailFishProcessor {
    fun convertToSnailFish(input: String): SnailFish {
        var currentInputPointer = 0
        fun parse(d: Int = 0, left: SnailFish? = null, right: SnailFish? = null): SnailFish {
            // there's no space to skip, we either have [, ], <int>, or ","
            when (val c = input[currentInputPointer]) {
                '[' -> {
                    currentInputPointer++
                    val newLeft = parse(d + 1)
                    currentInputPointer++
                    val newRight = parse(d + 1)
                    return parse(d, newLeft, newRight)
                }
                ']' -> {
                    currentInputPointer++
                    if (left == null || right == null) throw Exception("Bad data, currentPointer: $currentInputPointer, d: $d, left: $left, right: $right")
                    return SnailFishPair(left = left, right = right, depth = d)
                }
                ',' -> {
                    currentInputPointer++
                    // just skip this char, nothing special to do, the close bracket will deal with the end of the chain
                    return parse(d, left, right)
                }
                else -> {
                    // parse the next int until we hit a non int char. the initial input won't have this issue, but tests do, e.g. [10,0]
                    val chars = mutableListOf(c)
                    currentInputPointer++
                    var nextChar = input[currentInputPointer]
                    while (nextChar != '[' && nextChar != ']' && nextChar != ',') {
                        chars += nextChar
                        currentInputPointer++
                        nextChar = input[currentInputPointer]
                    }
                    return SnailFishValue(Integer.parseInt(chars.joinToString("")))
                }
            }
        }

        val pair = parse()
        pair.assignParent(null)
        return pair
    }

    fun add(left: SnailFish, right: SnailFish): SnailFish {
        // increment all depths, then return a new depth 0 pair
        left.incrementDepth()
        right.incrementDepth()
        val newRoot = SnailFishPair(left = left, right = right, depth = 0, parent = null)
        left.parent = newRoot
        right.parent = newRoot
        process(newRoot)
        return newRoot
    }

    fun findPairToExplode(pair: SnailFish): SnailFishPair? {
        if (pair !is SnailFishPair) return null
        if (pair.depth == 4) return pair
        val leftDepth4Pair = if (pair.left is SnailFishPair) {
            val leftPair = pair.left as SnailFishPair
            findPairToExplode(leftPair)
        } else null
        if (leftDepth4Pair != null) return leftDepth4Pair
        return if (pair.right is SnailFishPair) {
            val rightPair = pair.right as SnailFishPair
            findPairToExplode(rightPair)
        } else null
    }

    fun explode(pair: SnailFish) {
        // find the first child that has depth 4 in a dfs
        val firstDepth4Pair = findPairToExplode(pair) ?: return
        // safety check, data shouldn't trigger this though. we are expecting that any depth 4 pair ONLY has left/right values so we can explode them
        if (firstDepth4Pair.left !is SnailFishValue || firstDepth4Pair.right !is SnailFishValue) throw Exception("Found pair with child pairs: $firstDepth4Pair")

        val dfsValues = dfsValues(pair)

        // EXPLODE LEFT
        // we need to find the previous left value in the DFS list to add to
        val leftValueNode = firstDepth4Pair.left as SnailFishValue
        if (leftValueNode.value != 0) {
            val leftIndex = dfsValues.indexOfFirst { value -> value === leftValueNode }
            if (leftIndex > 0) {
                val previousValue = dfsValues[leftIndex - 1]
                previousValue.value += leftValueNode.value
            }
        }

        // EXPLODE RIGHT
        // we have to find the next value node to explode into
        val rightValueNode = (firstDepth4Pair.right as SnailFishValue)
        if (rightValueNode.value != 0) {
            val rightIndex = dfsValues.indexOfFirst { value -> value === rightValueNode }
            // This could be null if there's no value node to the right. skip over by 1
            val nextValue = dfsValues.getOrNull(rightIndex + 1)
            if (nextValue != null) {
                nextValue.value += rightValueNode.value
            }
        }

        // Now we can replace the depth 4 pair on the parent with a value of 0
        val parentPairOfDepth4Pair = firstDepth4Pair.parent as SnailFishPair
        val newZero = SnailFishValue(value = 0, parent = parentPairOfDepth4Pair)
        if (parentPairOfDepth4Pair.left === firstDepth4Pair) {
            parentPairOfDepth4Pair.left = newZero
        } else {
            parentPairOfDepth4Pair.right = newZero
        }
        // finally, recurse until there are none left to explode
        explode(pair)
    }

    fun split(pair: SnailFish) {
        val dfsValues = dfsValues(pair)
        val firstToSplit = dfsValues.firstOrNull { it.value >= 10 } ?: return

        val splitLeftValue = firstToSplit.value / 2
        val splitRightValue = firstToSplit.value - splitLeftValue

        val newPair = SnailFishPair(
            depth = (firstToSplit.parent as SnailFishPair).depth + 1,
            left = SnailFishValue(value = splitLeftValue),
            right = SnailFishValue(value = splitRightValue),
            parent = firstToSplit.parent
        )
        newPair.left.parent = newPair
        newPair.right.parent = newPair

        // replace the old value node with the new pair in the parent
        val parentOfValueToSplit = firstToSplit.parent as SnailFishPair
        if (parentOfValueToSplit.left == firstToSplit) {
            parentOfValueToSplit.left = newPair
        } else {
            parentOfValueToSplit.right = newPair
        }

    }

    fun process(pair: SnailFish) {
        // explode it, and split it until it doesn't change any more.
        // I was using magnitude, but that didn't catch some cases, so straight strings FTW
        var hasFinished = false
        while (!hasFinished) {
            val valueBefore = pair.toString()
            explode(pair)
            split(pair)
            if (pair.toString() == valueBefore) hasFinished = true
        }
    }

    fun process(inputs: List<String>): SnailFish {
        val snails = inputs.map { convertToSnailFish(it) }
        return snails.reduce { acc, snailFish ->
            add(acc, snailFish)
        }
    }

    fun dfsList(node: SnailFish): List<SnailFish> {
        fun doDFS(node: SnailFish, builtList: MutableList<SnailFish>) {
            builtList.add(node)
            if (node is SnailFishValue) {
                return
            }
            node as SnailFishPair
            doDFS(node.left, builtList)
            doDFS(node.right, builtList)
        }

        val builtList = mutableListOf<SnailFish>()
        doDFS(node, builtList)
        return builtList
    }

    fun dfsValues(node: SnailFish): List<SnailFishValue> {
        return dfsList(node).filterIsInstance<SnailFishValue>()
    }
}

sealed class SnailFish(
    open var parent: SnailFish?
) {
    abstract fun magnitude(): Long
    abstract fun incrementDepth()
    abstract fun assignParent(parent: SnailFish?)
}

class SnailFishPair(
    var left: SnailFish,
    var right: SnailFish,
    var depth: Int,
    override var parent: SnailFish? = null
) : SnailFish(parent) {
    override fun magnitude(): Long {
        return 3L * left.magnitude() + 2L * right.magnitude()
    }

    override fun incrementDepth() {
        left.incrementDepth()
        right.incrementDepth()
        depth++
    }

    override fun assignParent(parent: SnailFish?) {
        this.parent = parent
        left.assignParent(this)
        right.assignParent(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as SnailFishPair
        return this.left == other.left && this.right == other.right
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + depth
        return result
    }

    override fun toString(): String {
        return "[${left},${right}]"
    }
}

class SnailFishValue(
    var value: Int,
    override var parent: SnailFish? = null
) : SnailFish(parent) {
    override fun magnitude(): Long {
        return value.toLong()
    }

    // values don't have a depth, only pairs
    override fun incrementDepth() {}

    override fun assignParent(parent: SnailFish?) {
        this.parent = parent
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as SnailFishValue
        return this.value == other.value
    }

    override fun hashCode(): Int {
        return value
    }

}