package com.example.cycletracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cycletracker.data.SymptomEntity

@Composable
fun HistoryScreen(
	items: List<SymptomEntity>,
	onFilterChange: (String, Int) -> Unit,
	modifier: Modifier = Modifier
) {
	var type by remember { mutableStateOf("") }
	var days by remember { mutableStateOf(60f) }

	Column(modifier = modifier.padding(16.dp)) {
		Text("История", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		OutlinedTextField(
			value = type,
			onValueChange = { type = it },
			label = { Text("Фильтр по типу (например: боль, настроение)") },
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(Modifier.height(8.dp))
		Text("Период: ${days.toInt()} дней")
		Slider(value = days, onValueChange = { days = it }, valueRange = 7f..180f)
		Button(onClick = { onFilterChange(type, days.toInt()) }) { Text("Применить") }
		Spacer(Modifier.height(16.dp))

		if (items.isEmpty()) {
			Text("Пока пусто. Добавьте симптомы на экране календаря.")
		} else {
			items.groupBy { it.date }.forEach { (date, group) ->
				Text(date.toString(), style = MaterialTheme.typography.titleSmall)
				group.forEach { item ->
					Text("• ${item.type} ${item.intensity?.let { v -> ": $v" } ?: ""} ${item.note ?: ""}")
				}
				Spacer(Modifier.height(8.dp))
			}
		}
	}
}
