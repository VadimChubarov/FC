package data

import com.google.gson.annotations.SerializedName

data class VehicleData(@SerializedName("objectId") val objectId: String?,
                       @SerializedName("plate") val plate: String?,
                       @SerializedName("driverName") val driverName: String?,
                       @SerializedName("address") val address: String?,
                       @SerializedName("speed") val speed: String?,
                       @SerializedName("timestamp") val timestamp: String?)
