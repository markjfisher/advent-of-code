package advents.conwayhex.game

import advents.conwayhex.engine.GameEngine
import advents.conwayhex.engine.GameLogic
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Mesh
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.resourceLines
import net.fish.y2020.Day24
import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP

class ConwayHex2020Day24: GameLogic {
    private val data = resourceLines(2020, 24)
    val torusMinorRadius = 5.0
    val torusMajorRadius = 40.0
    val gridWidth = 320
    val gridHeight = 40
    val gridLayout = Layout(POINTY)
    val hexGrid = WrappingHexGrid(gridWidth, gridHeight, gridLayout, torusMinorRadius, torusMajorRadius)

    private var direction = 0
    private var color = 0.0f
    private val renderer: Renderer = Renderer()
    private lateinit var mesh: Mesh

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

    override fun init() {
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

        mesh = Mesh(positions, colours, indices)
        renderer.init()
    }

    override fun input(window: Window) {
        direction = when {
            window.isKeyPressed(GLFW_KEY_UP) -> 1
            window.isKeyPressed(GLFW_KEY_DOWN) -> -1
            else -> 0
        }
    }

    override fun update(interval: Float) {
        color += direction * 0.01f
        when {
            color > 1 -> color = 1.0f
            color < 0 -> color = 0.0f
        }
    }

    override fun render(window: Window) {
        window.setClearColor(color, color, color, 0.0f)
        renderer.render(window, mesh)
    }

    override fun cleanup() {
        renderer.cleanup()
        mesh.cleanUp()
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