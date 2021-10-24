package vehicles_model

import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import vehicles_model.network.VehiclesNetworkRepository
import java.util.*

class VehiclesRepository private constructor() : VehiclesRepositoryInterface {

    companion object
    {
        fun get(): VehiclesRepositoryInterface = VehiclesRepository()
    }

    private val networkRepository = VehiclesNetworkRepository()

    override fun updateApiKey(apiKey: String) {
       networkRepository.updateApiKey(apiKey)
    }

    override suspend fun getVehicles(): Flow<FetchResult<List<VehicleData>>> {
        return flow {

            emit(FetchResult.FetchPending(true))
            //emit(localRepository.getVehicles()) TODO: possible cashed data loading
            emit(networkRepository.getVehicles())
            emit(FetchResult.FetchPending(false))

        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getVehicleLocationHistory (vehicleId: Long, startDate: Date, endDate: Date): Flow<FetchResult<List<VehicleLocationData>>> {
        return flow {

            emit(FetchResult.FetchPending(true))
            emit(networkRepository.getVehicleLocationHistory(vehicleId, startDate, endDate))
            emit(FetchResult.FetchPending(false))

        }.flowOn(Dispatchers.IO)
    }
}