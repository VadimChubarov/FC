package vehicles_model.network

import data.VehicleData
import data.VehicleLocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehiclesNetworkInterface {

    @GET("getLastData?json=true")
    suspend fun getVehicles(@Query("key") key: String): Response<List<VehicleData>>

    @GET("getRawData?json=true")
    suspend fun getVehicleLocationHistory(@Query("key") key: String,
                                          @Query("objectId") objectId: String,
                                          @Query("begTimestamp") begTimestamp: String,
                                          @Query("endTimestamp") endTimestamp: String): Response<List<VehicleLocationData>>
}
