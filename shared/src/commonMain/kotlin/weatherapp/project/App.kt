package weatherapp.project

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import weatherapp.di.sharedModule

@Composable
fun App(content: @Composable () -> Unit = {}) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(sharedModule)
        }
    ) {
        content()
    }
}