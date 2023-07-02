package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 嵌套数据类，PlaceResponse类中有status和places这2个属性
 * Place中有名字、坐标、地址这3个属性
 * 坐标属性中又包含经度和纬度这2个属性
 **/
data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(val name: String, val location: Location, @SerializedName("formatted_address") val address: String)

data class Location(val lng: String, val lat: String)