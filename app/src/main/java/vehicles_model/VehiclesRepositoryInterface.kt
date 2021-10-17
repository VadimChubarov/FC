package vehicles_model

import data.VehicleData
import data.VehicleLocationData
import java.util.*

interface VehiclesRepositoryInterface {

    suspend fun getVehicles(): FetchResult<List<VehicleData>>
    suspend fun getVehicleLocationHistory(vehicleId: String, startDate: Date, endDate: Date): FetchResult<List<VehicleLocationData>>
}