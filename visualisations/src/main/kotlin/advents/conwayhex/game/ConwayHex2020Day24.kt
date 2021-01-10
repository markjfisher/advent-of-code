package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.item.GameItem
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.MouseInput
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Camera
import advents.conwayhex.engine.graph.OBJLoader.loadMesh
import advents.conwayhex.engine.graph.Renderer
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Math.abs
import org.joml.Math.toRadians
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_0
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_I
import org.lwjgl.glfw.GLFW.GLFW_KEY_J
import org.lwjgl.glfw.GLFW.GLFW_KEY_K
import org.lwjgl.glfw.GLFW.GLFW_KEY_L
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_O
import org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON
import org.lwjgl.glfw.GLFW.GLFW_KEY_U
import org.lwjgl.glfw.GLFW.GLFW_KEY_W
import org.lwjgl.glfw.GLFW.GLFW_KEY_X
import org.lwjgl.glfw.GLFW.GLFW_KEY_Z
import kotlin.math.PI
import kotlin.math.acos

class ConwayHex2020Day24 : GameLogic {
    private val data = resourceLines(2020, 24)
    val torusMinorRadius = 0.1
    val torusMajorRadius = 0.4
    val gridWidth = 118
    val gridHeight = 38
    val gameItemScale = 0.0085f
    val gridLayout = Layout(POINTY)
    val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    private val renderer = Renderer()
//    private val initialCameraPosition = Vector3f(0f, 0f, 0f)
//    private val initialCameraRotation = Vector3f(0f, 0f, 0f)

//    private val initialCameraPosition = Vector3f(0f, 0.38268f, 0.92388f)
//    private val initialCameraRotation = Vector3f(22.5f, 0f, 0f)

    private val initialCameraPosition = Vector3f(-0.3546f, -0.2919f, -0.01403f)
    private val initialCameraRotation = Vector3f(-96.26f, -2.292f, 0f)

    private val initialWorldCentre = Vector3f(0f, 0f, 0f)

    private val camera = Camera(Vector3f(initialCameraPosition), Vector3f(initialCameraRotation))
    // private val camera = Camera(Vector3f(0f, 0f, 0.2f), Vector3f(-74f, 0f, -90f))
    private val worldCentre = Vector3f(initialWorldCentre)

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
        val triangleMesh = loadMesh("/conwayhex/models/triangle.obj")
        val triangleItem = GameItem(triangleMesh)
        triangleItem.setPosition(Vector3f(worldCentre))
        triangleItem.scale = 0.02f
        gameItems.add(triangleItem)

        // mesh loading is via resources
        val mesh = loadMesh("/conwayhex/models/hexagon-debug.obj")

        // texture loading isn't via resources, so is relative to project root dir
        // mesh.texture = Texture("visualisations/textures/grassblock.png")

//        val testItem = GameItem(mesh)
//        testItem.position = Vector3f(0f, 0f, 0f)
//        testItem.scale = 0.5f
//        gameItems += testItem


        val hexAxes = hexGrid.hexAxes()
        val hexes = hexGrid.hexes().mapIndexed { index, hex -> Pair(index, hex) }
        // hexes.windowed(2, 2).map { it.first() }.take(12).forEach { (index, hex) ->
        hexes.forEach { (index, hex) ->
            val hexAxis = hexAxes[index]
            val location = hexAxis.location
            val axes = hexAxis.axes
            // axis is a Matrix3f with my 3 normals at the centre of the hexagon, e.g
            // cX  cY  cZ
            //  0  -1   0
            //  0   0  -1
            //  1   0   0

            mesh.colour = Vector3f(0.5f, 0.5f, 0.5f)
            val gameItem = GameItem(mesh)
            gameItem.setPosition(location)
            gameItem.scale = gameItemScale // TODO: calculate this according to the torus size

            val q = Quaternionf().setFromNormalized(axes)
            gameItem.setRotation(q)

            gameItems += gameItem

            // mesh.colour = Vector3f(0.1f, 0.1f, 0.8f)
//            val unit = GameItem(mesh)
//            unit.position = location
//            unit.scale = 0.02f
//            gameItems += unit
        }

        renderer.init(window)
    }

    fun vectorToRadians(v: Vector3f): Vector3f {
        return v.mul((PI / 180.0).toFloat(), Vector3f())
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        cameraInc.set(0.0, 0.0, 0.0)
        cameraRot.set(0.0, 0.0, 0.0)
        when {
            window.isKeyPressed(GLFW_KEY_W) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(forwardVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_S) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.add(forwardVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_A) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(leftVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_D) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.add(leftVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_Z) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(CAMERA_POS_STEP)
                val newCameraPosition = camera.position.sub(upVector, Vector3f())
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
            window.isKeyPressed(GLFW_KEY_X) -> {
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
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
                rotation.set(initialCameraRotation.x, initialCameraRotation.y, initialCameraRotation.z)
                position.set(initialCameraPosition.x, initialCameraPosition.y, initialCameraPosition.z)
                worldCentre.set(initialWorldCentre.x, initialWorldCentre.y, initialWorldCentre.z)
            }
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {

        // Update camera based on mouse..?
        when {
            mouseInput.isMiddleButtonPressed && (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) -> {
                // free camera move in its XY plane
                val oldCamera = Vector3f(camera.position)
                val oldWorld = Vector3f(worldCentre)
                val moveVec: Vector2f = mouseInput.displVec
                // camera.moveRotation(moveVec.x * MOUSE_SENSITIVITY, moveVec.y * MOUSE_SENSITIVITY, 0f)
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                // val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f()).mul(CAMERA_POS_STEP * moveVec.x)
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f()).mul(CAMERA_POS_STEP * moveVec.x * MOUSE_SENSITIVITY)
                val leftVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f()).mul(CAMERA_POS_STEP * moveVec.y * MOUSE_SENSITIVITY)
                val newCameraPosition = camera.position.add(upVector, Vector3f()).sub(leftVector)
                val delta = newCameraPosition.sub(camera.position, Vector3f())
                worldCentre.add(delta)
                gameItems[0].position.set(worldCentre)
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
                if (abs(delta.length()) > 0.001f) {
                    println("-------------------------\ndelta: $delta\noldCamera: $oldCamera\nnew: $newCameraPosition\nworld old: $oldWorld\nworld new: $worldCentre")
                }
            }
            mouseInput.isMiddleButtonPressed && abs(mouseInput.displVec.length()) > 0.001f -> {
                // any up/down is rotation around X axis of camera
                // any left/right is rotation around Y axis of camera
                // We are pointing at the world centre, so rotate the camera about that point.
                // translate camera copy to world centre, rotate, translate back out
                println("----------------------------")
                val translation = camera.position.sub(worldCentre, Vector3f())
                val moveVec: Vector2f = mouseInput.displVec
                val rotAngles = Vector3f(CAMERA_POS_STEP * moveVec.x, CAMERA_POS_STEP * moveVec.y, 0f)
                println("mouse: $moveVec, rotAngles: $rotAngles")
                val rotationMatrix = Matrix3f().rotateZYX(rotAngles)
                val appliedRotation = translation.mul(rotationMatrix, Vector3f())
                val newCameraPosition = appliedRotation.add(worldCentre)
                println("old camera: $camera")
                println("world: $worldCentre")
                println("rotation Matrix:\n$rotationMatrix")
                println("applied: $appliedRotation")
                println("new camera: $newCameraPosition")
                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
                // and set the camera rotation to point to the world centre again
                val vectorToWorldCentre = worldCentre.sub(newCameraPosition, Vector3f()).normalize()
                println("vector to world centre: $vectorToWorldCentre")
                val acosV3 = Vector3f(acos(vectorToWorldCentre.x) * 57.29578f, acos(vectorToWorldCentre.y) * 57.29578f, acos(vectorToWorldCentre.z) * 57.29578f)
                println("arc cosines: $acosV3")
                println("m1 (90-x) = ${90f - acosV3.y}, ${90f - acosV3.x}, ${180f - acosV3.z}")
                println("m2 moves to = ${camera.rotation.x - rotAngles.x * 57.29578f}, ${camera.rotation.y - rotAngles.y * 57.29578f}, ${camera.rotation.z}")
                println("m2 moves by = ${-rotAngles.x * 57.29578f}, ${-rotAngles.y * 57.29578f}, ${camera.rotation.z}")
                // camera.setRotation(90f - acosV3.y, 90f - acosV3.x, 180f - acosV3.z)
                // camera.setRotation(90f - acosV3.y, 90f - acosV3.x, 0f)
                camera.moveRotation(-rotAngles.x * 57.29578f, -rotAngles.y * 57.29578f, 0f)
            }
            window.isKeyPressed(GLFW_KEY_U) -> {
                // pretending this is camera rotation about the x axis, positive rotation, so moving "up" in camera reference
                val distanceFromWorldCentre = worldCentre.sub(camera.position, Vector3f()).length()
                // rotate slightly in the up direction (positive about X)
                val v = Matrix4f().translation(0f, 0f, -distanceFromWorldCentre)
                    .rotateX(toRadians(5f))
                    .translate(-worldCentre.x, -worldCentre.y, -worldCentre.z)

            }
            window.isKeyPressed(GLFW_KEY_J) -> {
                // pretending this is camera rotation about the x axis, positive rotation, so moving "up" in camera reference
                val unitVectorsForCameraOrientation = Matrix3f().rotateZYX(vectorToRadians(camera.rotation).mul(-1f))
                val rightVector = unitVectorsForCameraOrientation.getColumn(0, Vector3f())
                val upVector = unitVectorsForCameraOrientation.getColumn(1, Vector3f())
                val forwardVector = unitVectorsForCameraOrientation.getColumn(2, Vector3f())

                val distanceFromWorldCentre = worldCentre.sub(camera.position, Vector3f()).length()
                // rotate slightly in the up direction (positive about X)
                val rightRotation = unitVectorsForCameraOrientation.rotateY(toRadians(-5f), Matrix3f())
                val newLocation = rightRotation.getColumn(2, Vector3f()).mul(distanceFromWorldCentre)

                println("================\nU:\nunits:\n$unitVectorsForCameraOrientation\nupRot:\n$rightRotation\nold loc: ${camera.position}\nnew loc: $newLocation")
                val newCameraRotation = rightRotation.getEulerAnglesZYX(Vector3f()).mul(180f / PI.toFloat())
                println("old rot: ${camera.rotation}\nnew rot: $newCameraRotation")
                camera.setPosition(newLocation.x, newLocation.y, newLocation.z)
                camera.setRotation(newCameraRotation.x, newCameraRotation.y, newCameraRotation.z)
//                val newCameraPosition = camera.position.sub(forwardVector, Vector3f())
//                camera.setPosition(newCameraPosition.x, newCameraPosition.y, newCameraPosition.z)
            }
        }

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP)
        camera.moveRotation(cameraRot.x, cameraRot.y, cameraRot.z)
        // println("camera: ${camera.position}, ${camera.rotation}")
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