package net.fish

import org.reflections.Reflections

object Reflect {
    private val classNameRegex = "net.fish.y[0-9]{4}.Day[0-9]{2}".toRegex()
    fun getDays(year: Int) : List<Day> {
        val reflections =  Reflections("net.fish.y$year")

        return reflections.getSubTypesOf(Day::class.java)
            .filter { it.name.matches(classNameRegex) }
            .mapNotNull { it.kotlin.objectInstance }
            .sortedBy { it.javaClass.simpleName }
    }
}

fun main() {
    val days = Reflect.getDays(2020)
    days.forEach { println(it.javaClass) }
}