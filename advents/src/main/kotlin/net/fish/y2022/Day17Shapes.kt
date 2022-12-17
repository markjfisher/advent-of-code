package net.fish.y2022

import net.fish.geometry.Point

object Day17Shapes {
    private val shapeStrings = listOf("####",
        """
 # 
###
 #
    """,
        """
  #
  #
###
    """,
        """
#
#
#
#
    """,
        """
##
##
    """)

    val shapes = shapeStrings.map { s ->
        s.split("\n")
            .filter { it.isNotBlank() }
            .reversed() // we want the bottom of the shape to be y=0, and increment going UP the shape
            .flatMapIndexed { j, line ->
                line.mapIndexedNotNull { i, c ->
                    if (c == '#') Point(i, j) else null
                }
            }
    }
}