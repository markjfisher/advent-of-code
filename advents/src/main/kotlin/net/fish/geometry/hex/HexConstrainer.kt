package net.fish.geometry.hex

interface HexConstrainer {
    fun constrain(hex: Hex): Hex
    fun simpleName(): String
}

class DefaultHexConstrainer: HexConstrainer {
    override fun constrain(hex: Hex): Hex {
        return hex
    }

    override fun toString() = "DefaultHexConstrainer"
    override fun simpleName() = toString()
}