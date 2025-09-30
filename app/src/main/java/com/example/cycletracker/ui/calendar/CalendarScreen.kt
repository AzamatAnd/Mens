package com.example.cycletracker.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cycletracker.data.CycleEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarScreen(
	cycles: List<CycleEntity>,
	averageCycleDays: Int,
	onDateClick: (LocalDate) -> Unit,
	modifier: Modifier = Modifier
) {
	var currentMonth by remember { mutableStateOf(YearMonth.now()) }
	val days = remember(currentMonth) { generateMonthDays(currentMonth) }

	val lastCycleStart = cycles.maxByOrNull { it.startDate }?.startDate
	val predictedPeriod = remember(lastCycleStart, averageCycleDays) {
		if (lastCycleStart != null) {
			val nextStart = lastCycleStart.plusDays(averageCycleDays.toLong())
			(nextStart..nextStart.plusDays(5))
		} else emptyList()
	}

	Column(modifier = modifier.padding(16.dp)) {
		Header(currentMonth = currentMonth, onPrev = { currentMonth = currentMonth.minusMonths(1) }, onNext = { currentMonth = currentMonth.plusMonths(1) })
		Spacer(Modifier.height(8.dp))
		WeekDaysRow()
		Spacer(Modifier.height(4.dp))
		AnimatedContent(
			targetState = days,
			transitionSpec = { fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200)) },
			label = "monthSwitch"
		) { daysState ->
			for (week in daysState.chunked(7)) {
				Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					for (day in week) {
						DayCell(
							date = day,
							isCurrentMonth = day?.month == currentMonth.month,
							isPredicted = day in predictedPeriod,
							onClick = { day?.let(onDateClick) },
							modifier = Modifier.weight(1f).padding(4.dp)
						)
					}
				}
			}
		}
	}
}

@Composable private fun Header(currentMonth: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
	Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
		Text("<", modifier = Modifier.clickable { onPrev() }, style = MaterialTheme.typography.titleMedium)
		Text(
			"${currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, java.util.Locale.getDefault())} ${currentMonth.year}",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
		Text(">", modifier = Modifier.clickable { onNext() }, style = MaterialTheme.typography.titleMedium)
	}
}

@Composable private fun WeekDaysRow() {
	val labels = listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс")
	Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
		labels.forEach { label ->
			Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
				Text(label, style = MaterialTheme.typography.labelMedium)
			}
		}
	}
}

@Composable private fun DayCell(
	date: LocalDate?,
	isCurrentMonth: Boolean,
	isPredicted: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	val bgColor by animateColorAsState(
		targetValue = when {
			!isCurrentMonth || date == null -> MaterialTheme.colorScheme.surface
			isPredicted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
			else -> MaterialTheme.colorScheme.surface
		},
		label = "dayBg"
	)
	Box(
		modifier = modifier
			.aspectRatio(1f)
			.clip(CircleShape)
			.background(bgColor)
			.clickable(enabled = date != null && isCurrentMonth) { onClick() },
		contentAlignment = Alignment.Center
	) {
		Text(text = date?.dayOfMonth?.toString() ?: "", color = if (isCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
	}
}

private fun generateMonthDays(month: YearMonth): List<LocalDate?> {
	val firstDay = month.atDay(1)
	val firstWeekday = (firstDay.dayOfWeek.value + 6) % 7 // make Monday=0
	val daysInMonth = month.lengthOfMonth()
	val result = mutableListOf<LocalDate?>()
	repeat(firstWeekday) { result.add(null) }
	for (d in 1..daysInMonth) result.add(month.atDay(d))
	while (result.size % 7 != 0) result.add(null)
	return result
}

private operator fun LocalDate.rangeTo(other: LocalDate): List<LocalDate> {
	val days = mutableListOf<LocalDate>()
	var d = this
	while (!d.isAfter(other)) {
		days.add(d)
		d = d.plusDays(1)
	}
	return days
}
