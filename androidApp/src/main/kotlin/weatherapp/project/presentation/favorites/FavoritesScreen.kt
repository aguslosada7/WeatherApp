package weatherapp.project.presentation.favorites

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import weatherapp.domain.model.FavoriteCity
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.GetWeatherByCityUseCase
import weatherapp.presentation.favorites.ExpandedWeatherState
import weatherapp.presentation.favorites.FavoritesUiState
import weatherapp.presentation.favorites.FavoritesViewModel
import weatherapp.project.ui.getTimeBasedColors

@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
    getWeatherByCity: GetWeatherByCityUseCase = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCityId by viewModel.selectedCityId.collectAsStateWithLifecycle()
    val expandedWeather by viewModel.expandedWeather.collectAsStateWithLifecycle()
    val colors = getTimeBasedColors()

    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(colors.backgroundTop, colors.backgroundBottom)
            ))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    text = "Ciudades favoritas",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is FavoritesUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color.White) }

                is FavoritesUiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = Color.White) }

                is FavoritesUiState.Success -> {
                    if (state.favorites.isEmpty()) {
                        EmptyFavoritesContent()
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.favorites, key = { it.id }) { city ->
                                ExpandableFavoriteCityItem(
                                    city = city,
                                    isExpanded = selectedCityId == city.id,
                                    weatherState = expandedWeather[city.id],
                                    onSelect = {
                                        viewModel.selectCity(city) { getWeatherByCity(it) }
                                    },
                                    onDelete = { viewModel.removeById(city.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableFavoriteCityItem(
    city: FavoriteCity,
    isExpanded: Boolean,
    weatherState: ExpandedWeatherState?,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
            // Header siempre visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(city.cityName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text(city.country, color = Color.White.copy(alpha = 0.65f), fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) "▲" else "▼",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            // Contenido expandido con animación
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
            ) {
                when (weatherState) {
                    null, is ExpandedWeatherState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) }
                    }
                    is ExpandedWeatherState.Error -> {
                        Text(
                            "No se pudo cargar el clima",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    is ExpandedWeatherState.Success -> {
                        ExpandedWeatherContent(weather = weatherState.weather)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedWeatherContent(weather: Weather) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = weather.iconUrl,
                contentDescription = weather.description,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${weather.temperature.toInt()}°C",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Thin
            )
        }
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 15.sp
        )
        Text(
            text = "↑${weather.tempMax.toInt()}°  ↓${weather.tempMin.toInt()}°",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${weather.humidity}%", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Humedad", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${weather.windSpeed}m/s", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Viento", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${weather.feelsLike.toInt()}°C", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Sensación", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun EmptyFavoritesContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⭐", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No tenés ciudades favoritas", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Buscá una ciudad y agregála desde ahí", color = Color.White.copy(alpha = 0.65f), fontSize = 14.sp)
        }
    }
}