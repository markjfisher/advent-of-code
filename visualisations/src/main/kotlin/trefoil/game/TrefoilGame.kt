package trefoil.game

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
import commands.SingleKeyPressCommand
import commands.SingleStep
import commands.TogglePause
import engine.GameEngine
import engine.GameLogic
import engine.MouseInput
import engine.Timer
import engine.Window
import engine.graph.Camera
import engine.graph.OBJLoader
import engine.graph.OBJLoader.loadMeshFromFile
import engine.graph.Renderer
import engine.graph.Texture
import engine.item.GameItem
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.hex.projection.TorusKnotMappedWrappingHexGrid
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
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP
import org.lwjgl.glfw.GLFW.GLFW_KEY_W

class TrefoilGame : GameLogic {
    // Gfx helpers
    private val renderer = Renderer()
    private val hud = Hud()

    // Grid and space
    private val gridLayout = Layout(Orientation.ORIENTATION.POINTY)
    private val hexGrid = WrappingHexGrid(800, 16, gridLayout)
    private val knot = TorusKnotMappedWrappingHexGrid(hexGrid = hexGrid, p = 3, q = 7, r = 0.25, scale = 5.0)

    // Game state
    private var isPaused = true
    private val gameItems = mutableListOf<GameItem>()

    // keyboard timer handling
    private val keyPressedTimer = Timer()
    private var lastPressedAt = 0.0

    // text flashing
    private var flashPercentage: Int = 0
    private var flashMessage: String = ""

    // Textures
    lateinit var emptyTexture: Texture

    // make it read only by typing it as constant - ensures it's not changed
    private val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    // Camera and initial world positions and rotations
    private val initialWorldCentre = Vector3f(0f, 0f, 0f)
    // private val initialWorldCentre = Vector3f(9.434E-1f, -2.849E-1f,  5.293E-1f)
    private val worldCentre = Vector3f(initialWorldCentre)

    private val initialCameraPosition = Vector3f(0f, 0f, 7f)
    // private val initialCameraPosition = Vector3f(1.884E+0f, -2.336E-1f,  4.033E-1f)
    private val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), globalY).normalize()
    private val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    var distanceToWorldCentre = initialCameraPosition.sub(worldCentre, Vector3f()).length()

    // calculation values so they aren't created every loop
    private val cameraX = Vector3f()
    private val cameraY = Vector3f()
    private val cameraZ = Vector3f()
    private val newCameraVector = Vector3f()
    private val cameraDelta = Vector3f()

    override fun init(window: Window) {
        emptyTexture = Texture("visualisations/textures/stone3-wl-pointy.png")
        val triangleMesh = loadMeshFromFile("/conwayhex/models/simple-arrow.obj")

        // val knotData = Knots.torusKnot(3, 2, 1.8, 0.6, 1.0, 360 * 3)
//        val knotData = Knots.wikiTrefoilCoordinates(360 * 5)
//        knotData.forEach { data ->
//            val curvePoint = GameItem(triangleMesh)
//            curvePoint.setPosition(data.point)
//            val binormal = data.normal.cross(data.tangent, Vector3f())
//            val q = Quaternionf().setFromNormalized(Matrix3f(binormal, data.normal, data.tangent))
//            curvePoint.setRotation(q)
//
//            curvePoint.colour = Vector3f(abs(binormal.x) + 0.1f, abs(data.normal.y) + 0.1f, abs(data.tangent.z) + 0.1f)
//            curvePoint.scale = 0.05f
//            gameItems.add(curvePoint)
//    }

        hexGrid.hexes().forEachIndexed { index, hex ->
            val newMesh = OBJLoader.loadMesh(knot.hexToObj(hex))
            newMesh.texture = emptyTexture
            val gameItem = GameItem(newMesh)
            gameItem.setPosition(Vector3f(0f, 0f, 0f))
            gameItem.scale = 1f

            gameItems += gameItem
        }

        val triangleItem = GameItem(triangleMesh)
        triangleItem.setPosition(Vector3f(worldCentre))
        triangleItem.scale = 0.02f
        gameItems.add(triangleItem)


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
            window.isKeyPressed(GLFW_KEY_P) -> changeState(TogglePause)
            window.isKeyPressed(GLFW_KEY_1) -> changeState(SingleStep)
            window.isKeyPressed(GLFW_KEY_0) -> changeState(ResetCamera)
        }
    }

    private fun moveUp() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP)
        camera.position.add(cameraY, newCameraVector)
        worldCentre.add(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveDown() {
        camera.rotation.positiveY(cameraY).mul(CAMERA_POS_STEP)
        camera.position.sub(cameraY, newCameraVector)
        worldCentre.sub(cameraY)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveRight() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP)
        camera.position.add(cameraX, newCameraVector)
        worldCentre.add(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveLeft() {
        camera.rotation.positiveX(cameraX).mul(CAMERA_POS_STEP)
        camera.position.sub(cameraX, newCameraVector)
        worldCentre.sub(cameraX)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveBackward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP)
        camera.position.add(cameraZ, newCameraVector)
        worldCentre.add(cameraZ)
        camera.setPosition(newCameraVector.x, newCameraVector.y, newCameraVector.z)
    }

    private fun moveForward() {
        camera.rotation.positiveZ(cameraZ).mul(CAMERA_POS_STEP)
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
            TogglePause -> isPaused = !isPaused
            PrintState -> printGameState()
            ResetCamera -> resetCamera()
            MoveForward -> moveForward()
            MoveBackward -> moveBackward()
            MoveLeft -> moveLeft()
            MoveRight -> moveRight()
            MoveDown -> moveDown()
            MoveUp -> moveUp()
            else -> throw Exception("Unknown command: $command")
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
        flashPercentage = max(flashPercentage - 5, 0)
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
                val newDistanceToWorldCentre = distanceToWorldCentre * if (mouseInput.scrollDirection < 0) 1.05f else 0.94f
                if (newDistanceToWorldCentre > 0.05f) {
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


    override fun render(window: Window) {
        renderer.render(window, camera, gameItems)
        val hudData = HudData(
            isPaused = isPaused,
            flashMessage = flashMessage,
            flashPercentage = flashPercentage,
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
            val logic = TrefoilGame()
            val engine = GameEngine("Trefoil Render", 1200, 800, true, logic)
            engine.run()
        }
    }
}
