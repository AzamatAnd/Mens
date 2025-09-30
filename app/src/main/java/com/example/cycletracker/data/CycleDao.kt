package com.example.cycletracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CycleDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(cycle: CycleEntity): Long

	@Query("SELECT * FROM cycles ORDER BY startDate DESC")
	fun observeCycles(): Flow<List<CycleEntity>>

	@Query("SELECT * FROM cycles WHERE :date BETWEEN startDate AND IFNULL(endDate, startDate) LIMIT 1")
	suspend fun findCycleByDate(date: LocalDate): CycleEntity?

	@Delete
	suspend fun delete(cycle: CycleEntity)
}

@Dao
interface SymptomDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(symptom: SymptomEntity): Long

	@Query("SELECT * FROM symptoms WHERE date = :date ORDER BY id DESC")
	fun observeSymptomsByDate(date: LocalDate): Flow<List<SymptomEntity>>

	@Query("SELECT * FROM symptoms WHERE date BETWEEN :from AND :to ORDER BY date DESC")
	fun observeSymptomsRange(from: LocalDate, to: LocalDate): Flow<List<SymptomEntity>>

	@Delete
	suspend fun delete(symptom: SymptomEntity)

	@Update
	suspend fun update(symptom: SymptomEntity)
}


