package data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VehicleData(@SerializedName("objectId") val objectId: String?,
                       @SerializedName("plate") val plate: String?,
                       @SerializedName("driverName") val driverName: String?,
                       @SerializedName("address") val address: String?,
                       @SerializedName("speed") val speed: String?,
                       @SerializedName("timestamp") val timestamp: String?): Parcelable
