package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.MouseInput
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Camera
import advents.conwayhex.engine.graph.OBJLoader.loadMesh
import advents.conwayhex.engine.graph.Renderer
import advents.conwayhex.engine.item.GameItem
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Math.abs
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3fc
import org.lwjgl.glfw.GLFW.GLFW_KEY_0
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_I
import org.lwjgl.glfw.GLFW.GLFW_KEY_K
import org.lwjgl.glfw.GLFW.GLFW_KEY_L
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_O
import org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON
import org.lwjgl.glfw.GLFW.GLFW_KEY_W
import org.lwjgl.glfw.GLFW.GLFW_KEY_X
import org.lwjgl.glfw.GLFW.GLFW_KEY_Z


class ConwayHex2020Day24 : GameLogic {
    private val data = resourceLines(2020, 24)
    val torusMinorRadius = 0.1
    val torusMajorRadius = 0.4
    val gridWidth = 118
    val gridHeight = 38
    val gameItemScale = 0.0085f
    val gridLayout = Layout(POINTY)
    val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    // make it read only by typing it as constant - ensures it's not changed
    val globalY: Vector3fc = Vector3f(0f, 1f, 0f)

    private val renderer = Renderer()
    private val initialWorldCentre = Vector3f(0f, 0f, 0f)
    private val worldCentre = Vector3f(initialWorldCentre)

    private val initialCameraPosition = Vector3f(0.25f, 0.25f, 1.25f)
    private val initialCameraRotation = Quaternionf().lookAlong(worldCentre.sub(initialCameraPosition, Vector3f()), Vector3f(0f, 1f, 0f)).normalize().conjugate()

    private val camera = Camera(Vector3f(initialCameraPosition), Quaternionf(initialCameraRotation))

    private var cameraInc = Vector3f()
    private var cameraRot = Vector3f()
    private val gameItems = mutableListOf<GameItem>()

    fun readInitialPosition(): List<Hex> {
        val doubledCoords = Day24.walk(data)
        val hexes = doubledCoords.map { (col, row) ->
            val qc = col + row / 2 + row % 2
            val rc = -row
            val sc = 0 - qc - rc
            Hex(qc, rc, sc, hexGrid)
        }
        println("hexes: $hexes")
        return hexes
    }

    override fun init(window: Window) {
        val triangleMesh = loadMesh("/conwayhex/models/hexagon-debug.obj")
        val triangleItem = GameItem(triangleMesh)
        triangleItem.setPosition(Vector3f(worldCentre))
        triangleItem.scale = 0.02f
        gameItems.add(triangleItem)

        val hexagonMesh = loadMesh("/conwayhex/models/simple-hexagon.obj")

        // texture loading isn't via resources, so is relative to project root dir
        // mesh.texture = Texture("visualisations/textures/grassblock.png")

        val hexAxes = hexGrid.hexAxes()
        val hexes = hexGrid.hexes().mapIndexed { index, hex -> Pair(index, hex) }
        hexes.forEach { (index, hex) ->
            val hexAxis = hexAxes[index]
            val location = hexAxis.location
            val axes = hexAxis.axes

            // take the axes RGB values from the normal projections onto x/y/z axes
            hexagonMesh.colour = Vector3f(
                axes.getColumn(0, Vector3f()).x,
                axes.getColumn(1, Vector3f()).y,
                axes.getColumn(2, Vector3f()).z
            )

            val gameItem = GameItem(hexagonMesh)
            gameItem.setPosition(location)
            gameItem.scale = gameItemScale // TODO: calculate this according to the torus size

            val q = Quaternionf().setFromNormalized(axes)
            gameItem.setRotation(q)

            gameItems += gameItem
        }

        renderer.init(window)
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        cameraInc.set(0.0, 0.0, 0.0)
        cameraRot.set(0.0, 0.0, 0.0)
        when {
            window.isKeyPressed(GLFW_KEY_W) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(forwardVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_S) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.add(forwardVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_A) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(leftVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_D) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.add(leftVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_Z) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(upVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_X) -> {
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.add(upVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_I) -> cameraRot.x = -1f
            window.isKeyPressed(GLFW_KEY_O) -> cameraRot.x = 1f
            window.isKeyPressed(GLFW_KEY_K) -> cameraRot.y = -1f
            window.isKeyPressed(GLFW_KEY_L) -> cameraRot.y = 1f
            window.isKeyPressed(GLFW_KEY_COMMA) -> cameraRot.z = -1f
            window.isKeyPressed(GLFW_KEY_PERIOD) -> cameraRot.z = 1f
            window.isKeyPressed(GLFW_KEY_SEMICOLON) -> println("camera: $camera")
            window.isKeyPressed(GLFW_KEY_0) -> with(camera) {
                rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z, initialCameraRotation.w)
                position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
                worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
            }
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {

        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) -> {
                // free camera move in its XY plane
                val moveVec: Vector2f = mouseInput.displVec
                // val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val unitVectorsForCameraOrientation = camera.rotation.get(Matrix3f())
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(CAMERA_POS_STEP * moveVec.x * MOUSE_SENSITIVITY)
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP * moveVec.y * MOUSE_SENSITIVITY)
                val newCameraPosition = camera.position.add(upVector, Vector3f()).sub(leftVector)
                val delta = newCameraPosition.sub(camera.position, Vector3f())
                if (abs(delta.length()) > 0.001f) {
                    worldCentre.add(delta)
                    gameItems[0].position.set(worldCentre)
                    camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
                }
            }

            mouseInput.isMiddleButtonPressed && abs(mouseInput.displVec.length()) > 0.001f -> {
                val moveVec: Vector2f = mouseInput.displVec
                val rotAngles = Vector3f(-CAMERA_POS_STEP * moveVec.y, -CAMERA_POS_STEP * moveVec.x, 0f)

                // do left/right first so we don't affect the up vector
                val inverseCameraRotation = camera.rotation.conjugate(Quaternionf())

                val cameraY = inverseCameraRotation.positiveY(Vector3f())

                // calculate the new camera direction (relative to world centre) after rotation by global Y first
                val newCameraVector = camera.position.sub(worldCentre, Vector3f())
                if (rotAngles.x != 0f) {
                    newCameraVector.rotateAxis(rotAngles.x, globalY.x(), globalY.y(), globalY.z())
                }

                // calculate camera's new rotation vector before we rotate about its local X for any up/down movement
                val newX = cameraY.cross(newCameraVector, Vector3f()).normalize()

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

        }
    }

    override fun render(window: Window) {
        renderer.render(window, camera, gameItems)
    }

    override fun cleanup() {
        renderer.cleanup()
        gameItems.forEach { it.mesh.cleanUp() }
    }

    companion object {
        const val CAMERA_POS_STEP = 0.02f
        const val MOUSE_SENSITIVITY = 0.2f

        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            engine.run()
        }
    }
}