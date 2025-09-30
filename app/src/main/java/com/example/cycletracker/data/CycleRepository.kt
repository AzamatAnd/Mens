package com.example.cycletracker.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class CycleRepository(
	private val cycleDao: CycleDao,
	private val symptomDao: SymptomDao,
) {
	fun observeCycles(): Flow<List<CycleEntity>> = cycleDao.observeCycles()

	suspend fun upsertCycle(cycle: CycleEntity): Long = cycleDao.upsert(cycle)
	suspend fun deleteCycle(cycle: CycleEntity) = cycleDao.delete(cycle)
	suspend fun findCycleByDate(date: LocalDate): CycleEntity? = cycleDao.findCycleByDate(date)

	fun observeSymptoms(date: LocalDate): Flow<List<SymptomEntity>> = symptomDao.observeSymptomsByDate(date)
	fun observeSymptoms(from: LocalDate, to: LocalDate): Flow<List<SymptomEntity>> = symptomDao.observeSymptomsRange(from, to)
	suspend fun upsertSymptom(symptom: SymptomEntity): Long = symptomDao.upsert(symptom)
	suspend fun deleteSymptom(symptom: SymptomEntity) = symptomDao.delete(symptom)
}


