package com.fc.fctest.vehicles_view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fc.fctest.R
import com.fc.fctest.databinding.VehicleItemLayoutBinding
import data.VehicleData
import getDate
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime


class VehiclesAdapter(private val context: Context, private val selectCallback: (VehicleData) -> Unit): RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {

    private val vehicles = mutableListOf<VehicleData>()
    private val emptyDataPlaceholder = "."

    fun updateItems(items: Collection<VehicleData>) {
        vehicles.clear()
        vehicles.addAll(items)
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VehicleViewHolder(VehicleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val binding = holder.itemBinding
        val vehicleData = vehicles[position]

        binding.vehicleTitle.text = context.resources.getString(
            R.string.vehicle_title_pattern, getTitleString(vehicleData.plate), getTitleString(vehicleData.driverName))

        binding.vehicleLocation.text = getTitleString(vehicleData.address)
        binding.vehicleSpeed.text = getSpeedString(vehicleData.speed)
        binding.vehicleLocationTime.text = getTimeString(vehicleData.timestamp)
    }

    override fun getItemCount() = vehicles.size

    inner class VehicleViewHolder(val itemBinding: VehicleItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        init { itemView.setOnClickListener { selectCallback(vehicles[adapterPosition]) } }
    }

    private fun getTitleString(data: String?) = if(data.isNullOrEmpty()) emptyDataPlaceholder else data

    private fun getSpeedString(data: String?) =
        if(data.isNullOrEmpty()) emptyDataPlaceholder else context.resources.getString(R.string.vehicle_speed_pattern, data)

    @OptIn(ExperimentalTime::class)
    private fun getTimeString(data: String?): String {

        if(data.isNullOrEmpty())
            return emptyDataPlaceholder

        val time = getDate(data)
        val currentTime = Date()

        if(time == null || !time.before(currentTime))
            return emptyDataPlaceholder

        val duration = currentTime.time - time.time

        val hours = DurationUnit.MILLISECONDS.toHours(duration)
        if(hours > 0)
            return context.resources.getString(R.string.vehicle_time_hours_pattern, hours)

        val minutes = DurationUnit.MILLISECONDS.toMinutes(duration)
        val seconds = DurationUnit.MILLISECONDS.toSeconds(duration)
        if(minutes > 0)
        {
            return when(seconds > 0)
            {
                true -> context.resources.getString(R.string.vehicle_time_minutes_seconds_pattern, minutes, seconds)
                false -> context.resources.getString(R.string.vehicle_time_minutes_pattern, minutes)
            }
        }

        return when(seconds > 0)
        {
            true -> context.resources.getString(R.string.vehicle_time_seconds_pattern, seconds)
            false -> emptyDataPlaceholder
        }
    }
}