package com.legacy.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.legacy.game.model.GameState
import com.legacy.game.model.Sex
import com.legacy.game.viewmodel.LegacyViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LegacyApp(vm: LegacyViewModel = viewModel()) {
    val game by vm.game.collectAsStateWithLifecycle()

    if (game == null) {
        CreateFamilyScreen(onCreate = vm::createFamily)
    } else {
        GameScreen(game = game!!, onNextMonth = vm::nextMonth)
    }
}

@Composable
private fun CreateFamilyScreen(
    onCreate: (
        String,
        String,
        Sex,
        LocalDate,
        String,
        String,
        Double,
        Int
    ) -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    var personName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Portugal") }
    var city by remember { mutableStateOf("Lisboa") }
    var birthDateText by remember { mutableStateOf("01/01/1996") }
    var startYearText by remember { mutableStateOf("2026") }
    var capitalText by remember { mutableStateOf("10000") }
    var sex by remember { mutableStateOf(Sex.MASCULINO) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("LEGACY", style = MaterialTheme.typography.displaySmall)
        Text("0.1 — Família e Vida", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = familyName,
            onValueChange = { familyName = it; error = null },
            label = { Text("Nome da família") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = personName,
            onValueChange = { personName = it; error = null },
            label = { Text("Nome do personagem principal") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                sex = if (sex == Sex.MASCULINO) Sex.FEMININO else Sex.MASCULINO
            }) {
                Text(if (sex == Sex.MASCULINO) "Masculino" else "Feminino")
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDateText,
            onValueChange = { birthDateText = it; error = null },
            label = { Text("Data de nascimento (dd/MM/yyyy)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { country = it; error = null },
            label = { Text("País") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it; error = null },
            label = { Text("Cidade") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = startYearText,
            onValueChange = { startYearText = it; error = null },
            label = { Text("Ano de início da partida") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = capitalText,
            onValueChange = { capitalText = it; error = null },
            label = { Text("Capital inicial (€)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val birthDate = runCatching {
                    LocalDate.parse(
                        birthDateText,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                }.getOrNull()
                val startYear = startYearText.toIntOrNull()
                val capital = capitalText.replace(",", ".").toDoubleOrNull()

                error = when {
                    familyName.isBlank() -> "Indica o nome da família."
                    personName.isBlank() -> "Indica o nome do personagem principal."
                    birthDate == null -> "A data de nascimento deve estar no formato dd/MM/yyyy."
                    startYear == null || startYear !in 1800..2200 ->
                        "Indica um ano de início válido (1800–2200)."
                    birthDate.year > startYear ->
                        "O ano de início não pode ser anterior ao nascimento."
                    capital == null || capital < 0 ->
                        "Indica um capital inicial válido."
                    else -> null
                }

                if (error == null) {
                    onCreate(
                        familyName.trim(),
                        personName.trim(),
                        sex,
                        birthDate!!,
                        country.trim().ifBlank { "Portugal" },
                        city.trim().ifBlank { "Lisboa" },
                        capital!!,
                        startYear!!
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CRIAR FAMÍLIA")
        }
    }
}

@Composable
private fun GameScreen(game: GameState, onNextMonth: () -> Unit) {
    val person = game.family.members.first()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Família \${game.family.name}", style = MaterialTheme.typography.headlineMedium)
        Text("Início: \${game.currentDate.format(formatter)}")
        Spacer(Modifier.height(20.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(person.name, style = MaterialTheme.typography.titleLarge)
                Text("\${person.ageAt(game.currentDate)} anos")
                Text("\${person.city}, \${person.country}")
                Text("Dinheiro: €\${"%.2f".format(person.money)}")
                Text("Profissão: \${person.profession}")
                Text("Saúde: \${person.health}/100")
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onNextMonth,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("PASSAR MÊS")
        }
    }
}
