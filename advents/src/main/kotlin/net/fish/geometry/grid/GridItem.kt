package net.fish.geometry.grid

interface GridItem {
    fun neighbours(): List<GridItem>
    fun cardinals(): List<GridItem> = emptyList()
    fun simpleValue(): String
}
