package net.fish.y2020

import net.fish.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day04Test {
    private val passports: List<Day04.Passport> = Day04.toPassports(resourcePath("/2020/day04-test.txt").joinToString("\n"))

    @Test
    fun `should read 4 passwords`() {
        assertThat(passports).hasSize(4)
    }

    @Test
    fun `should validate a full record`() {
        val passport = Day04.Passport(
            byr = "byr",
            iyr = "iyr",
            eyr = "eyr",
            hgt = "hgt",
            hcl = "hcl",
            ecl = "ecl",
            pid = "pid",
            cid = "cid"
        )
        assertThat(passport.isValidPart1()).isTrue
    }

    @Test
    fun `should allow missing cid field`() {
        val passport = Day04.Passport(
            byr = "byr",
            iyr = "iyr",
            eyr = "eyr",
            hgt = "hgt",
            hcl = "hcl",
            ecl = "ecl",
            pid = "pid",
            cid = null
        )
        assertThat(passport.isValidPart1()).isTrue
    }

    @Test
    fun `should reject where missing cid and another`() {
        val passport = Day04.Passport(
            byr = "byr",
            iyr = "iyr",
            eyr = "eyr",
            hgt = "hgt",
            hcl = "hcl",
            ecl = "ecl",
            pid = null,
            cid = null
        )
        assertThat(passport.isValidPart1()).isFalse
    }

    @Test
    fun `should reject where missing another`() {
        val passport = Day04.Passport(
            byr = "byr",
            iyr = "iyr",
            eyr = "eyr",
            hgt = "hgt",
            hcl = "hcl",
            ecl = "ecl",
            pid = null,
            cid = "cid"
        )
        assertThat(passport.isValidPart1()).isFalse
    }

    @Test
    fun `isValidByr tests`() {
        assertThat(Day04.Passport().isValidByr()).isFalse
        assertThat(Day04.Passport(byr = "1919").isValidByr()).isFalse
        assertThat(Day04.Passport(byr = "1920").isValidByr()).isTrue
        assertThat(Day04.Passport(byr = "2002").isValidByr()).isTrue
        assertThat(Day04.Passport(byr = "2003").isValidByr()).isFalse
        assertThat(Day04.Passport(byr = "bad").isValidByr()).isFalse
    }

    @Test
    fun `isValidIyr tests`() {
        assertThat(Day04.Passport().isValidIyr()).isFalse
        assertThat(Day04.Passport(iyr = "2009").isValidIyr()).isFalse
        assertThat(Day04.Passport(iyr = "2010").isValidIyr()).isTrue
        assertThat(Day04.Passport(iyr = "2020").isValidIyr()).isTrue
        assertThat(Day04.Passport(iyr = "2021").isValidIyr()).isFalse
        assertThat(Day04.Passport(iyr = "bad").isValidIyr()).isFalse
    }

    @Test
    fun `isValidEyr tests`() {
        assertThat(Day04.Passport().isValidEyr()).isFalse
        assertThat(Day04.Passport(eyr = "2019").isValidEyr()).isFalse
        assertThat(Day04.Passport(eyr = "2020").isValidEyr()).isTrue
        assertThat(Day04.Passport(eyr = "2030").isValidEyr()).isTrue
        assertThat(Day04.Passport(eyr = "2031").isValidEyr()).isFalse
        assertThat(Day04.Passport(eyr = "bad").isValidEyr()).isFalse
    }

    @Test
    fun `isValidHgt tests`() {
        assertThat(Day04.Passport().isValidHgt()).isFalse
        assertThat(Day04.Passport(hgt = "").isValidHgt()).isFalse
        assertThat(Day04.Passport(hgt = "155").isValidHgt()).isFalse
        assertThat(Day04.Passport(hgt = "foo 155cm bar").isValidHgt()).isFalse

        assertThat(Day04.Passport(hgt = "149cm").isValidHgt()).isFalse
        assertThat(Day04.Passport(hgt = "150cm").isValidHgt()).isTrue
        assertThat(Day04.Passport(hgt = "193cm").isValidHgt()).isTrue
        assertThat(Day04.Passport(hgt = "194cm").isValidHgt()).isFalse

        assertThat(Day04.Passport(hgt = "58in").isValidHgt()).isFalse
        assertThat(Day04.Passport(hgt = "59in").isValidHgt()).isTrue
        assertThat(Day04.Passport(hgt = "76in").isValidHgt()).isTrue
        assertThat(Day04.Passport(hgt = "77in").isValidHgt()).isFalse
    }

    @Test
    fun `isValidHcl tests`() {
        assertThat(Day04.Passport().isValidHcl()).isFalse
        assertThat(Day04.Passport(hcl = "").isValidHcl()).isFalse
        assertThat(Day04.Passport(hcl = "bad").isValidHcl()).isFalse

        assertThat(Day04.Passport(hcl = "#12345").isValidHcl()).isFalse
        assertThat(Day04.Passport(hcl = "#123456").isValidHcl()).isTrue
        assertThat(Day04.Passport(hcl = "#abcdef").isValidHcl()).isTrue
        assertThat(Day04.Passport(hcl = "#abc123").isValidHcl()).isTrue
        assertThat(Day04.Passport(hcl = "#1234567").isValidHcl()).isFalse
    }

    @Test
    fun `isValidEcl tests`() {
        assertThat(Day04.Passport().isValidEcl()).isFalse
        assertThat(Day04.Passport(ecl = "").isValidEcl()).isFalse
        assertThat(Day04.Passport(ecl = "bad").isValidEcl()).isFalse
        assertThat(Day04.Passport(ecl = "amb1").isValidEcl()).isFalse

        assertThat(Day04.Passport(ecl = "amb").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "blu").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "brn").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "gry").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "grn").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "hzl").isValidEcl()).isTrue
        assertThat(Day04.Passport(ecl = "oth").isValidEcl()).isTrue
    }

    @Test
    fun `isValidPid tests`() {
        assertThat(Day04.Passport().isValidPid()).isFalse
        assertThat(Day04.Passport(pid = "").isValidPid()).isFalse
        assertThat(Day04.Passport(pid = "bad").isValidPid()).isFalse

        assertThat(Day04.Passport(pid = "000000000").isValidPid()).isTrue
        assertThat(Day04.Passport(pid = "123456789").isValidPid()).isTrue
        assertThat(Day04.Passport(pid = "000123456").isValidPid()).isTrue
    }

}