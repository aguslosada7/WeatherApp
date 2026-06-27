package weatherapp.data.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import weatherapp.domain.model.FavoriteCity

@Serializable
data class FavoriteCityDto(
    val id: String = "",
    @SerialName("city_name") val cityName: String,
    val country: String
)

fun FavoriteCityDto.toDomain() = FavoriteCity(
    id = id,
    cityName = cityName,
    country = country
)

fun FavoriteCity.toDto() = FavoriteCityDto(
    cityName = cityName,
    country = country
)