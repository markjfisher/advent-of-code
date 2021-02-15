package net.fish.geometry.grid

interface Grid {
    var width: Int
    var height: Int

    fun items(): Iterable<GridItem>
}