package com.cursosandroidant.forecastweather.mainModule.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cursosandroidant.forecastweather.R
import com.cursosandroidant.forecastweather.common.CommonUtils
import com.cursosandroidant.forecastweather.common.dataAccess.WeatherForecastService
import com.cursosandroidant.forecastweather.databinding.ActivityMainBinding
import com.cursosandroidant.forecastweather.entities.Forecast
import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.cursosandroidant.forecastweather.mainModule.view.adapters.ForecastAdapter
import com.cursosandroidant.forecastweather.mainModule.view.adapters.OnClickListener
import com.cursosandroidant.forecastweather.mainModule.viewModel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ForecastAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupAdapter() {
        adapter = ForecastAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        setupData()
    }

    private fun setupData() {
        lifecycleScope.launch {
            mainViewModel.getWeatherAndForecast(
                19.4342, -99.1962,
                "", "metric", "en"
            )
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.getResult().observe(this) { result ->
            setupUI(result)
        }
    }

    private fun setupUI(data: WeatherForecastEntity) {
        with(binding) {
            tvTimeZone.text = data.timezone.replace("_", " ")
            current.tvTemp.text = getString(R.string.weather_temp, data.current.temp)
            current.tvDt.text = CommonUtils.getFullDate(data.current.dt)
            current.tvHumidity.text = getString(R.string.weather_humidity, data.current.humidity)
            current.tvMain.text = CommonUtils.getWeatherMain(data.current.weather)
            current.tvDescription.text = CommonUtils.getWeatherDescription(data.current.weather)
        }
        adapter.submitList(data.hourly)
    }

    //https://openweathermap.org/api/one-call-api#current
    private suspend fun getHistoricalWeather(): WeatherForecastEntity =
        withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            // TODO: get appId
            val service = retrofit.create(WeatherForecastService::class.java)
            service.getWeatherForecastByCoordinates(
                19.4342, -99.1962, "",
                "metric", "en"
            )
        }

    override fun onClick(forecast: Forecast) {
        Snackbar.make(binding.root, CommonUtils.getFullDate(forecast.dt), Snackbar.LENGTH_LONG)
            .show()
    }
}
