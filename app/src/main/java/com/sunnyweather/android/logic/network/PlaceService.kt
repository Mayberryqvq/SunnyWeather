package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    //将query参数转化为HTTP请求的查询参数，Call处理网络请求的响应，服务器返回的JSON数据会被解析成PlaceResponse对象
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>

}