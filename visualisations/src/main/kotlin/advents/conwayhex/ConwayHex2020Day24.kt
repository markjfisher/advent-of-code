package advents.conwayhex

import advents.conwayhex.ConwayItemState.ALIVE
import advents.conwayhex.ConwayItemState.CREATING
import advents.conwayhex.ConwayItemState.DEAD
import advents.conwayhex.ConwayItemState.DESTROYING
import advents.ui.HudData
import advents.ui.SurfaceOptions.Companion.defaultSurfaces
import engine.GameEngine
import engine.GameLogic
import engine.GameWorld
import engine.MouseInput
import engine.Window
import engine.graph.CameraLoader
import engine.graph.OBJLoader.loadMesh
import engine.graph.Texture
import engine.item.GameItem
import glm_.vec4.Vec4
import imgui.ImGui
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.GridType
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.HexConstrainer
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.CameraData
import net.fish.geometry.square.WrappingSquareGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.Configuration
import kotlin.random.Random

class ConwayHex2020Day24 : GameLogic, GameWorld<ConwayItemData>(
    allSurfaces = defaultSurfaces,
    storage = HashMapBackedGridItemDataStorage(),
    hud = ConwayHud()
) {
    // Input from the original puzzle!
    private val conwayInputData = resourceLines(2020, 24)

    // Game state
    private val alive = mutableSetOf<GridItem>()
    private val creating = mutableSetOf<GridItem>()
    private val destroying = mutableSetOf<GridItem>()
    private val initialPoints = 300 // we have up to 597 initial points from the original game
    private var createdCount = 0
    private var destroyedCount = 0

    // Textures
    lateinit var aliveTexturePointy: Texture
    lateinit var aliveTextureFlat: Texture

    // Colour for on
    private var colourOn = Vector4f(0.75f, 0.75f, 0.75f, 0.95f)

    override fun getCameraPaths(): Map<String, () -> List<CameraData>> = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Knot 3/7 Inner Circle" to { CameraLoader.loadCamera("/conwayhex/knot-3-7-inner-circle.txt") },
        "Knot 11/17 Inner Circle" to { CameraLoader.loadCamera("/conwayhex/knot-11-17-inner-circle-wide.txt") },
        "Fly by 10b (a)" to { CameraLoader.loadCamera("/conwayhex/fly-by-10b.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )

    override fun setGameOptions() {
        globalOptions.gameOptions.setGSData("aliveColour", colourOn)
        globalOptions.uiExtensionFunction = ::addUiExtensions
    }

    // This can't be private silly IntelliJ, as it's invoked indirectly in GameOptionsWidget
    @Suppress("MemberVisibilityCanBePrivate")
    fun addUiExtensions() {
        // i'm shocked this works. a call back function from the UI.
        // (•_•)
        // ( •_•)>⌐■-■
        // (⌐■_■)
        val aliveColour = globalOptions.gameOptions.getVector4f("aliveColour")!!
        val aliveColour4v = Vec4(aliveColour.x, aliveColour.y, aliveColour.z, aliveColour.w)
        if (ImGui.colorEdit4("Alive colour", aliveColour4v)) {
            globalOptions.gameOptions.setGSData("aliveColour", Vector4f(aliveColour4v.x, aliveColour4v.y, aliveColour4v.z, aliveColour4v.w))
        }
    }

    private fun readInitialPosition(): Set<GridItem> {
        return when (surfaceMapper.mappingType()) {
            GridType.HEX -> {
                // This will need changing to check it's right type
                val constrainer = surfaceMapper.grid() as HexConstrainer
                val doubledCoords = Day24.walk(conwayInputData).take(initialPoints)
                val hexes = doubledCoords.map { (col, row) ->
                    val qc = col + row / 2 + row % 2
                    val rc = -row
                    val sc = 0 - qc - rc
                    Hex(qc, rc, sc, constrainer)
                }
                hexes.map { constrainer.constrain(it) }.toSet()
            }
            GridType.SQUARE -> {
                val constrainer = surfaceMapper.grid() as WrappingSquareGrid

//                val sliderOffsets = listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2), Pair(2, 1), Pair(1, 0))
//                val sliderCount = 25
//                val sliders = mutableSetOf<Square>()
//                for (s in 0..sliderCount) {
//                    sliderOffsets.forEach {
//                        sliders.add(Square(it.first + s * 6, it.second + s, constrainer))
//                    }
//                }
//                return sliders

                return constrainer.items().mapNotNull { if (Random.nextInt(10) < 5) it else null }.toSet()
            }
            else -> throw Exception("Not implemented")
        }
    }

    override fun init(window: Window) {
        gameInit(window)
    }

    override fun createGameItems() {
        surfaceMapper.grid().items().forEachIndexed { _, item ->
            val newMesh = loadMesh(surfaceMapper.itemToObj(item))
            val hexColour = itemToColour(item)

            val gameItem = GameItem(newMesh)
            gameItem.colour = hexColour
            // items are relative to world coords already in both position, scale and have axes same as world coords
            // Another way to do this is calculate N meshes for the minor circle, as they will be repeated around the major circle, but with different location and rotations
            // which would make N mesh instead of NxM mesh
            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
            storage.addItem(item, ConwayItemData(gameItem, DEAD))
        }
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        gameInput(window, mouseInput)
    }

    override fun createSurface() {
        // Conway specific clean up
        alive.clear()
        creating.clear()
        destroying.clear()

        // standard clean up and initialise
        gameCreateSurface()
    }

    override fun loadTextures() {
        aliveTexturePointy = Texture("visualisations/textures/new-white-pointy.png")
        aliveTextureFlat = Texture("visualisations/textures/new-white-flat.png")
    }

    override fun resetGame() {
        alive.clear()
        creating.clear()
        destroying.clear()
        gameIteration = 0
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)!!
            data.state = DEAD
            data.gameItem.colour = itemToColour(item)
            data.gameItem.mesh.texture = null
        }
    }

    override fun toggleTextureMode() {
        if (globalOptions.gameOptions.useTexture) {
            // for any currently animating, turn them into full textures
            destroying.forEach { hex ->
                val data = storage.getData(hex)!!
                data.gameItem.colour.set(itemToColour(hex))
                data.gameItem.mesh.texture = null
            }
            alive.forEach { hex ->
                val data = storage.getData(hex)!!
                data.gameItem.colour.set(itemToColour(hex))
                data.gameItem.mesh.texture = getAliveTexture()
            }
        } else {
            // change to colour mode
            // all the currently alive (includes creating), turn them fully on, then deal with the changing
            alive.forEach { hex ->
                val data = storage.getData(hex)!!
                data.gameItem.colour.set(globalOptions.gameOptions.getVector4f("aliveColour"))
                data.gameItem.mesh.texture = null
            }
            // any changing states can be in their correct animation phase
            setAnimationColours(currentStepDelay % globalOptions.gameOptions.gameSpeed)
        }
    }

    override fun setGameItemsAlpha() {
        val currentPauseState = globalOptions.gameOptions.pauseGame
        globalOptions.gameOptions.pauseGame = true
        // any non animating or alive hexes need to change their alpha value.
        (storage.items - alive - creating - destroying).forEach { hex ->
            val data = storage.getData(hex)!!
            data.gameItem.colour.w = globalOptions.surfaceOptions.globalAlpha
        }
        globalOptions.gameOptions.pauseGame = currentPauseState
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        gameUpdate(interval, mouseInput, window)
    }

    override fun render(window: Window) {
        gameRender(window)
    }

    override fun performStep() {
        // mark all the hexes that were creating as alive, destroying as dead, set their colours to end conditions
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)!!
            if (data.state == CREATING) {
                data.state = ALIVE
                if (globalOptions.gameOptions.useTexture) {
                    data.gameItem.mesh.texture = getAliveTexture()
                } else {
                    data.gameItem.colour.set(globalOptions.gameOptions.getVector4f("aliveColour"))
                }
            }
            if (data.state == DESTROYING) {
                data.state = DEAD
                data.gameItem.colour.set(itemToColour(item))
                data.gameItem.mesh.texture = null
            }
        }

        // get the new state for entire grid, then work out which have flipped
        val newAlive = runConway()
        val newOn = newAlive - alive
        val newOff = alive - newAlive
        createdCount = newOn.count()
        destroyedCount = newOff.count()

        alive.clear()
        alive.addAll(newAlive)
        creating.clear()
        creating.addAll(newOn)
        destroying.clear()
        destroying.addAll(newOff)

        if (globalOptions.gameOptions.useTexture) {
            newOff.forEach { hex ->
                val data = storage.getData(hex)!!
                data.state = DESTROYING
                data.gameItem.mesh.texture = null
            }

            newOn.forEach { hex ->
                val data = storage.getData(hex)!!
                data.state = CREATING
                data.gameItem.mesh.texture = getAliveTexture()
            }
        }
    }

    private fun getAliveTexture(): Texture {
        return when (surfaceMapper.mappingType()) {
            GridType.HEX -> {
                val hexGrid = surfaceMapper.grid() as WrappingHexGrid
                if (hexGrid.layout.orientation == POINTY) aliveTexturePointy else aliveTextureFlat
            }
            GridType.SQUARE -> aliveTexturePointy // TODO make a texture
            else -> throw Exception("Type not implemented yet: ${surfaceMapper.mappingType()}")
        }
    }

    private fun runConway(): Set<GridItem> {
        gameIteration++
        if (alive.size == 0) {
            alive.addAll(readInitialPosition())
            alive.forEach { gridItem ->
                val data = storage.getData(gridItem)!!
                data.state = ALIVE
            }
        }
        val allTouchingGridItems = mutableSetOf<GridItem>()
        alive.forEach { item ->
            allTouchingGridItems.addAll(item.neighbours())
        }

        val splitSize = allTouchingGridItems.size / 12
        val blocks = allTouchingGridItems.chunked(splitSize)
        val newAlive = mutableSetOf<GridItem>()
        runBlocking {
            val defs = blocks.map { hexList ->
                async(Dispatchers.Default) { calculateAsync(hexList) }
            }
            defs.awaitAll().map { newAlive.addAll(it) }
        }

        return newAlive
    }

    private suspend fun calculateAsync(gridItems: List<GridItem>): Set<GridItem> = withContext(Dispatchers.Default) {
        calculate(gridItems)
    }

    private fun calculate(gridItems: List<GridItem>): MutableSet<GridItem> {
        return gridItems.fold(mutableSetOf()) { newAlive, gridItem ->
            val neighbourCount = gridItem.neighbours().intersect(alive).count()
            val isAlive = alive.contains(gridItem)

            when (surfaceMapper.mappingType()) {
                GridType.HEX -> {
                    when {
                        isAlive && (neighbourCount == 0 || neighbourCount > 2) -> newAlive.remove(gridItem)
                        !isAlive && neighbourCount == 2 -> newAlive.add(gridItem)
                        isAlive -> newAlive.add(gridItem)
                    }
                }
                GridType.SQUARE -> {
                    when {
                        isAlive && (neighbourCount == 2 || neighbourCount == 3) -> newAlive.add(gridItem)
                        !isAlive && neighbourCount == 3 -> newAlive.add(gridItem)
                    }
                }
                else -> throw Exception("Unknown surface mapper type: ${surfaceMapper.mappingType()}")
            }
            newAlive
        }
    }

    override fun getItemsToRender(): List<GameItem> {
        // TODO, work out how to use generics properly so we don't need the casts
        val aliveGameItems = storage.data.filter { data -> setOf(ALIVE).contains(data.state) }.map { it.gameItem }
        val creatingGameItems = storage.data.filter { data -> setOf(CREATING).contains(data.state) }.map { it.gameItem }
        val destroyingGameItems = storage.data.filter { data -> setOf(DESTROYING).contains(data.state) }.map { it.gameItem }
        val notAliveGameItems = gameItems - aliveGameItems.toSet() - creatingGameItems.toSet() - destroyingGameItems.toSet()
        return aliveGameItems + creatingGameItems + destroyingGameItems + notAliveGameItems
    }

    override fun setAnimationColours(animationStep: Int) {
        val animationPercentage = (animationStep + 1) / globalOptions.gameOptions.gameSpeed.toFloat()
        val animationCurveValue = calculatePercentage(animationPercentage)
        creating.forEach { gridItem ->
            setAnimationColour(gridItem, 1f - animationCurveValue)
        }
        destroying.forEach { gridItem ->
            setAnimationColour(gridItem, animationCurveValue)
        }
    }

    private fun setAnimationColour(gridItem: GridItem, animationCurveValue: Float) {
        val aliveColour = globalOptions.gameOptions.getVector4f("aliveColour")
        val newColour = itemToColour(gridItem).sub(aliveColour).mul(animationCurveValue).add(aliveColour)
        val data = storage.getData(gridItem)!!
        data.gameItem.colour.set(newColour)
        data.gameItem.mesh.texture = null
    }

    private fun calculatePercentage(animationPercentage: Float): Float {
        val lower10 = (animationPercentage * 10f).toInt() * 10 // e.g. 0.47 -> 40
        val upper10 = ((animationPercentage + 0.1f) * 10f).toInt() * 10
        val between = (animationPercentage * 100f).toInt() - lower10
        val lowerP = globalOptions.surfaceOptions.animationPercentages.getOrDefault(lower10, 1f)
        val upperP = globalOptions.surfaceOptions.animationPercentages.getOrDefault(upper10, 1f)
        return lowerP * (10f - between) / 10f + upperP * between / 10f
    }

    override fun addCustomHudData(hudData: HudData) {
        val customData = mapOf(
            "createdCount" to createdCount.toLong(),
            "destroyedCount" to destroyedCount.toLong(),
            "liveCount" to alive.count().toLong()
        )
        hudData.customData.putAll(customData)
    }

    override fun cleanup() {
        gameCleanup()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            Configuration.DEBUG.set(true)
            engine.run()
        }
    }
}
