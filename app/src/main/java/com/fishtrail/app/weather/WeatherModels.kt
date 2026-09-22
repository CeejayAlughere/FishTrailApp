package com.fishtrail.app.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("current")
    val current: CurrentWeather?,

    @SerializedName("daily")
    val daily: DailyWeather?
)

data class CurrentWeather(

    @SerializedName("time")
    val time: String,

    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,

    @SerializedName("relative_humidity_2m")
    val relativeHumidity: Double,

    @SerializedName("precipitation")
    val precipitation: Double,

    @SerializedName("weather_code")
    val weatherCode: Int,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double,

    @SerializedName("wind_direction_10m")
    val windDirection: Double
)

data class DailyWeather(

    @SerializedName("time")
    val time: List<String>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,

    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,

    @SerializedName("precipitation_probability_max")
    val precipitationProbability: List<Double>,

    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>,

    @SerializedName("wind_speed_10m_max")
    val windSpeedMax: List<Double>,

    @SerializedName("sunrise")
    val sunrise: List<String>,

    @SerializedName("sunset")
    val sunset: List<String>
)