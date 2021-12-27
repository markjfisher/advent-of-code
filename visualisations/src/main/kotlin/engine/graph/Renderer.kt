package engine.graph

import engine.Utils
import engine.Window
import engine.item.GameItem
import org.joml.Math.toRadians
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11C.GL_STENCIL_BUFFER_BIT
import org.lwjgl.opengl.GL11C.glClear
import org.lwjgl.opengl.GL11C.glViewport

class Renderer {
    private val transformation: Transformation = Transformation()
    private var sceneShaderProgram: ShaderProgram = ShaderProgram()

    companion object {
        val FOV = toRadians(35f)
        private const val Z_NEAR = 0.01f
        private const val Z_FAR = 1000f
        val defaultShaders = ShaderResources(vertexShader = "/simple/shaders/vertex.vs", fragmentShader = "/simple/shaders/fragment.fs")
    }

    fun init(shaders: ShaderResources = defaultShaders) {
        setupSceneShader(shaders)
    }

    private fun setupSceneShader(shaders: ShaderResources) {
        sceneShaderProgram.createProgram()
        sceneShaderProgram.createVertexShader(Utils.loadResource(shaders.vertexShader))
        sceneShaderProgram.createFragmentShader(Utils.loadResource(shaders.fragmentShader))
        sceneShaderProgram.link()

        // Create uniforms for world and projection matrices
        sceneShaderProgram.createUniform("projectionMatrix")
        sceneShaderProgram.createUniform("modelViewMatrix")
        sceneShaderProgram.createUniform("texture_sampler")

        // Create uniform for default colour and the flag that controls it
        sceneShaderProgram.createUniform("colour")
        sceneShaderProgram.createUniform("useColour")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(window: Window, camera: Camera, gameItems: List<GameItem>, fov: Float = FOV) {
        clear()
        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        renderScene(window, camera, gameItems, fov)
    }

    fun renderScene(window: Window, camera: Camera, gameItems: List<GameItem>, fov: Float = FOV) {
        sceneShaderProgram.bind()

        // Update projection Matrix
        val projectionMatrix = transformation.getProjectionMatrix(fov, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix)
        // Update view Matrix
        val viewMatrix = transformation.getViewMatrix(camera)

        // Need to change this if we have more than 1 texture, presumably
        sceneShaderProgram.setUniform("texture_sampler", 0)

        for (gameItem in gameItems) {
            val modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix)
            sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
            sceneShaderProgram.setUniform("colour", gameItem.colour)
            sceneShaderProgram.setUniform("useColour", if (gameItem.mesh.isTextured()) 0 else 1)
            gameItem.mesh.render()
        }

        sceneShaderProgram.unbind()
    }

    fun cleanup() {
        sceneShaderProgram.cleanup()
    }
}
