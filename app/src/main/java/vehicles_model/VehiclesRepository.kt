package vehicles_model

import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vehicles_model.network.VehiclesNetworkRepository
import java.util.*

class VehiclesRepository private constructor() : VehiclesRepositoryInterface {

    companion object
    {
        fun get(): VehiclesRepositoryInterface = VehiclesRepository()
    }

    private val networkRepository = VehiclesNetworkRepository()

    override suspend fun getVehicles(): FetchResult<List<VehicleData>> = withContext(Dispatchers.IO) { networkRepository.getVehicles() }

    override suspend fun getVehicleLocationHistory(
        vehicleId: Long,
        startDate: Date,
        endDate: Date
    ): FetchResult<List<VehicleLocationData>> = withContext(Dispatchers.IO) { networkRepository.getVehicleLocationHistory(vehicleId, startDate, endDate) }
}