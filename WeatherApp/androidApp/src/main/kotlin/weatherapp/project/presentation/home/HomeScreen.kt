package weatherapp.project.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import weatherapp.domain.model.Weather
import weatherapp.presentation.home.HomeUiState
import weatherapp.presentation.home.HomeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSearch,
                containerColor = Color.White.copy(alpha = 0.3f)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF42A5F5))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(color = Color.White)
                is HomeUiState.Error -> ErrorContent(message = state.message)
                is HomeUiState.Success -> WeatherContent(weather = state.weather)
            }
        }
    }
}

@Composable
fun WeatherContent(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ciudad
        Text(
            text = "${weather.cityName}, ${weather.country}",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ícono del clima
        AsyncImage(
            model = weather.iconUrl,
            contentDescription = weather.description,
            modifier = Modifier.size(120.dp)
        )

        // Temperatura principal
        Text(
            text = "${weather.temperature.toInt()}°C",
            color = Color.White,
            fontSize = 80.sp,
            fontWeight = FontWeight.Thin
        )

        // Descripción
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Min / Max
        Text(
            text = "Máx: ${weather.tempMax.toInt()}°  Mín: ${weather.tempMin.toInt()}°",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de detalles
        WeatherDetailsCard(weather = weather)
    }
}

@Composable
fun WeatherDetailsCard(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherStat(label = "Humedad", value = "${weather.humidity}%")
            WeatherStatDivider()
            WeatherStat(label = "Viento", value = "${weather.windSpeed} m/s")
            WeatherStatDivider()
            WeatherStat(label = "Sensación", value = "${weather.feelsLike.toInt()}°C")
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp
        )
    }
}

@Composable
fun WeatherStatDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
fun ErrorContent(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "⚠️",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se pudo cargar el clima",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}