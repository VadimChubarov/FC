package data

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class MapRouteData(
    val coordinates: List<LatLng>,
    val start: LatLng,
    val end: LatLng,
    val bounds: LatLngBounds,
    val distance: Double?)