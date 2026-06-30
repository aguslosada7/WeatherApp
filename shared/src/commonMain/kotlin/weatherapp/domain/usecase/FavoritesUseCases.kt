package weatherapp.domain.usecase

import weatherapp.domain.model.FavoriteCity

class GetFavoritesUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(): Result<List<FavoriteCity>> = repository.getFavorites()
}

class AddFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(city: FavoriteCity): Result<Unit> = repository.addFavorite(city)
}

class RemoveFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.removeFavorite(id)
}

class IsFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(cityName: String): Result<Boolean> = repository.isFavorite(cityName)
}