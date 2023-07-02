package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//用于访问彩云天气API的Retrofit接口
interface WeatherService {
    //获取实时天气信息
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RealtimeResponse>

    //获取未来天气信息
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily?dailysteps=5")
    //使用@Path注解来向请求接口动态传入经纬度的坐标
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>

}