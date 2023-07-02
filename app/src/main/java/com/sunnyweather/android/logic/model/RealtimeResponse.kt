package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 在 Kotlin 的数据类中，当在主构造函数中声明属性时，这些属性会自动成为数据类的属性
 *
 * 将所有数据类都定义在RealtimeResponse内部，就可以防止出现和其他接口的数据类同名发生冲突的情况
 *
 * 嵌套数据类，RealtimeResponse数据类中包含status属性和result属性
 * result属性中又包含realtime属性
 * realtime属性又包含天气图标、温度、空气质量这3个属性
 * 空气质量属性又包含aqi这个属性
 * aqi是最底层的数据类，是一个浮点数
 **/

data class RealtimeResponse(val status: String, val result: Result) {

    data class Result(val realtime: Realtime)

    data class Realtime(val skycon: String, val temperature: Float, @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)

}