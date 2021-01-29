package advents.conwayhex.game

import advents.conwayhex.game.ui.CameraOptions
import advents.conwayhex.game.ui.ConwayOptions
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
import commands.SetLookahead
import commands.SingleKeyPressCommand
import commands.SingleStep
import commands.ToggleHud
import commands.ToggleImgUI
import commands.ToggleMessages
import commands.TogglePause
import engine.GameEngine
import engine.GameLogic
import engine.MouseInput
import engine.Timer
import engine.Window
import engine.graph.Camera
import engine.graph.CameraLoader
import engine.graph.OBJLoader.loadMesh
import engine.graph.Renderer
import engine.graph.Texture
import engine.item.GameItem
import imgui.ImGui
import imgui.font.Font
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.projection.DecoratedKnotSurface
import net.fish.geometry.hex.projection.DecoratedKnotType
import net.fish.geometry.hex.projection.DecoratedKnotType.Type10b
import net.fish.geometry.hex.projection.DecoratedKnotType.Type11c
import net.fish.geometry.hex.projection.EpitrochoidSurface
import net.fish.geometry.hex.projection.SimpleTorusSurface
import net.fish.geometry.hex.projection.ThreeFactorParametricSurface
import net.fish.geometry.hex.projection.TorusKnotSurface
import net.fish.geometry.hex.projection.TrefoilSurface
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.CameraPath
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Math.abs
import org.joml.Math.max
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3fc
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
import kotlin.collections.set

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
    private val hexToGameItem = mutableMapOf<Hex, GameItem>()
    private val gameItemToHex = mutableMapOf<GameItem, Hex>()
    private val alive = mutableSetOf<Hex>()
    private val initialPoints = 25 // we have up to 597 initial points from the original game
    private var createdCount = 0
    private var destroyedCount = 0

    // keyboard handling
    private val keyPressedTimer = Timer()
    private var lastPressedAt = 0.0
    private var turboMove = false

    // text flashing
    private var flashPercentage: Int = 0
    private var flashMessage: String = ""

    // Textures
    lateinit var aliveTexturePointy: Texture
    lateinit var aliveTextureFlat: Texture

    // make it read only by typing it as constant - ensures it's not changed
    private val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    private val cameraMovementTimer = Timer()

    private val cameraPaths = mutableMapOf(
        "Simple Circle Path" to { CameraLoader.loadCamera("/conwayhex/simple-circle-path.txt") },
        "Fly by 11c (a)" to { CameraLoader.loadCamera("/conwayhex/fly-by-11c.txt") },
        "Fly by 11c (b)" to { CameraLoader.loadCamera("/conwayhex/fly-by-11c-2.txt") },
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

    // Surfaces
    private val surfaces = mutableMapOf(
        "Torus" to SimpleTorusSurface(160, 50, POINTY, 8.0f, 1.5f, 1.0f),
        "Trefoil Knot" to TrefoilSurface(600, 26, POINTY, 0.6f, 3.0f),
        "Torus Knot 3,7" to TorusKnotSurface(900, 16, POINTY, 3, 7, 1.0f, 0.2f, 0.2f, 5.0f),
        "Torus Knot 11,17" to TorusKnotSurface(1300,12, POINTY, 11, 17, 1.0f, 0.2f, 0.2f, 5.0f),
        "Decorated Torus Knot" to DecoratedKnotSurface(900, 16, POINTY, Type10b, 0.25f, 5.0f),
        "Epitrochoid" to EpitrochoidSurface(1200, 12, POINTY, 5f, 1f, 3.5f, 0.2f, 0.5f),
        "3 Factor Parametric" to ThreeFactorParametricSurface(1220, 12, POINTY, 2, 5, 4, 0.3f, 3f)
    )

    // Main Options for application
    private val conwayOptions = ConwayOptions(
        gameSpeed = 5,
        globalAlpha = 1f,
        showHud = false,
        showMessage = false,
        pauseGame = true,
        showPolygons = false,
        cameraOptions = CameraOptions(
            cameraFrameNumber = 1,
            maxCameraFrames = -1,
            movingCamera = false,
            loopCamera = false,
            cameraPathNames = cameraPaths.keys.sorted(),
            currentCameraPath = -1,
            lookAhead = 10
        ),
        currentSurfaceName = "Decorated Torus Knot",
        surfaces = surfaces,
        stateChangeFunction = ::changeState
    )

    private var surface = surfaces[conwayOptions.currentSurfaceName]!!

    private fun readInitialPosition(): Set<Hex> {
        val doubledCoords = Day24.walk(data).take(initialPoints)
        val hexes = doubledCoords.map { (col, row) ->
            val qc = col + row / 2 + row % 2
            val rc = -row
            val sc = 0 - qc - rc
            Hex(qc, rc, sc, surface.hexGrid)
        }
        return hexes.map { surface.hexGrid.constrain(it) }.toSet()
    }

    override fun init(window: Window) {
        loadTextures()

        surface.createMapper()
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
        surface.hexGrid.hexes().forEachIndexed { _, hex ->
            val newMesh = loadMesh(surface.mapper.hexToObj(hex))
            val hexColour = hexToColour(hex)
            newMesh.colour = hexColour

            val gameItem = GameItem(newMesh)
            // items are relative to world coords already in both position, scale and have axes same as world coords
            // Another way to do this is calculate N meshes for the minor circle, as they will be repeated around the major circle, but with different location and rotations
            // which would make N mesh instead of NxM mesh
            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
            hexToGameItem[hex] = gameItem
            gameItemToHex[gameItem] = hex
        }
    }

    private fun hexToColour(hex: Hex): Vector3f {
        val hexAxis = surface.mapper.hexAxis(hex)
        return Vector3f(
            hexAxis.axes.m00 * 0.65f + 0.1f,
            hexAxis.axes.m11 * 0.65f + 0.1f,
            hexAxis.axes.m22 * 0.65f + 0.1f
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
        return CameraPath.generateCameraPath(surface, conwayOptions.cameraOptions.lookAhead)
    }

    private fun moveCamera() {
        with(conwayOptions.cameraOptions) {
            if (currentCameraPath == -1) return
            if ((loopCamera || cameraFrameNumber <= cameraData.size) && cameraMovementTimer.accumulative > 0.03) {
                setCamera()
                cameraFrameNumber++
                if (cameraFrameNumber > cameraData.size) {
                    if (!loopCamera) {
                        cameraFrameNumber = cameraData.size
                    } else {
                        cameraFrameNumber = 1
                    }
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
        conwayIteration = 0
        resetCamera()
        cleanup()
        gameItems.clear()
        hexToGameItem.clear()
        gameItemToHex.clear()
        surface = surfaces[conwayOptions.currentSurfaceName]!!
        surface.createMapper()
        createGameItems()
        renderer.init()
    }

    private fun loadTextures() {
        aliveTexturePointy = Texture("visualisations/textures/new-white-pointy-50trans.png")
        aliveTextureFlat = Texture("visualisations/textures/new-white-flat.png")
    }

    private fun resetGame() {
        alive.clear()
        conwayIteration = 0
        surface.hexGrid.hexes().forEach {
            hexToGameItem[it]!!.mesh.colour = hexToColour(it)
            hexToGameItem[it]!!.mesh.texture = null
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

    fun changeState(command: KeyCommand) {
        if (command is SingleKeyPressCommand) {
            // Stop key repeats when this is meant to be a 1 shot press
            val pressedAt = keyPressedTimer.time
            if ((pressedAt - lastPressedAt) < KEYBOARD_REPEAT_DELAY) {
                return
            }
            lastPressedAt = pressedAt
            if (conwayOptions.showMessage) {
                flashMessage = command.toString()
                flashPercentage = 100
            }
        }

        when (command) {
            DecreaseSpeed -> conwayOptions.gameSpeed = (conwayOptions.gameSpeed + 1).coerceAtMost(50)
            IncreaseSpeed -> conwayOptions.gameSpeed = (conwayOptions.gameSpeed - 1).coerceAtLeast(1)
            ResetGame -> resetGame()
            TogglePause -> conwayOptions.pauseGame = !conwayOptions.pauseGame
            PrintState -> printGameState()
            SingleStep -> if (conwayOptions.pauseGame) performStep()
            ResetCamera -> resetCamera()
            LoadCamera -> loadInitialCameraPosition(keepFrame = false)
            SetLookahead -> loadInitialCameraPosition(keepFrame = true)
            NextCamera -> nextCamera()
            SetCamera -> setCamera()
            RunCamera -> conwayOptions.cameraOptions.movingCamera = !conwayOptions.cameraOptions.movingCamera
            ToggleMessages -> conwayOptions.showMessage = !conwayOptions.showMessage
            ToggleHud -> conwayOptions.showHud = !conwayOptions.showHud
            ToggleImgUI -> showImgUI = !showImgUI
            CreateSurface -> createSurface()

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
        // get the new state for entire grid, then work out which have flipped
        val newAlive = runConway()
        val newOn = newAlive - alive
        val newOff = alive - newAlive
        createdCount = newOn.count()
        destroyedCount = newOff.count()

        alive.clear()
        alive.addAll(newAlive)

        newOff.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = null
            hexToGameItem[hex]!!.mesh.colour = hexToColour(hex)
        }

        newOn.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = if (surface.hexGrid.layout.orientation == POINTY) aliveTexturePointy else aliveTextureFlat
        }
    }

    private fun runConway(): Set<Hex> {
        conwayIteration++
        if (alive.size == 0) {
            alive.addAll(readInitialPosition())
        }
        val allTouchingHexes = mutableSetOf<Hex>()
        alive.forEach { hex ->
            allTouchingHexes.addAll(hex.neighbours())
        }

        val splitSize = allTouchingHexes.size / 12
        val blocks = allTouchingHexes.chunked(splitSize)
        val newAlive = mutableSetOf<Hex>()
        runBlocking {
            val defs = blocks.map { hexList ->
                async(Dispatchers.Default) { calculateAsync(hexList) }
            }
            defs.awaitAll().map { newAlive.addAll(it) }
        }

        return newAlive
    }

    private suspend fun calculateAsync(hexes: List<Hex>): Set<Hex> = withContext(Dispatchers.Default) {
        calculate(hexes)
    }

    private fun calculate(hexes: List<Hex>): MutableSet<Hex> {
        return hexes.fold(mutableSetOf()) { newAlive, hex ->
            val neighbourCount = hex.neighbours().intersect(alive).count()
            val isAlive = alive.contains(hex)

            when {
                isAlive && (neighbourCount == 0 || neighbourCount > 2) -> newAlive.remove(hex)
                !isAlive && neighbourCount == 2 -> newAlive.add(hex)
                isAlive -> newAlive.add(hex)
            }

            newAlive
        }
    }

    override fun render(window: Window) {
        if (conwayOptions.cameraOptions.movingCamera) {
            moveCamera()
        }


        if (!conwayOptions.pauseGame) {
            // do next conway step
            currentStepDelay += 1
            if (currentStepDelay % conwayOptions.gameSpeed == 0) {
                currentStepDelay = 0
                performStep()
            }
        }
        flashPercentage = max(flashPercentage - 7, 0)
        if (flashPercentage == 0) flashMessage = ""



        val polygonMode = if (conwayOptions.showPolygons) GL_LINE else GL_FILL
        glPolygonMode(GL11C.GL_FRONT_AND_BACK, polygonMode)
        // sort the gameItems so that the "on" items are at the start - vertices don't seem to get overwritten by later items (expected it to be other way around!)
        val notAliveGameItems = gameItems.filterNot { item ->
            alive.contains(gameItemToHex[item])
        }
        val aliveGameItems = gameItems - notAliveGameItems
        renderer.render(window, camera, aliveGameItems + notAliveGameItems, conwayOptions.globalAlpha)
        glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL_FILL)
        doHud(window)
        if (showImgUI) doImgUI(window)
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
            speed = conwayOptions.gameSpeed,
            iteration = conwayIteration,
            isPaused = conwayOptions.pauseGame,
            liveCount = alive.count(),
            flashMessage = flashMessage,
            flashPercentage = flashPercentage,
            createdCount = createdCount,
            destroyedCount = destroyedCount,
            showBar = conwayOptions.showHud
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
