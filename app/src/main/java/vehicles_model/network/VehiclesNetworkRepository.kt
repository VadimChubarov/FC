package vehicles_model.network

import data.VehicleData
import data.VehicleLocationData
import getDateString
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vehicles_model.FetchResult
import java.util.*

class VehiclesNetworkRepository {

    private val networkApi: VehiclesNetworkInterface
    private val baseUrl = "https://app.ecofleet.com/seeme/Api/Vehicles/"
    private var apiKey = ""
    private val dateFormat = "yy-MM-dd"

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
        return processResponse { networkApi.getVehicles() }
    }

    suspend fun getVehicleLocationHistory(vehicleId: Long, startDate: Date, endDate: Date): FetchResult<List<VehicleLocationData>> {
        val startDateString = getDateString(startDate, dateFormat)!!
        val endDateString = getDateString(endDate, dateFormat)!!

        return processResponse { networkApi.getVehicleLocationHistory(vehicleId, startDateString, endDateString) }
    }

    private suspend fun <T>processResponse(request: suspend () -> Response<VehiclesResponse<T>>): FetchResult<T> {
        return try {
            val response = request.invoke()

            when(response.isSuccessful) {
                true -> FetchResult.FetchData(response.body()?.response)
                false -> FetchResult.FetchError(response.code().toString())
            }
        } catch(e: Throwable) {
            FetchResult.FetchError(e.message.toString())
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