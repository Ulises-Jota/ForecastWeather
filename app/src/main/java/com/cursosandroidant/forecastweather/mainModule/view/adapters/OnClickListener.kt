package com.cursosandroidant.forecastweather.mainModule.view.adapters

import com.cursosandroidant.forecastweather.entities.Forecast

interface OnClickListener {
    fun onClick(forecast: Forecast)
}