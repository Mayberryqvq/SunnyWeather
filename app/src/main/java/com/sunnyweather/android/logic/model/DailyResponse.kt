package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 在 Kotlin 的数据类中，当在主构造函数中声明属性时，这些属性会自动成为数据类的属性
 *
 * 嵌套数据类，RealtimeResponse数据类中包含status属性和result属性
 * result属性中又包含daily属性
 * daily属性又包含天气图标、温度、生活指数这3个属性
 * 温度属性又包含最大值和最小值这2个属性
 * 天气图标又包含日期和值这2个属性
 * 生活指数又包含寒冷指数、洗车指数、紫外线强度、穿衣指数这4个属性
 * 生活指数描述是最底层的数据类，是一串字符串
 **/

data class DailyResponse(val status: String, val result: Result) {
    //RealtimeResponse数据类中也有一个Result类，不过由于都是定义在数据类内部，所以不会发生冲突
    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>, @SerializedName("life_index") val lifeIndex : LifeIndex)

    data class Temperature(val max: Float, val min: Float)

    data class Skycon(val value: String, val date: Date)

    data class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)

    data class LifeDescription(val desc: String)

}