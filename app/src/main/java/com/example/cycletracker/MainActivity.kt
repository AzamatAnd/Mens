package com.example.cycletracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cycletracker.data.export.ExportImport
import com.example.cycletracker.data.preferences.UserPreferences
import com.example.cycletracker.di.ServiceLocator
import com.example.cycletracker.ui.CycleViewModel
import com.example.cycletracker.ui.calendar.CalendarScreen
import com.example.cycletracker.ui.history.HistoryScreen
import com.example.cycletracker.ui.settings.SettingsScreen
import com.example.cycletracker.ui.theme.CycleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	private val viewModel: CycleViewModel by viewModels { ServiceLocator.provideViewModelFactory(this) }

	private val createJson = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
		uri ?: return@registerForActivityResult
		lifecycleScope.launch {
			val cycles = viewModel.cycles.value
			val symptoms = viewModel.recentSymptoms.value // quick export; could query all
			ExportImport.exportJson(contentResolver, uri, cycles, symptoms)
			Toast.makeText(this@MainActivity, "Экспорт JSON завершён", Toast.LENGTH_SHORT).show()
		}
	}
	private val openJson = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
		uri ?: return@registerForActivityResult
		lifecycleScope.launch {
			val (cycles, symptoms) = ExportImport.importJson(contentResolver, uri)
			// naive import: just insert
			cycles.forEach { viewModel.addCycle(it.startDate, it.endDate) }
			symptoms.forEach { viewModel.addSymptom(it.date, it.type, it.intensity, it.note) }
			Toast.makeText(this@MainActivity, "Импорт JSON завершён", Toast.LENGTH_SHORT).show()
		}
	}
	private val createCsv = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
		uri ?: return@registerForActivityResult
		lifecycleScope.launch {
			val symptoms = viewModel.recentSymptoms.value
			ExportImport.exportCsv(contentResolver, uri, symptoms)
			Toast.makeText(this@MainActivity, "Экспорт CSV завершён", Toast.LENGTH_SHORT).show()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			CycleTheme {
				val prefs = UserPreferences(this)
				val avgCycle by prefs.averageCycleDays.collectAsState(initial = 28)
				val avgLuteal by prefs.averageLutealDays.collectAsState(initial = 14)
				AppScaffold(
					avgCycleDays = avgCycle,
					avgLutealDays = avgLuteal,
					onSavePrefs = { c, l -> lifecycleScope.launch { prefs.setAverageCycleDays(c); prefs.setAverageLutealDays(l) } },
					onExportJson = { createJson.launch("cycletracker-backup.json") },
					onImportJson = { openJson.launch(arrayOf("application/json")) },
					onExportCsv = { createCsv.launch("symptoms.csv") },
					vm = viewModel
				)
			}
		}
	}
}

private enum class AppScreen(val route: String, val label: String) {
	Home("home", "Главная"),
	Calendar("calendar", "Календарь"),
	History("history", "История"),
	Settings("settings", "Настройки")
}

@Composable
private fun AppScaffold(
	avgCycleDays: Int,
	avgLutealDays: Int,
	onSavePrefs: (Int, Int) -> Unit,
	onExportJson: () -> Unit,
	onImportJson: () -> Unit,
	onExportCsv: () -> Unit,
	vm: CycleViewModel
) {
	val navController = rememberNavController()
	val cycles by vm.cycles.collectAsState()
	val history by vm.recentSymptoms.collectAsState()

	Scaffold(
		bottomBar = {
			NavigationBar {
				val navBackStackEntry by navController.currentBackStackEntryAsState()
				val currentDestination = navBackStackEntry?.destination
				AppScreen.entries.forEach { screen ->
					NavigationBarItem(
						selected = currentDestination.isRouteSelected(screen.route),
						onClick = {
							navController.navigate(screen.route) {
								popUpTo(navController.graph.findStartDestination().id) { saveState = true }
								launchSingleTop = true
								restoreState = true
							}
						},
						label = { Text(screen.label) },
						icon = { Icon(Icons.Filled.Circle, contentDescription = screen.label) }
					)
				}
			}
		}
	) { innerPadding ->
		NavHost(
			navController = navController,
			startDestination = AppScreen.Home.route,
			modifier = Modifier.padding(innerPadding)
		) {
			composable(AppScreen.Home.route) { HomeScreen() }
			composable(AppScreen.Calendar.route) {
				CalendarScreen(
					cycles = cycles,
					averageCycleDays = avgCycleDays,
					onDateClick = { vm.selectDate(it) }
				)
			}
			composable(AppScreen.History.route) {
				HistoryScreen(
					items = history,
					onFilterChange = { type, days -> vm.setHistoryFilter(type, days) }
				)
			}
			composable(AppScreen.Settings.route) {
				SettingsScreen(
					avgCycleDays = avgCycleDays,
					avgLutealDays = avgLutealDays,
					onSave = onSavePrefs,
					onExportJson = onExportJson,
					onImportJson = onImportJson,
					onExportCsv = onExportCsv
				)
			}
		}
	}
}

@Composable private fun HomeScreen() { Text("Добро пожаловать!") }

private fun NavDestination?.isRouteSelected(route: String): Boolean =
	this?.hierarchy?.any { it.route == route } == true
