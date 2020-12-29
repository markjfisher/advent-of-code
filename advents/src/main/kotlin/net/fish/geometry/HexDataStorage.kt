package net.fish.geometry

import net.fish.Maybe

interface HexDataStorage<T: HexData> {
    val hexes: Iterable<Hex>

    fun addHex(hex: Hex)
    fun addHex(hex: Hex, data: T): Boolean

    fun getData(hex: Hex): Maybe<T>
    fun containsHex(hex: Hex): Boolean
    fun hasData(hex: Hex): Boolean
    fun clearData(hex: Hex): Boolean
}

class DefaultHexDataStorage<T: HexData> : HexDataStorage<T> {
    private val storage = LinkedHashMap<Hex, Maybe<T>>()

    override val hexes: Iterable<Hex>
        get() = storage.keys

    override fun addHex(hex: Hex) {
        storage[hex] = Maybe.empty()
    }

    override fun addHex(hex: Hex, data: T): Boolean {
        val previous = storage.put(hex, Maybe.of(data))
        return previous != null
    }

    override fun getData(hex: Hex): Maybe<T> {
        return storage.getOrDefault(hex, Maybe.empty())
    }

    override fun containsHex(hex: Hex): Boolean {
        return storage.containsKey(hex)
    }

    override fun hasData(hex: Hex): Boolean {
        return storage.containsKey(hex) && storage[hex]!!.isPresent
    }

    override fun clearData(hex: Hex): Boolean {
        val result = hasData(hex)
        addHex(hex)
        return result
    }

}