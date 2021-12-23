package advents.dumbooctopus

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
import net.fish.dumbooctopus.Flashing
import net.fish.geometry.Point
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface
import net.fish.geometry.square.Square
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.Configuration
import kotlin.random.Random

class DumboOctopusGame : GameLogic, GameWorld<DumboOctopusItemData>(
    allSurfaces = listOf(
        Surface("(SS) 10,10 grid", mutableMapOf("gridType" to "non_wrapping_square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0.0f, 1.0f),
//        Surface("(Square) 3,7 Torus Knot", mutableMapOf("gridType" to "square", "width" to "800", "height" to "16", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
//        Surface("(Square) Simple Grid", mutableMapOf("gridType" to "square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0f, 1f)
    ),
    storage = HashMapBackedGridItemDataStorage(),
    hud = OctopusHud()
) {
    // keep the state of the engine
    private var engine = DumboOctopusEngine(surfaceMapper.grid(), storage)
    private val inputData = resourceLines(2021, 11)
    private var flashing: Set<Flashing> = emptySet()
    private var needToLoadInitialData = true

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {
        globalOptions.gameOptions.gameSpeed = 500
        globalOptions.gameOptions.maxGameSpeed = 1500
    }

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
            val gameItem = GameItem(newMesh)
            // Initially paused and mid grey
            gameItem.colour = Vector4f(0.5f)

            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
            storage.addItem(item, DumboOctopusItemData(gameItem, 0, 0))
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
            data.gameItem.colour = Vector4f(0.0f, 0.0f, 0.0f, 0.0f)
            data.gameItem.mesh.texture = null
        }
        needToLoadInitialData = true
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        gameUpdate(interval, mouseInput, window)
    }

    override fun performStep() {
        gameIteration++

        // Do the work of a world step. Anything marked as flashing will be picked up in the animation stage.
        // A step will mutate the values of the grid for us, we just need to handle new flashers
        // Skip the step on load, so we see the initial load data in the grid
        if (!needToLoadInitialData) flashing = engine.step()

        // check to load initial data
        if (needToLoadInitialData) {
            needToLoadInitialData = false
            val mapOfPoints = GridDataUtils.mapPointsFromLines(inputData)
            engine.grid.items().forEach { item ->
                val square = item as Square
                val luminescence = mapOfPoints[Point(square.x, square.y)] ?: throw Exception("Couldn't find data for square: $square in $mapOfPoints")
                val gameItem = storage.getData(item)!!
                gameItem.energyLevel = luminescence
                gameItem.flashingIteration = 0
            }
        }

        // Handle non flashing colours
        engine.grid.items().forEach { item ->
            val octopus = storage.getData(item)!!
            if (octopus.energyLevel > -1) {
                octopus.gameItem.colour = Vector4f(octopus.energyLevel * 0.1f, octopus.energyLevel * 0.1f / 3, octopus.energyLevel * 0.1f / 3, octopus.energyLevel * 0.1f)
            }
        }

    }

    override fun render(window: Window) {
        gameRender(window)
    }

    override fun setAnimationColours(animationStep: Int) {
        // Between states, we can animate any spreading flashing here

        // simple on flashing for now
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
            val engine = GameEngine(windowTitle = "Dumbo Octopus", width = 1200, height = 800, vSync = true, gameLogic = logic, targetUPS = 2000)
            Configuration.DEBUG.set(true)
            engine.run()
        }
    }

}
