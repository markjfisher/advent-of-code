package net.fish.y2020

import mu.KotlinLogging
import net.fish.Day
import net.fish.resourceLines

private val logger = KotlinLogging.logger { }

object Day21 : Day {
    private val recipes by lazy { toRecipes(resourceLines(2020, 21)) }

    fun toRecipes(data: List<String>): List<Recipe> {
        val wordExtractor = Regex("""(\p{Alpha}+)""")
        return data.map { recipe ->
            val words = wordExtractor.findAll(recipe).map { it.destructured.let { (word) -> word } }.toList()
            val ingredients = words.takeWhile { it != "contains" }.toSet()
            val alergens = words.drop(ingredients.count() + 1).toSet()
            Recipe(ingredients, alergens)
        }
    }

    fun doPart1(recipes: List<Recipe>): Int {
        val alegenToIngredients = createAlegensToIngredients(recipes)
        val ingredientToCount = countAllIngredients(recipes)
        val allAlegenIngredients = alegenToIngredients.values.flatten().toSet()

        val nonAlegenIngredientsWithCount = ingredientToCount.filterKeys { !allAlegenIngredients.contains(it) }
        return nonAlegenIngredientsWithCount.values.sum()
    }

    fun doPart2(recipes: List<Recipe>): String {
        val alegenToIngredients = createAlegensToIngredients(recipes)
        // we currently have "eggs -> [nlxsmb], peanuts -> [nlxsmb, ttxvphb], ...
        // find the unique ingredient for the alegen by removing known single values and recursing until everything matches
        val alegenToUniqueIngredient = findUniqueIngredientForAlegen(alegenToIngredients, mapOf())

        return alegenToUniqueIngredient.toSortedMap().values.joinToString(",")
    }

    private fun findUniqueIngredientForAlegen(alegensWithMultipleIngredients: Map<String, Set<String>>, alegenToSingleIngredient: Map<String, String>): Map<String, String> {
        if (alegensWithMultipleIngredients.isEmpty()) return alegenToSingleIngredient

        val alegensWithUniqueIngredients = alegensWithMultipleIngredients.filterValues { it.count() == 1 }
        val remainingAlegensWithMultipleIngredients = alegensWithMultipleIngredients.filterKeys { !alegensWithUniqueIngredients.containsKey(it) }
        val newSingleIngredients = alegensWithUniqueIngredients.map { it.key to it.value.first() }.toMap() + alegenToSingleIngredient

        val multiplesWithKnownRemoved = remainingAlegensWithMultipleIngredients.map { (alegen, ingredients) ->
            alegen to ingredients.subtract(newSingleIngredients.values)
        }.toMap()

        // recurse with reduces unknowns, and increased knowns
        return findUniqueIngredientForAlegen(multiplesWithKnownRemoved, newSingleIngredients)
    }

    private fun countAllIngredients(recipes: List<Recipe>): Map<String, Int> {
        return recipes.flatMap { it.ingredients }.groupingBy { it }.eachCount()
    }

    private fun createAlegensToIngredients(recipes: List<Recipe>): MutableMap<String, Set<String>> {
        return recipes.fold(mutableMapOf()) { acc, recipe ->
            for (alegen in recipe.alergens) {
                val recipesWithAlegen = recipes.filter { it.alergens.contains(alegen) }
                // of these recipes, which ingredients are common with the current recipe's ingredients
                val commonIngredients = findCommonIngredients(recipe, recipesWithAlegen)
                acc[alegen] = acc.getOrDefault(alegen, emptySet()) + commonIngredients
            }
            acc
        }
    }

    private fun findCommonIngredients(recipe: Recipe, recipes: List<Recipe>): List<String> {
        return recipe.ingredients.filter { ingredient ->
            recipes.all { r -> r.ingredients.contains(ingredient) }
        }
    }

    override fun part1() = doPart1(recipes)
    override fun part2() = doPart2(recipes)

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

data class Recipe(
    val ingredients: Set<String>,
    val alergens: Set<String>
)

/*

--- Day 21: Allergen Assessment ---
You reach the train's last stop and the closest you can get to your vacation island without getting wet.
There aren't even any boats here, but nothing can stop you now: you build a raft. You just need a few days'
worth of food for your journey.

You don't speak the local language, so you can't read any ingredients lists. However, sometimes, allergens
are listed in a language you do understand. You should be able to use this information to determine which
ingredient contains which allergen and work out which foods are safe to take with you on your trip.

You start by compiling a list of foods (your puzzle input), one food per line. Each line includes that food's
ingredients list followed by some or all of the allergens the food contains.

Each allergen is found in exactly one ingredient. Each ingredient contains zero or one allergen. Allergens
aren't always marked; when they're listed (as in (contains nuts, shellfish) after an ingredients list), the
ingredient that contains each listed allergen will be somewhere in the corresponding ingredients list. However,
even if an allergen isn't listed, the ingredient that contains that allergen could still be present: maybe
they forgot to label it, or maybe it was labeled in a language you don't know.

For example, consider the following list of foods:

mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)

The first food in the list has four ingredients (written in a language you don't understand):
mxmxvkd, kfcds, sqjhc, and nhms.

While the food might contain other allergens, a few allergens the food definitely contains are
listed afterward: dairy and fish.

The first step is to determine which ingredients can't possibly contain any of the allergens in
any food in your list. In the above example, none of the ingredients kfcds, nhms, sbzzf, or trh can
contain an allergen. Counting the number of times any of these ingredients appear in any ingredients
list produces 5: they all appear once each except sbzzf, which appears twice.

Determine which ingredients cannot possibly contain any of the allergens in your list. How many times
do any of those ingredients appear?

 */

/*
Now that you've isolated the inert ingredients, you should have enough information to figure out which
ingredient contains which allergen.

In the above example:

mxmxvkd contains dairy.
sqjhc contains fish.
fvjkl contains soy.

Arrange the ingredients alphabetically by their allergen and separate them by commas to produce your canonical
dangerous ingredient list. (There should not be any spaces in your canonical dangerous ingredient list.)

In the above example, this would be mxmxvkd,sqjhc,fvjkl.

Time to stock your raft with supplies. What is your canonical dangerous ingredient list?


 */