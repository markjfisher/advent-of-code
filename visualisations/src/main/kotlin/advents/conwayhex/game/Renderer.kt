package advents.conwayhex.game

import advents.conwayhex.engine.GameItem
import advents.conwayhex.engine.Utils
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Camera
import advents.conwayhex.engine.graph.ShaderProgram
import advents.conwayhex.engine.graph.Transformation
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11C.glClear
import org.lwjgl.opengl.GL11C.glViewport

class Renderer {
    private val transformation: Transformation = Transformation()
    private var shaderProgram: ShaderProgram = ShaderProgram()

    companion object {
        private val FOV = Math.toRadians(60.0).toFloat()
        private const val Z_NEAR = 0.01f
        private const val Z_FAR = 1000f
    }

    fun init(window: Window) {
        shaderProgram.createProgram()
        shaderProgram.createVertexShader(Utils.loadResource("/conwayhex/shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/conwayhex/shaders/fragment.fs"))
        shaderProgram.link()

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("texture_sampler")

        // Create uniform for default colour and the flag that controls it
        shaderProgram.createUniform("colour")
        shaderProgram.createUniform("useColour")

    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun render(window: Window, camera: Camera, gameItems: List<GameItem>) {
        clear()
        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        shaderProgram.bind()

        // Update projection Matrix
        val projectionMatrix = transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)
        // Update view Matrix
        val viewMatrix = transformation.getViewMatrix(camera)

        // Need to change this if we have more than 1 texture, presumably
        shaderProgram.setUniform("texture_sampler", 0)

        // Render our items
        for (gameItem in gameItems) {
            val mesh = gameItem.mesh
            val modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix)
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
            shaderProgram.setUniform("colour", mesh.colour)
            shaderProgram.setUniform("useColour", if (mesh.isTextured()) 0 else 1)
            gameItem.mesh.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}
