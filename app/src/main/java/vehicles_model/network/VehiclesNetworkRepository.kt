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
import java.util.concurrent.TimeUnit

class VehiclesNetworkRepository {

    private val networkApi: VehiclesNetworkInterface
    private val httpClient: OkHttpClient
    private val baseUrl = "https://app.ecofleet.com/seeme/Api/Vehicles/"
    private var apiKey = ""
    private val dateFormat = "yy-MM-dd"

    init
    {
       httpClient = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor())
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .build()

        networkApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(VehiclesNetworkInterface::class.java)
    }

    fun updateApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    suspend fun getVehicles(): FetchResult<List<VehicleData>> {
        val tag = VehiclesNetworkInterface.VEHICLES_TAG
        cancelPending(tag)
        return processResponse { networkApi.getVehicles(tag) }
    }

    suspend fun getVehicleLocationHistory(vehicleId: String, startDate: Date, endDate: Date): FetchResult<List<VehicleLocationData>> {
        val tag = VehiclesNetworkInterface.VEHICLE_LOCATION_TAG
        cancelPending(tag)

        val startDateString = getDateString(startDate, dateFormat)!!
        val endDateString = getDateString(endDate, dateFormat)!!

        return processResponse { networkApi.getVehicleLocationHistory(tag, vehicleId, startDateString, endDateString) }
    }

    private suspend fun <T>processResponse(request: suspend () -> Response<VehiclesResponse<T>>): FetchResult<T> {

        return if(apiKey.isNotEmpty()) {
            try {
                val response = request.invoke()

                when (response.isSuccessful) {
                    true -> FetchResult.FetchData(response.body()?.response)
                    false -> FetchResult.FetchError(response.code().toString())
                }
            } catch (e: Throwable) {
                FetchResult.FetchError(e.message.toString())
            }
        } else
            FetchResult.FetchError("Missing api key")
    }

    private fun cancelPending(tag: String) {
        for (call in httpClient.dispatcher.queuedCalls()) {
            if(call.request().header("tag") == tag) {
                call.cancel()
            }
        }

        for (call in httpClient.dispatcher.runningCalls()) {
            if(call.request().header("tag") == tag) {
                call.cancel()
            }
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