package advents.conwayhex.game.ui

import commands.CreateSurface
import commands.KeyCommand
import commands.SetGlobalAlpha
import imgui.ImGui
import imgui.dsl
import imgui.dsl.collapsingHeader
import net.fish.geometry.hex.Orientation
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator
import net.fish.geometry.projection.DecoratedKnotType
import net.fish.geometry.projection.EpitrochoidSurface
import net.fish.geometry.projection.SimpleTorusSurface
import net.fish.geometry.projection.ThreeFactorParametricSurface
import net.fish.geometry.projection.TorusKnotSurface

object SurfaceOptionsWidget {
    operator fun invoke(surfaceOptions: SurfaceOptions, stateChangeFunction: (KeyCommand) -> Unit) {
        val currentSurface = surfaceOptions.surfaces[surfaceOptions.currentSurfaceName]!!
        val currentSurfaceMapper = surfaceOptions.mappers[surfaceOptions.currentSurfaceMapperName]!!

        val gridWidth = currentSurfaceMapper.grid().width
        val gridHeight = currentSurfaceMapper.grid().height
        val gridSize4i = intArrayOf(gridWidth, gridHeight, 2000, ConwayOptions.MAX_M_BY_N / gridHeight.coerceAtLeast(1))

        collapsingHeader("Surface") {
            if (ImGui.sliderFloat("Global Alpha", surfaceOptions::globalAlpha, 0f, 1f)) {
                stateChangeFunction(SetGlobalAlpha)
            }

            if (ImGui.dragInt2("Grid Size", gridSize4i, 2f, 2, 2000)) {
                var newWidth = gridSize4i[0].coerceAtLeast(2)
                if (newWidth % 2 == 1) newWidth--
                var maxV = ConwayOptions.MAX_M_BY_N / newWidth
                if (maxV % 2 == 1) maxV--
                currentSurfaceMapper.grid().width = newWidth
                currentSurfaceMapper.grid().height = gridSize4i[1].coerceAtMost(maxV).coerceAtLeast(2)
            }
            // TODO: move hex specific stuff around
            dsl.radioButton("Pointy", currentSurface.gridOrientation == Orientation.ORIENTATION.POINTY) {
                currentSurface.gridOrientation = Orientation.ORIENTATION.POINTY
            }
            ImGui.sameLine()
            dsl.radioButton("Flat", currentSurface.gridOrientation == Orientation.ORIENTATION.FLAT) {
                currentSurface.gridOrientation = Orientation.ORIENTATION.FLAT
            }
            surfaceOptions.mappers.keys.sorted().forEachIndexed { i, mapperName ->
                if (ImGui.selectable(String.format("%1d. %s", i + 1, mapperName), surfaceOptions.currentSurfaceMapperName == mapperName)) {
                    surfaceOptions.currentSurfaceMapperName = mapperName
                }
            }
            ImGui.separator()
            // for the given surface type, show its controls
            when (currentSurface) {
                is SimpleTorusSurface -> {
                    ImGui.sliderFloat("Major Radius", currentSurface::majorRadius, 0.0001f, 20f)
                }
                is TorusKnotSurface -> {
                    ImGui.sliderInt("p", currentSurface::p, 1, 20)
                    ImGui.sliderInt("q", currentSurface::q, 1, 20)

                    ImGui.sliderFloat("a", currentSurface::a, 0.1f, 2f)
                    ImGui.sliderFloat("b", currentSurface::b, 0.1f, 2f)
                }
                is EpitrochoidSurface -> {
                    ImGui.inputFloat("a", currentSurface::a, 0.1f, 10f, "%.2f")
                    ImGui.inputFloat("b", currentSurface::b, 0.1f, 10f, "%.2f")
                    ImGui.inputFloat("c", currentSurface::c, 0.1f, 10f, "%.2f")
                }
                is ThreeFactorParametricSurface -> {
                    ImGui.inputInt("a", currentSurface::a, 0, 20)
                    ImGui.inputInt("b", currentSurface::b, 0, 20)
                    ImGui.inputInt("c", currentSurface::c, 0, 20)
                }
                else -> {
                    // todo
                }
            }
            when (currentSurfaceMapper.pathCreator) {
                is DecoratedTorusKnotPathCreator -> {
                    var pattern = (currentSurfaceMapper.pathCreator as DecoratedTorusKnotPathCreator).pattern
                    dsl.radioButton("4b", pattern == DecoratedKnotType.Type4b) { pattern = DecoratedKnotType.Type4b }
                    ImGui.sameLine()
                    dsl.radioButton("7a", pattern == DecoratedKnotType.Type7a) { pattern = DecoratedKnotType.Type7a }
                    ImGui.sameLine()
                    dsl.radioButton("7b", pattern == DecoratedKnotType.Type7b) { pattern = DecoratedKnotType.Type7b }
                    ImGui.sameLine()
                    dsl.radioButton("10b", pattern == DecoratedKnotType.Type10b) { pattern = DecoratedKnotType.Type10b }
                    ImGui.sameLine()
                    dsl.radioButton("11c", pattern == DecoratedKnotType.Type11c) { pattern = DecoratedKnotType.Type11c }
                    (currentSurfaceMapper.pathCreator as DecoratedTorusKnotPathCreator).pattern = pattern
                }
//                is SimpleTorusPathCreator -> {
//                    currentSurfaceMapper.pathCreator as SimpleTorusPathCreator
//                    sliderFloat("Major Radius", (currentSurfaceMapper.pathCreator as SimpleTorusPathCreator)::r, 0.0001f, 20f)
//                }
                else -> {
                    // to do
                }
            }
            ImGui.sliderFloat("Sweep Radius", currentSurface::r, 0.1f, 5f)
            ImGui.sliderFloat("Scale", currentSurface::scale, 0.2f, 10f)
            ImGui.separator()
            if (ImGui.button("Create Surface", ConwayOptions.fullSize)) stateChangeFunction(CreateSurface)
        }

    }
}