package com.legacy.game.viewmodel

import androidx.lifecycle.ViewModel
import com.legacy.game.model.Family
import com.legacy.game.model.GameState
import com.legacy.game.model.Person
import com.legacy.game.model.Sex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class LegacyViewModel : ViewModel() {
    private val _game = MutableStateFlow<GameState?>(null)
    val game: StateFlow<GameState?> = _game

    fun createFamily(
        familyName: String,
        personName: String,
        sex: Sex,
        birthDate: LocalDate,
        country: String,
        city: String,
        capital: Double,
        startYear: Int
    ) {
        val startDate = LocalDate.of(startYear, 1, 1)
        val person = Person(
            name = personName,
            sex = sex,
            birthDate = birthDate,
            country = country,
            city = city,
            money = capital
        )
        _game.value = GameState(
            currentDate = startDate,
            family = Family(familyName, listOf(person))
        )
    }

    fun nextMonth() {
        val current = _game.value ?: return
        val newDate = current.currentDate.plusMonths(1)
        val updatedMembers = current.family.members.map { person ->
            val age = person.ageAt(newDate)
            person.copy(
                money = person.money + person.salary,
                health = if (age >= 70) (person.health - 1).coerceAtLeast(1) else person.health
            )
        }
        _game.value = current.copy(
            currentDate = newDate,
            family = current.family.copy(members = updatedMembers)
        )
    }
}
