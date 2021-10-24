package vehicles_viewmodel

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
    private val dataFetchPending = MutableLiveData(false)

    init
    {
        fetchVehicles()
    }

    fun getVehicles(): LiveData<List<VehicleData>> = vehiclesList
    fun getVehicleLocationHistory(): LiveData<List<VehicleLocationData>> = vehicleLocationHistory
    fun getFetchPending(): LiveData<Boolean> = dataFetchPending
    fun getFetchError(): LiveData<String?> = dataFetchError

    fun onApiKeySelected(apiKey: String) {
        repository.updateApiKey(apiKey)
        fetchVehicles()
    }

    fun fetchVehicles() {
        viewModelScope.launch {
            repository.getVehicles().collect {
                when(it) {
                    is FetchResult.FetchData -> { vehiclesList.value = it.data }
                    is FetchResult.FetchPending -> { dataFetchPending.value = it.pending }
                    is FetchResult.FetchError -> {
                        dataFetchError.value = it.message
                        dataFetchError.value = null
                    }
                }
            }
        }
    }

    fun fetchVehicleLocationHistory(vehicleId: Long, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getVehicleLocationHistory(vehicleId, startDate, endDate).collect {
                when(it) {
                    is FetchResult.FetchData -> { vehicleLocationHistory.value = it.data }
                    is FetchResult.FetchPending -> { dataFetchPending.value = it.pending }
                    is FetchResult.FetchError -> {
                        dataFetchError.value = it.message
                        dataFetchError.value = null
                    }
                }
            }
        }
    }
}