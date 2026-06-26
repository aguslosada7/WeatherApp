package weatherapp.domain.repository

import weatherapp.domain.model.FavoriteCity

interface FavoritesRepository {
    suspend fun getFavorites(): Result<List<FavoriteCity>>
    suspend fun addFavorite(city: FavoriteCity): Result<Unit>
    suspend fun removeFavorite(id: String): Result<Unit>
    suspend fun isFavorite(cityName: String): Result<Boolean>
}