package advents.conwayhex.engine.graph

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer


class Mesh(positions: FloatArray, colours: FloatArray, indices: IntArray) {
    var vaoId = 0
    private var posVboId = 0
    private var colourVboId = 0
    private var idxVboId = 0
    var vertexCount = 0

    fun cleanUp() {
        GL20.glDisableVertexAttribArray(0)

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        GL15.glDeleteBuffers(posVboId)
        GL15.glDeleteBuffers(idxVboId)

        // Delete the VAO
        GL30.glBindVertexArray(0)
        GL30.glDeleteVertexArrays(vaoId)
    }

    init {
        val posBuffer: FloatBuffer = MemoryUtil.memAllocFloat(positions.size)
        val colourBuffer: FloatBuffer = MemoryUtil.memAllocFloat(colours.size)
        val indicesBuffer: IntBuffer = MemoryUtil.memAllocInt(indices.size)
        try {
            vertexCount = indices.size
            vaoId = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(vaoId)

            // Position VBO
            posVboId = GL15.glGenBuffers()
            posBuffer.put(positions).flip()
            glBindBuffer(GL_ARRAY_BUFFER, posVboId)
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Colour VBO
            colourVboId = GL15.glGenBuffers()
            colourBuffer.put(colours).flip()
            glBindBuffer(GL_ARRAY_BUFFER, colourVboId)
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)

            // Index VBO
            idxVboId = GL15.glGenBuffers()
            indicesBuffer.put(indices).flip()
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, idxVboId)
            glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            GL30.glBindVertexArray(0)
        } finally {
            MemoryUtil.memFree(posBuffer)
            MemoryUtil.memFree(colourBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }
}