package weatherapp.project.presentation

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
import weatherapp.presentation.HomeUiState
import weatherapp.presentation.HomeViewModel
import weatherapp.project.location.getCurrentLocation
import weatherapp.project.ui.getTimeBasedColors
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import weatherapp.domain.model.HourlyForecast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import weatherapp.domain.model.DailyForecast

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = getTimeBasedColors()

    // Animación de entrada
    var visible by remember { mutableStateOf(value = false) }
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
        val granted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
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
                is HomeUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.loadWeatherByCity("Buenos Aires") }
                )
                is HomeUiState.Success -> WeatherContent(
                    weather = state.weather,
                    hourlyForecast = state.hourlyForecast,
                    dailyForecast = state.dailyForecast,
                    isStale = state.isStale
                )
            }
        }
    }
}

@Composable
fun WeatherContent(
    weather: Weather,
    hourlyForecast: List<HourlyForecast>,
    dailyForecast: List<DailyForecast>,
    isStale: Boolean = false
) {
    val todayForecast = dailyForecast.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        if (isStale) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📡", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sin conexión · mostrando último dato guardado",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

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
            text = if (todayForecast != null) {
                "↑${todayForecast.tempMax.toInt()}°  ↓${todayForecast.tempMin.toInt()}°"
            } else {
                "↑${weather.tempMax.toInt()}°  ↓${weather.tempMin.toInt()}°"
            },
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (hourlyForecast.isNotEmpty()) {
            HourlyForecastRow(hourlyForecast)
        }

        Spacer(modifier = Modifier.height(16.dp))

        WeatherDetailsCard(weather = weather)

        Spacer(modifier = Modifier.height(16.dp))

        if (dailyForecast.isNotEmpty()) {
            DailyForecastCard(dailyForecast)
        }

        Spacer(modifier = Modifier.height(64.dp))
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
            modifier = Modifier.size(44.dp)
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
fun ErrorContent(message: String, onRetry: (() -> Unit)? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 48.sp)
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
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(24.dp))
                FilledTonalButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
fun DailyForecastCard(forecast: List<DailyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            forecast.forEach { day ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Día
                    Text(
                        text = day.dayName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(90.dp)
                    )

                    // Probabilidad de lluvia
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(52.dp)
                    ) {
                        Text("💧", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${day.rainProbability}%",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }

                    // Ícono
                    AsyncImage(
                        model = day.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Min / Max
                    Text(
                        text = "${day.tempMax.toInt()}°",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${day.tempMin.toInt()}°",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 15.sp,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        }
    }
}