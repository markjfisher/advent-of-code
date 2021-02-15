package net.fish.geometry.grid

interface GridItem {
    fun neighbours(): List<GridItem>
}
