package com.example.cycletracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate

@Database(
	entities = [CycleEntity::class, SymptomEntity::class],
	version = 1,
	exportSchema = true
)
@TypeConverters(LocalDateConverters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun cycleDao(): CycleDao
	abstract fun symptomDao(): SymptomDao
}

class LocalDateConverters {
	@TypeConverter
	fun fromEpochDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

	@TypeConverter
	fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}


