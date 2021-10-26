package vehicles_viewmodel

import addDays
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import vehicles_model.FetchResult
import vehicles_model.VehiclesRepository
import java.util.*

class VehiclesViewModel: ViewModel() {

    private val repository = VehiclesRepository.get()

    private val vehiclesList = MutableLiveData<List<VehicleData>>(mutableListOf())
    private val vehicleLocationHistory = MutableLiveData<List<VehicleLocationData>>(mutableListOf())
    private val dataFetchError = MutableLiveData<String?>(null)
    private val vehiclesFetchPending = MutableLiveData(false)
    private val locationFetchPending = MutableLiveData(false)
    private val locationDate = MutableLiveData(Date())

    init
    {
        fetchVehicles()
    }

    fun getVehicles(): LiveData<List<VehicleData>> = vehiclesList
    fun getVehicleLocationHistory(): LiveData<List<VehicleLocationData>> = vehicleLocationHistory
    fun getVehiclesFetchPending(): LiveData<Boolean> = vehiclesFetchPending
    fun getLocationFetchPending(): LiveData<Boolean> = locationFetchPending
    fun getLocationDate(): LiveData<Date> = locationDate
    fun getFetchError(): LiveData<String?> = dataFetchError

    fun onApiKeySelected(apiKey: String) {
        repository.updateApiKey(apiKey)
        fetchVehicles()
    }

    fun fetchVehicles() {
        viewModelScope.launch {
            repository.getVehicles().collect {
                when(it) {
                    is FetchResult.FetchData -> { vehiclesList.value = it.data!! }
                    is FetchResult.FetchPending -> { vehiclesFetchPending.value = it.pending }
                    is FetchResult.FetchError -> {
                        dataFetchError.value = it.message
                        dataFetchError.value = null
                    }
                }
            }
        }
    }

    fun onLocationDateSelected(vehicleId: String, date: Date) {
        locationDate.value = date
        fetchVehicleLocationHistory(vehicleId, date, addDays(date, 1))
    }

    private fun fetchVehicleLocationHistory(vehicleId: String, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getVehicleLocationHistory(vehicleId, startDate, endDate).collect {
                when(it) {
                    is FetchResult.FetchData -> { vehicleLocationHistory.value = it.data!! }
                    is FetchResult.FetchPending -> { locationFetchPending.value = it.pending }
                    is FetchResult.FetchError -> {
                        dataFetchError.value = it.message
                        dataFetchError.value = null
                    }
                }
            }
        }
    }
}