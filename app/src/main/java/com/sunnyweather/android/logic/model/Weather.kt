package com.sunnyweather.android.logic.model

//服务器返回的天气信息，包含实时天气和每日天气这2个属性
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily) {
}