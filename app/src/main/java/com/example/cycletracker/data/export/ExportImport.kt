package com.example.cycletracker.data.export

import android.content.ContentResolver
import android.net.Uri
import com.example.cycletracker.data.CycleEntity
import com.example.cycletracker.data.SymptomEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object ExportImport {
	fun exportJson(resolver: ContentResolver, uri: Uri, cycles: List<CycleEntity>, symptoms: List<SymptomEntity>) {
		val root = JSONObject().apply {
			put("version", 1)
			put("cycles", JSONArray().apply {
				cycles.forEach { c -> put(JSONObject().apply {
					put("id", c.id)
					put("startDate", c.startDate.toString())
					put("endDate", c.endDate?.toString())
					put("averageLengthDays", c.averageLengthDays)
					put("averageLutealDays", c.averageLutealDays)
				}) }
			})
			put("symptoms", JSONArray().apply {
				symptoms.forEach { s -> put(JSONObject().apply {
					put("id", s.id)
					put("date", s.date.toString())
					put("type", s.type)
					put("intensity", s.intensity)
					put("note", s.note)
				}) }
			})
		}
		resolver.openOutputStream(uri)?.use { os ->
			OutputStreamWriter(os).use { it.write(root.toString()) }
		}
	}

	fun importJson(resolver: ContentResolver, uri: Uri): Pair<List<CycleEntity>, List<SymptomEntity>> {
		val text = resolver.openInputStream(uri)?.use { input ->
			BufferedReader(InputStreamReader(input)).readText()
		} ?: return emptyList<CycleEntity>() to emptyList()
		val root = JSONObject(text)
		val cycles = mutableListOf<CycleEntity>()
		val symptoms = mutableListOf<SymptomEntity>()
		root.optJSONArray("cycles")?.let { arr ->
			for (i in 0 until arr.length()) {
				val o = arr.getJSONObject(i)
				cycles.add(
					CycleEntity(
						id = 0,
						startDate = java.time.LocalDate.parse(o.getString("startDate")),
						endDate = o.optString("endDate").takeIf { it.isNotBlank() && it != "null" }?.let(java.time.LocalDate::parse),
						averageLengthDays = o.optInt("averageLengthDays").takeIf { o.has("averageLengthDays") },
						averageLutealDays = o.optInt("averageLutealDays").takeIf { o.has("averageLutealDays") }
					)
				)
			}
		}
		root.optJSONArray("symptoms")?.let { arr ->
			for (i in 0 until arr.length()) {
				val o = arr.getJSONObject(i)
				symptoms.add(
					SymptomEntity(
						id = 0,
						date = java.time.LocalDate.parse(o.getString("date")),
						type = o.getString("type"),
						intensity = if (o.has("intensity")) o.optInt("intensity") else null,
						note = o.optString("note").takeIf { it.isNotBlank() }
					)
				)
			}
		}
		return cycles to symptoms
	}

	fun exportCsv(resolver: ContentResolver, uri: Uri, symptoms: List<SymptomEntity>) {
		resolver.openOutputStream(uri)?.use { os ->
			OutputStreamWriter(os).use { w ->
				w.appendLine("date,type,intensity,note")
				symptoms.forEach { s ->
					w.appendLine("${s.date},${s.type},${s.intensity ?: ""},${s.note?.replace(",", ";") ?: ""}")
				}
			}
		}
	}
}


