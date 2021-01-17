package advents.conwayhex.engine

import java.nio.charset.StandardCharsets
import java.util.Scanner
import java.util.stream.Collectors
import org.lwjgl.BufferUtils.createByteBuffer

import java.nio.channels.ReadableByteChannel

import java.io.InputStream

import org.lwjgl.BufferUtils

import java.nio.channels.SeekableByteChannel

import java.nio.file.Path

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths


object Utils {
    fun loadResource(fileName: String): String {
        var result: String
        Utils::class.java.getResourceAsStream(fileName).use { inputStream ->
            // \A is "beginining of input"
            Scanner(inputStream, StandardCharsets.UTF_8.name()).use { scanner -> result = scanner.useDelimiter("\\A").next() }
        }
        return result
    }

    fun readAllLines(fileName: String): List<String> = Utils.javaClass.getResourceAsStream(fileName).bufferedReader().lines().collect(Collectors.toList())

    fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
        var buffer: ByteBuffer
        val path: Path = Paths.get(resource)
        if (Files.isReadable(path)) {
            Files.newByteChannel(path).use { fc ->
                buffer = createByteBuffer(fc.size().toInt() + 1)
                while (fc.read(buffer) != -1);
            }
        } else {
            Utils::class.java.getResourceAsStream(resource).use { source ->
                Channels.newChannel(source).use { rbc ->
                    buffer = createByteBuffer(bufferSize)
                    while (true) {
                        val bytes: Int = rbc.read(buffer)
                        if (bytes == -1) {
                            break
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                        }
                    }
                }
            }
        }
        buffer.flip()
        return buffer
    }

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }
}