package com.fc.fctest.vehicles_view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.fc.fctest.databinding.LocationHistoryFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import getDate
import getDateString
import java.util.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.fc.fctest.R
import data.MapRouteData
import data.VehicleData
import vehicles_viewmodel.VehiclesViewModel


class LocationHistoryFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "LocationHistoryFragment"
        const val VEHICLE_DATA = "vehicle_data"
    }

    private val dateFormat = "dd / MM / yyyy"
    private lateinit var viewBinding: LocationHistoryFragmentBinding
    private lateinit var datePicker: DatePicker
    private var vehicleData: VehicleData? = null
    private var map: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = LocationHistoryFragmentBinding.inflate(inflater, container, false)
        datePicker = DatePicker(requireContext())

        viewBinding.dateInput.editText?.inputType = InputType.TYPE_NULL
        viewBinding.setDateImage.setOnClickListener { showDatePicker() }
        viewBinding.topAppBar.setNavigationOnClickListener { dismiss() }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: VehiclesViewModel by activityViewModels()

        vehicleData = arguments?.getParcelable(VEHICLE_DATA)
        if(vehicleData == null) {
            dismiss()
            return
        }

        val vehicleDate = vehicleData!!.timestamp

        if(savedInstanceState == null) {
            if (vehicleDate.isNullOrEmpty())
                selectDate(Date())
            else
                getDate(vehicleDate)?.let { selectDate(it) }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            viewModel.getVehicleLocationHistory().observe(viewLifecycleOwner, this::showRoute)
        }

        viewModel.getLocationFetchPending().observe(viewLifecycleOwner, this::showLoading)
        viewModel.getLocationDate().observe(viewLifecycleOwner, this::showSelectedDate)

        val plateNumber = vehicleData!!.plate
        if(!plateNumber.isNullOrEmpty())
            viewBinding.locationHistoryTitle.text = this.context?.getString(R.string.location_history_pattern, plateNumber)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { bottomSheet ->
                (bottomSheet as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheet.behavior.isDraggable = false
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if(isLoading() && !requireActivity().isChangingConfigurations) {
            val viewModel: VehiclesViewModel by activityViewModels()
            viewModel.onLocationDataCancel()
        }
    }

    private fun showDatePicker() {
        if(!isLoading()) {
            val viewModel: VehiclesViewModel by activityViewModels()
            datePicker.showPickDate(viewModel.getLocationDate().value!!, this::selectDate)
        }
    }

    private fun showSelectedDate(date: Date?) {
        viewBinding.dateInput.editText?.setText(if(date != null) getDateString(date, dateFormat) else "")
    }

    private fun selectDate(date: Date) {
        val viewModel: VehiclesViewModel by activityViewModels()
        viewModel.onLocationDateSelected(vehicleData!!.objectId!!, date)
    }

    private fun showRoute(mapRouteData: MapRouteData?) {
        if(mapRouteData == null) {
            return
        }

        map?.let { map ->
            showPolyline(map, mapRouteData)
            showMarkers(map, mapRouteData)

            val distance = mapRouteData.distance
            distance?.let { showDistance( it / 1000) }
        }
    }

    private fun showPolyline(map: GoogleMap, mapRouteData: MapRouteData) {
        val line = PolylineOptions().addAll(mapRouteData.coordinates).apply {
            endCap(RoundCap())
            startCap(RoundCap())
            width(20F)
            color(R.color.map_route_color)
            jointType(JointType.ROUND)
        }

        map.apply {
            addPolyline(line)
            moveCamera(CameraUpdateFactory.newLatLngBounds(mapRouteData.bounds, 100))
        }
    }

    private fun showMarkers(map: GoogleMap, mapRouteData: MapRouteData) {
        val markerIcon = BitmapDescriptorFactory.fromBitmap(
            ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_location_marker)?.toBitmap())

        map.addMarker(MarkerOptions()
            .position(mapRouteData.start)
            .title(getString(R.string.start_marker_title)))
            ?.setIcon(markerIcon)

        map.addMarker(MarkerOptions()
            .position(mapRouteData.end)
            .title(getString(R.string.end_marker_title)))
            ?.setIcon(markerIcon)
    }

    private fun showDistance(distance: Double?) {
        viewBinding.distanceText.text = if(distance != null)
            getString(R.string.location_history_distance_pattern, distance) else ""
    }

    private fun enableDateSelection(enabled: Boolean) {
        viewBinding.dateInput.isEnabled = enabled
        viewBinding.setDateImage.apply {
            alpha = if(enabled) 1.0f else 0.25f
            isClickable = enabled
        }
    }

    private fun isLoading(): Boolean {
        val viewModel: VehiclesViewModel by activityViewModels()
        return viewModel.getLocationFetchPending().value!!
    }

    private fun showLoading(loading: Boolean) {
        viewBinding.mapProgress.visibility = if(loading) View.VISIBLE else View.GONE
        enableDateSelection(!loading)

        if(loading) {
            map?.clear()
            showDistance(null)
        }
    }
}