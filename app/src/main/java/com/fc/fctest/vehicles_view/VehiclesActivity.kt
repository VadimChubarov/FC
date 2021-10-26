package com.fc.fctest.vehicles_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.fc.fctest.R
import com.fc.fctest.databinding.ActivityVehiclesBinding
import com.fc.fctest.databinding.ApiKeyDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import data.VehicleData
import vehicles_viewmodel.VehiclesViewModel

class VehiclesActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityVehiclesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityVehiclesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val viewModel: VehiclesViewModel by viewModels()

        viewBinding.imageKey.setOnClickListener { showApiKeyDialog() }
        viewBinding.vehiclesList.adapter = VehiclesAdapter(viewBinding.root.context, this::onVehicleSelected)

        viewBinding.swipeToRefreshLayout.setOnRefreshListener {
            if(!viewModel.getVehiclesFetchPending().value!!) {
                viewModel.fetchVehicles()
            }
        }

        viewModel.getVehicles().observe(this, this::showVehicles)
        viewModel.getVehiclesFetchPending().observe(this, this::showLoading)
        viewModel.getFetchError().observe(this, this::showError)
    }

    private fun showApiKeyDialog() {
        val viewModel: VehiclesViewModel by viewModels()

        val dialogViewBinding = ApiKeyDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogViewBinding.root)
            .create()

        dialogViewBinding.dialogButtonOk.setOnClickListener {
            val apiKey = dialogViewBinding.apiKeyInput.editText!!.text.toString()

            if(apiKey.isNotEmpty()) {
                viewModel.onApiKeySelected(apiKey)
                dialog.dismiss()
            }
            else
                dialogViewBinding.apiKeyInput.error = this.getString(R.string.dialog_edit_error)
        }
        dialogViewBinding.dialogButtonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showVehicles(data: List<VehicleData>) {
        (viewBinding.vehiclesList.adapter as VehiclesAdapter).updateItems(data)
    }

    private fun showLoading(loading: Boolean) {
        viewBinding.swipeToRefreshLayout.isRefreshing = loading
    }

    private fun showError(error: String?) {
        error?.let {  Toast.makeText(this, this.getString(R.string.fetch_error_message, error), Toast.LENGTH_LONG).show() }
    }

    private fun onVehicleSelected(vehicleData: VehicleData) {
        if(vehicleData.objectId.isNullOrEmpty())
            return

        val fragment = LocationHistoryFragment()
        val args = Bundle()
        args.putString(LocationHistoryFragment.VEHICLE_ID, vehicleData.objectId)
        args.putString(LocationHistoryFragment.VEHICLE_DATE, vehicleData.timestamp)
        fragment.arguments = args
        fragment.show(supportFragmentManager, LocationHistoryFragment.TAG)
    }
}