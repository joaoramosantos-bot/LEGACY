package com.legacy.game.model

import java.time.LocalDate
import java.time.Period

enum class Sex { MASCULINO, FEMININO }

data class Person(
    val name: String,
    val sex: Sex,
    val birthDate: LocalDate,
    val country: String,
    val city: String,
    val money: Double,
    val profession: String = "Desconhecida",
    val salary: Double = 0.0,
    val health: Int = 100
) {
    fun ageAt(date: LocalDate): Int = Period.between(birthDate, date).years
}

data class Family(
    val name: String,
    val members: List<Person>
)

data class GameState(
    val currentDate: LocalDate,
    val family: Family
)
