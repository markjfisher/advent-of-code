package net.fish.geometry.hex

interface HexDataStorage<T: HexData> {
    val hexes: Iterable<Hex>

    fun addHex(hex: Hex, data: T): Boolean

    fun getData(hex: Hex): T?
    fun containsHex(hex: Hex): Boolean
    fun hasData(hex: Hex): Boolean
    fun clearData(hex: Hex): Boolean
    fun clearAll()
}

class HashMapBackedHexDataStorage<T:HexData> : HexDataStorage<T> {
    private val storage = LinkedHashMap<Hex, T?>()

    override val hexes: Iterable<Hex>
        get() = storage.keys

    override fun addHex(hex: Hex, data: T): Boolean {
        val previous = storage.put(hex, data)
        return previous != null
    }

    override fun getData(hex: Hex): T? {
        return storage[hex]
    }

    override fun containsHex(hex: Hex): Boolean {
        return storage.containsKey(hex)
    }

    override fun hasData(hex: Hex): Boolean {
        return storage[hex] != null
    }

    override fun clearData(hex: Hex): Boolean {
        val result = hasData(hex)
        storage.remove(hex)
        return result
    }

    override fun clearAll() {
        storage.clear()
    }

}