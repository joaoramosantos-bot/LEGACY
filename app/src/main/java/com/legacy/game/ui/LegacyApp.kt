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
    onCreate: (String, String, Sex, LocalDate, String, String, Double, LocalDate) -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    var personName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Portugal") }
    var city by remember { mutableStateOf("Lisboa") }
    var birthYear by remember { mutableStateOf("1996") }
    var capital by remember { mutableStateOf("10000") }
    var sex by remember { mutableStateOf(Sex.MASCULINO) }

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
            onValueChange = { familyName = it },
            label = { Text("Nome da família") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = personName,
            onValueChange = { personName = it },
            label = { Text("Nome do personagem principal") },
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
            value = birthYear,
            onValueChange = { birthYear = it },
            label = { Text("Ano de nascimento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("País") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Cidade") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = capital,
            onValueChange = { capital = it },
            label = { Text("Capital inicial (€)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val year = birthYear.toIntOrNull() ?: 1996
                val money = capital.toDoubleOrNull() ?: 0.0
                onCreate(
                    familyName.ifBlank { "Família Sem Nome" },
                    personName.ifBlank { "Personagem" },
                    sex,
                    LocalDate.of(year, 1, 1),
                    country.ifBlank { "Portugal" },
                    city.ifBlank { "Lisboa" },
                    money,
                    LocalDate.of(year + 30, 1, 1)
                )
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
        Text("Família ${game.family.name}", style = MaterialTheme.typography.headlineMedium)
        Text(game.currentDate.format(formatter))
        Spacer(Modifier.height(20.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(person.name, style = MaterialTheme.typography.titleLarge)
                Text("${person.ageAt(game.currentDate)} anos")
                Text("${person.city}, ${person.country}")
                Text("Dinheiro: €${"%.2f".format(person.money)}")
                Text("Profissão: ${person.profession}")
                Text("Saúde: ${person.health}/100")
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
