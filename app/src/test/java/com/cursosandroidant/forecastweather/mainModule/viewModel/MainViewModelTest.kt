package com.cursosandroidant.forecastweather.mainModule.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cursosandroidant.forecastweather.MainCoroutineRule
import com.cursosandroidant.forecastweather.common.dataAccess.JSONFileLoader
import com.cursosandroidant.forecastweather.common.dataAccess.WeatherForecastService
import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.cursosandroidant.historicalweatherref.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModelTest{
    @get:Rule
    val instantExcecutorRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutinesRule = MainCoroutineRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var service: WeatherForecastService

    companion object{
        private lateinit var retrofit: Retrofit

        @BeforeClass
        @JvmStatic
        fun setupCommon(){
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    @Before
    fun setup(){
        mainViewModel = MainViewModel()
        service = retrofit.create(WeatherForecastService::class.java)
    }

    @Test
    fun checkCurrentWeatherIsNotNullTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(19.4342, -99.1962,
                "", "metric", "en")
            assertThat(result.current, `is`(notNullValue()))
        }
    }

    @Test
    fun checkTimezoneReturnsMexicoCityTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(19.4342, -99.1962,
                "", "metric", "en")
            assertThat(result.timezone, `is`("America/Mexico_City"))
        }
    }

    @Test
    fun checkErrorResponseWithOnlyCoordinatesTes(){
        runBlocking {
            try {
                service.getWeatherForecastByCoordinates(19.4342, -99.1962,
                    "", "", "")
            } catch (e: Exception) {
                assertThat(e.localizedMessage, `is`("HTTP 401 Unauthorized"))
            }
        }
    }

    @Test
    fun checkHourlySizeTest(){
        runBlocking {
            mainViewModel.getWeatherAndForecast(19.4342, -99.1962,
                "", "metric", "en")
            val result = mainViewModel.getResult().getOrAwaitValue()
            assertThat(result.hourly.size, `is`(48))
        }
    }

    @Test
    fun checkHourlySizeRemoteWithLocalTest(){
        runBlocking {
            val remoteResult = service.getWeatherForecastByCoordinates(19.4342, -99.1962,
                "", "metric", "en")

            val localResult = JSONFileLoader().loadWeatherForecastEntity("weather_forecast_response_success")

            assertThat(localResult?.hourly?.size, `is`(remoteResult.hourly.size))
            assertThat(localResult?.timezone, `is`(remoteResult.timezone))
        }
    }
}