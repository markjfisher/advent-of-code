package advents.conwayhex.engine.graph

import org.lwjgl.opengl.GL20
import java.lang.Exception
import org.lwjgl.system.MemoryStack

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20C.glGetUniformLocation
import org.lwjgl.opengl.GL20C.glUniformMatrix4fv


class ShaderProgram {
    private var programId: Int = 0
    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    private val uniforms: MutableMap<String, Int> = mutableMapOf()

    fun createVertexShader(shaderCode: String) {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode: String) {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER)
    }

    fun createProgram() {
        programId = GL20.glCreateProgram()
    }

    fun createUniform(uniformName: String) {
        val uniformLocation: Int = glGetUniformLocation(programId, uniformName)
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


    private fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = GL20.glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }
        GL20.glShaderSource(shaderId, shaderCode)
        GL20.glCompileShader(shaderId)
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024))
        }
        GL20.glAttachShader(programId, shaderId)
        return shaderId
    }

    fun link() {
        GL20.glLinkProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024))
        }
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId)
        }
        GL20.glValidateProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024))
        }
    }

    fun bind() {
        GL20.glUseProgram(programId)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            GL20.glDeleteProgram(programId)
        }
    }

//    init {
//        if (programId == 0) {
//            throw Exception("Could not create Shader")
//        }
//    }
}
