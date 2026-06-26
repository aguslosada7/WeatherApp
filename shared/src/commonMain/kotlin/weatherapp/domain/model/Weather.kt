package weatherapp.domain.model

data class Weather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val iconCode: String,
    val windSpeed: Double,
    val tempMin: Double,
    val tempMax: Double
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/$iconCode@2x.png"
}

data class HourlyForecast(
    val hour: String,
    val temperature: Double,
    val iconCode: String
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/$iconCode@2x.png"
}