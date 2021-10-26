package vehicles_viewmodel

import addDays
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import data.MapRouteData
import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vehicles_model.FetchResult
import vehicles_model.VehiclesRepository
import java.util.*

class VehiclesViewModel: ViewModel() {

    private val repository = VehiclesRepository.get()

    private val vehiclesList = MutableLiveData<List<VehicleData>>(mutableListOf())
    private val vehicleLocationHistory = MutableLiveData<MapRouteData?>()
    private val dataFetchError = MutableLiveData<String?>(null)
    private val vehiclesFetchPending = MutableLiveData(false)
    private val locationFetchPending = MutableLiveData(false)
    private val locationDate = MutableLiveData(Date())

    init
    {
        fetchVehicles()
    }

    fun getVehicles(): LiveData<List<VehicleData>> = vehiclesList
    fun getVehicleLocationHistory(): LiveData<MapRouteData?> = vehicleLocationHistory
    fun getVehiclesFetchPending(): LiveData<Boolean> = vehiclesFetchPending
    fun getLocationFetchPending(): LiveData<Boolean> = locationFetchPending
    fun getLocationDate(): LiveData<Date> = locationDate
    fun getFetchError(): LiveData<String?> = dataFetchError

    fun onApiKeySelected(apiKey: String) {
        repository.updateApiKey(apiKey)
        fetchVehicles()
    }

    fun onLocationDateSelected(vehicleId: String, date: Date) {
        locationDate.value = date
        vehicleLocationHistory.value = null
        fetchVehicleLocationHistory(vehicleId, date, addDays(date, 1))
    }

    fun onLocationDataCancel() {
        repository.cancelVehicleLocationRequests()
    }

    fun fetchVehicles() {
        viewModelScope.launch {
            repository.getVehicles().collect { fetchResult ->
                when(fetchResult) {
                    is FetchResult.FetchData -> { vehiclesList.value = fetchResult.data!! }
                    is FetchResult.FetchPending -> { vehiclesFetchPending.value = fetchResult.pending }
                    is FetchResult.FetchError -> { handleError(fetchResult) }
                }
            }
        }
    }

    private fun fetchVehicleLocationHistory(vehicleId: String, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getVehicleLocationHistory(vehicleId, startDate, endDate).collect { fetchResult ->
                when(fetchResult) {
                    is FetchResult.FetchData -> { vehicleLocationHistory.value = createMapRouteData(fetchResult.data!!)  }
                    is FetchResult.FetchPending -> { locationFetchPending.value = fetchResult.pending }
                    is FetchResult.FetchError -> { handleError(fetchResult) }
                }
            }
        }
    }

    private fun <T>handleError(error: FetchResult.FetchError<T>) {
        if(!error.isCanceled()) {
            dataFetchError.value = error.message
            dataFetchError.value = null
        }
    }

    private suspend fun createMapRouteData(dataList: List<VehicleLocationData>): MapRouteData? {
        return withContext(Dispatchers.Default) {
            if(dataList.isEmpty())
                return@withContext null

            val coordinates = mutableListOf<LatLng>()
            dataList.forEach {
                val latitude = it.latitude
                val longitude = it.longitude

                if(latitude != null && longitude != null)
                    coordinates.add(LatLng(latitude, longitude))
            }

            if(coordinates.isEmpty())
                return@withContext null

            MapRouteData(
                coordinates,
                coordinates.first(),
                coordinates.last(),
                calculateBounds(coordinates),
                dataList.last().distance)
        }
    }

    private fun calculateBounds(list: List<LatLng> ): LatLngBounds {
        val latMin = list.minOf { it.latitude }
        val latMax = list.maxOf { it.latitude }
        val longMib = list.minOf { it.longitude }
        val longMax = list.maxOf { it.longitude }

        return LatLngBounds(LatLng(latMin, longMib), LatLng(latMax, longMax))
    }
}