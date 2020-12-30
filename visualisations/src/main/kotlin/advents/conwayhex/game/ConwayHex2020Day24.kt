package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameItem
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.Timer
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Mesh
import advents.conwayhex.engine.graph.Texture
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.lwjgl.glfw.GLFW.GLFW_KEY_A
import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT
import org.lwjgl.glfw.GLFW.GLFW_KEY_Q
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP
import org.lwjgl.glfw.GLFW.GLFW_KEY_X
import org.lwjgl.glfw.GLFW.GLFW_KEY_Z
import java.util.Date

class ConwayHex2020Day24 : GameLogic {
    private val data = resourceLines(2020, 24)
    val torusMinorRadius = 5.0
    val torusMajorRadius = 40.0
    val gridWidth = 320
    val gridHeight = 40
    val gridLayout = Layout(POINTY)
    val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    private val renderer: Renderer = Renderer()
    private val gameItems = mutableListOf<GameItem>()
    private var displxInc = 0
    private var displyInc = 0
    private var displzInc = 0
    private var scaleInc = 0

    private var textureNumber = 0
    private val timer = Timer()
    private var nextUpdate: Double = timer.time + 0.5

    val textCoords1 = floatArrayOf(
        // front face starting
        0.0f, 0.0f,
        0.0f, 0.5f,
        0.5f, 0.5f,
        0.5f, 0.0f,

        0.0f, 0.0f,
        0.5f, 0.0f,
        0.0f, 0.5f,
        0.5f, 0.5f,
        // For text coords in top face
        0.0f, 0.5f,
        0.5f, 0.5f,
        0.0f, 1.0f,
        0.5f, 1.0f,
        // For text coords in right face
        0.0f, 0.0f,
        0.0f, 0.5f,
        // For text coords in left face
        0.5f, 0.0f,
        0.5f, 0.5f,
        // For text coords in bottom face
        0.5f, 0.0f,
        1.0f, 0.0f,
        0.5f, 0.5f,
        1.0f, 0.5f
    )
    val textCoords2 = floatArrayOf(
        // change first 4 pairs for front face
        0.0f, 0.5f,
        0.0f, 1.0f,
        0.5f, 1.0f,
        0.5f, 0.5f,

        0.0f, 0.0f,
        0.5f, 0.0f,
        0.0f, 0.5f,
        0.5f, 0.5f,
        // For text coords in top face
        0.0f, 0.5f,
        0.5f, 0.5f,
        0.0f, 1.0f,
        0.5f, 1.0f,
        // For text coords in right face
        0.0f, 0.0f,
        0.0f, 0.5f,
        // For text coords in left face
        0.5f, 0.0f,
        0.5f, 0.5f,
        // For text coords in bottom face
        0.5f, 0.0f,
        1.0f, 0.0f,
        0.5f, 0.5f,
        1.0f, 0.5f
    )


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
        // Create the Mesh
        val positions = floatArrayOf(
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,

            // For texture coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f
        )
        val indices = intArrayOf(
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7
        )

        // relative to project root directory
        val texture = Texture("visualisations/textures/grassblock.png")
        val mesh = Mesh(positions, textCoords1, indices, texture)
        val gameItem = GameItem(mesh)
        gameItem.setPosition(0f, 0f, -2f)
        gameItems += gameItem

        renderer.init(window)
    }

    override fun input(window: Window) {
        displyInc = 0
        displxInc = 0
        displzInc = 0
        scaleInc = 0
        when {
            window.isKeyPressed(GLFW_KEY_UP) -> displyInc = 1
            window.isKeyPressed(GLFW_KEY_DOWN) -> displyInc = -1
            window.isKeyPressed(GLFW_KEY_LEFT) -> displxInc = -1
            window.isKeyPressed(GLFW_KEY_RIGHT) -> displxInc = 1
            window.isKeyPressed(GLFW_KEY_A) -> displzInc = -1
            window.isKeyPressed(GLFW_KEY_Q) -> displzInc = 1
            window.isKeyPressed(GLFW_KEY_Z) -> scaleInc = -1
            window.isKeyPressed(GLFW_KEY_X) -> scaleInc = 1
        }
    }

    override fun update(interval: Float) {
        for (gameItem in gameItems) {
            // Update position
            val itemPos = gameItem.position
            val posx = itemPos.x + displxInc * 0.01f
            val posy = itemPos.y + displyInc * 0.01f
            val posz = itemPos.z + displzInc * 0.01f
            gameItem.setPosition(posx, posy, posz)

            // Update scale
            var scale = gameItem.scale
            scale += scaleInc * 0.05f
            if (scale < 0) {
                scale = 0f
            }
            gameItem.scale = scale

            // Update rotation angle
            var rotationX: Float = gameItem.rotation.x + 0.05f
            if (rotationX > 360f) {
                rotationX -= 360f
            }
            var rotationY: Float = gameItem.rotation.y + 2.3f
            if (rotationY > 360f) {
                rotationY -= 360f
            }
            var rotationZ: Float = gameItem.rotation.z + 0.02f
            if (rotationZ > 360f) {
                rotationZ -= 360f
            }
            gameItem.setRotation(rotationX, rotationY, rotationZ)
            val mesh = gameItem.mesh
            if (timer.time > nextUpdate) {
                nextUpdate += 0.5
                textureNumber = (textureNumber + 1) % 2
                mesh.textCoords = if (textureNumber == 0) textCoords1 else textCoords2
                mesh.updateTexture = true
            }

        }
    }

    override fun render(window: Window) {
        renderer.render(window, gameItems)
    }

    override fun cleanup() {
        renderer.cleanup()
        gameItems.forEach { it.mesh.cleanUp() }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logic = ConwayHex2020Day24()
            val engine = GameEngine("Conway Hex", 1200, 800, true, logic)
            engine.run()
        }
    }
}