package com.example.widgets.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.widgets.R


private val WATER_COUNT_KEY = intPreferencesKey("water_count")

object WaterTrackerWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WaterTrackerContent()
            }
        }
    }
}

@Composable
fun WaterTrackerContent() {
    val prefs = currentState<Preferences>()
    val count = prefs[WATER_COUNT_KEY] ?: 0

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(GlanceTheme.colors.background)
            .cornerRadius(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title
        Text(
            text = "Water Tracker",
            style = TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Counter Row
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(12.dp)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrement Button
            Text(
                text = "−",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionRunCallback<DecrementWaterAction>())
            )

            Spacer(modifier = GlanceModifier.width(16.dp))

            // Count Display with Glass Icon
            Row(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$count",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                Image(
                    provider = ImageProvider(R.drawable.ic_water_glass),
                    contentDescription = "Water Glass",
                    modifier = GlanceModifier.size(45.dp)
                )
            }

            Spacer(modifier = GlanceModifier.width(16.dp))

            // Increment Button
            Text(
                text = "+",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionRunCallback<IncrementWaterAction>())
            )
        }
    }
}

class IncrementWaterAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[WATER_COUNT_KEY] = (this[WATER_COUNT_KEY] ?: 0) + 1
            }
        }
        WaterTrackerWidget.update(context, glanceId)
    }
}

class DecrementWaterAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                val current = prefs[WATER_COUNT_KEY] ?: 0
                this[WATER_COUNT_KEY] = maxOf(0, current - 1)
            }
        }
        WaterTrackerWidget.update(context, glanceId)
    }
}

class WaterTrackerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WaterTrackerWidget
}