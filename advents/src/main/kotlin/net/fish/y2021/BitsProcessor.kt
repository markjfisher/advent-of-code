package net.fish.y2021

import java.math.BigInteger

data class BitsProcessor(
    val hexPacket: String
) {
    val bits = convertToBinary()
    var currentBit = 0

    private fun convertToBinary(): List<Int> {
        return BigInteger(hexPacket, 16).toString(2).toList().map { it.code - 48 }.toMutableList().also { l ->
            val zerosToPad = nextBoundary(l.size, 8) - l.size
            (0 until zerosToPad).forEach { _ -> l.add(0, 0) }
        }
    }

    private fun nextBoundary(size: Int, boundary: Int): Int {
        if (size % boundary == 0) return size
        return ((size / boundary) + 1) * boundary
    }

    private fun convertToInt(ints: List<Int>): Int {
        return Integer.parseInt(ints.joinToString(""), 2)
    }

    private fun convertToLong(ints: List<Int>): Long {
        return BigInteger(ints.joinToString(""), 2).toLong()
    }

    fun readPacket(): Packet {
        val header = readHeader()
        return when (header.typeId) {
            4 -> readLiteral(header)
            else -> processOperator(header)
        }
    }

    private fun processOperator(header: PacketHeader): Packet {
        currentBit += 6 // skip the header

        val mode = bits[currentBit]
        currentBit += 1

        val operatorSubPackets = when (mode) {
            0 -> processMode0()
            1 -> processMode1()
            else -> throw Exception("Unknown mode: $mode")
        }
        return OperatorPacket(header = header, mode = mode, subPackets = operatorSubPackets)
    }

    private fun processMode0(): List<Packet> {
        // next 15 bits represent the length of bits for sub-packets
        val subPacketLength = readNextBitsAsInt(15)
        currentBit += 15
        val endOfPackets = currentBit + subPacketLength

        // get the sub-packets, each will move the currentBit along naturally
        val subPackets = mutableListOf<Packet>()
        while (currentBit < endOfPackets) {
            subPackets += readPacket()
        }

        return subPackets
    }

    private fun processMode1(): List<Packet> {
        // next 11 bits represent the number of subpackets to read
        val subPacketCount = readNextBitsAsInt(11)
        currentBit += 11

        val subPackets = (0 until subPacketCount).map {
            readPacket()
        }

        return subPackets
    }

    private fun readNextBitsAsInt(count: Int): Int {
        return convertToInt(bits.subList(currentBit, currentBit + count))
    }

    private fun readLiteral(header: PacketHeader): Packet {
        val literalBits = mutableListOf<Int>()
        var isEndOfLiteralBits = false
        var literalBitsIndex = currentBit + 6 // move past the header
        while (!isEndOfLiteralBits) {
            val next5 = bits.subList(literalBitsIndex, literalBitsIndex + 5)
            literalBits.addAll(next5.drop(1))
            literalBitsIndex += 5
            isEndOfLiteralBits = (next5.first() == 0)
        }
        // Move current pointer on past the literal bits
        currentBit = literalBitsIndex

        // return it
        val literalValue = convertToLong(literalBits)
        return LiteralPacket(header = header, value = literalValue)
    }

    private fun readHeader(): PacketHeader {
        val version = convertToInt(bits.drop(currentBit).take(3))
        val typeId = convertToInt(bits.drop(currentBit + 3).take(3))
        return PacketHeader(version, typeId)
    }

    fun sumVersions(): Int {
        val top = readPacket()
        return top.versionSum()
    }
}

sealed class Packet(
    open val header: PacketHeader
) {
    abstract fun versionSum(): Int
    abstract fun calc(): Long
}

data class OperatorPacket(
    override val header: PacketHeader,
    val mode: Int,
    val subPackets: List<Packet>
) : Packet(header) {
    override fun versionSum(): Int = header.version + subPackets.sumOf { it.versionSum() }

    override fun calc(): Long {
        return when (header.typeId) {
            0 -> subPackets.sumOf { it.calc() }
            1 -> subPackets.fold(1L) { acc, p -> acc * p.calc() }
            2 -> subPackets.minOf { it.calc() }
            3 -> subPackets.maxOf { it.calc() }
            5 -> if (subPackets[0].calc() > subPackets[1].calc()) 1L else 0L
            6 -> if (subPackets[0].calc() < subPackets[1].calc()) 1L else 0L
            7 -> if (subPackets[0].calc() == subPackets[1].calc()) 1L else 0L
            else -> throw Exception("Unknown header typeId: ${header.typeId}")
        }
    }

}

data class LiteralPacket(
    override val header: PacketHeader,
    val value: Long
) : Packet(header) {
    override fun versionSum(): Int = header.version
    override fun calc(): Long = value
}

data class PacketHeader(
    val version: Int,
    val typeId: Int
)
