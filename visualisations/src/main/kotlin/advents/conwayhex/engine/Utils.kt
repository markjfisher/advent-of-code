package advents.conwayhex.engine

import java.nio.charset.StandardCharsets
import java.util.Scanner

object Utils {
    fun loadResource(fileName: String): String {
        var result: String
        Utils::class.java.getResourceAsStream(fileName).use { inputStream ->
            Scanner(inputStream, StandardCharsets.UTF_8.name()).use { scanner -> result = scanner.useDelimiter("\\A").next() }
        }
        return result
    }
}