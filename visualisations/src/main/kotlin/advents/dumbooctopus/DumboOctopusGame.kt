package advents.dumbooctopus

import advents.conwayhex.ConwayItemData
import advents.conwayhex.ConwayItemState
import advents.ui.HudData
import engine.GameEngine
import engine.GameLogic
import engine.GameWorld
import engine.MouseInput
import engine.Window
import engine.graph.CameraLoader
import engine.graph.OBJLoader
import engine.item.GameItem
import net.fish.dumbooctopus.DumboOctopusEngine
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface
import net.fish.resourceLines
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.Configuration
import kotlin.random.Random

class DumboOctopusGame : GameLogic, GameWorld<DumboOctopusItemData>(
    allSurfaces = listOf(
        Surface("(SS) 5,5 grid", mutableMapOf("gridType" to "non_wrapping_square", "width" to "5", "height" to "5"), PathType.StaticPoint, 0.0f, 1.0f),
//        Surface("(Square) 3,7 Torus Knot", mutableMapOf("gridType" to "square", "width" to "800", "height" to "16", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
//        Surface("(Square) Simple Grid", mutableMapOf("gridType" to "square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0f, 1f)
    ),
    storage = HashMapBackedGridItemDataStorage(),
    hud = OctopusHud()
) {

    private var engine = DumboOctopusEngine(surfaceMapper.grid(), storage)
    private val inputData = resourceLines(2021, 11)

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {}

    override fun createSurface() {
        gameCreateSurface()
    }

    override fun getItemsToRender(): List<GameItem> {
        return storage.data.map { it.gameItem }
    }

    override fun addCustomHudData(hudData: HudData) {}

    override fun init(window: Window) {
        gameInit(window)
    }

    override fun createGameItems() {
        surfaceMapper.grid().items().forEachIndexed { _, item ->
            val newMesh = OBJLoader.loadMesh(surfaceMapper.itemToObj(item))
            val hexColour = itemToColour(item)

            val gameItem = GameItem(newMesh)
//            gameItem.colour = hexColour
            gameItem.colour = Vector4f(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 0.6f)

            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
            storage.addItem(item, DumboOctopusItemData(gameItem, 0))
        }
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        gameInput(window, mouseInput)
    }

    override fun loadTextures() {
    }

    override fun resetGame() {
        gameIteration = 0
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)!!
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

    // **************************************************************************
    // Game Logic Code


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
