package weatherapp.data.repository

import io.github.jan.supabase.postgrest.from
import weatherapp.data.local.dto.FavoriteCityDto
import weatherapp.data.local.dto.toDto
import weatherapp.data.local.dto.toDomain
import weatherapp.data.local.supabaseClient
import weatherapp.domain.model.FavoriteCity
import weatherapp.domain.repository.FavoritesRepository

class FavoritesRepositoryImpl : FavoritesRepository {

    override suspend fun getFavorites(): Result<List<FavoriteCity>> = runCatching {
        supabaseClient.from("favorite_cities")
            .select()
            .decodeList<FavoriteCityDto>()
            .map { it.toDomain() }
    }

    override suspend fun addFavorite(city: FavoriteCity): Result<Unit> = runCatching {
        supabaseClient.from("favorite_cities")
            .insert(city.toDto())
    }

    override suspend fun removeFavorite(id: String): Result<Unit> = runCatching {
        supabaseClient.from("favorite_cities")
            .delete { filter { eq("id", id) } }
    }

    override suspend fun isFavorite(cityName: String): Result<Boolean> = runCatching {
        supabaseClient.from("favorite_cities")
            .select { filter { eq("city_name", cityName) } }
            .decodeList<FavoriteCityDto>()
            .isNotEmpty()
    }
}