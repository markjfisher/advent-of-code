package advents.conwayhex.engine.graph

import advents.conwayhex.engine.Utils
import org.joml.Vector2f
import org.joml.Vector3f

object OBJLoader {

    fun loadMesh(fileName: String): Mesh {
        val lines = Utils.readAllLines(fileName)
        val vertices = mutableListOf<Vector3f>()
        val textures = mutableListOf<Vector2f>()
        val normals = mutableListOf<Vector3f>()
        val faces = mutableListOf<Face>()

        for (line in lines) {
            val tokens = line.split("\\s".toRegex())
            when (tokens[0]) {
                "v" -> vertices.add(Vector3f(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()))
                "vt" -> textures.add(Vector2f(tokens[1].toFloat(), tokens[2].toFloat()))
                "vn" -> normals.add(Vector3f(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()))
                "f" -> faces.add(Face(tokens[1], tokens[2], tokens[3]))
            }
        }
        return reorderLists(vertices, textures, normals, faces)
    }

    private fun reorderLists(posList: List<Vector3f>, textCoordList: List<Vector2f>, normList: List<Vector3f>, facesList: List<Face>): Mesh {
        val indices = mutableListOf<Int>()
        // Create position array in the order it has been declared
        val posArr = FloatArray(posList.size * 3)
        for ((i, pos) in posList.withIndex()) {
            posArr[i * 3] = pos.x
            posArr[i * 3 + 1] = pos.y
            posArr[i * 3 + 2] = pos.z
        }
        val textCoordArr = FloatArray(posList.size * 2)
        val normArr = FloatArray(posList.size * 3)
        for (face in facesList) {
            for (indValue in face.faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList, indices, textCoordArr, normArr)
            }
        }
        return Mesh(posArr, textCoordArr, normArr, indices.toIntArray())
    }

    private fun processFaceVertex(
        indices: IdxGroup,
        textCoordList: List<Vector2f>,
        normList: List<Vector3f>,
        indicesList: MutableList<Int>,
        texCoordArr: FloatArray,
        normArr: FloatArray
    ) {

        // Set index for vertex coordinates
        val posIndex = indices.idxPos
        indicesList.add(posIndex)

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            val textCoord = textCoordList[indices.idxTextCoord]
            texCoordArr[posIndex * 2] = textCoord.x
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y
        }
        // Reorder vector normals
        if (indices.idxVecNormal >= 0) {
            val vecNorm = normList[indices.idxVecNormal]
            normArr[posIndex * 3] = vecNorm.x
            normArr[posIndex * 3 + 1] = vecNorm.y
            normArr[posIndex * 3 + 2] = vecNorm.z
        }
    }

    private class Face(v1: String, v2: String, v3: String) {
        // List of idxGroup groups for a face triangle (3 vertices per face).
        var faceVertexIndices = listOf(parseLine(v1), parseLine(v2), parseLine(v3))

        private fun parseLine(line: String): IdxGroup {
            val idxGroup = IdxGroup()
            val lineTokens = line.split("/").toTypedArray()
            val length = lineTokens.size
            idxGroup.idxPos = lineTokens[0].toInt() - 1
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                val textCoord = lineTokens[1]
                idxGroup.idxTextCoord = if (textCoord.isNotEmpty()) textCoord.toInt() - 1 else IdxGroup.NO_VALUE
                if (length > 2) {
                    idxGroup.idxVecNormal = lineTokens[2].toInt() - 1
                }
            }
            return idxGroup
        }
    }

    private data class IdxGroup(
        var idxPos: Int = NO_VALUE,
        var idxTextCoord: Int = NO_VALUE,
        var idxVecNormal: Int = NO_VALUE
    ) {
        companion object {
            const val NO_VALUE = -1
        }
    }
}