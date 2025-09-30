package com.example.cycletracker.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.cycletracker.data.AppDatabase
import com.example.cycletracker.data.CycleRepository
import com.example.cycletracker.ui.CycleViewModel

object ServiceLocator {
	@Volatile private var database: AppDatabase? = null

	fun provideDatabase(context: Context): AppDatabase =
		database ?: synchronized(this) {
			database ?: Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				"cycle.db"
			).fallbackToDestructiveMigration().build().also { database = it }
		}

	fun provideRepository(context: Context): CycleRepository {
		val db = provideDatabase(context)
		return CycleRepository(db.cycleDao(), db.symptomDao())
	}

	fun provideViewModelFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
		@Suppress("UNCHECKED_CAST")
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(CycleViewModel::class.java)) {
				return CycleViewModel(provideRepository(context)) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}


