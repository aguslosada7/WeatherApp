package weatherapp.project.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import weatherapp.domain.model.Weather
import weatherapp.presentation.SearchUiState
import weatherapp.presentation.SearchViewModel
import weatherapp.domain.model.FavoriteCity
import weatherapp.presentation.FavoritesViewModel
import weatherapp.project.ui.getTimeBasedColors

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val isFavorite by favoritesViewModel.isFavoriteState.collectAsStateWithLifecycle()

    val colors = getTimeBasedColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.backgroundTop, colors.backgroundBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Barra superior
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                IconButton(
                    onClick = {
                        viewModel.reset()
                        onNavigateBack()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    text = "Buscar ciudad",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                IconButton(onClick = onNavigateToFavorites) {
                    Text("⭐", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de búsqueda
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = {
                    Text("Ej: Madrid, Tokyo, New York", color = Color.White.copy(alpha = 0.6f))
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::search) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Resultado
            when (val state = uiState) {
                is SearchUiState.Idle -> SearchIdleContent()
                is SearchUiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
                is SearchUiState.Error -> SearchErrorContent(message = state.message)
                is SearchUiState.Success -> {
                    LaunchedEffect(state.weather.cityName) {
                        favoritesViewModel.checkIsFavorite(state.weather.cityName)
                    }
                    SearchResultContent(
                        weather = state.weather,
                        isFavorite = isFavorite,
                    ) {
                        favoritesViewModel.toggleFavorite(
                            FavoriteCity(
                                cityName = state.weather.cityName,
                                country = state.weather.country
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchIdleContent() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Buscá una ciudad para ver su clima",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SearchErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌐", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verificá el nombre e intentá de nuevo",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SearchResultContent(
    weather: Weather,
    isFavorite: Boolean,
    isStale: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isStale) {
                Text(
                    text = "📡 Sin conexión · último dato guardado",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${weather.cityName}, ${weather.country}",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onToggleFavorite) {
                    Text(
                        text = if (isFavorite) "⭐" else "☆",
                        fontSize = 24.sp
                    )
                }
            }
            AsyncImage(
                model = weather.iconUrl,
                contentDescription = weather.description,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "${weather.temperature.toInt()}°C",
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${weather.humidity}%", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Humedad", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${weather.windSpeed} m/s", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Viento", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${weather.feelsLike.toInt()}°C", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Sensación", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            }
        }
    }
}
