package weatherapp.project.ui

import androidx.compose.ui.graphics.Color
import java.util.Calendar

data class WeatherColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val label: String,
)

fun getTimeBasedColors(): WeatherColors {
    return when (Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
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

fun getIconTint(iconCode: String): Color = when {
    iconCode.endsWith("n") -> when {
        iconCode.startsWith("01") -> Color(0xFFB3C5E8) // luna despejada — azul pálido
        iconCode.startsWith("02") -> Color(0xFF90A4AE) // luna con nubes — gris azulado
        else -> Color(0xFFB3C5E8) // cualquier otro nocturno
    }
    iconCode.startsWith("01") -> Color(0xFFFFBF00) // sol despejado — dorado
    iconCode.startsWith("02") || iconCode.startsWith("03") -> Color(0xFFB0BEC5) // nubes — gris claro
    iconCode.startsWith("09") || iconCode.startsWith("10") -> Color(0xFF90CAF9) // lluvia — azul claro
    iconCode.startsWith("11") -> Color(0xFFCE93D8) // tormenta — violeta
    iconCode.startsWith("13") -> Color(0xFFE3F2FD) // nieve — blanco azulado
    else -> Color.White
}