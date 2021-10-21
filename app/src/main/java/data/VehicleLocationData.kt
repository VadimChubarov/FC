package data

import com.google.gson.annotations.SerializedName

data class VehicleLocationData(@SerializedName("Longitude") val longitude: Float?,
                               @SerializedName("Latitude") val latitude: Float?,
                               @SerializedName("Distance") val distance: Float?,
                               @SerializedName("timestamp") val timestamp: String?)