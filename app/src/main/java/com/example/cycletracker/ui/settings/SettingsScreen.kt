package com.example.cycletracker.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
	avgCycleDays: Int,
	avgLutealDays: Int,
	onSave: (Int, Int) -> Unit,
	onExportJson: () -> Unit,
	onImportJson: () -> Unit,
	onExportCsv: () -> Unit,
	modifier: Modifier = Modifier
) {
	var cycle by remember(avgCycleDays) { mutableStateOf(avgCycleDays.toString()) }
	var luteal by remember(avgLutealDays) { mutableStateOf(avgLutealDays.toString()) }

	Column(modifier = modifier.padding(16.dp)) {
		Text("Настройки", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		OutlinedTextField(value = cycle, onValueChange = { cycle = it }, label = { Text("Средняя длина цикла, дни") })
		Spacer(Modifier.height(8.dp))
		OutlinedTextField(value = luteal, onValueChange = { luteal = it }, label = { Text("Лютеиновая фаза, дни") })
		Spacer(Modifier.height(12.dp))
		Button(onClick = { onSave(cycle.toIntOrNull() ?: avgCycleDays, luteal.toIntOrNull() ?: avgLutealDays) }) { Text("Сохранить") }
		Spacer(Modifier.height(24.dp))
		Text("Экспорт/Импорт", style = MaterialTheme.typography.titleMedium)
		Spacer(Modifier.height(8.dp))
		Row { Button(onClick = onExportJson) { Text("Экспорт JSON") } Spacer(Modifier.width(8.dp)); Button(onClick = onImportJson) { Text("Импорт JSON") } }
		Spacer(Modifier.height(8.dp))
		Button(onClick = onExportCsv) { Text("Экспорт CSV (симптомы)") }
	}
}
