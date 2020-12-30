package advents.conwayhex.engine.graph

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C.GL_FLOAT
import org.lwjgl.opengl.GL11C.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11C.GL_TRIANGLES
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glBindTexture
import org.lwjgl.opengl.GL11C.glDrawElements
import org.lwjgl.opengl.GL13C.GL_TEXTURE0
import org.lwjgl.opengl.GL13C.glActiveTexture
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15C.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15C.glBindBuffer
import org.lwjgl.opengl.GL15C.glBufferData
import org.lwjgl.opengl.GL15C.glDeleteBuffers
import org.lwjgl.opengl.GL15C.glGenBuffers
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20C.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glVertexAttribPointer
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.lwjgl.opengl.GL30C.glGenVertexArrays
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.ArrayList

class Mesh(
    positions: FloatArray,
    textCoords: FloatArray,
    indices: IntArray,
    private val texture: Texture
) {
    var vaoId = 0
    private var vboIdList = mutableListOf<Int>()
    var vertexCount = 0

    fun render() {
        // Activate first texture bank
        glActiveTexture(GL_TEXTURE0)
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, texture.id)

        // Draw the mesh
        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glBindVertexArray(0)
    }

    fun cleanUp() {
        glDisableVertexAttribArray(0)

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        vboIdList.forEach { glDeleteBuffers(it) }

        // Delete the texture
        texture.cleanup()

        // Delete the VAO
        GL30.glBindVertexArray(0)
        GL30.glDeleteVertexArrays(vaoId)
    }

    init {
        val posBuffer: FloatBuffer = MemoryUtil.memAllocFloat(positions.size)
        var textCoordsBuffer: FloatBuffer = MemoryUtil.memAllocFloat(textCoords.size)
        val indicesBuffer: IntBuffer = MemoryUtil.memAllocInt(indices.size)

        try {
            vertexCount = indices.size
            vboIdList = ArrayList()
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Position VBO
            var vboId = glGenBuffers()
            vboIdList.add(vboId)
            posBuffer.put(positions).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Texture coordinates VBO
            vboId = glGenBuffers()
            vboIdList.add(vboId)
            textCoordsBuffer.put(textCoords).flip()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW)
            GL20.glEnableVertexAttribArray(1)
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0)

            // Index VBO
            vboId = glGenBuffers()
            vboIdList.add(vboId)
            indicesBuffer.put(indices).flip()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            MemoryUtil.memFree(posBuffer)
            MemoryUtil.memFree(textCoordsBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }
}