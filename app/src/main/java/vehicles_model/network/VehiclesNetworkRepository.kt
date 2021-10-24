package vehicles_model.network

import data.VehicleData
import data.VehicleLocationData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vehicles_model.FetchResult
import java.text.SimpleDateFormat
import java.util.*

class VehiclesNetworkRepository {

    private val networkApi: VehiclesNetworkInterface
    private val baseUrl = "https://app.ecofleet.com/seeme/Api/Vehicles/"
    private var apiKey = ""
    private val dateFormat = "yy-mm-dd"

    init
    {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor())
            .build()

        networkApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(VehiclesNetworkInterface::class.java)
    }

    fun updateApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    suspend fun getVehicles(): FetchResult<List<VehicleData>> {
        val response = networkApi.getVehicles()

        return processResponse(response)
    }

    suspend fun getVehicleLocationHistory(vehicleId: Long, startDate: Date, endDate: Date): FetchResult<List<VehicleLocationData>> {
        val startDateString = SimpleDateFormat(dateFormat, Locale.ENGLISH).format(startDate)
        val endDateString = SimpleDateFormat(dateFormat, Locale.ENGLISH).format(endDate)

        val response = networkApi.getVehicleLocationHistory(vehicleId, startDateString, endDateString)

        return processResponse(response)
    }

    private fun <T>processResponse(response: Response<VehiclesResponse<T>>): FetchResult<T> {
        return when(response.isSuccessful)
        {
            true -> FetchResult.FetchData(response.body()?.response)
            false -> FetchResult.FetchError(response.errorBody().toString())
        }
    }

    private inner class ApiInterceptor: Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()

            val url = request.url.newBuilder()
                .addQueryParameter("key", apiKey)
                .addQueryParameter("json", "true")
                .build()

            return chain.proceed(request.newBuilder().url(url).build())
        }
    }
}