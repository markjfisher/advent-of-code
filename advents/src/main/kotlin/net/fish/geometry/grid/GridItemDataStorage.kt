package net.fish.geometry.grid

interface GridItemDataStorage<T: GridItemData> {
    val items: Iterable<GridItem>
    val data: Iterable<T>

    fun addItem(item: GridItem, data: T): Boolean

    fun getData(item: GridItem): T?
    fun containsItem(item: GridItem): Boolean
    fun hasData(item: GridItem): Boolean
    fun clearData(item: GridItem): Boolean
    fun clearAll()
}

class HashMapBackedGridItemDataStorage<T: GridItemData> : GridItemDataStorage<T> {
    private val storage = LinkedHashMap<GridItem, T?>()

    override val items: Iterable<GridItem>
        get() = storage.keys

    override val data: Iterable<T>
        get() = storage.values.filterNotNull().asIterable()

    override fun addItem(item: GridItem, data: T): Boolean {
        val previous = storage.put(item, data)
        return previous != null
    }

    override fun getData(item: GridItem): T? {
        return storage[item]
    }

    override fun containsItem(item: GridItem): Boolean {
        return storage.containsKey(item)
    }

    override fun hasData(item: GridItem): Boolean {
        return storage[item] != null
    }

    override fun clearData(item: GridItem): Boolean {
        val result = hasData(item)
        storage.remove(item)
        return result
    }

    override fun clearAll() {
        storage.clear()
    }

}