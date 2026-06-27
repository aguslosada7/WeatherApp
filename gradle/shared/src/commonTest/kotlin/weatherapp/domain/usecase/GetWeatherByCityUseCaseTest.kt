package weatherapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import weatherapp.domain.model.Weather
import weatherapp.domain.repository.WeatherRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetWeatherByCityUseCaseTest {

    private val repository = mockk<WeatherRepository>()
    private val useCase = GetWeatherByCityUseCase(repository)

    private val fakeWeather = Weather(
        cityName = "Buenos Aires",
        country = "AR",
        temperature = 22.0,
        feelsLike = 21.0,
        humidity = 65,
        description = "cielo despejado",
        iconCode = "01d",
        windSpeed = 3.5,
        tempMin = 18.0,
        tempMax = 25.0
    )

    @Test
    fun `returns success when repository returns weather`() = runTest {
        coEvery { repository.getWeatherByCity("Buenos Aires") } returns Result.success(fakeWeather)

        val result = useCase("Buenos Aires")

        assertTrue(result.isSuccess)
        assertEquals("Buenos Aires", result.getOrNull()?.cityName)
    }

    @Test
    fun `returns failure when repository throws exception`() = runTest {
        coEvery { repository.getWeatherByCity("Ciudad Inexistente") } returns
                Result.failure(Exception("Ciudad no encontrada"))

        val result = useCase("Ciudad Inexistente")

        assertTrue(result.isFailure)
        assertEquals("Ciudad no encontrada", result.exceptionOrNull()?.message)
    }

    @Test
    fun `calls repository with correct city name`() = runTest {
        coEvery { repository.getWeatherByCity(any()) } returns Result.success(fakeWeather)

        useCase("Córdoba")

        coVerify(exactly = 1) { repository.getWeatherByCity("Córdoba") }
    }
}