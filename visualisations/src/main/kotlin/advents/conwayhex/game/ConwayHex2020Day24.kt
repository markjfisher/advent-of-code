package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameItem
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Mesh
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

class ConwayHex2020Day24: GameLogic {
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
        val positions = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        )
        val colours = floatArrayOf(
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.15f, 0.15f
        )
        val indices = intArrayOf(
            0, 1, 3, 3, 1, 2
        )

        val mesh = Mesh(positions, colours, indices)
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
            var rotation = gameItem.rotation.z + 1.5f
            if (rotation > 360f) {
                rotation = 0f
            }
            gameItem.setRotation(0f, 0f, rotation)
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