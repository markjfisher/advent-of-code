package engine

import advents.conwayhex.game.ConwayHex2020Day24
import advents.dumbooctopus.OctopusHud
import advents.ui.CameraOptions
import advents.ui.DebugOptions
import advents.ui.GameOptions
import advents.ui.GlobalOptions
import advents.ui.HudData
import advents.ui.SurfaceOptions
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
import engine.graph.Camera
import engine.graph.Renderer
import engine.item.GameItem
import imgui.ImGui
import imgui.font.Font
import net.fish.geometry.grid.GridItem
import net.fish.geometry.grid.HashMapBackedGridItemDataStorage
import net.fish.geometry.paths.CameraData
import net.fish.geometry.paths.CameraPath
import net.fish.geometry.projection.Surface
import net.fish.geometry.projection.SurfaceMapper
import org.joml.Math
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11C

abstract class GameWorld(
    val allSurfaces: List<Surface>,
    var surface: Surface = allSurfaces.first().copy(),
    var surfaceMapper: SurfaceMapper = surface.createSurfaceMapper(),
    val storage: HashMapBackedGridItemDataStorage<*>,
    val hud: OctopusHud
) {
    abstract fun performStep()
    abstract fun loadTextures()
    abstract fun createGameItems()
    abstract fun resetGame()
    abstract fun toggleTextureMode()
    abstract fun setGameItemsAlpha()
    abstract fun setAnimationColours(animationStep: Int)
    abstract fun getCameraPaths(): Map<String, () -> List<CameraData>>

    // lateinit var storage: HashMapBackedGridItemDataStorage<*>

    // Gfx helpers
    val renderer = Renderer()
    var showImgUI = false
    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font

    // Game state
    var currentStepDelay = 0
    var gameIteration = 0
    val gameItems = mutableListOf<GameItem>()

    var performSingleStep = false

    // keyboard handling
    val keyPressedTimer = Timer()
    var lastPressedAt = 0.0
    var turboMove = false

    // text flashing and other animation
    var flashPercentage: Int = 0
    var flashMessage: String = ""

    // make it read only by typing it as constant - ensures it's not changed
    val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    val cameraMovementTimer = Timer()
    val cameraData = mutableListOf<CameraData>()

    // Free form camera movement
    val initialWorldCentre = Vector3f(0f, 0f, 0f)
    val worldCentre = Vector3f(initialWorldCentre)

    val initialCameraPosition = Vector3f(0f, 0f, 16f)
    val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize()
    val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    // calculation values so they aren't created every loop
    val cameraX = Vector3f()
    val cameraY = Vector3f()
    val cameraZ = Vector3f()
    val newCameraVector = Vector3f()
    val cameraDelta = Vector3f()

    // Main Options for application
    val globalOptions = GlobalOptions(
        optionsName = "Octopus Options",
        gameOptions = GameOptions(
            pauseGame = true,
            gameSpeed = 5,
            useTexture = false,
            // TODO: needs moving, this is conway specific
            aliveColour = Vector4f(0f, 0f, 0f, 0f)
        ),
        surfaceOptions = SurfaceOptions(
            globalAlpha = 1f,
            surface = surface,
            surfaces = allSurfaces
        ),
        cameraOptions = CameraOptions(
            cameraFrameNumber = 1,
            maxCameraFrames = -1,
            movingCamera = false,
            loopCamera = false,
            cameraPathNames = getCameraPaths().keys.sorted(),
            currentCameraPath = -1,
            lookAhead = 10,
            fov = Renderer.FOV
        ),
        debugOptions = DebugOptions(
            showHud = false,
            showMessage = false,
            showPolygons = false
        ),
        stateChangeFunction = ::changeState
    )

    fun gameInit(window: Window) {
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

    fun gameInput(window: Window, mouseInput: MouseInput) {
        // skip if imgUI is handling input
        if (window.ctx.io.wantCaptureKeyboard or window.ctx.io.wantCaptureMouse or window.ctx.io.wantTextInput) return

        turboMove = window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)
        when {
            window.isKeyPressed(GLFW.GLFW_KEY_W) -> changeState(MoveForward)
            window.isKeyPressed(GLFW.GLFW_KEY_S) -> changeState(MoveBackward)
            window.isKeyPressed(GLFW.GLFW_KEY_A) -> changeState(MoveLeft)
            window.isKeyPressed(GLFW.GLFW_KEY_D) -> changeState(MoveRight)
            window.isKeyPressed(GLFW.GLFW_KEY_DOWN) -> changeState(MoveDown)
            window.isKeyPressed(GLFW.GLFW_KEY_UP) -> changeState(MoveUp)
            window.isKeyPressed(GLFW.GLFW_KEY_SEMICOLON) -> changeState(PrintState)
            window.isKeyPressed(GLFW.GLFW_KEY_EQUAL) -> changeState(IncreaseSpeed)
            window.isKeyPressed(GLFW.GLFW_KEY_MINUS) -> changeState(DecreaseSpeed)
            window.isKeyPressed(GLFW.GLFW_KEY_R) -> changeState(ResetGame)
            window.isKeyPressed(GLFW.GLFW_KEY_P) -> changeState(TogglePause)
            window.isKeyPressed(GLFW.GLFW_KEY_1) -> changeState(SingleStep)
            window.isKeyPressed(GLFW.GLFW_KEY_0) -> changeState(ResetCamera)
            window.isKeyPressed(GLFW.GLFW_KEY_C) -> changeState(RunCamera)
            window.isKeyPressed(GLFW.GLFW_KEY_M) -> changeState(ToggleMessages)
            window.isKeyPressed(GLFW.GLFW_KEY_H) -> changeState(ToggleHud)
            window.isKeyPressed(GLFW.GLFW_KEY_GRAVE_ACCENT) -> changeState(ToggleImgUI)
            window.isKeyPressed(GLFW.GLFW_KEY_N) -> changeState(NextCamera)
        }

    }

    fun changeState(command: KeyCommand) {
        if (command is SingleKeyPressCommand) {
            // Stop key repeats when this is meant to be a 1 shot press
            val pressedAt = keyPressedTimer.time
            if ((pressedAt - lastPressedAt) < ConwayHex2020Day24.KEYBOARD_REPEAT_DELAY) {
                return
            }
            lastPressedAt = pressedAt
            if (globalOptions.debugOptions.showMessage) {
                flashMessage = command.toString()
                flashPercentage = 100
            }
        }

        when (command) {
            DecreaseSpeed -> globalOptions.gameOptions.gameSpeed = (globalOptions.gameOptions.gameSpeed + 1).coerceAtMost(50)
            IncreaseSpeed -> globalOptions.gameOptions.gameSpeed = (globalOptions.gameOptions.gameSpeed - 1).coerceAtLeast(1)
            ResetGame -> resetGame()
            TogglePause -> globalOptions.gameOptions.pauseGame = !globalOptions.gameOptions.pauseGame
            PrintState -> printGameState()
            SingleStep -> performSingleStep = true
            ResetCamera -> resetCamera()
            LoadCamera -> loadInitialCameraPosition(keepFrame = false)
            SetLookahead -> loadInitialCameraPosition(keepFrame = true)
            NextCamera -> nextCamera()
            SetCamera -> setCamera()
            RunCamera -> globalOptions.cameraOptions.movingCamera = !globalOptions.cameraOptions.movingCamera
            ToggleMessages -> globalOptions.debugOptions.showMessage = !globalOptions.debugOptions.showMessage
            ToggleHud -> globalOptions.debugOptions.showHud = !globalOptions.debugOptions.showHud
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

    fun loadInitialCameraPosition(keepFrame: Boolean) {
        with(globalOptions.cameraOptions) {
            cameraData.clear()
            cameraData.addAll(getCameraPaths()[cameraPathNames[currentCameraPath]]!!.invoke())

            if (!keepFrame) cameraFrameNumber = 1
            maxCameraFrames = cameraData.size

            setCamera()
        }
    }

    fun moveUp() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraY, newCameraVector)
        worldCentre.add(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun moveDown() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraY, newCameraVector)
        worldCentre.sub(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun moveRight() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraX, newCameraVector)
        worldCentre.add(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun moveLeft() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraX, newCameraVector)
        worldCentre.sub(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun moveBackward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.add(cameraZ, newCameraVector)
        worldCentre.add(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun moveForward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if (turboMove) 5f else 1f)
        camera.position.sub(cameraZ, newCameraVector)
        worldCentre.sub(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    fun resetCamera() = with(camera) {
        rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z, initialCameraRotation.w)
        position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
        worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
        with(globalOptions.cameraOptions) {
            movingCamera = false
            loopCamera = false
            cameraFrameNumber = 1
            maxCameraFrames = -1
            currentCameraPath = -1
            fov = Renderer.FOV
        }
    }

    fun calculateTunnelPath(): List<CameraData> {
        return CameraPath.generateCameraPath(surfaceMapper.pathCreator, surfaceMapper.grid().width, globalOptions.cameraOptions.lookAhead)
    }

    fun moveCamera() {
        with(globalOptions.cameraOptions) {
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

    fun setCamera() {
        with(globalOptions.cameraOptions) {
            val cp = cameraData[cameraFrameNumber - 1].location
            val cr = cameraData[cameraFrameNumber - 1].rotation
            camera.setPosition(cp.x, cp.y, cp.z)
            camera.setRotation(cr.w, cr.x, cr.y, cr.z)
            val lookAt = (cameraFrameNumber + lookAhead - 1) % maxCameraFrames
            worldCentre.set(cameraData[lookAt].location)
        }
    }

    fun nextCamera() {
        globalOptions.cameraOptions.currentCameraPath = (globalOptions.cameraOptions.currentCameraPath + 1) % getCameraPaths().size
        globalOptions.cameraOptions.movingCamera = false
        loadInitialCameraPosition(false)
    }

    fun createSurface() {
        // re-initialise everything with new surface
        loadTextures()
        gameIteration = 0
        resetCamera()
        gameCleanup()
        gameItems.clear()
        storage.clearAll()
        surface = globalOptions.surfaceOptions.surface
        surfaceMapper = surface.createSurfaceMapper()
        createGameItems()
        renderer.init()
    }


    fun itemToColour(item: GridItem): Vector4f {
        val itemAxis = surfaceMapper.itemAxis(item)
        return Vector4f(
            itemAxis.axes.m00 * 0.65f + 0.1f,
            itemAxis.axes.m11 * 0.65f + 0.1f,
            itemAxis.axes.m22 * 0.65f + 0.1f,
            globalOptions.surfaceOptions.globalAlpha
        )
    }

    fun printGameState() {
        println(
            """
            camera: $camera
            world centre: $worldCentre
            """.trimIndent()
        )
    }

    fun gameUpdate(interval: Float, mouseInput: MouseInput, window: Window) {
        // Don't process mouse inputs if it is over a ImgUI window
        if (window.ctx.io.wantCaptureKeyboard or window.ctx.io.wantCaptureMouse or window.ctx.io.wantTextInput) {
            mouseInput.scrollDirection = 0 // stop weird hangover of scrolling continuing to show once if used when focus is on ImgUI
            return
        }

        // Mouse detection
        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)) && Math.abs(mouseInput.displVec.lengthSquared()) > 0.001f -> {
                globalOptions.cameraOptions.movingCamera = false
                // free camera move in its XY plane
                val moveVec: Vector2f = mouseInput.displVec
                camera.rotation.positiveY(cameraY).mul(moveVec.x * MOUSE_SENSITIVITY)
                camera.rotation.positiveX(cameraX).mul(moveVec.y * MOUSE_SENSITIVITY)
                cameraY.sub(cameraX, cameraDelta)
                worldCentre.add(cameraDelta)
                camera.setPosition(camera.position.x + cameraDelta.x, camera.position.y + cameraDelta.y, camera.position.z + cameraDelta.z)
            }

            mouseInput.isMiddleButtonPressed && Math.abs(mouseInput.displVec.lengthSquared()) > 0.001f -> {
                globalOptions.cameraOptions.movingCamera = false
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
                globalOptions.cameraOptions.movingCamera = false
                // move camera a percentage closer/further from world centre.
                val percentageChange = if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)) 0.10f else 0.02f

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

    fun gameRender(window: Window) {
        if (globalOptions.cameraOptions.movingCamera) {
            moveCamera()
        }

        if (!globalOptions.gameOptions.pauseGame || performSingleStep) {
            // do next step
            currentStepDelay++

            var animationStep = currentStepDelay % globalOptions.gameOptions.gameSpeed
            if (performSingleStep && globalOptions.gameOptions.useTexture) animationStep = 0 // ok for textures, not colours
            if (animationStep == 0) {
                currentStepDelay = 0
                performStep()
            }

            if (!globalOptions.gameOptions.useTexture) {
                setAnimationColours(animationStep)
            }
            performSingleStep = false
        }
        flashPercentage = Math.max(flashPercentage - 7, 0)
        if (flashPercentage == 0) flashMessage = ""

        val polygonMode = if (globalOptions.debugOptions.showPolygons) GL11C.GL_LINE else GL11C.GL_FILL
        GL11C.glPolygonMode(GL11C.GL_FRONT_AND_BACK, polygonMode)
        // sort the gameItems so that the "on" items are at the start - vertices don't seem to get overwritten by later items (expected it to be other way around!)
        val items = emptyList<GameItem>()
        renderer.render(window, camera, items, globalOptions.cameraOptions.fov)
        GL11C.glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL11C.GL_FILL)
        doHud(window)
        if (showImgUI) doImgUI(window)
    }

    fun doImgUI(window: Window) {
        window.implGl3.newFrame()
        window.implGlfw.newFrame()
        ImGui.run {
            newFrame()
            pushFont(ubuntuFont)
            globalOptions.render(window.width)
        }
        ImGui.render()
        window.implGl3.renderDrawData(ImGui.drawData!!)
    }

    fun doHud(window: Window) {
        val hudData = HudData(
            speed = globalOptions.gameOptions.gameSpeed,
            iteration = gameIteration,
            isPaused = globalOptions.gameOptions.pauseGame,
            flashMessage = flashMessage,
            flashPercentage = flashPercentage,
            showBar = globalOptions.debugOptions.showHud
        )
        hud.render(window, hudData)
    }

    fun gameCleanup() {
        renderer.cleanup()
        gameItems.forEach { it.mesh.cleanUp() }
    }

    companion object {
        const val CAMERA_POS_STEP = 0.015f
        const val MOUSE_SENSITIVITY = 0.003f
        const val KEYBOARD_REPEAT_DELAY = 0.2
    }
}