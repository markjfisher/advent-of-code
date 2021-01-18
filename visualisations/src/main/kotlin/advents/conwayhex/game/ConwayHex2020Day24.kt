package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.MouseInput
import advents.conwayhex.engine.Timer
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Camera
import advents.conwayhex.engine.graph.OBJLoader.loadMesh
import advents.conwayhex.engine.graph.Renderer
import advents.conwayhex.engine.graph.Texture
import advents.conwayhex.engine.item.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
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

    // Hex grid config
    private val torusMinorRadius = 0.25
    private val torusMajorRadius = 0.8
    private val gridWidth = 120
    private val gridHeight = 40
    private val gridLayout = Layout(POINTY)
    private val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    // Game state
    private var conwayStepDelay = 15
    private var currentStepDelay = 0
    private var conwayIteration = 0
    private var isPaused = true
    private val gameItems = mutableListOf<GameItem>()
    private val hexToGameItem = mutableMapOf<Hex, GameItem>()
    private val alive = mutableSetOf<Hex>()
    private val initialPoints = 600 // we have up to 597 initial points from the original game

    // keyboard timer handling
    private val keyPressedTimer = Timer()
    private var lastPressedAt = 0.0

    // text flashing
    private var flashPercentage: Int = 0
    private var flashMessage: String = ""

    // Textures
    lateinit var emptyTexture: Texture
    lateinit var aliveTexture: Texture

    // make it read only by typing it as constant - ensures it's not changed
    private val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    // private val initialWorldCentre = Vector3f(0f, 0f, 0f)
    private val initialWorldCentre = Vector3f(5.357e-3f, -2.461e-1f, -2.243e-1f)
    // private val initialWorldCentre = Vector3f(0.08395f, 1.296f, -1.223f)
    private val worldCentre = Vector3f(initialWorldCentre)

    private val initialCameraPosition = Vector3f(3.463E-2f,  1.245E+0f, -1.464E+0f)
    private val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize().conjugate()
    private val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    var distanceToWorldCentre = initialCameraPosition.sub(worldCentre, Vector3f()).length()

    private fun readInitialPosition() {
        val doubledCoords = Day24.walk(data).take(initialPoints)
        val hexes = doubledCoords.map { (col, row) ->
            val qc = col + row / 2 + row % 2
            val rc = -row
            val sc = 0 - qc - rc
            Hex(qc, rc, sc, hexGrid)
        }
        alive.addAll(hexes.map { hexGrid.constrain(it) })
    }

    override fun init(window: Window) {
        readInitialPosition()

        aliveTexture = Texture("visualisations/textures/stone3-b.png")
        emptyTexture = Texture("visualisations/textures/stone3-w.png")

        hexGrid.hexes().forEachIndexed { index, hex ->
            val isAlive = alive.contains(hex)

            val newMesh = loadMesh(hexGrid.hexObj2(hex))
            newMesh.texture = if (isAlive) aliveTexture else emptyTexture

            val gameItem = GameItem(newMesh)
            // items are relative to world coords already in both position, scale and have axes same as world coords
            // Another way to do this is calculate N meshes for the minor circle, as they will be repeated around the major circle, but with different location and rotations
            // which would make N mesh instead of NxM mesh
            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f
            val q = Quaternionf().setFromNormalized(Matrix3f())
            gameItem.setRotation(q)

            gameItems += gameItem
            hexToGameItem[hex] = gameItem
        }

        hud.init(window)
        renderer.init(window)
        keyPressedTimer.init()
    }

    override fun input(window: Window, mouseInput: MouseInput) {
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
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val upVector = inverseCameraRotation.positiveY(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.add(upVector, Vector3f())
        worldCentre.add(upVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun moveDown() {
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val upVector = inverseCameraRotation.positiveY(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.sub(upVector, Vector3f())
        worldCentre.sub(upVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun moveRight() {
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val leftVector = inverseCameraRotation.positiveX(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.add(leftVector, Vector3f())
        worldCentre.add(leftVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun moveLeft() {
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val leftVector = inverseCameraRotation.positiveX(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.sub(leftVector, Vector3f())
        worldCentre.sub(leftVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun moveBackward() {
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val forwardVector = inverseCameraRotation.positiveZ(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.add(forwardVector, Vector3f())
        worldCentre.add(forwardVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun moveForward() {
        val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
        val forwardVector = inverseCameraRotation.positiveZ(Vector3f()).mul(CAMERA_POS_STEP)
        val newCameraPosition = camera.position.sub(forwardVector, Vector3f())
        worldCentre.sub(forwardVector)
        camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
    }

    private fun resetCamera() = with(camera) {
        rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z, initialCameraRotation.w)
        position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
        worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
        distanceToWorldCentre = initialCameraPosition.length()
    }

    private fun resetGame() {
        alive.clear()
        readInitialPosition()
        conwayIteration = 0
        hexGrid.hexes().forEach { hex -> hexToGameItem[hex]!!.mesh.texture = emptyTexture }
        alive.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = aliveTexture
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
        flashPercentage = max(flashPercentage - 5, 0)
        if (flashPercentage == 0) flashMessage = ""

        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) -> {
                // free camera move in its XY plane
                val moveVec: Vector2f = mouseInput.displVec
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(moveVec.x * MOUSE_SENSITIVITY)
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(moveVec.y * MOUSE_SENSITIVITY)
                val newCameraPosition = camera.position.add(upVector, Vector3f()).sub(leftVector)
                val delta = newCameraPosition.sub(camera.position, Vector3f())
                if (abs(delta.length()) > 0.001f) {
                    worldCentre.add(delta)
                    camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
                }
            }

            mouseInput.isMiddleButtonPressed && abs(mouseInput.displVec.length()) > 0.001f -> {
                val moveVec: Vector2f = mouseInput.displVec
                val rotAngles = Vector3f(-MOUSE_SENSITIVITY * moveVec.y, -MOUSE_SENSITIVITY * moveVec.x, 0f)

                // do left/right first so we don't affect the up vector
                val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())

                val cameraX = inverseCameraRotation.positiveX(Vector3f())

                // calculate the new camera direction (relative to world centre) after rotation by global Y first
                val newCameraVector = camera.position.sub(worldCentre, Vector3f())
                if (rotAngles.x != 0f) {
                    newCameraVector.rotateAxis(rotAngles.x, globalY.x(), globalY.y(), globalY.z())
                }

                // calculate camera's new rotation vector before we rotate about its local X for any up/down movement
                // the X vector effectively rotates about the Gy vector by same angle. Paper and pen! and this introduces no roll
                val newX = cameraX.rotateAxis(rotAngles.x, globalY.x(), globalY.y(), globalY.z())

                // now rotate about any up/down
                if (rotAngles.y != 0f) {
                    newCameraVector.rotateAxis(rotAngles.y, newX.x, newX.y, newX.z)
                }
                // calculate the new up vector from the direction vector and the unchanged (for this part of the rotation) X vector
                val newZ = newCameraVector.normalize(Vector3f())
                val newY = newZ.cross(newX, Vector3f())

                val newRotationMatrix = Matrix3f().setColumn(0, newX).setColumn(1, newY).setColumn(2, newZ)
                val newRotation = Quaternionf().setFromNormalized(newRotationMatrix)

                newCameraVector.add(worldCentre)
                camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
                camera.setRotation(newRotation.w, newRotation.x, newRotation.y, newRotation.z)
            }

            mouseInput.scrollDirection != 0 -> {
                // move camera a percentage closer/further from world centre.
                val newDistanceToWorldCentre = distanceToWorldCentre * if (mouseInput.scrollDirection < 0) 1.05f else 0.94f
                if (newDistanceToWorldCentre > 0.05f) {
                    distanceToWorldCentre = newDistanceToWorldCentre
                }

                val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())
                val forwardVector = inverseCameraRotation.positiveZ(Vector3f())
                val newCameraPosition = worldCentre.add(forwardVector.mul(distanceToWorldCentre), Vector3f())
                camera.position.set(newCameraPosition)

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
        alive.clear()
        alive.addAll(newAlive)

        newOn.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = aliveTexture
        }

        newOff.forEach { hex ->
            hexToGameItem[hex]!!.mesh.texture = emptyTexture
        }
    }

    private fun runConway(): Set<Hex> {
        conwayIteration++
        if (alive.size == 0) return emptySet()
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
            flashPercentage = flashPercentage
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

sealed class KeyCommand

sealed class SingleKeyPressCommand: KeyCommand() {
    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

object DecreaseSpeed: SingleKeyPressCommand()
object IncreaseSpeed: SingleKeyPressCommand()
object TogglePause: SingleKeyPressCommand()
object ResetGame: SingleKeyPressCommand()
object SingleStep: SingleKeyPressCommand()
object PrintState: SingleKeyPressCommand()
object ResetCamera: SingleKeyPressCommand()

sealed class MovementCommand: KeyCommand()

object MoveForward: MovementCommand()
object MoveBackward: MovementCommand()
object MoveLeft: MovementCommand()
object MoveRight: MovementCommand()
object MoveDown: MovementCommand()
object MoveUp: MovementCommand()