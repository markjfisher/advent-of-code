package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HexTest {
    @Test
    fun `hex arithmatic`() {
        assertThat(Hex(3, -7, 4) + Hex(1, -3, 2)).isEqualTo(Hex(4, -10, 6))
        assertThat(Hex(3, -7, 4) - Hex(1, -3, 2)).isEqualTo(Hex(2, -4, 2))

        assertThat(Hex(1, 2, -3).scale(3)).isEqualTo(Hex(3, 6, -9))
    }

    @Test
    fun `hex direction`() {
        assertThat(Hex.direction(2)).isEqualTo(Hex(0, -1, 1))
    }

    @Test
    fun `hex neighbour`() {
        assertThat(Hex(1, -2, 1).neighbour(2)).isEqualTo(Hex(1, -3, 2))
    }

    @Test
    fun `hex diagonal neighbour`() {
        assertThat(Hex(1, -2, 1).diagonalNeighbour(3)).isEqualTo(Hex(-1, -1, 2))
    }

    @Test
    fun `hex distance`() {
        assertThat(Hex(3, -7, 4).distance(Hex(0, 0, 0))).isEqualTo(7)
    }

    @Test
    fun `hex rotation`() {
        assertThat(Hex(1, -3, 2).rotateRight()).isEqualTo(Hex(3, -2, -1))
        assertThat(Hex(1, -3, 2).rotateLeft()).isEqualTo(Hex(-2, -1, 3))
    }

    @Test
    fun `hex rounding`() {
        val a = FractionalHex(0.0, 0.0, 0.0)
        val b = FractionalHex(1.0, -1.0, 0.0)
        val c = FractionalHex(0.0, -1.0, 1.0)

        assertThat(FractionalHex(0.0, 0.0, 0.0).hexLerp(FractionalHex(10.0, -20.0, 10.0), 0.5).hexRound()).isEqualTo(Hex(5, -10, 5))

        assertThat(a.hexLerp(b, 0.499).hexRound()).isEqualTo(a.hexRound())
        assertThat(a.hexLerp(b, 0.501).hexRound()).isEqualTo(b.hexRound())

        val e1 = FractionalHex(a.q * 0.4 + b.q * 0.3 + c.q * 0.3, a.r * 0.4 + b.r * 0.3 + c.r * 0.3, a.s * 0.4 + b.s * 0.3 + c.s * 0.3).hexRound()
        assertThat(e1).isEqualTo(a.hexRound())

        val e2 = FractionalHex(a.q * 0.3 + b.q * 0.3 + c.q * 0.4, a.r * 0.3 + b.r * 0.3 + c.r * 0.4, a.s * 0.3 + b.s * 0.3 + c.s * 0.4).hexRound()
        assertThat(e2).isEqualTo(c.hexRound())
    }

    @Test
    fun `hex line draw`() {
        val lineDraw = FractionalHex.hexLinedraw(Hex(0, 0, 0), Hex(1, -5, 4))
        assertThat(lineDraw).containsExactly(
            Hex(0, 0, 0), Hex(0, -1, 1), Hex(0, -2, 2), Hex(1, -3, 2), Hex(1, -4, 3), Hex(1, -5, 4)
        )
    }

    @Test
    fun `layout setup`() {
        val h = Hex(3, 4, -7)
        val flat = Layout(Layout.flat, Point2D(10.0, 15.0), Point2D(35.0, 71.0))
        assertThat(flat.pixelToHex(flat.hexToPixel(h)).hexRound()).isEqualTo(h)

        val pointy = Layout(Layout.pointy, Point2D(10.0, 15.0), Point2D(35.0, 71.0))
        assertThat(pointy.pixelToHex(pointy.hexToPixel(h)).hexRound()).isEqualTo(h)
    }

    @Test
    fun `doubled roundtrip`() {
        val a = Hex(3, 4, -7)
        val b = DoubledCoord(1, -3)

        assertThat(DoubledCoord.qdoubledFromCube(a).qdoubledToCube()).isEqualTo(a)
        assertThat(DoubledCoord.qdoubledFromCube(b.qdoubledToCube())).isEqualTo(b)

        assertThat(DoubledCoord.rdoubledFromCube(a).rdoubledToCube()).isEqualTo(a)
        assertThat(DoubledCoord.rdoubledFromCube(b.rdoubledToCube())).isEqualTo(b)
    }

    @Test
    fun `doubled from and to cube`() {
        assertThat(DoubledCoord.qdoubledFromCube(Hex(1, 2, -3))).isEqualTo(DoubledCoord(1, 5))
        assertThat(DoubledCoord.rdoubledFromCube(Hex(1, 2, -3))).isEqualTo(DoubledCoord(4, 2))

        assertThat(DoubledCoord(1, 5).qdoubledToCube()).isEqualTo(Hex(1, 2, -3))
        assertThat(DoubledCoord(4, 2).rdoubledToCube()).isEqualTo(Hex(1, 2, -3))
    }

    @Test
    fun `offset roundtrip`() {
        val a = Hex(3, 4, -7)
        val b = OffsetCoord(1, -3)

        assertThat(OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, a))).isEqualTo(a)
        assertThat(OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, b))).isEqualTo(b)

        assertThat(OffsetCoord.qoffsetToCube(OffsetCoord.ODD, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, a))).isEqualTo(a)
        assertThat(OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, OffsetCoord.qoffsetToCube(OffsetCoord.ODD, b))).isEqualTo(b)

        assertThat(OffsetCoord.roffsetToCube(OffsetCoord.EVEN, OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, a))).isEqualTo(a)
        assertThat(OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, OffsetCoord.roffsetToCube(OffsetCoord.EVEN, b))).isEqualTo(b)

        assertThat(OffsetCoord.roffsetToCube(OffsetCoord.ODD, OffsetCoord.roffsetFromCube(OffsetCoord.ODD, a))).isEqualTo(a)
        assertThat(OffsetCoord.roffsetFromCube(OffsetCoord.ODD, OffsetCoord.roffsetToCube(OffsetCoord.ODD, b))).isEqualTo(b)
    }

    @Test
    fun `offset to and from cube`() {
        assertThat(OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, Hex(1, 2, -3))).isEqualTo(OffsetCoord(1, 3))
        assertThat(OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, Hex(1, 2, -3))).isEqualTo(OffsetCoord(1, 2))

        assertThat(OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, OffsetCoord(1, 3))).isEqualTo(Hex(1, 2, -3))
        assertThat(OffsetCoord.qoffsetToCube(OffsetCoord.ODD, OffsetCoord(1, 2))).isEqualTo(Hex(1, 2, -3))
    }
}