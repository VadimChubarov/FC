package com.fc.fctest.vehicles_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.fc.fctest.databinding.ActivityVehiclesBinding
import data.VehicleData
import vehicles_viewmodel.VehiclesViewModel

class VehiclesActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityVehiclesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityVehiclesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val viewModel: VehiclesViewModel by viewModels()

        viewBinding.imageKey.setOnClickListener { showApiKeyDialog() }
        viewBinding.vehiclesList.adapter = VehiclesAdapter(viewBinding.root.context, this::onVehicleSelected)

        viewModel.getVehicles().observe(this, this::showVehicles)
        viewModel.getFetchPending().observe(this, this::showLoading)
        viewModel.getFetchError().observe(this, this::showError)

        viewModel.fetchVehicles()
    }

    private fun showApiKeyDialog() {

    }

    private fun showVehicles(data: List<VehicleData>) {
        (viewBinding.vehiclesList.adapter as VehiclesAdapter).updateItems(data)
    }

    private fun showLoading(loading: Boolean) {
        viewBinding.loadingProgress.visibility = if(loading) View.VISIBLE else View.GONE
    }

    private fun showError(error: String?) {
        error?.let {  Toast.makeText(this, it, Toast.LENGTH_LONG) }
    }

    private fun onVehicleSelected(vehicleData: VehicleData) {

    }
}