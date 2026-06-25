package weatherapp.project

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import weatherapp.di.sharedModule

@Composable
fun App() {
    KoinApplication(application = {
        modules(sharedModule)
    }) {
        // la pantalla de inicio va acá
        WeatherAppContent()
    }
}

@Composable
fun WeatherAppContent() {
    // placeholder por ahora
    androidx.compose.material3.MaterialTheme {
        androidx.compose.material3.Text("WeatherApp cargando...")
    }
}