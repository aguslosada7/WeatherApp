package weatherapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import weatherapp.domain.model.Weather
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetWeatherByCoordsUseCaseTest {

    private val repository = mockk<WeatherRepository>()
    private val useCase = GetWeatherByCoordsUseCase(repository)

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
    fun `returns success when repository returns weather for coords`() = runTest {
        coEvery { repository.getWeatherByCoords(-34.6, -58.4) } returns Result.success(fakeWeather)

        val result = useCase(-34.6, -58.4)

        assertTrue(result.isSuccess)
        assertEquals("AR", result.getOrNull()?.country)
    }

    @Test
    fun `returns failure when repository fails for coords`() = runTest {
        coEvery { repository.getWeatherByCoords(any(), any()) } returns
                Result.failure(Exception("Error de red"))

        val result = useCase(0.0, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Error de red", result.exceptionOrNull()?.message)
    }

    @Test
    fun `calls repository with exact coordinates`() = runTest {
        coEvery { repository.getWeatherByCoords(any(), any()) } returns Result.success(fakeWeather)

        useCase(-34.6037, -58.3816)

        coVerify(exactly = 1) { repository.getWeatherByCoords(-34.6037, -58.3816) }
    }
}