package advents.conwayhex.engine.graph

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20C.GL_LINK_STATUS
import org.lwjgl.opengl.GL20C.GL_VALIDATE_STATUS
import org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL20C.glAttachShader
import org.lwjgl.opengl.GL20C.glCompileShader
import org.lwjgl.opengl.GL20C.glCreateProgram
import org.lwjgl.opengl.GL20C.glCreateShader
import org.lwjgl.opengl.GL20C.glDeleteProgram
import org.lwjgl.opengl.GL20C.glDetachShader
import org.lwjgl.opengl.GL20C.glGetProgramInfoLog
import org.lwjgl.opengl.GL20C.glGetProgrami
import org.lwjgl.opengl.GL20C.glGetShaderInfoLog
import org.lwjgl.opengl.GL20C.glGetShaderi
import org.lwjgl.opengl.GL20C.glGetUniformLocation
import org.lwjgl.opengl.GL20C.glLinkProgram
import org.lwjgl.opengl.GL20C.glShaderSource
import org.lwjgl.opengl.GL20C.glUniform1i
import org.lwjgl.opengl.GL20C.glUniformMatrix4fv
import org.lwjgl.opengl.GL20C.glUseProgram
import org.lwjgl.opengl.GL20C.glValidateProgram
import org.lwjgl.system.MemoryStack


class ShaderProgram {
    private var programId: Int = 0
    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    private val uniforms = mutableMapOf<String, Int>()

    fun createVertexShader(shaderCode: String) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode: String) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    fun createProgram() {
        programId = glCreateProgram()
    }

    fun createUniform(uniformName: String) {
        val uniformLocation = glGetUniformLocation(programId, uniformName)
        if (uniformLocation < 0) {
            throw Exception("Could not find uniform:$uniformName")
        }
        uniforms[uniformName] = uniformLocation
    }

    fun setUniform(uniformName: String?, value: Matrix4f) {
        // Dump the matrix into a float buffer
        MemoryStack.stackPush().use { stack ->
            uniforms[uniformName]?.let { glUniformMatrix4fv(it, false, value[stack.mallocFloat(16)]) }
        }
    }

    fun setUniform(uniformName: String, value: Int) {
        uniforms[uniformName]?.let { glUniform1i(it, value) }
    }

    private fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }
        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }
        glAttachShader(programId, shaderId)
        return shaderId
    }

    fun link() {
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId)
        }
        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
    }

    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }

}
