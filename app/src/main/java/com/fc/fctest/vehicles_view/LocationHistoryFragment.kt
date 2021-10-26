package com.fc.fctest.vehicles_view

import android.app.Dialog
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
import data.VehicleLocationData
import getDate
import getDateString
import java.util.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.fc.fctest.R
import vehicles_viewmodel.VehiclesViewModel


class LocationHistoryFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "LocationHistoryFragment"
        const val VEHICLE_ID = "vehicle_id"
        const val VEHICLE_DATE = "vehicle_date"
    }

    private val dateFormat = "dd / MM / yyyy"
    private lateinit var viewBinding: LocationHistoryFragmentBinding
    private lateinit var datePicker: DatePicker
    private var vehicleId: String = ""
    private var map: GoogleMap? = null
    private val markerIcon = BitmapDescriptorFactory.fromBitmap(
        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_location_marker)?.toBitmap())

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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            viewModel.getVehicleLocationHistory().observe(viewLifecycleOwner, this::showRoute)
        }

        viewModel.getLocationFetchPending().observe(viewLifecycleOwner, this::showLoading)
        viewModel.getLocationDate().observe(viewLifecycleOwner, this::showSelectedDate)
        
        vehicleId = arguments?.getString(VEHICLE_ID) ?: ""
        val vehicleDate = arguments?.getString(VEHICLE_DATE) ?: ""

        viewBinding.locationHistoryTitle.text = this.context?.getString(R.string.location_history_pattern, vehicleId)

        if(savedInstanceState == null) {
            if (vehicleDate.isNotEmpty())
                getDate(vehicleDate)?.let { selectDate(it) }
            else
                selectDate(Date())
        }
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
        if(!isLoading()) {
            val viewModel: VehiclesViewModel by activityViewModels()
            datePicker.showPickDate(viewModel.getLocationDate().value!!, this::selectDate)
        }
    }

    private fun showSelectedDate(date: Date) {
        viewBinding.dateInput.editText?.setText(getDateString(date, dateFormat))
    }

    private fun selectDate(date: Date) {
        val viewModel: VehiclesViewModel by activityViewModels()
        viewModel.onLocationDateSelected(vehicleId, date)
    }

    private fun showRoute(dataList: List<VehicleLocationData>) {
        if(isLoading())
            return

        map?.let { map ->
            val coordinates = mutableListOf<LatLng>()
            dataList.forEach {
                val latitude = it.latitude
                val longitude = it.longitude

                if(latitude != null && longitude != null)
                    coordinates.add(LatLng(latitude, longitude))
            }

            showPolyline(map, coordinates)
            showMarkers(map, coordinates.first(), coordinates.last())

            val distance = dataList.last().distance
            distance?.let { showDistance( it / 1000) }
        }
    }

    private fun showPolyline(map: GoogleMap, coordinates: List<LatLng>) {
        if(coordinates.isEmpty())
            return

        val line = PolylineOptions().addAll(coordinates).apply {
            endCap(RoundCap())
            startCap(RoundCap())
            width(20F)
            color(R.color.map_route_color)
            jointType(JointType.ROUND)
        }

        map.apply {
            addPolyline(line)
            moveCamera(CameraUpdateFactory.newLatLngBounds(calculateBounds(coordinates), 100))
        }
    }

    private fun showMarkers(map: GoogleMap, start: LatLng, end: LatLng) {
        map.addMarker(MarkerOptions().position(start).title("Start")).setIcon(markerIcon)
        map.addMarker(MarkerOptions().position(end).title("End")).setIcon(markerIcon)
    }

    private fun showDistance(distance: Double) {
        viewBinding.distanceText.text = getString(R.string.location_history_distance_pattern, distance)
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
        viewBinding.distanceText.text = ""
        enableDateSelection(!loading)

        if(loading)
            map?.clear()
    }

    private fun calculateBounds(list: List<LatLng> ): LatLngBounds {
        var latMin = list.minOf { it.latitude }
        var latMax = list.maxOf { it.latitude }
        var longMib = list.minOf { it.longitude }
        var longMax = list.maxOf { it.longitude }

        return LatLngBounds(LatLng(latMin, longMib), LatLng(latMax, longMax))
    }
}