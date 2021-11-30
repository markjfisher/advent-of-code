package advents.conwayhex.game

import advents.conwayhex.game.ConwayItemState.ALIVE
import advents.conwayhex.game.ConwayItemState.CREATING
import advents.conwayhex.game.ConwayItemState.DEAD
import advents.conwayhex.game.ConwayItemState.DESTROYING
import advents.conwayhex.game.ui.CameraOptions
import advents.conwayhex.game.ui.ConwayOptions
import advents.conwayhex.game.ui.DebugOptions
import advents.conwayhex.game.ui.GameOptions
import advents.conwayhex.game.ui.SurfaceOptions
import commands.CreateSurface
import commands.DecreaseSpeed
import commands.IncreaseSpeed
import commands.KeyCommand
import commands.LoadCamera
import commands.MoveBackward
import commands.MoveDown
import commands.MoveForward
import commands.MoveLeft
import commands.MoveRight
import commands.MoveUp
import commands.NextCamera
import commands.PrintState
import commands.ResetCamera
import commands.ResetGame
import commands.RunCamera
import commands.SetCamera
import commands.SetGlobalAlpha
import commands.SetLookahead
import commands.SingleKeyPressCommand
import commands.SingleStep
import commands.ToggleHud
import commands.ToggleImgUI
import commands.ToggleMessages
import commands.TogglePause
import commands.ToggleTexture
import engine.GameEngine
import engine.GameLogic
import engine.MouseInput
import engine.Timer
import engine.Window
import engine.graph.Camera
import engine.graph.CameraLoader
import engine.graph.OBJLoader.loadMesh
import engine.graph.Renderer
import engine.graph.Renderer.Companion.FOV
import engine.graph.Texture
import engine.item.GameItem
import imgui.ImGui
import imgui.font.Font
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
import net.fish.geometry.paths.CameraPath
import net.fish.geometry.paths.PathType.DecoratedTorusKnot
import net.fish.geometry.paths.PathType.Epitrochoid
import net.fish.geometry.paths.PathType.SimpleTorus
import net.fish.geometry.paths.PathType.TorusKnot
import net.fish.geometry.paths.PathType.Trefoil
import net.fish.geometry.projection.Surface
import net.fish.geometry.square.Square
import net.fish.geometry.square.WrappingSquareGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Math.abs
import org.joml.Math.max
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_KEY_0
import org.lwjgl.glfw.GLFW.GLFW_KEY_1
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_C
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
import org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL
import org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT
import org.lwjgl.glfw.GLFW.GLFW_KEY_H
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS
import org.lwjgl.glfw.GLFW.GLFW_KEY_N
import org.lwjgl.glfw.GLFW.GLFW_KEY_P
import org.lwjgl.glfw.GLFW.GLFW_KEY_R
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP
import org.lwjgl.glfw.GLFW.GLFW_KEY_W
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL11C.GL_FILL
import org.lwjgl.opengl.GL11C.GL_LINE
import org.lwjgl.opengl.GL11C.glPolygonMode
import org.lwjgl.system.Configuration
import kotlin.random.Random

class ConwayHex2020Day24 : GameLogic {
    // Gfx helpers
    private val renderer = Renderer()
    private val hud = Hud()
    private var showImgUI = false
    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font

    // Input from the original puzzle!
    private val data = resourceLines(2020, 24)

    // Game state
    private var currentStepDelay = 0
    private var conwayIteration = 0
    private val gameItems = mutableListOf<GameItem>()
    private val storage = HashMapBackedGridItemDataStorage<ConwayItemData>()
    private val alive = mutableSetOf<GridItem>()
    private val creating = mutableSetOf<GridItem>()
    private val destroying = mutableSetOf<GridItem>()
    private val initialPoints = 25 // we have up to 597 initial points from the original game
    private var createdCount = 0
    private var destroyedCount = 0

    private var performSingleStep = false

    // keyboard handling
    private val keyPressedTimer = Timer()
    private var lastPressedAt = 0.0
    private var turboMove = false

    // text flashing and other animation
    private var flashPercentage: Int = 0
    private var flashMessage: String = ""

    // y = 1648x / (1250x + 625), or y = (n π + 2) x / ( π (n x + 1)). higher n = steeper drop/climb
    private val animationPercentages = mutableMapOf(
        0 to 0f,
        10 to 0.3152f,
        20 to 0.5087f,
        30 to 0.6397f,
        40 to 0.7342f,
        50 to 0.8056f,
        60 to 0.8614f,
        70 to 0.9063f,
        80 to 0.9431f,
        90 to 0.9739f,
        100 to 1f
    )

    // Textures
    lateinit var aliveTexturePointy: Texture
    lateinit var aliveTextureFlat: Texture

    // Colour for on
    private var colourOn = Vector4f(0.75f, 0.75f, 0.75f, 0.95f)

    // make it read only by typing it as constant - ensures it's not changed
    private val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    private val cameraMovementTimer = Timer()

    private val cameraPaths = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Circle Path Quick" to { CameraLoader.loadCamera("/conwayhex/quick-simple-circle.txt") },
        "Knot 3/7 Inner Circle" to { CameraLoader.loadCamera("/conwayhex/knot-3-7-inner-circle.txt") },
        "Knot 11/17 Inner Circle" to { CameraLoader.loadCamera("/conwayhex/knot-11-17-inner-circle-wide.txt") },
        "Fly by 10b (a)" to { CameraLoader.loadCamera("/conwayhex/fly-by-10b.txt") },
        "Current Tunnel" to { calculateTunnelPath() }
    )
    private val cameraData = mutableListOf<CameraData>()

    // Free form camera movement
    private val initialWorldCentre = Vector3f(0f, 0f, 0f)
    private val worldCentre = Vector3f(initialWorldCentre)

    private val initialCameraPosition = Vector3f(0f, 0f, 16f)
    private val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize()
    private val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    // calculation values so they aren't created every loop
    private val cameraX = Vector3f()
    private val cameraY = Vector3f()
    private val cameraZ = Vector3f()
    private val newCameraVector = Vector3f()
    private val cameraDelta = Vector3f()

    private val exampleSurfaces = listOf(
        Surface("(Hex) 3,7 Torus Knot", mutableMapOf("gridType" to "hex", "width" to "800", "height" to "16", "orientation" to "pointy", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), TorusKnot, 0.2f, 5.0f),
        Surface("(Square) 3,7 Torus Knot", mutableMapOf("gridType" to "square", "width" to "800", "height" to "16", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), TorusKnot, 0.2f, 5.0f),
        Surface("(Hex) 11,17 Torus Knot", mutableMapOf("gridType" to "hex", "width" to "1100", "height" to "12", "orientation" to "pointy", "p" to "11", "q" to "17", "a" to "1.0", "b" to "0.2"), TorusKnot, 0.2f, 5.0f),
        Surface("(Square) 11,17 Torus Knot", mutableMapOf("gridType" to "square", "width" to "1100", "height" to "13", "p" to "11", "q" to "17", "a" to "1.0", "b" to "0.2"), TorusKnot, 0.2f, 5.0f),
        Surface("(Hex) Torus", mutableMapOf("gridType" to "hex", "width" to "160", "height" to "50", "orientation" to "pointy", "majorRadius" to "8.0f"), SimpleTorus, 1.5f, 1.0f),
        Surface("(Square) Torus", mutableMapOf("gridType" to "square", "width" to "160", "height" to "50", "majorRadius" to "8.0f"), SimpleTorus, 1.5f, 1.0f),
        Surface("(Hex) 10b Decorated Torus Knot", mutableMapOf("gridType" to "hex", "width" to "900", "height" to "16", "orientation" to "pointy", "pattern" to "Type10b"), DecoratedTorusKnot, 0.25f, 5.0f),
        Surface("(Square) 10b Decorated Torus Knot", mutableMapOf("gridType" to "square", "width" to "900", "height" to "16", "pattern" to "Type10b"), DecoratedTorusKnot, 0.25f, 5.0f),
        Surface("(Hex) Trefoil", mutableMapOf("gridType" to "hex", "width" to "600", "height" to "26", "orientation" to "pointy"), Trefoil, 0.6f, 3.0f),
        Surface("(Square) Trefoil", mutableMapOf("gridType" to "square", "width" to "600", "height" to "26"), Trefoil, 0.6f, 3.0f),
        Surface("(Hex) 3 Factor Parametric", mutableMapOf("gridType" to "hex", "width" to "1000", "height" to "12", "orientation" to "pointy", "a" to "2", "b" to "5", "c" to "4"), Trefoil, 0.3f, 3.0f),
        Surface("(Square) 3 Factor Parametric", mutableMapOf("gridType" to "square", "width" to "1000", "height" to "12", "a" to "2", "b" to "5", "c" to "4"), Trefoil, 0.3f, 3.0f),
        Surface("(Hex) Epitrochoid", mutableMapOf("gridType" to "hex", "width" to "1000", "height" to "12", "orientation" to "pointy", "a" to "5.0", "b" to "1.0", "c" to "3.5"), Epitrochoid, 0.3f, 3.0f),
        Surface("(Square) Epitrochoid", mutableMapOf("gridType" to "square", "width" to "1000", "height" to "12", "a" to "5.0", "b" to "1.0", "c" to "3.5"), Epitrochoid, 0.3f, 3.0f),
    )

    private var surface = exampleSurfaces.first().copy()
    private var surfaceMapper = surface.createSurfaceMapper()

    // Main Options for application
    private val conwayOptions = ConwayOptions(
        gameOptions = GameOptions(
            pauseGame = true,
            gameSpeed = 5,
            useTexture = false,
            aliveColour = colourOn
        ),
        surfaceOptions = SurfaceOptions(
            globalAlpha = 1f,
            animationPercentages = animationPercentages,
            surface = surface,
            surfaces = exampleSurfaces
        ),
        cameraOptions = CameraOptions(
            cameraFrameNumber = 1,
            maxCameraFrames = -1,
            movingCamera = false,
            loopCamera = false,
            cameraPathNames = cameraPaths.keys.sorted(),
            currentCameraPath = -1,
            lookAhead = 10,
            fov = FOV
        ),
        debugOptions = DebugOptions(
            showHud = false,
            showMessage = false,
            showPolygons = false
        ),
        stateChangeFunction = ::changeState
    )

    // this is hex specific, needs refactoring
    private fun readInitialPosition(): Set<GridItem> {
        return when (surfaceMapper.mappingType()) {
            GridType.HEX -> {
                // This will need changing to check it's right type
                val constrainer = surfaceMapper.grid() as HexConstrainer
                val doubledCoords = Day24.walk(data).take(initialPoints)
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
                val slider = setOf(
                    Square(0, 2, constrainer),
                    Square(1, 2, constrainer),
                    Square(2, 2, constrainer),
                    Square(2, 1, constrainer),
                    Square(1, 0, constrainer)
                )
                val squares = constrainer.items().mapNotNull { if (Random.nextBoolean()) it else null }
                return squares.toSet()
            }
        }
    }

    override fun init(window: Window) {
        loadTextures()
        createGameItems()

        with(ImGui) {
            sysDefault = io.fonts.addFontDefault()
            ubuntuFont = io.fonts.addFontFromFileTTF("fonts/UbuntuMono-R.ttf", 18.0f) ?: sysDefault
        }

        hud.init(window)
        renderer.init()
        keyPressedTimer.set()
        cameraMovementTimer.set()
    }

    private fun createGameItems() {
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

    private fun itemToColour(item: GridItem): Vector4f {
        val hexAxis = surfaceMapper.itemAxis(item)
        return Vector4f(
            hexAxis.axes.m00 * 0.65f + 0.1f,
            hexAxis.axes.m11 * 0.65f + 0.1f,
            hexAxis.axes.m22 * 0.65f + 0.1f,
            conwayOptions.surfaceOptions.globalAlpha
        )
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        // skip if imgUI is handling input
        if (window.ctx.io.wantCaptureKeyboard or window.ctx.io.wantCaptureMouse or window.ctx.io.wantTextInput) return

        turboMove = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)
        when {
            window.isKeyPressed(GLFW_KEY_W) -> changeState(MoveForward)
            window.isKeyPressed(GLFW_KEY_S) -> changeState(MoveBackward)
            window.isKeyPressed(GLFW_KEY_A) -> changeState(MoveLeft)
            window.isKeyPressed(GLFW_KEY_D) -> changeState(MoveRight)
            window.isKeyPressed(GLFW_KEY_DOWN) -> changeState(MoveDown)
            window.isKeyPressed(GLFW_KEY_UP) -> changeState(MoveUp)
            window.isKeyPressed(GLFW_KEY_SEMICOLON) -> changeState(PrintState)
            window.isKeyPressed(GLFW_KEY_EQUAL) -> changeState(IncreaseSpeed)
            window.isKeyPressed(GLFW_KEY_MINUS) -> changeState(DecreaseSpeed)
            window.isKeyPressed(GLFW_KEY_R) -> changeState(ResetGame)
            window.isKeyPressed(GLFW_KEY_P) -> changeState(TogglePause)
            window.isKeyPressed(GLFW_KEY_1) -> changeState(SingleStep)
            window.isKeyPressed(GLFW_KEY_0) -> changeState(ResetCamera)
            window.isKeyPressed(GLFW_KEY_C) -> changeState(RunCamera)
            window.isKeyPressed(GLFW_KEY_M) -> changeState(ToggleMessages)
            window.isKeyPressed(GLFW_KEY_H) -> changeState(ToggleHud)
            window.isKeyPressed(GLFW_KEY_GRAVE_ACCENT) -> changeState(ToggleImgUI)
            window.isKeyPressed(GLFW_KEY_N) -> changeState(NextCamera)
        }
    }

    private fun moveUp() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraY, newCameraVector)
        worldCentre.add(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveDown() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraY, newCameraVector)
        worldCentre.sub(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveRight() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraX, newCameraVector)
        worldCentre.add(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveLeft() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraX, newCameraVector)
        worldCentre.sub(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveBackward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraZ, newCameraVector)
        worldCentre.add(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveForward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraZ, newCameraVector)
        worldCentre.sub(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun resetCamera() = with(camera) {
        rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z, initialCameraRotation.w)
        position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
        worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
        with(conwayOptions.cameraOptions) {
            movingCamera = false
            loopCamera = false
            cameraFrameNumber = 1
            maxCameraFrames = -1
            currentCameraPath = -1
            fov = FOV
        }
    }

    private fun loadInitialCameraPosition(keepFrame: Boolean) {
        with(conwayOptions.cameraOptions) {
            cameraData.clear()
            cameraData.addAll(cameraPaths[cameraPathNames[currentCameraPath]]!!.invoke())

            if (!keepFrame) cameraFrameNumber = 1
            maxCameraFrames = cameraData.size

            setCamera()
        }
    }

    private fun calculateTunnelPath(): List<CameraData> {
        return CameraPath.generateCameraPath(surfaceMapper.pathCreator, surfaceMapper.grid().width, conwayOptions.cameraOptions.lookAhead)
    }

    private fun moveCamera() {
        with(conwayOptions.cameraOptions) {
            if (currentCameraPath == -1) return
            if ((loopCamera || cameraFrameNumber <= cameraData.size) && cameraMovementTimer.accumulative > 0.03) {
                setCamera()
                cameraFrameNumber++
                if (cameraFrameNumber > cameraData.size) {
                    cameraFrameNumber = if (!loopCamera) cameraData.size else 1
                }
                cameraMovementTimer.set()
            }
        }
    }

    private fun setCamera() {
        with(conwayOptions.cameraOptions) {
            val cp = cameraData[cameraFrameNumber - 1].location
            val cr = cameraData[cameraFrameNumber - 1].rotation
            camera.setPosition(cp.x, cp.y, cp.z)
            camera.setRotation(cr.w, cr.x, cr.y, cr.z)
            val lookAt = (cameraFrameNumber + lookAhead - 1) % maxCameraFrames
            worldCentre.set(cameraData[lookAt].location)
        }
    }

    private fun nextCamera() {
        conwayOptions.cameraOptions.currentCameraPath = (conwayOptions.cameraOptions.currentCameraPath + 1) % cameraPaths.size
        conwayOptions.cameraOptions.movingCamera = false
        loadInitialCameraPosition(false)
    }

    private fun createSurface() {
        // re-initialise everything with new surface
        loadTextures()
        alive.clear()
        creating.clear()
        destroying.clear()
        conwayIteration = 0
        resetCamera()
        cleanup()
        gameItems.clear()
        storage.clearAll()
        surface = conwayOptions.surfaceOptions.surface
        surfaceMapper = surface.createSurfaceMapper()
        createGameItems()
        renderer.init()
    }

    private fun loadTextures() {
        aliveTexturePointy = Texture("visualisations/textures/new-white-pointy.png")
        aliveTextureFlat = Texture("visualisations/textures/new-white-flat.png")
    }

    private fun resetGame() {
        alive.clear()
        creating.clear()
        destroying.clear()
        conwayIteration = 0
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)!!
            data.state = DEAD
            data.gameItem.colour = itemToColour(item)
            data.gameItem.mesh.texture = null
        }
    }

    private fun printGameState() {
        println(
            """
            camera: $camera
            world centre: $worldCentre
            """.trimIndent()
        )
    }

    private fun toggleTextureMode() {
        if (conwayOptions.gameOptions.useTexture) {
            // for any currently animating, turn them into full textures
            destroying.forEach { hex ->
                storage.getData(hex)!!.gameItem.colour.set(itemToColour(hex))
                storage.getData(hex)!!.gameItem.mesh.texture = null
            }
            alive.forEach { hex ->
                storage.getData(hex)!!.gameItem.colour.set(itemToColour(hex))
                storage.getData(hex)!!.gameItem.mesh.texture = getAliveTexture()
            }
        } else {
            // change to colour mode
            // all the currently alive (includes creating), turn them fully on, then deal with the changing
            alive.forEach { hex ->
                storage.getData(hex)!!.gameItem.colour.set(conwayOptions.gameOptions.aliveColour)
                storage.getData(hex)!!.gameItem.mesh.texture = null
            }
            // any changing states can be in their correct animation phase
            setAnimationColours(currentStepDelay % conwayOptions.gameOptions.gameSpeed)
        }
    }

    private fun setGameItemsAlpha() {
        val currentPauseState = conwayOptions.gameOptions.pauseGame
        conwayOptions.gameOptions.pauseGame = true
        // any non animating or alive hexes need to change their alpha value.
        (storage.items - alive - creating - destroying).forEach { hex ->
            storage.getData(hex)!!.gameItem.colour.w = conwayOptions.surfaceOptions.globalAlpha
        }
        conwayOptions.gameOptions.pauseGame = currentPauseState
    }

    private fun changeState(command: KeyCommand) {
        if (command is SingleKeyPressCommand) {
            // Stop key repeats when this is meant to be a 1 shot press
            val pressedAt = keyPressedTimer.time
            if ((pressedAt - lastPressedAt) < KEYBOARD_REPEAT_DELAY) {
                return
            }
            lastPressedAt = pressedAt
            if (conwayOptions.debugOptions.showMessage) {
                flashMessage = command.toString()
                flashPercentage = 100
            }
        }

        when (command) {
            DecreaseSpeed -> conwayOptions.gameOptions.gameSpeed = (conwayOptions.gameOptions.gameSpeed + 1).coerceAtMost(50)
            IncreaseSpeed -> conwayOptions.gameOptions.gameSpeed = (conwayOptions.gameOptions.gameSpeed - 1).coerceAtLeast(1)
            ResetGame -> resetGame()
            TogglePause -> conwayOptions.gameOptions.pauseGame = !conwayOptions.gameOptions.pauseGame
            PrintState -> printGameState()
            SingleStep -> performSingleStep = true
            ResetCamera -> resetCamera()
            LoadCamera -> loadInitialCameraPosition(keepFrame = false)
            SetLookahead -> loadInitialCameraPosition(keepFrame = true)
            NextCamera -> nextCamera()
            SetCamera -> setCamera()
            RunCamera -> conwayOptions.cameraOptions.movingCamera = !conwayOptions.cameraOptions.movingCamera
            ToggleMessages -> conwayOptions.debugOptions.showMessage = !conwayOptions.debugOptions.showMessage
            ToggleHud -> conwayOptions.debugOptions.showHud = !conwayOptions.debugOptions.showHud
            ToggleImgUI -> showImgUI = !showImgUI
            CreateSurface -> createSurface()
            ToggleTexture -> toggleTextureMode()
            SetGlobalAlpha -> setGameItemsAlpha()

            MoveForward -> moveForward()
            MoveBackward -> moveBackward()
            MoveLeft -> moveLeft()
            MoveRight -> moveRight()
            MoveDown -> moveDown()
            MoveUp -> moveUp()
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        // Don't process mouse inputs if it is over a ImgUI window
        if (window.ctx.io.wantCaptureKeyboard or window.ctx.io.wantCaptureMouse or window.ctx.io.wantTextInput) {
            mouseInput.scrollDirection = 0 // stop weird hangover of scrolling continuing to show once if used when focus is on ImgUI
            return
        }

        // Mouse detection
        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) && abs(mouseInput.displVec.lengthSquared()) > 0.001f -> {
                conwayOptions.cameraOptions.movingCamera = false
                // free camera move in its XY plane
                val moveVec: Vector2f = mouseInput.displVec
                camera.rotation.positiveY(cameraY).mul(moveVec.x * MOUSE_SENSITIVITY)
                camera.rotation.positiveX(cameraX).mul(moveVec.y * MOUSE_SENSITIVITY)
                cameraY.sub(cameraX, cameraDelta)
                worldCentre.add(cameraDelta)
                camera.setPosition(camera.position.x + cameraDelta.x, camera.position.y + cameraDelta.y, camera.position.z + cameraDelta.z)
            }

            mouseInput.isMiddleButtonPressed && abs(mouseInput.displVec.lengthSquared()) > 0.001f -> {
                conwayOptions.cameraOptions.movingCamera = false
                val moveVec: Vector2f = mouseInput.displVec
                val rotAngles = Vector3f(-MOUSE_SENSITIVITY * moveVec.y, -MOUSE_SENSITIVITY * moveVec.x, 0f)

                // do left/right first so we don't affect the up vector
                camera.rotation.positiveX(cameraX)

                // calculate the new camera direction (relative to world centre) after rotation by global Y first
                camera.position.sub(worldCentre, newCameraVector)
                if (rotAngles.x != 0f) {
                    newCameraVector.rotateAxis(rotAngles.x, globalY.x(), globalY.y(), globalY.z())
                }

                // calculate camera's new rotation vector before we rotate about its local X for any up/down movement
                // the X vector effectively rotates about the Gy vector by same angle. Paper and pen! and this introduces no roll
                cameraX.rotateAxis(rotAngles.x, globalY.x(), globalY.y(), globalY.z())

                // now rotate about any up/down
                if (rotAngles.y != 0f) {
                    newCameraVector.rotateAxis(rotAngles.y, cameraX.x, cameraX.y, cameraX.z)
                }
                // calculate the new up vector from the direction vector and the unchanged (for this part of the rotation) X vector
                newCameraVector.normalize(cameraZ)
                cameraZ.cross(cameraX, cameraY)

                val newRotationMatrix = Matrix3f().setColumn(0, cameraX).setColumn(1, cameraY).setColumn(2, cameraZ)
                val newRotation = Quaternionf().setFromNormalized(newRotationMatrix).conjugate()

                newCameraVector.add(worldCentre)
                camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
                camera.setRotation(newRotation.w, newRotation.x, newRotation.y, newRotation.z)
            }

            mouseInput.scrollDirection != 0 -> {
                conwayOptions.cameraOptions.movingCamera = false
                // move camera a percentage closer/further from world centre.
                val percentageChange = if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) 0.10f else 0.02f

                val scaleFactor = if (mouseInput.scrollDirection < 0) (1f + percentageChange) else (1f - percentageChange)
                val newDistanceToWorldCentre = (camera.position.distance(worldCentre) * scaleFactor).coerceAtLeast(0.04f)

                // Store the camera's unit Z axis in cameraZ, then move the camera in that direction to its new location
                camera.rotation.positiveZ(cameraZ)
                worldCentre.add(cameraZ.mul(newDistanceToWorldCentre), newCameraVector)
                camera.position.set(newCameraVector)

                // remove the scroll indicator as it does not automatically reset itself when we stop scrolling wheel
                mouseInput.scrollDirection = 0
            }
        }
    }

    private fun performStep() {
        // mark all the hexes that were creating as alive, destroying as dead, set their colours to end conditions
        surfaceMapper.grid().items().forEach { item ->
            val data = storage.getData(item)
            if (data?.state == CREATING) {
                data.state = ALIVE
                if (conwayOptions.gameOptions.useTexture) {
                    data.gameItem.mesh.texture = getAliveTexture()
                } else {
                    data.gameItem.colour.set(conwayOptions.gameOptions.aliveColour)
                }
            }
            if (data?.state == DESTROYING) {
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

        if (conwayOptions.gameOptions.useTexture) {
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
        conwayIteration++
        if (alive.size == 0) {
            alive.addAll(readInitialPosition())
            alive.forEach { gridItem ->
                storage.getData(gridItem)!!.state = ALIVE
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
            }
            newAlive
        }
    }

    override fun render(window: Window) {
        if (conwayOptions.cameraOptions.movingCamera) {
            moveCamera()
        }

        if (!conwayOptions.gameOptions.pauseGame || performSingleStep) {
            // do next conway step
            currentStepDelay++

            var animationStep = currentStepDelay % conwayOptions.gameOptions.gameSpeed
            if (performSingleStep && conwayOptions.gameOptions.useTexture) animationStep = 0 // ok for textures, not colours
            if (animationStep == 0) {
                currentStepDelay = 0
                performStep()
            }

            if (!conwayOptions.gameOptions.useTexture) {
                setAnimationColours(animationStep)
            }
            performSingleStep = false
        }
        flashPercentage = max(flashPercentage - 7, 0)
        if (flashPercentage == 0) flashMessage = ""

        val polygonMode = if (conwayOptions.debugOptions.showPolygons) GL_LINE else GL_FILL
        glPolygonMode(GL11C.GL_FRONT_AND_BACK, polygonMode)
        // sort the gameItems so that the "on" items are at the start - vertices don't seem to get overwritten by later items (expected it to be other way around!)
        val aliveGameItems = storage.data.filter { data -> setOf(ALIVE).contains(data.state) }.map { it.gameItem }
        val creatingGameItems = storage.data.filter { data -> setOf(CREATING).contains(data.state) }.map { it.gameItem }
        val destroyingGameItems = storage.data.filter { data -> setOf(DESTROYING).contains(data.state) }.map { it.gameItem }
        val notAliveGameItems = gameItems - aliveGameItems - creatingGameItems - destroyingGameItems
        renderer.render(window, camera, aliveGameItems + creatingGameItems + destroyingGameItems + notAliveGameItems, conwayOptions.cameraOptions.fov)
        glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL_FILL)
        doHud(window)
        if (showImgUI) doImgUI(window)
    }

    private fun setAnimationColours(animationStep: Int) {
        val animationPercentage = (animationStep + 1) / conwayOptions.gameOptions.gameSpeed.toFloat()
        // Now find all hexes that are animating and work out their new colours
//        alive.forEach { hex ->
//            hexStorage.getData(hex)!!.gameItem.colour.set(colourOn)
//        }
//        println("step: $animationStep, pc: $animationPercentage, alive: ${alive.count()}, creating: ${creating.count()}, destroying: ${destroying.count()}")
//        println("creating: ${creating.map { it.simpleValue() }}")
//        println("destroying: ${destroying.map { it.simpleValue() }}")
        val animationCurveValue = calculatePercentage(animationPercentage)
        creating.forEach { gridItem ->
            setAnimationColour(gridItem, 1f - animationCurveValue)
        }
        destroying.forEach { gridItem ->
            setAnimationColour(gridItem, animationCurveValue)
        }
    }

    private fun setAnimationColour(gridItem: GridItem, animationCurveValue: Float) {
        val newColour = itemToColour(gridItem).sub(conwayOptions.gameOptions.aliveColour).mul(animationCurveValue).add(conwayOptions.gameOptions.aliveColour)
        storage.getData(gridItem)!!.gameItem.colour.set(newColour)
        storage.getData(gridItem)!!.gameItem.mesh.texture = null
    }

    private fun calculatePercentage(animationPercentage: Float): Float {
        val lower10 = (animationPercentage * 10f).toInt() * 10 // e.g. 0.47 -> 40
        val upper10 = ((animationPercentage + 0.1f) * 10f).toInt() * 10
        val between = (animationPercentage * 100f).toInt() - lower10
        val lowerP = conwayOptions.surfaceOptions.animationPercentages.getOrDefault(lower10, 1f)
        val upperP = conwayOptions.surfaceOptions.animationPercentages.getOrDefault(upper10, 1f)
        return lowerP * (10f - between) / 10f + upperP * between / 10f
    }

    private fun doImgUI(window: Window) {
        window.implGl3.newFrame()
        window.implGlfw.newFrame()
        ImGui.run {
            newFrame()
            pushFont(ubuntuFont)
            conwayOptions.render(window.width)
        }
        ImGui.render()
        window.implGl3.renderDrawData(ImGui.drawData!!)
    }

    private fun doHud(window: Window) {
        val hudData = HudData(
            speed = conwayOptions.gameOptions.gameSpeed,
            iteration = conwayIteration,
            isPaused = conwayOptions.gameOptions.pauseGame,
            liveCount = alive.count(),
            flashMessage = flashMessage,
            flashPercentage = flashPercentage,
            createdCount = createdCount,
            destroyedCount = destroyedCount,
            showBar = conwayOptions.debugOptions.showHud
        )
        hud.render(window, hudData)
    }

    override fun cleanup() {
        renderer.cleanup()
        gameItems.forEach { it.mesh.cleanUp() }
    }

    companion object {
        const val CAMERA_POS_STEP = 0.015f
        const val MOUSE_SENSITIVITY = 0.003f
        const val KEYBOARD_REPEAT_DELAY = 0.2

        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            Configuration.DEBUG.set(true)
            engine.run()
        }
    }
}
