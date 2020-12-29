package net.fish.function

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class MaybeTest {
    @Test
    fun `empty maybe`() {
        val empty = Maybe.empty<Int>()
        assertThat(empty.isEmpty()).isTrue
        assertThat(empty.isPresent).isFalse

        assertDoesNotThrow { empty.ifPresent { throw Exception("will not throw") } }

        val nsee = assertThrows<NoSuchElementException> { empty.get() }
        assertThat(nsee.message).isEqualTo("No value present")

        assertThat(empty.map { it + 1 }.isEmpty()).isTrue
        assertThat(empty.flatMap { Maybe.of(it + 1) }.isEmpty()).isTrue

        assertThat(empty.fold(whenEmpty = { 0 }, whenPresent = { throw Exception("will not throw") })).isEqualTo(0)

        assertThat(empty.orElse(0)).isEqualTo(0)
        assertThat(empty.orElseGet { 0 }).isEqualTo(0)

        val e = assertThrows<Exception> { empty.orElseThrow { Exception("it was empty") } }
        assertThat(e.message).isEqualTo("it was empty")

        assertThat(empty.toString()).isEqualTo("Maybe.empty")
    }

    @Test
    fun `int maybe`() {
        val intMaybe = Maybe.of(1)
        assertThat(intMaybe.isEmpty()).isFalse
        assertThat(intMaybe.isPresent).isTrue
        assertThat(intMaybe).isEqualTo(Maybe.of(1))
        assertThat(intMaybe == Maybe.of(1)).isTrue

        assertThat(intMaybe.get()).isEqualTo(1)
        assertThat(intMaybe.fold({ throw Exception("no value") }, { it + 1 })).isEqualTo(2)

        assertThat(intMaybe.orElse(0)).isEqualTo(1)
        assertThat(intMaybe.orElseGet { 0 }).isEqualTo(1)
        assertDoesNotThrow { intMaybe.orElseThrow { Exception("it was empty") } }

        assertThat(intMaybe.map { it + 1 }.get()).isEqualTo(2)
        assertThat(intMaybe.flatMap { Maybe.of(it + 1) }).isEqualTo(Maybe.of(2))

        assertThat(intMaybe.toString()).isEqualTo("Maybe[1]")
    }

    @Test
    fun `nullable maybe`() {
        val nullMaybe = Maybe.ofNullable(null)

        assertThat(nullMaybe.isPresent).isFalse
        assertThat(nullMaybe.isEmpty()).isTrue
        assertDoesNotThrow { nullMaybe.ifPresent { throw Exception("will not throw") } }
    }
}