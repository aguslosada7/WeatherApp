package weatherapp.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import weatherapp.data.remote.api.WeatherApi
import weatherapp.data.repository.WeatherRepositoryImpl
import weatherapp.domain.repository.WeatherRepository
import weatherapp.domain.usecase.GetWeatherByCityUseCase
import weatherapp.domain.usecase.GetWeatherByCoordsUseCase
import weatherapp.presentation.home.HomeViewModel
import weatherapp.presentation.search.SearchViewModel
import weatherapp.domain.usecase.GetForecastByCityUseCase
import weatherapp.domain.usecase.GetForecastByCoordsUseCase
import weatherapp.data.repository.FavoritesRepositoryImpl
import weatherapp.domain.repository.FavoritesRepository
import weatherapp.domain.usecase.AddFavoriteUseCase
import weatherapp.domain.usecase.GetFavoritesUseCase
import weatherapp.domain.usecase.IsFavoriteUseCase
import weatherapp.domain.usecase.RemoveFavoriteUseCase
import weatherapp.presentation.favorites.FavoritesViewModel

val sharedModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }
    single { WeatherApi(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl() }
    factory { GetWeatherByCityUseCase(get()) }
    factory { GetWeatherByCoordsUseCase(get()) }
    factory { GetForecastByCityUseCase(get()) }
    factory { GetForecastByCoordsUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { AddFavoriteUseCase(get()) }
    factory { RemoveFavoriteUseCase(get()) }
    factory { IsFavoriteUseCase(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { FavoritesViewModel(get(), get(), get(), get()) }
}