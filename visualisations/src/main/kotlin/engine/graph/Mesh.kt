package engine.graph

import org.joml.Vector3f
import org.lwjgl.opengl.GL11C.GL_FLOAT
import org.lwjgl.opengl.GL11C.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11C.GL_TRIANGLES
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glBindTexture
import org.lwjgl.opengl.GL11C.glDrawElements
import org.lwjgl.opengl.GL13C.GL_TEXTURE0
import org.lwjgl.opengl.GL13C.glActiveTexture
import org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15C.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15C.glBindBuffer
import org.lwjgl.opengl.GL15C.glBufferData
import org.lwjgl.opengl.GL15C.glDeleteBuffers
import org.lwjgl.opengl.GL15C.glGenBuffers
import org.lwjgl.opengl.GL20C.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glVertexAttribPointer
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.lwjgl.opengl.GL30C.glDeleteVertexArrays
import org.lwjgl.opengl.GL30C.glGenVertexArrays
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(
    val positions: FloatArray,
    val textCoords: FloatArray,
    val normals: FloatArray,
    val indices: IntArray,
    var texture: Texture? = null,
    var colour: Vector3f = DEFAULT_COLOUR
    // var updateTexture: Boolean = false
) {
    var vaoId = 0
        private set

    private var vboIdList = mutableListOf<Int>()
    private val vertexCount = indices.size

    fun isTextured() = texture != null

    fun render() {
        texture?.id?.let {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0)
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, it)

            // private hack of texture coordinates - TODO: Come up with something better
//            if (updateTexture) {
//                updateTexture = false
//                glBindBuffer(GL_ARRAY_BUFFER, vboIdList[1])
//                glBufferSubData(GL_ARRAY_BUFFER, 0, textCoords)
//            }
        }

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
        texture?.cleanup()

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    init {
        val posBuffer: FloatBuffer = MemoryUtil.memAllocFloat(positions.size)
        val textCoordsBuffer: FloatBuffer = MemoryUtil.memAllocFloat(textCoords.size)
        val vecNormalsBuffer: FloatBuffer = MemoryUtil.memAllocFloat(normals.size)
        val indicesBuffer: IntBuffer = MemoryUtil.memAllocInt(indices.size)

        try {
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            createVBO(0, 3, posBuffer, positions, GL_STATIC_DRAW)
            createVBO(1, 2, textCoordsBuffer, textCoords, GL_STATIC_DRAW)
            createVBO(2, 3, vecNormalsBuffer, normals, GL_STATIC_DRAW)

            // Index VBO
            val vboId = glGenBuffers()
            vboIdList.add(vboId)
            indicesBuffer.put(indices).flip()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

            // finally, finish the vertex array we started at the top
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            MemoryUtil.memFree(posBuffer)
            MemoryUtil.memFree(textCoordsBuffer)
            MemoryUtil.memFree(vecNormalsBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    private fun createVBO(vertexAttribIndex: Int, vertexAttribSize: Int, buffer: FloatBuffer, values: FloatArray, bufferType: Int) {
        val vboId = glGenBuffers()
        vboIdList.add(vboId)
        buffer.put(values).flip()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, buffer, bufferType)
        glEnableVertexAttribArray(vertexAttribIndex)
        glVertexAttribPointer(vertexAttribIndex, vertexAttribSize, GL_FLOAT, false, 0, 0)
    }

    override fun toString(): String {
        return String.format("Mesh[vaoId: %d, vbos: %d, positions: %d, textureCoords: %d, normals: %d, indices: %d, texture: %s, colour: %s]", vaoId, vboIdList.size, positions.size, textCoords.size, normals.size, indices.size, isTextured(), colour)
    }

    companion object {
        val DEFAULT_COLOUR = Vector3f(1.0f, 1.0f, 1.0f)
    }
}