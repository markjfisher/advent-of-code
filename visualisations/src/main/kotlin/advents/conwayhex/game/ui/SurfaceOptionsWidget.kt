package advents.conwayhex.game.ui

import commands.CreateSurface
import commands.KeyCommand
import commands.SetGlobalAlpha
import imgui.ImGui
import imgui.dsl
import imgui.dsl.collapsingHeader
import net.fish.geometry.grid.GridType
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.projection.Surface

object SurfaceOptionsWidget {
    private fun createData(surface: Surface): SurfaceData {
        val gridWidth = surface.surfaceData["width"]!!.toInt()
        val gridHeight = surface.surfaceData["height"]!!.toInt()
        val gridType = GridType.from(surface.surfaceData["gridType"]!!)!!
        val gridSize = intArrayOf(gridWidth, gridHeight, 2000, ConwayOptions.MAX_M_BY_N / gridHeight.coerceAtLeast(2))
        return SurfaceData(gridWidth, gridHeight, gridType, gridSize)
    }

    operator fun invoke(surfaceOptions: SurfaceOptions, stateChangeFunction: (KeyCommand) -> Unit) {
        var currentSurface = surfaceOptions.surface
        var surfaceData = createData(currentSurface)

        collapsingHeader("Surface") {
            if (ImGui.sliderFloat("Global Alpha", surfaceOptions::globalAlpha, 0f, 1f)) {
                stateChangeFunction(SetGlobalAlpha)
            }

            if (ImGui.dragInt2("Grid Size", surfaceData.size, 2f, 2, 2000)) {
                var newWidth = surfaceData.size[0].coerceAtLeast(2)
                if (newWidth % 2 == 1) newWidth--
                var maxV = ConwayOptions.MAX_M_BY_N / newWidth
                if (maxV % 2 == 1) maxV--
                currentSurface.surfaceData["width"] = newWidth.toString()
                val newHeight = surfaceData.size[1].coerceAtMost(maxV).coerceAtLeast(2)
                currentSurface.surfaceData["height"] = newHeight.toString()
            }
            surfaceOptions.surfaces.forEachIndexed { i, surface ->
                if (ImGui.selectable(String.format("%1d. %s", i + 1, surface.name), currentSurface.name == surface.name)) {
                    currentSurface = surfaceOptions.surfaces[i].copy()
                    surfaceOptions.surface = currentSurface.copy()
                    surfaceData = createData(currentSurface)
                    println("changed to $currentSurface")
                }
            }
            if (surfaceData.gridType == GridType.HEX) {
                val orientation = Orientation.ORIENTATION.from(currentSurface.surfaceData["orientation"]!!)
                dsl.radioButton("Pointy", orientation == POINTY) {
                    currentSurface.surfaceData["orientation"] = POINTY.toString()
                }
                ImGui.sameLine()
                dsl.radioButton("Flat", orientation == FLAT) {
                    currentSurface.surfaceData["orientation"] = FLAT.toString()
                }
            }
            ImGui.separator()
            // for the given surface type, show its controls
//            when (currentSurface) {
//                is SimpleTorusSurface -> {
//                    ImGui.sliderFloat("Major Radius", currentSurface::majorRadius, 0.0001f, 20f)
//                }
//                is TorusKnotSurface -> {
//                    ImGui.sliderInt("p", currentSurface::p, 1, 20)
//                    ImGui.sliderInt("q", currentSurface::q, 1, 20)
//
//                    ImGui.sliderFloat("a", currentSurface::a, 0.1f, 2f)
//                    ImGui.sliderFloat("b", currentSurface::b, 0.1f, 2f)
//                }
//                is EpitrochoidSurface -> {
//                    ImGui.inputFloat("a", currentSurface::a, 0.1f, 10f, "%.2f")
//                    ImGui.inputFloat("b", currentSurface::b, 0.1f, 10f, "%.2f")
//                    ImGui.inputFloat("c", currentSurface::c, 0.1f, 10f, "%.2f")
//                }
//                is ThreeFactorParametricSurface -> {
//                    ImGui.inputInt("a", currentSurface::a, 0, 20)
//                    ImGui.inputInt("b", currentSurface::b, 0, 20)
//                    ImGui.inputInt("c", currentSurface::c, 0, 20)
//                }
//                else -> {
//                    // todo
//                }
//            }
//            when (currentSurfaceMapper.pathCreator) {
//                is DecoratedTorusKnotPathCreator -> {
//                    var pattern = (currentSurfaceMapper.pathCreator as DecoratedTorusKnotPathCreator).pattern
//                    dsl.radioButton("4b", pattern == DecoratedKnotType.Type4b) { pattern = DecoratedKnotType.Type4b }
//                    ImGui.sameLine()
//                    dsl.radioButton("7a", pattern == DecoratedKnotType.Type7a) { pattern = DecoratedKnotType.Type7a }
//                    ImGui.sameLine()
//                    dsl.radioButton("7b", pattern == DecoratedKnotType.Type7b) { pattern = DecoratedKnotType.Type7b }
//                    ImGui.sameLine()
//                    dsl.radioButton("10b", pattern == DecoratedKnotType.Type10b) { pattern = DecoratedKnotType.Type10b }
//                    ImGui.sameLine()
//                    dsl.radioButton("11c", pattern == DecoratedKnotType.Type11c) { pattern = DecoratedKnotType.Type11c }
//                    (currentSurfaceMapper.pathCreator as DecoratedTorusKnotPathCreator).pattern = pattern
//                }
////                is SimpleTorusPathCreator -> {
////                    currentSurfaceMapper.pathCreator as SimpleTorusPathCreator
////                    sliderFloat("Major Radius", (currentSurfaceMapper.pathCreator as SimpleTorusPathCreator)::r, 0.0001f, 20f)
////                }
//                else -> {
//                    // to do
//                }
//            }
//            ImGui.sliderFloat("Sweep Radius", currentSurface::r, 0.1f, 5f)

            ImGui.sliderFloat("Scale", currentSurface::scale, 0.2f, 10f)
            ImGui.separator()
            if (ImGui.button("Create Surface", ConwayOptions.fullSize)) stateChangeFunction(CreateSurface)
        }

    }
}

data class SurfaceData(
    val width: Int,
    val height: Int,
    val gridType: GridType,
    val size: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SurfaceData

        if (width != other.width) return false
        if (height != other.height) return false
        if (gridType != other.gridType) return false
        if (!size.contentEquals(other.size)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + gridType.hashCode()
        result = 31 * result + size.contentHashCode()
        return result
    }
}