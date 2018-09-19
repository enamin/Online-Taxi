package ena.min.android.onlinetaxi.presenter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.transition.TransitionInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.core.Const
import ena.min.android.onlinetaxi.data.Vehicle
import ena.min.android.onlinetaxi.model.AvailableVehicles
import ena.min.android.onlinetaxi.util.bitmapDescriptorFromVector
import ena.min.android.onlinetaxi.view.MapView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class MapPresenter : AbsPresenter<MapView>() {

    private val SELECTED_VEHICLE_ZOOM_LEVEL = 16
    private val DEFAULT_ZOOM_LEVEL = 10
    private val normalMarkers = ArrayList<Marker>()
    private var selectedVehicle: Vehicle? = null
    private var selectedMarker: Marker? = null

    companion object {
        val ARGUMENT_SELECTED_VEHICLE = "ARGUMENT_SELECTED_VEHICLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val t = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
            t.interpolator = AccelerateDecelerateInterpolator()
            sharedElementEnterTransition =t
        }
    }

    override fun createView(context: Context?): MapView? {
        if (theView == null) {
            return MapView(context, null, 0)
        }

        onViewResumed()
        return theView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToUiEvents()
    }

    private fun onMapReady() {
        if (arguments?.containsKey(ARGUMENT_SELECTED_VEHICLE) == true) {
            selectedVehicle = arguments?.getSerializable(ARGUMENT_SELECTED_VEHICLE) as? Vehicle?
            selectedVehicle?.let {
                addSelectedVehicleMarkerToMap()
                theView?.aniamteTo(LatLng(it.coordinate.latitude, it.coordinate.longitude), SELECTED_VEHICLE_ZOOM_LEVEL)
            }
            theView?.showDriverCard()
            theView?.setDriverInfo(selectedVehicle)
        } else {
            selectedVehicle = null
            theView?.hideDriverCard()
            theView?.jumpTo(Const.HAMBURG_CENTER, DEFAULT_ZOOM_LEVEL)
        }

    }

    private fun addSelectedVehicleMarkerToMap() {
        val sv = selectedVehicle?: return
        selectedMarker = theView?.addMarker(createSelectedTaxiMarkerOption(
                sv.coordinate.latitude, sv.coordinate.longitude))
    }

    private fun subscribeToUiEvents() {
        val dispList = listOf(
                theView?.mapReady?.subscribe { onMapReady() },

                theView?.cameraIdle?.subscribe { onCameraIdle() },

                theView?.closeTheMap?.throttleFirst(2, TimeUnit.SECONDS)?.subscribe {
                    closeTheMap()
                }
        )

        dispList.filterNotNull().forEach { compositeDisposable.add(it) }
    }

    private fun onViewResumed() {
        removeNormalMarkers()
        selectedMarker?.remove()
        selectedVehicle = null
        onMapReady()
    }

    private fun onCameraIdle() {
        removeNormalMarkers()
        theView?.showLoading()
        theView?.postDelayed({
            fetchVehiclesInMapBoundary()
        }, 500)
    }

    private fun removeNormalMarkers() {
        normalMarkers.forEach { it.remove() }
    }

    private fun fetchVehiclesInMapBoundary() {
        val mapBounds = theView?.getBounds() ?: return
        AvailableVehicles.fetch(mapBounds.farLeft.latitude, mapBounds.farLeft.longitude,
                mapBounds.nearRight.latitude, mapBounds.nearRight.longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onFetchSuccessful, ::onFetchError)
    }

    private fun onFetchSuccessful(availableVehicles: AvailableVehicles) {
        theView?.hideLoading()
        removeNormalMarkers()
        selectedMarker?.remove()
        addNormalVehiclesToMap(availableVehicles)
        updateSelectedVehicle(availableVehicles)
    }

    private fun updateSelectedVehicle(availableVehicles: AvailableVehicles) {
        /*
        Actually We need below code in the form of this commented code to update the selected vehicle's data
        (new lat, lng) but because it's a mock server it actually does not return this id in the next calls:
        *
        selectedVehicle = availableVehicles.poiList.firstOrNull { it.id == selectedVehicle?.id }
        */

        availableVehicles.poiList.firstOrNull { it.id == selectedVehicle?.id }?.let {
            selectedVehicle = it
        }

        selectedVehicle?.let {
            addSelectedVehicleMarkerToMap()
        }
    }

    private fun addNormalVehiclesToMap(availableVehicles: AvailableVehicles) {
        availableVehicles.poiList
                .filter { it.id != selectedVehicle?.id }
                .mapNotNull { theView?.addMarker(createNormalTaxiMarkerOption(it.coordinate.latitude, it.coordinate.longitude)) }
                .forEach { normalMarkers.add(it) }
    }

    private fun onFetchError(throwable: Throwable) {
        theView?.hideLoading()
        theView?.showMessage(R.string.error)
    }

    private fun createNormalTaxiMarkerOption(lat: Double, lng: Double): MarkerOptions? {
        return createMarkerOption(R.drawable.ic_taxi, lat, lng)
    }

    private fun createSelectedTaxiMarkerOption(lat: Double, lng: Double): MarkerOptions? {
        return createMarkerOption(R.drawable.ic_selected_taxi, lat, lng)
    }

    private fun createMarkerOption(@DrawableRes markerResId: Int, lat: Double, lng: Double): MarkerOptions? {
        val context = theView?.context ?: return null
        return MarkerOptions().position(LatLng(lat, lng)).icon(bitmapDescriptorFromVector(context, markerResId))
    }

    private fun closeTheMap() {
        activity?.onBackPressed()
    }
}