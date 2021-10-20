package com.fc.fctest.vehicles_view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fc.fctest.R
import com.fc.fctest.databinding.VehicleItemLayoutBinding
import data.VehicleData

class VehiclesAdapter(private val context: Context, private val selectCallback: (VehicleData) -> Unit): RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {

    private val vehicles = mutableListOf<VehicleData>()

    fun updateItems(items: Collection<VehicleData>) {
        vehicles.clear()
        vehicles.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VehicleViewHolder(VehicleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val binding = holder.itemBinding
        val vehicleData = vehicles[position]

        binding.vehicleTitle.text = context.resources.getString(R.string.vehicle_title_pattern, vehicleData.plate, vehicleData.driverName)
        binding.vehicleLocation.text = vehicleData.address
        binding.vehicleSpeed.text = if(vehicleData.speed.isEmpty()) context.resources.getString(R.string.vehicle_speed_pattern, vehicleData.speed) else "."
        binding.vehicleLocationTime.text = 

    }

    override fun getItemCount() = vehicles.size

    inner class VehicleViewHolder(val itemBinding: VehicleItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        init { itemView.setOnClickListener { selectCallback(vehicles[adapterPosition]) } }
    }
}