package weatherapp.project.presentation.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import weatherapp.domain.model.Weather
import weatherapp.presentation.home.HomeUiState
import weatherapp.presentation.home.HomeViewModel
import weatherapp.project.location.getCurrentLocation
import weatherapp.project.ui.getTimeBasedColors
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import weatherapp.domain.model.HourlyForecast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = getTimeBasedColors()

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "fade"
    )
    LaunchedEffect(Unit) { visible = true }

    // Pedir permiso y cargar ubicación
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            scope.launch {
                val loc = getCurrentLocation(context)
                if (loc != null) viewModel.loadWeatherByCoords(loc.lat, loc.lon)
                else viewModel.loadWeatherByCity("Buenos Aires")
            }
        } else {
            viewModel.loadWeatherByCity("Buenos Aires")
        }
    }

    LaunchedEffect(Unit) {
        locationLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(colors.backgroundTop, colors.backgroundBottom)
            ))
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Barra superior con búsqueda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = colors.label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                FilledTonalButton(
                    onClick = onNavigateToSearch,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buscar ciudad")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color.White) }
                is HomeUiState.Error -> ErrorContent(message = state.message)
                is HomeUiState.Success -> WeatherContent(
                    weather = state.weather,
                    hourlyForecast = state.hourlyForecast
                )
            }
        }
    }
}

@Composable
fun WeatherContent(weather: Weather, hourlyForecast: List<HourlyForecast>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), // permite scroll si no entra
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${weather.cityName}, ${weather.country}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = weather.iconUrl,
            contentDescription = weather.description,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "${weather.temperature.toInt()}°",
            color = Color.White,
            fontSize = 96.sp,
            fontWeight = FontWeight.Thin,
            lineHeight = 96.sp
        )

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "↑${weather.tempMax.toInt()}°  ↓${weather.tempMin.toInt()}°",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (hourlyForecast.isNotEmpty()) {
            HourlyForecastRow(hourlyForecast)
        }

        Spacer(modifier = Modifier.height(16.dp))

        WeatherDetailsCard(weather = weather)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HourlyForecastRow(forecast: List<HourlyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(forecast) { index, item ->
                HourlyForecastItem(item = item, isFirst = index == 0)
            }
        }
    }
}

@Composable
fun HourlyForecastItem(item: HourlyForecast, isFirst: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = if (isFirst) "Ahora" else item.hour,
            color = Color.White.copy(alpha = if (isFirst) 1f else 0.7f),
            fontSize = 13.sp,
            fontWeight = if (isFirst) FontWeight.SemiBold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(6.dp))
        AsyncImage(
            model = item.iconUrl,
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${item.temperature.toInt()}°",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeatherDetailsCard(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherStat(label = "Humedad", value = "${weather.humidity}%")
            WeatherStatDivider()
            WeatherStat(label = "Viento", value = "${weather.windSpeed}m/s")
            WeatherStatDivider()
            WeatherStat(label = "Sensación", value = "${weather.feelsLike.toInt()}°")
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp)
    }
}

@Composable
fun WeatherStatDivider() {
    Box(
        modifier = Modifier
            .height(36.dp).width(1.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(Color.White.copy(alpha = 0.25f))
    )
}

@Composable
fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No se pudo cargar el clima", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}