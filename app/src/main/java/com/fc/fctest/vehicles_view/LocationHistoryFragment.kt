package com.fc.fctest.vehicles_view

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fc.fctest.R
import com.fc.fctest.databinding.LocationHistoryFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import getDateString
import java.util.*

class LocationHistoryFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "LocationHistoryFragment"
        const val VEHICLE_ID = "vehicle_id"
    }

    private val dateFormat = "dd / MM / yyyy"
    private lateinit var viewBinding: LocationHistoryFragmentBinding
    private lateinit var datePicker: DatePicker
    private var selectedDate: Date = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val vehicleId = arguments?.getString(VEHICLE_ID) ?: 0

        viewBinding = LocationHistoryFragmentBinding.inflate(inflater, container, false)
        datePicker = DatePicker(requireContext())

        viewBinding.locationHistoryTitle.text = this.context?.getString(R.string.location_history_pattern, vehicleId)
        viewBinding.dateInput.editText?.inputType = InputType.TYPE_NULL
        viewBinding.setDateImage.setOnClickListener { showDatePicker() }
        viewBinding.topAppBar.setNavigationOnClickListener { dismiss() }

        selectDate(selectedDate)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync {

            
        }

        return viewBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { bottomSheet ->
                (bottomSheet as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheet.behavior.isDraggable = false
            }
        }
    }

    private fun showDatePicker() {
        datePicker.showPickDate(this::selectDate)
    }

    private fun selectDate(date: Date) {
        selectedDate = date
        viewBinding.dateInput.editText?.setText(getDateString(date, dateFormat))


        //TODO:populate map direction
    }
}