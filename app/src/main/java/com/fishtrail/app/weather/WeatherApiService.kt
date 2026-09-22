package com.fishtrail.app.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m," +
                    "apparent_temperature," +
                    "relative_humidity_2m," +
                    "precipitation," +
                    "weather_code," +
                    "wind_speed_10m," +
                    "wind_direction_10m",

        @Query("daily")
        daily: String =
            "weather_code," +
                    "temperature_2m_max," +
                    "temperature_2m_min," +
                    "precipitation_probability_max," +
                    "precipitation_sum," +
                    "wind_speed_10m_max," +
                    "sunrise," +
                    "sunset",

        @Query("forecast_days")
        forecastDays: Int = 7,

        @Query("timezone")
        timezone: String = "auto",

        @Query("temperature_unit")
        temperatureUnit: String = "celsius",

        @Query("wind_speed_unit")
        windSpeedUnit: String = "kmh"
    ): WeatherResponse

    companion object {

        fun create(): WeatherApiService {

            return Retrofit.Builder()
                .baseUrl(
                    "https://api.open-meteo.com/"
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}