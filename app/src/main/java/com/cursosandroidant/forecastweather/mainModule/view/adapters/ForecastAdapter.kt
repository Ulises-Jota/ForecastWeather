package com.cursosandroidant.forecastweather.mainModule.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cursosandroidant.forecastweather.R
import com.cursosandroidant.forecastweather.common.CommonUtils
import com.cursosandroidant.forecastweather.databinding.ItemForecastWeatherBinding
import com.cursosandroidant.forecastweather.entities.Forecast

class ForecastAdapter(private val listener: OnClickListener) :
    ListAdapter<Forecast, RecyclerView.ViewHolder>(ForecastDiffCallback()) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_forecast_weather,
            parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val forecast = getItem(position)

        with(holder as ViewHolder){
            setListener(forecast)

            binding.tvTemp.text = context.getString(R.string.weather_temp, forecast.temp)
            binding.tvDt.text = CommonUtils.getHour(forecast.dt)
            binding.tvHumidity.text = context.getString(R.string.weather_humidity, forecast.humidity)
            binding.tvPop.text = context.getString(R.string.weather_pop, forecast.pop)
            binding.tvMain.text = CommonUtils.getWeatherMain(forecast.weather)
            binding.tvDescription.text = CommonUtils.getWeatherDescription(forecast.weather)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemForecastWeatherBinding.bind(view)

        fun setListener(forecast: Forecast){
            binding.root.setOnClickListener { listener.onClick(forecast) }
        }
    }

    class ForecastDiffCallback: DiffUtil.ItemCallback<Forecast>() {
        override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean = oldItem.dt == newItem.dt

        override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean = oldItem == newItem
    }
}