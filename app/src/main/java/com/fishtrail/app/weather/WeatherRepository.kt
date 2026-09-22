package com.fishtrail.app.weather

class WeatherRepository(
    private val apiService: WeatherApiService
) {

    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse> {

        return try {

            Result.success(
                apiService.getForecast(
                    latitude = latitude,
                    longitude = longitude
                )
            )

        } catch (exception: Exception) {

            Result.failure(exception)
        }
    }
}