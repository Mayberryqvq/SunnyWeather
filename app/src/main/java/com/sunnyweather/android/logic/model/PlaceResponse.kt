package com.sunnyweather.android.logic.model

import android.location.Address
import android.provider.ContactsContract.StatusUpdates
import com.google.gson.annotations.SerializedName
import kotlin.contracts.CallsInPlace

data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(val name: String, val location: Location, @SerializedName("formatted_address") val address: String)

data class Location(val lng: String, val lat: String)