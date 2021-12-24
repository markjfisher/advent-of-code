package advents.dumbooctopus

import advents.ui.HudData
import advents.ui.SurfaceOptions
import engine.GameEngine
import engine.GameLogic
import engine.GameWorld
import engine.MouseInput
import engine.Window
import engine.graph.CameraLoader
import engine.graph.OBJLoader
import engine.item.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.dumbooctopus.DumboOctopusEngine
import net.fish.dumbooctopus.Flashing
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface
import net.fish.resourceLines
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.Configuration
import java.lang.Integer.max
import kotlin.random.Random

class DumboOctopusGame : GameLogic, GameWorld<DumboOctopusItemData>(
    allSurfaces = listOf(
        Surface("(SS) 10,10 grid", mutableMapOf("gridType" to "non_wrapping_square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0.0f, 0.95f),
        Surface("(SS) 50,50 grid", mutableMapOf("gridType" to "non_wrapping_square", "width" to "50", "height" to "50"), PathType.StaticPoint, 0.0f, 0.2f),
        Surface("(SS) 200,200 grid", mutableMapOf("gridType" to "non_wrapping_square", "width" to "200", "height" to "200"), PathType.StaticPoint, 0.0f, 0.05f),
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

    private val flashingBrightnessMap = mapOf(
        0 to 0.7f,
        5 to 0.85f,
        10 to 0.975f,
        15 to 0.975f,
        20 to 0.87f,
        25 to 0.625f,
        30 to 0.35f,
        35 to 0.25f,
        40 to 0.1375f,
        45 to 0.075f,
        50 to 0.05f,
        55 to 0.0375f,
        60 to 0.025f,
        65 to 0.018f,
        70 to 0.0125f,
        75 to 0.0094f,
        80 to 0.00625f,
        85 to 0.0047f,
        90 to 0.00312f,
        95 to 0.0015f,
        100 to 0f,

    )

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {
        globalOptions.gameOptions.gameSpeed = 750
        globalOptions.gameOptions.maxGameSpeed = 1500
    }

    override fun createSurface() {
        flashing = emptySet()
        gameCreateSurface()
        engine = DumboOctopusEngine(surfaceMapper.grid(), storage)
        needToLoadInitialData = true
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
        globalOptions.gameOptions.pauseGame = true
        storage.clearAll()
        needToLoadInitialData = true
        flashing = emptySet()
        createGameItems()
        engine = DumboOctopusEngine(surfaceMapper.grid(), storage)
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
            engine.grid.items().forEach { item ->
                val gameItem = storage.getData(item)!!
                gameItem.energyLevel = Random.nextInt(9) + 1
                gameItem.flashingIteration = 0
            }
        }

        // Handle non flashing colours
        engine.grid.items().forEach { item ->
            val octopus = storage.getData(item)!!
            if (octopus.energyLevel > 0) {
                // levels go from 1-9, we need to scale to 0-0.7
                val red = (octopus.energyLevel - 1) * 0.7f / 8f
                val green = red * 2 / 3
                val blue = green
                octopus.gameItem.colour = Vector4f(red, blue, green, 1f)
            }
        }

    }

    override fun render(window: Window) {
        gameRender(window)
    }

    override fun setAnimationColours(animationStep: Int) {
        if (flashing.isEmpty()) return
        val gameSpeed = globalOptions.gameOptions.gameSpeed.toFloat()

        // we want to split the flashing iterations up between all the animation steps evenly, and only start them when they reach that frame
        val largestFlashingIteration = flashing.maxOf { it.iterationStarted }
        val animationPercentageBlocks = gameSpeed / (largestFlashingIteration + 1) / 100f

        // async the flashing setup
        val splitSize = max(flashing.size / 12, 1)
        val blocks = flashing.chunked(splitSize)
        runBlocking {
            val defs = blocks.map { flashBlock ->
                async(Dispatchers.Default) { performFlashAsync(flashBlock, animationStep, gameSpeed, animationPercentageBlocks) }
            }
            defs.awaitAll()
        }
    }

    private suspend fun performFlashAsync(flashing: List<Flashing>, animationStep: Int, gameSpeed: Float, animationPercentageBlocks: Float) = withContext(Dispatchers.Default) {
        flashing.forEach { flasher ->
            val startsOn = (flasher.iterationStarted * animationPercentageBlocks).toInt()
            if (animationStep >= startsOn) {
                // now work out the compressed pulse flashing colour for this item.
                // Normally, if it started at animation 0, then we look up the animation colours at x position directly.
                // Buf it we have less steps to take, we need to speed through the animation
                val percentageThrough = (animationStep + 1 - startsOn) / (gameSpeed - startsOn)
                val brightness = SurfaceOptions.calculatePercentage(percentageThrough, flashingBrightnessMap, 5)
                val octopus = storage.getData(flasher.item)!!
                octopus.gameItem.colour = Vector4f(brightness, brightness, brightness, 1f)
            }
        }

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
