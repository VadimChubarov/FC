package vehicles_model.network

import data.VehicleData
import data.VehicleLocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehiclesNetworkInterface {

    @GET("getLastData?json=true")
    suspend fun getVehicles(@Query("key") key: String): Response<VehiclesResponse<VehicleData>>

    @GET("getRawData?json=true")
    suspend fun getVehicleLocationHistory(@Query("key") key: String,
                                          @Query("objectId") objectId: Long,
                                          @Query("begTimestamp") begTimestamp: String,
                                          @Query("endTimestamp") endTimestamp: String): Response<VehiclesResponse<VehicleLocationData>>
}

data class VehiclesResponse<T>(var status : Int? = null, var response: List<T>? = null)