package net.fish.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class SortedListTest {

    private fun <T> assertCollectionEquals(c1: Iterable<T>, c2: Iterable<T>) {
        for ((e1, e2) in c1 zip c2) {
            assertEquals(e1, e2, "Collection $c1 is not equal to $c2")
        }
    }

    @Test
    fun `Get returns an element at the position`() {
        val l1 = sortedMutableListOf(1, 2, 5, 6)
        assertEquals(1, l1[0])
        assertEquals(2, l1[1])
        assertEquals(5, l1[2])
        assertEquals(6, l1[3])
    }

    @Test
    fun `Iteration is iterating over all elements in the correct order`() {
        assertCollectionEquals(listOf(1, 2, 5, 6), sortedMutableListOf(1, 2, 5, 6))
    }

    @Test
    fun `Size returns size of the collection`() {
        assertEquals(4, sortedMutableListOf(1, 2, 5, 6).size)
    }

    @Test
    fun `When we define elements using sortedMutableListOf, their order is corrected`() {
        assertCollectionEquals(listOf(1, 2, 5, 6), sortedMutableListOf(2, 6, 1, 5))
        assertCollectionEquals(listOf(1, 2, 5, 6), sortedMutableListOf(compareBy { it }, 2, 6, 1, 5))
        assertCollectionEquals(listOf(6, 5, 2, 1), sortedMutableListOf(compareBy { -it }, 2, 6, 1, 5))
    }

    @Test
    fun `When we add an element to collection with natural order, it is placed in the correct position according to this order`() {
        val l1 = sortedMutableListOf(1, 2, 5, 6)
        l1.add(4)
        assertTrue(4 in l1)
        assertEquals(4, l1[2])
        assertCollectionEquals(sortedMutableListOf(1, 2, 4, 5, 6), l1)

        val l2: SortedMutableList<String> = sortedMutableListOf("A", "B", "D")
        l2.add("C")
        assertTrue("C" in l2)
        assertEquals("C", l2[2])
        assertCollectionEquals(sortedMutableListOf("A", "B", "C", "D"), l2)
    }

    @Test
    fun `When we add an element, it is placed in the correct position according to the comparator`() {
        val l: SortedMutableList<String> = sortedMutableListOf(compareBy { it.length }, "B", "AAA")
        l.add("DD")
        assertEquals(3, l.size)
        assertTrue("DD" in l)
        assertCollectionEquals(listOf("B", "DD", "AAA"), l)
    }

    @Test
    fun `Check remove`() {
        assertCollectionEquals(listOf(1, 2, 5, 6), sortedMutableListOf(2, 6, 8, 1, 5).apply { remove(8) })
        assertCollectionEquals(listOf(1, 2, 5, 8), sortedMutableListOf(2, 6, 8, 1, 5).apply { remove(6) })
        assertCollectionEquals(listOf(1, 2, 5, 6, 8), sortedMutableListOf(2, 6, 8, 1, 5).apply { remove(3) })
    }

    @Test
    fun `Contains is based on real equility, not based on comparator`() {
        val l: SortedMutableList<String> = sortedMutableListOf(compareBy { it.length }, "B", "CC", "AA", "AAA")
        assertTrue("CC" in l)
        assertTrue("DD" !in l)
        assertTrue("AA" in l)
        assertTrue("AAA" in l)
    }

    @Test
    fun `Contains should do around log2(size) checks`() {
        val numbers = (1..100000).map { Random.nextInt(10_000_000) }

        var comparatorCounter = 0
        val comparator = Comparator<Int> { t1, t2 ->
            comparatorCounter++
            t1.compareTo(t2)
        }
        val tree = sortedMutableListOf(comparator)

        for (num in numbers) {
            tree.add(num)
        }

        comparatorCounter = 0
        tree.add(5432143)

        assertTrue(comparatorCounter < 32)
    }

    @Test
    fun `Sorting is correct`() {
        val numbers = (1..1000).map { Random.nextInt(10_000_000) }
        val tree = sortedMutableListOf<Int>().apply {
            for(num in numbers) add(num)
        }

        assertCollectionEquals(tree, numbers.sorted())
    }
}