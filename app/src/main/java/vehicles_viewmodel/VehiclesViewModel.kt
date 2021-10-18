package vehicles_viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.launch
import vehicles_model.FetchResult
import vehicles_model.VehiclesRepository
import java.util.*

class VehiclesViewModel: ViewModel() {

    private val repository = VehiclesRepository.get()

    private val vehiclesList = MutableLiveData<ViewModelData<VehicleData>>(ViewModelData())
    private val vehicleLocationHistory = MutableLiveData<ViewModelData<VehicleLocationData>>(ViewModelData())
    private val dataFetchError = MutableLiveData<String?>(null)

    fun getVehicles(): LiveData<ViewModelData<VehicleData>> = vehiclesList
    fun getVehicleLocationHistory(): LiveData<ViewModelData<VehicleLocationData>> = vehicleLocationHistory

    fun fetchVehicles() {
        viewModelScope.launch {
            updateVehicleList(loading = true)
            val vehiclesData = processFetchResult(repository.getVehicles())
            updateVehicleList(false, vehiclesData)
        }
    }

    fun fetchVehicleLocationHistory(vehicleId: Long, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            updateVehicleLocationHistory(loading = true)
            val locationHistory = processFetchResult(repository.getVehicleLocationHistory(vehicleId, startDate, endDate))
            updateVehicleLocationHistory(false, locationHistory)
        }
    }

    private fun <T>processFetchResult(fetchResult: FetchResult<T>): T? {
        return when(fetchResult.hasData())
        {
            true -> fetchResult.data

            false ->
            {
                if(fetchResult.hasError())
                    dataFetchError.value = fetchResult.error?.message

                null
            }
        }
    }

    private fun updateVehicleList(loading: Boolean = false, data: List<VehicleData>? = null) {
        val updateData: List<VehicleData> = data ?: vehiclesList.value!!.result
        vehiclesList.value = ViewModelData(loading, updateData)
    }

    private fun updateVehicleLocationHistory(loading: Boolean = false, data: List<VehicleLocationData>? = null) {
        val updateData: List<VehicleLocationData> = data ?: mutableListOf()
        vehicleLocationHistory.value = ViewModelData(loading, updateData)
    }

    class ViewModelData<T>(val loading: Boolean = false, val result: List<T> = mutableListOf())
}