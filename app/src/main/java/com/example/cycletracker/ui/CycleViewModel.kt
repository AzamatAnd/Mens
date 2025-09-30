package com.example.cycletracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycletracker.data.CycleEntity
import com.example.cycletracker.data.CycleRepository
import com.example.cycletracker.data.SymptomEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class CycleViewModel(
	private val repository: CycleRepository
) : ViewModel() {
	private val _selectedDate = MutableStateFlow(LocalDate.now())
	val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

	val cycles: StateFlow<List<CycleEntity>> = repository.observeCycles().stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5000),
		emptyList()
	)

	val symptomsForSelectedDate: StateFlow<List<SymptomEntity>> = _selectedDate
		.flatMapLatest { repository.observeSymptoms(it) }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	private val _historyFilter = MutableStateFlow(HistoryFilter())
	val historyFilter: StateFlow<HistoryFilter> = _historyFilter.asStateFlow()

	val recentSymptoms: StateFlow<List<SymptomEntity>> = combine(_historyFilter) { (filter) -> filter }
		.flatMapLatest { filter ->
			val to = LocalDate.now()
			val from = to.minusDays(filter.daysBack.toLong())
			repository.observeSymptoms(from, to)
		}
		.map { list ->
			list.filter { s ->
				(_historyFilter.value.type.isBlank() || s.type.contains(_historyFilter.value.type, ignoreCase = true))
			}
		}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	fun selectDate(date: LocalDate) { _selectedDate.value = date }

	fun addCycle(start: LocalDate, end: LocalDate?) {
		viewModelScope.launch {
			repository.upsertCycle(
				CycleEntity(startDate = start, endDate = end, averageLengthDays = null, averageLutealDays = null)
			)
		}
	}

	fun addSymptom(date: LocalDate, type: String, intensity: Int?, note: String?) {
		viewModelScope.launch {
			repository.upsertSymptom(
				SymptomEntity(date = date, type = type, intensity = intensity, note = note)
			)
		}
	}

	fun setHistoryFilter(type: String, daysBack: Int) {
		_historyFilter.value = _historyFilter.value.copy(type = type, daysBack = daysBack)
	}
}

data class HistoryFilter(
	val type: String = "",
	val daysBack: Int = 60
)
