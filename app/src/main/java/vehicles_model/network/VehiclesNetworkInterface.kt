package vehicles_model.network

import com.google.gson.annotations.SerializedName
import data.VehicleData
import data.VehicleLocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface VehiclesNetworkInterface {

    companion object {
        const val VEHICLE_LOCATION_TAG = "vehicle_location"
    }

    @GET("getLastData")
    suspend fun getVehicles(): Response<VehiclesResponse<List<VehicleData>>>

    @GET("getRawData")
    suspend fun getVehicleLocationHistory(@Header("tag") tag: String,
                                          @Query("objectId") objectId: String,
                                          @Query("begTimestamp") begTimestamp: String,
                                          @Query("endTimestamp") endTimestamp: String): Response<VehiclesResponse<List<VehicleLocationData>>>
}

data class VehiclesResponse <T> (
    @SerializedName("status") var status : Int?,
    @SerializedName("response") var response: T?)