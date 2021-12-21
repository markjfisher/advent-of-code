package net.fish.geometry.projection

import net.fish.geometry.grid.Grid
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridItemAxis
import net.fish.geometry.grid.GridType
import net.fish.geometry.paths.PathCreator

interface SurfaceMapper {
    fun itemToObj(item: GridItem): List<String>
    fun itemAxis(item: GridItem): GridItemAxis

    fun grid(): Grid

    fun mappingType(): GridType
}

interface PathingSurfaceMapper: SurfaceMapper {
    var pathCreator: PathCreator
}