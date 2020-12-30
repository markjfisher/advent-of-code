package advents.conwayhex.game

import advents.conwayhex.engine.Utils
import advents.conwayhex.engine.Window
import advents.conwayhex.engine.graph.Mesh
import advents.conwayhex.engine.graph.ShaderProgram
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glViewport
import org.lwjgl.opengl.GL11C.glClear
import org.lwjgl.opengl.GL11C.glDrawElements
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray
import org.lwjgl.opengl.GL30C.glBindVertexArray

class Renderer {
    private var shaderProgram: ShaderProgram = ShaderProgram()

    fun init() {
        shaderProgram.createProgram()
        shaderProgram.createVertexShader(Utils.loadResource("/conwayhex/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/conwayhex/fragment.fs"))
        shaderProgram.link()
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun render(window: Window, mesh: Mesh) {
        clear()
        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        shaderProgram.bind()

        // Draw the mesh
        glBindVertexArray(mesh.vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glBindVertexArray(0)
        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}
