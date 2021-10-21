package com.fc.fctest.vehicles_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.fc.fctest.databinding.ActivityVehiclesBinding
import data.VehicleData
import vehicles_viewmodel.VehiclesViewModel

class VehiclesActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityVehiclesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityVehiclesBinding.inflate(layoutInflater)

        viewBinding.imageKey.setOnClickListener { showApiKeyDialog() }
        viewBinding.vehiclesList.adapter = VehiclesAdapter(viewBinding.root.context, this::onVehicleSelected)

        val viewModel: VehiclesViewModel by viewModels()

        viewModel.getVehicles().observe(this, this::showVehicles)
        viewModel.fetchVehicles()
    }

    private fun showApiKeyDialog() {

    }

    private fun showVehicles(data: VehiclesViewModel.ViewModelData<VehicleData>) =
        (viewBinding.vehiclesList.adapter as VehiclesAdapter).updateItems(data.result)

    private fun onVehicleSelected(vehicleData: VehicleData) {

    }
}