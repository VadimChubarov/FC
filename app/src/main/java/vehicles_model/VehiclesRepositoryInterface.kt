package vehicles_model

import data.VehicleData
import data.VehicleLocationData
import kotlinx.coroutines.flow.Flow
import java.util.*

interface VehiclesRepositoryInterface {

    suspend fun getVehicles(): Flow<FetchResult<List<VehicleData>>>
    suspend fun getVehicleLocationHistory(vehicleId: Long, startDate: Date, endDate: Date): Flow<FetchResult<List<VehicleLocationData>>>
}