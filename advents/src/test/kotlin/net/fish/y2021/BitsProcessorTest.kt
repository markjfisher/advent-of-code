package net.fish.y2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BitsProcessorTest {
    @Test
    fun `can read hex strings as list of ints`() {
        assertThat(BitsProcessor("A5").bits).containsExactly(1, 0, 1, 0, 0, 1, 0, 1)
        assertThat(BitsProcessor("5A").bits).containsExactly(0, 1, 0, 1, 1, 0, 1, 0)
    }

    @Test
    fun `can read a simple literal 2021 and move currentBit pointer past it`() {
        val processor = BitsProcessor("D2FE28")
        assertThat(processor.readPacket()).isEqualTo(
            LiteralPacket(header = PacketHeader(6, 4), value = 2021)
        )
        assertThat(processor.currentBit).isEqualTo(21)
    }

    @Test
    fun `can read a simple literal 1 and move currentBit pointer past it`() {
        val processor = BitsProcessor("5020")
        assertThat(processor.readPacket()).isEqualTo(
            LiteralPacket(header = PacketHeader(2, 4), value = 1)
        )
        assertThat(processor.currentBit).isEqualTo(11)
    }

    @Test
    fun `can read a simple literal 2 and move currentBit pointer past it`() {
        val processor = BitsProcessor("9040")
        assertThat(processor.readPacket()).isEqualTo(
            LiteralPacket(header = PacketHeader(4, 4), value = 2)
        )
        assertThat(processor.currentBit).isEqualTo(11)
    }

    @Test
    fun `can read a simple literal 3 and move currentBit pointer past it`() {
        val processor = BitsProcessor("3060")
        assertThat(processor.readPacket()).isEqualTo(
            LiteralPacket(header = PacketHeader(1, 4), value = 3)
        )
        assertThat(processor.currentBit).isEqualTo(11)
    }

    @Test
    fun `can read operator with sub-packets for test string 38006F45291200`() {
        val processor = BitsProcessor("38006F45291200")
        val packet = processor.readPacket()
        assertThat(packet).isInstanceOf(OperatorPacket::class.java)
        val p = packet as OperatorPacket
        assertThat(p.subPackets).hasSize(2)
        assertThat(p.subPackets[0]).isEqualTo(LiteralPacket(header = PacketHeader(6, 4), value = 10))
        assertThat(p.subPackets[1]).isEqualTo(LiteralPacket(header = PacketHeader(2, 4), value = 20))
    }

    @Test
    fun `can read operator with sub-packets for test string EE00D40C823060`() {
        val processor = BitsProcessor("EE00D40C823060")
        val packet = processor.readPacket()
        assertThat(packet).isInstanceOf(OperatorPacket::class.java)
        val p = packet as OperatorPacket
        assertThat(p.subPackets).hasSize(3)
        assertThat(p.subPackets[0]).isEqualTo(LiteralPacket(header = PacketHeader(2, 4), value = 1))
        assertThat(p.subPackets[1]).isEqualTo(LiteralPacket(header = PacketHeader(4, 4), value = 2))
        assertThat(p.subPackets[2]).isEqualTo(LiteralPacket(header = PacketHeader(1, 4), value = 3))
    }

    @Test
    fun `can read operator with sub-packets for test string 8A004A801A8002F478`() {
        val processor = BitsProcessor("8A004A801A8002F478")
        val packet = processor.readPacket()
        assertThat(packet).isInstanceOf(OperatorPacket::class.java)
        val p1 = packet as OperatorPacket
        assertThat(p1.header).isEqualTo(PacketHeader(version = 4, typeId = 2))
        assertThat(p1.subPackets).hasSize(1)
        assertThat(p1.subPackets[0]).isInstanceOf(OperatorPacket::class.java)
        val sp1p1 = p1.subPackets[0] as OperatorPacket
        assertThat(sp1p1.header).isEqualTo(PacketHeader(version = 1, typeId = 2))
        assertThat(sp1p1.subPackets).hasSize(1)
        val sp1p1p1 = sp1p1.subPackets[0] as OperatorPacket
        assertThat(sp1p1p1.header).isEqualTo(PacketHeader(version = 5, typeId = 2))
        assertThat(sp1p1p1.subPackets).hasSize(1)
        val literalPacket = sp1p1p1.subPackets[0] as LiteralPacket
        assertThat(literalPacket.header).isEqualTo(PacketHeader(version = 6, typeId = 4))
        assertThat(literalPacket.value).isEqualTo(15)
    }

}