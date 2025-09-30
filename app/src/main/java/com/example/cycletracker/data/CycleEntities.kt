package com.example.cycletracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "cycles")
data class CycleEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val startDate: LocalDate,
	val endDate: LocalDate?,
	val averageLengthDays: Int?,
	val averageLutealDays: Int?
)

@Entity(tableName = "symptoms")
data class SymptomEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val date: LocalDate,
	val type: String,
	val intensity: Int?,
	val note: String?
)


