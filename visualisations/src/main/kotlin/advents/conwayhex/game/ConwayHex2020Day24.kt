package advents.conwayhex.game

import commands.DecreaseSpeed
import commands.IncreaseSpeed
import commands.KeyCommand
import commands.MoveBackward
import commands.MoveDown
import commands.MoveForward
import commands.MoveLeft
import commands.MoveRight
import commands.MoveUp
import commands.PrintState
import commands.ResetCamera
import commands.ResetGame
import commands.SingleKeyPressCommand
import commands.SingleStep
import commands.TogglePause
import engine.GameEngine
import engine.GameLogic
import engine.MouseInput
import engine.Timer
import engine.Window
import engine.graph.Camera
import engine.graph.OBJLoader.loadMesh
import engine.graph.Renderer
import engine.graph.Texture
import engine.item.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.hex.projection.PathMappedWrappingHexGrid
import net.fish.geometry.hex.projection.TorusMappedWrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator
import net.fish.geometry.paths.TorusKnotPathCreator
import net.fish.geometry.paths.TrefoilPathCreator
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
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
import org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS
import org.lwjgl.glfw.GLFW.GLFW_KEY_P
import org.lwjgl.glfw.GLFW.GLFW_KEY_R
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP
import org.lwjgl.glfw.GLFW.GLFW_KEY_W

class ConwayHex2020Day24 : GameLogic {
    // Gfx helpers
    private val renderer = Renderer()
    private val hud = Hud()

    // Input from the original puzzle!
    private val data = resourceLines(2020, 24)

//    // Torus
//    private val surface = TorusMappedWrappingHexGrid(
//        hexGrid = WrappingHexGrid(m = 160, n = 40, layout = Layout(POINTY)),
//        r1 = 1.5,
//        r2 = 8.0
//    )

//     // trefoil:
    private val surface = PathMappedWrappingHexGrid(
        hexGrid = WrappingHexGrid(600, 26, Layout(POINTY)),
        pathCreator = TrefoilPathCreator(scale = 3.0, segments = 1200),
        r = 0.6
    )

//    // TorusKnot
//    private val surface = PathMappedWrappingHexGrid(
//        hexGrid = WrappingHexGrid(900, 16, Layout(POINTY)),
//        pathCreator = TorusKnotPathCreator(p = 3, q = 7, scale = 5.0, segments = 1800),
//        r = 0.25
//    )

//    // Dense TorusKnot
//    private val surface = PathMappedWrappingHexGrid(
//        hexGrid = WrappingHexGrid(1300, 12, Layout(POINTY)),
//        pathCreator = TorusKnotPathCreator(p = 11, q = 17, b = 0.2, scale = 5.0, segments = 2600),
//        r = 0.20
//    )

    // Decorated Torus Knot
//    private val surface = PathMappedWrappingHexGrid(
//        hexGrid = WrappingHexGrid(900, 16, Layout(POINTY)),
//        // Valid patterns: 4b, 7a, 7b, 10b, 11c
//        pathCreator = DecoratedTorusKnotPathCreator(pattern = "11c", scale = 5.0, segments = 1800),
//        r = 0.25
//    )


    // Game state
    private var conwayStepDelay = 1
    private var currentStepDelay = 0
    private var conwayIteration = 0
    private var isPaused = true
    private val gameItems = mutableListOf<GameItem>()
    private val hexToGameItem = mutableMapOf<Hex, GameItem>()
    private val alive = mutableSetOf<Hex>()
    private val initialPoints = 600 // we have up to 597 initial points from the original game
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
    lateinit var aliveTexture: Texture

    // make it read only by typing it as constant - ensures it's not changed
    private val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    private val initialWorldCentre = Vector3f(0f, 0f, 0f)
    // private val initialWorldCentre = Vector3f(5.357e-3f, -2.461e-1f, -2.243e-1f)
    // private val initialWorldCentre = Vector3f(0.08395f, 1.296f, -1.223f)
    private val worldCentre = Vector3f(initialWorldCentre)

    // private val initialCameraPosition = Vector3f(3.463E-2f,  1.245E+0f, -1.464E+0f)
    private val initialCameraPosition = Vector3f(0f,  0f, 16f)
    private val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize()//.conjugate()
    private val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    var distanceToWorldCentre = initialCameraPosition.sub(worldCentre, Vector3f()).length()

    // calculation values so they aren't created every loop
    val cameraX = Vector3f()
    val cameraY = Vector3f()
    val cameraZ = Vector3f()
    val newCameraVector = Vector3f()
    val cameraDelta = Vector3f()

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
        aliveTexture = Texture("visualisations/textures/new-white-pointy.png")

        surface.hexGrid.hexes().forEachIndexed { _, hex ->
            val newMesh = loadMesh(surface.hexToObj(hex))
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
        }

        hud.init(window)
        renderer.init(window)
        keyPressedTimer.init()
    }

    private fun hexToColour(hex: Hex): Vector3f {
        val hexAxis = surface.hexAxis(hex)
        return Vector3f(
            hexAxis.axes.m00 * 0.65f + 0.1f,
            hexAxis.axes.m11 * 0.65f + 0.1f,
            hexAxis.axes.m22 * 0.65f + 0.1f
        )
    }

    override fun input(window: Window, mouseInput: MouseInput) {
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
        }
    }

    private fun moveUp() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.add(cameraY, newCameraVector)
        worldCentre.add(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveDown() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.sub(cameraY, newCameraVector)
        worldCentre.sub(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveRight() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.add(cameraX, newCameraVector)
        worldCentre.add(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveLeft() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.sub(cameraX, newCameraVector)
        worldCentre.sub(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveBackward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.add(cameraZ, newCameraVector)
        worldCentre.add(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveForward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP * if(turboMove) 5f else 1f)
        camera.position.sub(cameraZ, newCameraVector)
        worldCentre.sub(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun resetCamera() = with(camera) {
        rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z, initialCameraRotation.w)
        position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
        worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
        distanceToWorldCentre = initialCameraPosition.length()
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
        println("""
            camera: $camera
            world centre: $worldCentre
        """.trimIndent())
    }

    private fun changeState(command: KeyCommand) {
        if (command is SingleKeyPressCommand) {
            // Stop key repeats when this is meant to be a 1 shot press
            val pressedAt = keyPressedTimer.time
            if ((pressedAt - lastPressedAt) < KEYBOARD_BOUNCE_DELAY) {
                return
            }
            lastPressedAt = pressedAt
            flashMessage = command.toString()
            flashPercentage = 100
        }

        when (command) {
            DecreaseSpeed -> conwayStepDelay++
            IncreaseSpeed -> conwayStepDelay = max(conwayStepDelay - 1, 1)
            ResetGame -> resetGame()
            TogglePause -> isPaused = !isPaused
            PrintState -> printGameState()
            SingleStep -> if (isPaused) performStep()
            ResetCamera -> resetCamera()
            MoveForward -> moveForward()
            MoveBackward -> moveBackward()
            MoveLeft -> moveLeft()
            MoveRight -> moveRight()
            MoveDown -> moveDown()
            MoveUp -> moveUp()
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        if (!isPaused) {
            currentStepDelay += 1
            if (currentStepDelay % conwayStepDelay == 0) {
                currentStepDelay = 0
                performStep()
            }
        }
        flashPercentage = max(flashPercentage - 7, 0)
        if (flashPercentage == 0) flashMessage = ""

        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) && abs(mouseInput.displVec.length()) > 0.001f -> {
                // free camera move in its XY plane
                val moveVec: Vector2f = mouseInput.displVec
                camera.rotation.positiveY(cameraY).mul(moveVec.x * MOUSE_SENSITIVITY)
                camera.rotation.positiveX(cameraX).mul(moveVec.y * MOUSE_SENSITIVITY)
                cameraY.sub(cameraX, cameraDelta)
                worldCentre.add(cameraDelta)
                camera.setPosition(camera.position.x + cameraDelta.x, camera.position.y + cameraDelta.y, camera.position.z + cameraDelta.z)
            }

            mouseInput.isMiddleButtonPressed && abs(mouseInput.displVec.length()) > 0.001f -> {
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
                // move camera a percentage closer/further from world centre.
                val percentageChange = if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) 0.10f else 0.02f

                val newDistanceToWorldCentre = distanceToWorldCentre * if (mouseInput.scrollDirection < 0) (1f + percentageChange) else (1f - percentageChange)
                if (newDistanceToWorldCentre > 0.04f) {
                    distanceToWorldCentre = newDistanceToWorldCentre
                }

                camera.rotation.positiveZ(cameraZ)
                worldCentre.add(cameraZ.mul(distanceToWorldCentre), newCameraVector)
                camera.position.set(newCameraVector)

                // stop the scroll!
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

        newOn.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = aliveTexture
            // hexToGameItem[hex]!!.mesh.colour = Vector3f(1f, 1f, 1f)
        }

        newOff.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = null
            hexToGameItem[hex]!!.mesh.colour = hexToColour(hex)
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
        renderer.render(window, camera, gameItems)
        val hudData = HudData(
            speed = conwayStepDelay,
            iteration = conwayIteration,
            isPaused = isPaused,
            liveCount = alive.count(),
            flashMessage = flashMessage,
            flashPercentage = flashPercentage,
            createdCount = createdCount,
            destroyedCount = destroyedCount
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
        const val KEYBOARD_BOUNCE_DELAY = 0.2

        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            engine.run()
        }
    }
}
