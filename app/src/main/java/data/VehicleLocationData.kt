package data

import com.google.gson.annotations.SerializedName

data class VehicleLocationData(@SerializedName("Longitude") val longitude: Float? = null,
                               @SerializedName("Latitude") val latitude: Float? = null,
                               @SerializedName("Distance") val distance: Float? = null,
                               var timestamp: String? = null)