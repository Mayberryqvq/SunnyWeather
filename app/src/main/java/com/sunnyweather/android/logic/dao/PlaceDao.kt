package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

/**
 * Dao，数据访问对象，属于模型层（Model），主要负责与数据源进行交互，
 * 提供对数据的访问和操作方法
 *
 * 由于要存储的数据并不属于关系型数据，因此用不着使用数据库存储技术
 **/
object PlaceDao {

    //将地点存储在本地
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            //调用Gson库，将Place对象转化为Json字符串
            putString("place", Gson().toJson(place))
        }
    }

    //从本地读取保存地点
    fun getSavedPlace(): Place {
        //若取不到，则用空字符串作为默认值
        val placeJson = sharedPreferences().getString("place", "")
        //调用Gson库，将Json字符串转化为Place对象
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}