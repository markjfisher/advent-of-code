package net.fish.geometry.hex

interface HexDataStorage {
    val hexes: Iterable<Hex>

    fun addHex(hex: Hex)
    fun addHex(hex: Hex, data: HexData): Boolean

    fun getData(hex: Hex): HexData
    fun containsHex(hex: Hex): Boolean
    fun hasData(hex: Hex): Boolean
    fun clearData(hex: Hex): Boolean
}

class DefaultHexDataStorage : HexDataStorage {
    private val storage = LinkedHashMap<Hex, HexData>()

    override val hexes: Iterable<Hex>
        get() = storage.keys

    override fun addHex(hex: Hex) {
        storage[hex] = EmptyHexData
    }

    override fun addHex(hex: Hex, data: HexData): Boolean {
        val previous = storage.put(hex, data)
        return previous != null
    }

    override fun getData(hex: Hex): HexData {
        return storage.getOrDefault(hex, EmptyHexData)
    }

    override fun containsHex(hex: Hex): Boolean {
        return storage.containsKey(hex)
    }

    override fun hasData(hex: Hex): Boolean {
        return storage.getOrDefault(hex, EmptyHexData) != EmptyHexData
    }

    override fun clearData(hex: Hex): Boolean {
        val result = hasData(hex)
        storage.remove(hex)
        return result
    }

}