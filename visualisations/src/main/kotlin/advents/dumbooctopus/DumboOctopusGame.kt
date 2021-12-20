package advents.dumbooctopus

import advents.ui.HudData
import engine.GameEngine
import engine.GameLogic
import engine.GameWorld
import engine.MouseInput
import engine.Window
import engine.graph.CameraLoader
import engine.item.GameItem
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridType
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface
import net.fish.resourceLines
import org.joml.Vector4f
import org.lwjgl.system.Configuration

class DumboOctopusGame : GameLogic, GameWorld<DumboOctopusItemData>(
    allSurfaces = listOf(Surface("(Square) Simple Grid", mutableMapOf("gridType" to "square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0f, 1f)),
    storage = HashMapBackedGridItemDataStorage(),
    hud = OctopusHud()
) {

    private val data = resourceLines(2021, 11)

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {}

    override fun createSurface() {
    }

    override fun getItemsToRender(): List<GameItem> {
        return emptyList()
    }

    override fun addCustomHudData(hudData: HudData) {
    }

    private fun readInitialPosition(): Set<GridItem> {
        return when (surfaceMapper.mappingType()) {
            GridType.HEX -> {
                throw Exception("TODO")
            }
            GridType.SQUARE -> {
                throw Exception("TODO")
            }
            GridType.NON_WRAPPING_SQUARE -> {
                emptySet()
            }
        }
    }

    override fun init(window: Window) {
        gameInit(window)
    }

    override fun createGameItems() {
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        gameInput(window, mouseInput)
    }

    override fun loadTextures() {
    }

    override fun resetGame() {
        gameIteration = 0
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)!! as DumboOctopusItemData
            data.energyLevel = 0
            data.gameItem.colour = Vector4f(0.5f, 0.5f, 0.5f, 0.5f)
            data.gameItem.mesh.texture = null
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        gameUpdate(interval, mouseInput, window)
    }

    override fun performStep() {
        // Do the work of a world step
    }

    override fun render(window: Window) {
        gameRender(window)
    }

    override fun setAnimationColours(animationStep: Int) {
    }

    override fun cleanup() {
        gameCleanup()
    }

    override fun toggleTextureMode() {
    }

    override fun setGameItemsAlpha() {
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logic = DumboOctopusGame()
            val engine = GameEngine("Dumbo Octopus", 1200, 800, true, logic)
            Configuration.DEBUG.set(true)
            engine.run()
        }
    }

}
