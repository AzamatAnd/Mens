package com.example.cycletracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import com.example.cycletracker.data.AppDatabase
import java.time.LocalDate

class CycleWidget : GlanceAppWidget() {
	override val sizeMode: SizeMode = SizeMode.Responsive(setOf())

	override suspend fun provideGlance(context: Context, id: GlanceId) {
		provideContent {
			WidgetContent(context)
		}
	}
}

@Composable
private fun WidgetContent(context: Context) {
	// naive: read last cycle and estimate next start
	val db = AppDatabaseSingleton.get(context)
	val dao = db.cycleDao()
	val last = runCatching { dao.observeCycles() }.getOrNull()
	// For simplicity in widget, show static text; full implementation would use coroutine state
	Column(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
		Text("CycleTracker", style = TextStyle(color = ColorProvider(android.graphics.Color.WHITE)))
		Text("Откройте приложение для прогноза", style = TextStyle(color = ColorProvider(android.graphics.Color.LTGRAY)))
	}
}

object AppDatabaseSingleton {
	@Volatile private var db: AppDatabase? = null
	fun get(context: Context): AppDatabase = db ?: synchronized(this) {
		db ?: AppDatabaseBuilder.build(context).also { db = it }
	}
}

object AppDatabaseBuilder {
	fun build(context: Context): AppDatabase {
		return androidx.room.Room.databaseBuilder(
			context.applicationContext,
			com.example.cycletracker.data.AppDatabase::class.java,
			"cycle.db"
		).build()
	}
}

class CycleWidgetReceiver : GlanceAppWidgetReceiver() {
	override val glanceAppWidget: GlanceAppWidget = CycleWidget()
}


