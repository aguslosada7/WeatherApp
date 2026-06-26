package weatherapp.project.ui

import androidx.compose.ui.graphics.Color
import java.util.Calendar

data class WeatherColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val label: String
)

fun getTimeBasedColors(): WeatherColors {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..6 -> WeatherColors(
            backgroundTop = Color(0xFF1A1A2E),
            backgroundBottom = Color(0xFFE8956D),
            label = "Amanecer"
        )
        in 7..11 -> WeatherColors(
            backgroundTop = Color(0xFF1565C0),
            backgroundBottom = Color(0xFF42A5F5),
            label = "Mañana"
        )
        in 12..16 -> WeatherColors(
            backgroundTop = Color(0xFF0277BD),
            backgroundBottom = Color(0xFF81D4FA),
            label = "Tarde"
        )
        in 17..19 -> WeatherColors(
            backgroundTop = Color(0xFF4A148C),
            backgroundBottom = Color(0xFFE8956D),
            label = "Atardecer"
        )
        in 20..21 -> WeatherColors(
            backgroundTop = Color(0xFF1A1A2E),
            backgroundBottom = Color(0xFF16213E),
            label = "Anochecer"
        )
        else -> WeatherColors(
            backgroundTop = Color(0xFF0D0D1A),
            backgroundBottom = Color(0xFF1A1A2E),
            label = "Noche"
        )
    }
}