package advents.seacucumber

import advents.ui.HudData
import engine.GameEngine
import engine.GameLogic
import engine.GameWorld
import engine.MouseInput
import engine.Window
import engine.graph.CameraLoader
import engine.graph.OBJLoader
import engine.item.GameItem
import glm_.vec4.Vec4
import imgui.ImGui
import net.fish.geometry.Point
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface
import net.fish.geometry.square.Square
import net.fish.resourceLines
import net.fish.seacucumber.SeaCucumberEngine
import net.fish.seacucumber.SeaCucumberFloorSimple
import net.fish.seacucumber.SeaCucumberFloorValue
import net.fish.seacucumber.SeaCucumberFloorValue.E
import net.fish.seacucumber.SeaCucumberFloorValue.EMPTY
import net.fish.seacucumber.SeaCucumberFloorValue.S
import net.fish.y2021.GridDataUtils
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.Configuration
import kotlin.random.Random

class SeaCucumberGame : GameLogic, GameWorld<SeaCucumberFloorItemData>(
    allSurfaces = listOf(
        // TODO: we need to be able to render a flat grid that is also wrapping. it shouldn't just be the torus that can do this
        // Surface("(SS) 139,137 grid", mutableMapOf("gridType" to "square", "width" to "139", "height" to "137"), PathType.SimpleTorus, 0.0f, 0.06f),
        Surface("(SS) 139,137 Torus", mutableMapOf("gridType" to "square", "width" to "200", "height" to "120", "majorRadius" to "8.0f"), PathType.SimpleTorus, 1.5f, 1.0f),
        Surface("(Square) Trefoil", mutableMapOf("gridType" to "square", "width" to "200", "height" to "120"), PathType.Trefoil, 0.75f, 1.5f),
        Surface("(Square) 10b Decorated Torus Knot", mutableMapOf("gridType" to "square", "width" to "500", "height" to "86", "pattern" to "Type10b"), PathType.DecoratedTorusKnot, 0.25f, 5.0f),
        Surface("(Square) 3,7 Torus Knot", mutableMapOf("gridType" to "square", "width" to "500", "height" to "86", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),

        ),
    storage = HashMapBackedGridItemDataStorage(),
    hud = SeaCucumberHud()
) {
    // keep the state of the engine
    private var engine = SeaCucumberEngine(surfaceMapper.grid(), storage)
    private val inputData = resourceLines(2021, 25)
    private var needToLoadInitialData = true

    private var eastFacingColour = Vector4f(0.8f, 0.2f, 0.3f, 0.7f)
    private var southFacingColour = Vector4f(0.2f, 0.3f, 0.8f, 0.7f)
//    private var emptyColour = Vector4f(0.2f, 0.2f, 0.2f, 1f)

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {
        globalOptions.gameOptions.gameSpeed = 3
        globalOptions.gameOptions.maxGameSpeed = 20
        globalOptions.gameOptions.setGSData("eastFacingColour", eastFacingColour)
        globalOptions.gameOptions.setGSData("southFacingColour", southFacingColour)
//        globalOptions.gameOptions.setGSData("emptyColour", emptyColour)
        globalOptions.uiExtensionFunction = ::addUiExtensions
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun addUiExtensions() {
        val eastFacingColour = globalOptions.gameOptions.getVector4f("eastFacingColour")!!
        val southFacingColour = globalOptions.gameOptions.getVector4f("southFacingColour")!!
//        val emptyColour = globalOptions.gameOptions.getVector4f("emptyColour")!!

        val eastFacingColour4v = Vec4(eastFacingColour.x, eastFacingColour.y, eastFacingColour.z, eastFacingColour.w)
        if (ImGui.colorEdit4("East colour", eastFacingColour4v)) {
            globalOptions.gameOptions.setGSData("eastFacingColour", Vector4f(eastFacingColour4v.x, eastFacingColour4v.y, eastFacingColour4v.z, eastFacingColour4v.w))
        }

        val southFacingColour4v = Vec4(southFacingColour.x, southFacingColour.y, southFacingColour.z, southFacingColour.w)
        if (ImGui.colorEdit4("South colour", southFacingColour4v)) {
            globalOptions.gameOptions.setGSData("southFacingColour", Vector4f(southFacingColour4v.x, southFacingColour4v.y, southFacingColour4v.z, southFacingColour4v.w))
        }

//        val emptyColour4v = Vec4(emptyColour.x, emptyColour.y, emptyColour.z, emptyColour.w)
//        if (ImGui.colorEdit4("Empty colour", emptyColour4v)) {
//            globalOptions.gameOptions.setGSData("emptyColour", Vector4f(emptyColour4v.x, emptyColour4v.y, emptyColour4v.z, emptyColour4v.w))
//        }

    }

    override fun createSurface() {
        gameCreateSurface()
        engine = SeaCucumberEngine(surfaceMapper.grid(), storage)
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
            gameItem.colour = Vector4f(0.6f, 0.6f, 0.6f, 0.8f).mul(itemToColour(item))

            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
            storage.addItem(item, SeaCucumberFloorItemData(gameItem, EMPTY))
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
        createGameItems()
        engine = SeaCucumberEngine(surfaceMapper.grid(), storage)
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        gameUpdate(interval, mouseInput, window)
    }

    override fun performStep() {
        gameIteration++

        // Do the work of a world step.
        if (!needToLoadInitialData) engine.step()

        // check to load initial data
        if (needToLoadInitialData) {
            needToLoadInitialData = false
            val mapOfPoints = GridDataUtils.mapCharPointsFromLines(inputData)
            engine.grid.items().forEach { item ->
                val square = item as Square
//                val char = mapOfPoints[Point(square.x, square.y)] ?: throw Exception("Couldn't find data for square: $square in $mapOfPoints")

                val char = when(Random.nextInt(4)) {
                    0,1 -> '.'
                    2 -> '>'
                    else -> 'v'
                }

                val gameItem = storage.getData(item)!!
                gameItem.value = when(char) {
                    '>' -> E
                    'v' -> S
                    '.' -> EMPTY
                    else -> throw Exception("Unknown char in data: $char")
                }
            }
        }

        engine.grid.items().forEach { item ->
            val floor = storage.getData(item)!!
            val colour = when (floor.value) {
                E -> globalOptions.gameOptions.getVector4f("eastFacingColour")!!.let { it.w = globalOptions.surfaceOptions.globalAlpha; it }
                S -> globalOptions.gameOptions.getVector4f("southFacingColour")!!.let { it.w = globalOptions.surfaceOptions.globalAlpha; it }
                EMPTY -> itemToColour(item)
            }
            floor.gameItem.colour.set(colour)
        }

    }

    override fun render(window: Window) {
        gameRender(window)
    }

    override fun setAnimationColours(animationStep: Int) {
        // nothing between cells here
    }

    override fun cleanup() {
        gameCleanup()
    }

    override fun toggleTextureMode() {
    }

    override fun setGameItemsAlpha() {
        val currentPauseState = globalOptions.gameOptions.pauseGame
        globalOptions.gameOptions.pauseGame = true
        // any non animating or alive hexes need to change their alpha value.
        storage.items.forEach { item ->
            val data = storage.getData(item)!!
            data.gameItem.colour.w = globalOptions.surfaceOptions.globalAlpha
        }
        globalOptions.gameOptions.pauseGame = currentPauseState

    }

    // **************************************************************************
    // Game Logic Code


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logic = SeaCucumberGame()
            val engine = GameEngine(windowTitle = "Sea Cucumber", width = 1200, height = 800, vSync = true, gameLogic = logic, targetUPS = 200)
            Configuration.DEBUG.set(true)
            engine.run()
        }
    }

}
