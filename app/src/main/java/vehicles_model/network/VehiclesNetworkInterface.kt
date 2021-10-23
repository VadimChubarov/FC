package vehicles_model.network

import com.google.gson.annotations.SerializedName
import data.VehicleData
import data.VehicleLocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehiclesNetworkInterface {

    @GET("getLastData?json=true")
    suspend fun getVehicles(@Query("key") key: String): Response<VehiclesResponse<List<VehicleData>>>

    @GET("getRawData?json=true")
    suspend fun getVehicleLocationHistory(@Query("key") key: String,
                                          @Query("objectId") objectId: Long,
                                          @Query("begTimestamp") begTimestamp: String,
                                          @Query("endTimestamp") endTimestamp: String): Response<VehiclesResponse<List<VehicleLocationData>>>
}

data class VehiclesResponse <T>(
    @SerializedName("status") var status : Int?,
    @SerializedName("response") var response: T?)