package vehicles_model.network

import data.VehicleData
import data.VehicleLocationData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vehicles_model.FetchResult
import vehicles_model.Error
import java.text.SimpleDateFormat
import java.util.*

class VehiclesNetworkRepository {

    private val networkApi: VehiclesNetworkInterface
    private val baseUrl = "https://app.ecofleet.com/seeme/Api/Vehicles/"
    private val apiKey = ""

    private val dateFormat = ""

    init
    {
        networkApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(VehiclesNetworkInterface::class.java)
    }

    suspend fun getVehicles(): FetchResult<List<VehicleData>> {
        val response = networkApi.getVehicles(apiKey)

        return processResponse(response)
    }

    suspend fun getVehicleLocationHistory(vehicleId: String, startDate: Date, endDate: Date): FetchResult<List<VehicleLocationData>> {
        val startDateString = SimpleDateFormat(dateFormat, Locale.ENGLISH).format(startDate)
        val endDateString = SimpleDateFormat(dateFormat, Locale.ENGLISH).format(endDate)

        val response = networkApi.getVehicleLocationHistory(apiKey, vehicleId, startDateString, endDateString)

        return processResponse(response)
    }

    private fun <T>processResponse(response: Response<T>): FetchResult<T> {
        return when(response.isSuccessful)
        {
            true -> FetchResult(data = response.body())
            false -> FetchResult(error = Error(response.errorBody().toString()))
        }
    }
}