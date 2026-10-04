package com.legacy.game.viewmodel

import com.legacy.game.model.Sex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate

class LegacyViewModelTest {

    @Test
    fun createFamily_usesSelectedStartYearAndCalculatesAge() {
        val viewModel = LegacyViewModel()

        viewModel.createFamily(
            familyName = "Ramos",
            personName = "João Ramos",
            sex = Sex.MASCULINO,
            birthDate = LocalDate.of(1990, 6, 15),
            country = "Portugal",
            city = "Lisboa",
            capital = 10000.0,
            startYear = 2020
        )

        val game = viewModel.game.value
        assertNotNull(game)
        assertEquals(LocalDate.of(2020, 1, 1), game!!.currentDate)
        assertEquals("Ramos", game.family.name)
        assertEquals("João Ramos", game.family.members.single().name)
        assertEquals(29, game.family.members.single().ageAt(game.currentDate))
        assertEquals(10000.0, game.family.members.single().money, 0.001)
    }

    @Test
    fun nextMonth_advancesDateAndKeepsZeroSalaryUnchanged() {
        val viewModel = LegacyViewModel()

        viewModel.createFamily(
            familyName = "Ramos",
            personName = "João Ramos",
            sex = Sex.MASCULINO,
            birthDate = LocalDate.of(1990, 1, 1),
            country = "Portugal",
            city = "Lisboa",
            capital = 5000.0,
            startYear = 2020
        )

        viewModel.nextMonth()

        val game = viewModel.game.value
        assertEquals(LocalDate.of(2020, 2, 1), game!!.currentDate)
        assertEquals(5000.0, game.family.members.single().money, 0.001)
    }
}
