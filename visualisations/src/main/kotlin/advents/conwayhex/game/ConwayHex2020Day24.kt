package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameItem
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.MouseInput
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Camera
import advents.conwayhex.engine.graph.OBJLoader.loadMesh
import advents.conwayhex.engine.graph.Texture
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_D
import org.lwjgl.glfw.GLFW.GLFW_KEY_S
import org.lwjgl.glfw.GLFW.GLFW_KEY_W
import org.lwjgl.glfw.GLFW.GLFW_KEY_X
import org.lwjgl.glfw.GLFW.GLFW_KEY_Z
import kotlin.math.PI

class ConwayHex2020Day24 : GameLogic {
    private val data = resourceLines(2020, 24)
    val torusMinorRadius = 0.25
    val torusMajorRadius = 1.0
    val gridWidth = 8
    val gridHeight = 4
    val gridLayout = Layout(POINTY)
    val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    private val renderer = Renderer()
    private val camera = Camera(Vector3f(2f, 1f, 1.9f), Vector3f(19f, -38f, 0f))
    private var cameraInc = Vector3f()
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
        // mesh loading is via resources
        // val mesh = loadMesh("/conwayhex/models/cube.obj")
        val mesh = loadMesh("/conwayhex/models/simple-hexagon.obj")

        // texture loading isn't via resources, so is relative to project root dir
        mesh.texture = Texture("visualisations/textures/grassblock.png")

        val gameItem = GameItem(mesh)
        gameItem.position = Vector3f(0f, 0f, -2f)
        gameItem.scale = 0.5f
        gameItems += gameItem
        println("added: $gameItem")

        hexGrid.centres().forEach { hexCentre ->
            val centre = Vector3f(hexCentre.x.toFloat(), hexCentre.y.toFloat(), hexCentre.z.toFloat())
            val normal = centre.normalize(Vector3f())
            val q = Quaternionf()
            q.fromAxisAngleRad(normal, 45f)
            println("n: $normal, q: $q")
            val gameItem = GameItem(mesh)
            gameItem.position = centre
            val rot = q.getEulerAnglesXYZ(Vector3f())
            gameItem.rotation = Vector3f(rot.x * 360f / 2f / PI.toFloat(), rot.y * 360f / 2f / PI.toFloat(), rot.z * 360f / 2f / PI.toFloat())
            gameItem.scale = 1f / gridWidth
            println("added $gameItem")
            gameItems += gameItem
        }

        renderer.init(window)
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        cameraInc.set(0.0, 0.0, 0.0)
        when {
            window.isKeyPressed(GLFW_KEY_W) -> cameraInc.z = -1f
            window.isKeyPressed(GLFW_KEY_S) -> cameraInc.z = 1f
            window.isKeyPressed(GLFW_KEY_A) -> cameraInc.x = -1f
            window.isKeyPressed(GLFW_KEY_D) -> cameraInc.x = 1f
            window.isKeyPressed(GLFW_KEY_Z) -> cameraInc.y = -1f
            window.isKeyPressed(GLFW_KEY_X) -> cameraInc.y = 1f
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP)

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed) {
            val rotVec: Vector2f = mouseInput.displVec
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0f)
        }
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
        const val CAMERA_POS_STEP = 0.05f
        const val MOUSE_SENSITIVITY = 0.2f

        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            engine.run()
        }
    }
}