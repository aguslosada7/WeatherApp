package weatherapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import weatherapp.domain.model.FavoriteCity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoritesUseCasesTest {

    private val repository = mockk<FavoritesRepository>()

    private val fakeCity = FavoriteCity(
        id = "abc-123",
        cityName = "Madrid",
        country = "ES",
    )

    // GetFavoritesUseCase
    @Test
    fun `GetFavorites returns list when successful`() = runTest {
        val useCase = GetFavoritesUseCase(repository)
        coEvery { repository.getFavorites() } returns Result.success(listOf(fakeCity))

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Madrid", result.getOrNull()?.first()?.cityName)
    }

    @Test
    fun `GetFavorites returns empty list when no favorites`() = runTest {
        val useCase = GetFavoritesUseCase(repository)
        coEvery { repository.getFavorites() } returns Result.success(emptyList())

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expected = true, actual = result.getOrNull()?.isEmpty())
    }

    // AddFavoriteUseCase
    @Test
    fun `AddFavorite returns success when added correctly`() = runTest {
        val useCase = AddFavoriteUseCase(repository)
        coEvery { repository.addFavorite(fakeCity) } returns Result.success(Unit)

        val result = useCase(fakeCity)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addFavorite(fakeCity) }
    }

    @Test
    fun `AddFavorite returns failure when repository fails`() = runTest {
        val useCase = AddFavoriteUseCase(repository)
        coEvery { repository.addFavorite(any()) } returns Result.failure(Exception("Error al guardar"))

        val result = useCase(fakeCity)

        assertTrue(result.isFailure)
    }

    // RemoveFavoriteUseCase
    @Test
    fun `RemoveFavorite calls repository with correct id`() = runTest {
        val useCase = RemoveFavoriteUseCase(repository)
        coEvery { repository.removeFavorite("abc-123") } returns Result.success(Unit)

        val result = useCase("abc-123")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.removeFavorite("abc-123") }
    }

    // IsFavoriteUseCase
    @Test
    fun `IsFavorite returns true when city is saved`() = runTest {
        val useCase = IsFavoriteUseCase(repository)
        coEvery { repository.isFavorite("Madrid") } returns Result.success(value = true)

        val result = useCase("Madrid")

        assertTrue(result.isSuccess)
        assertEquals(expected = true, actual = result.getOrNull())
    }

    @Test
    fun `IsFavorite returns false when city is not saved`() = runTest {
        val useCase = IsFavoriteUseCase(repository)
        coEvery { repository.isFavorite("Tokyo") } returns Result.success(value = false)

        val result = useCase("Tokyo")

        assertTrue(result.isSuccess)
        assertEquals(expected = false, actual = result.getOrNull())
    }
}
