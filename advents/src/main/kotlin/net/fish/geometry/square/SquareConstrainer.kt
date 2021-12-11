package net.fish.geometry.square

interface SquareConstrainer {
    fun constrain(item: Square?): Square?
}

open class DefaultSquareConstrainer: SquareConstrainer {
    override fun constrain(item: Square?): Square? {
        return item
    }

    override fun toString(): String = "DefaultSquareConstrainer"
}
