package data

import com.google.gson.annotations.SerializedName

data class VehicleLocationData(@SerializedName("Longitude") val longitude: Double?,
                               @SerializedName("Latitude") val latitude: Double?,
                               @SerializedName("Distance") val distance: Double?,
                               @SerializedName("timestamp") val timestamp: String?)