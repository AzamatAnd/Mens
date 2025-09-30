package com.example.cycletracker.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class UserPreferences(private val context: Context) {
	private object Keys {
		val avgCycleDays: Preferences.Key<Int> = intPreferencesKey("avg_cycle_days")
		val avgLutealDays: Preferences.Key<Int> = intPreferencesKey("avg_luteal_days")
	}

	val averageCycleDays: Flow<Int> = context.dataStore.data.map { it[Keys.avgCycleDays] ?: 28 }
	val averageLutealDays: Flow<Int> = context.dataStore.data.map { it[Keys.avgLutealDays] ?: 14 }

	suspend fun setAverageCycleDays(days: Int) {
		context.dataStore.edit { it[Keys.avgCycleDays] = days }
	}

	suspend fun setAverageLutealDays(days: Int) {
		context.dataStore.edit { it[Keys.avgLutealDays] = days }
	}
}


