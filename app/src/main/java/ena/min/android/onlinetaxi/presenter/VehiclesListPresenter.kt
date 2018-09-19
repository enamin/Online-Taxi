package ena.min.android.onlinetaxi.presenter

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.View
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.adapter.VehiclesListAdapter
import ena.min.android.onlinetaxi.core.Const
import ena.min.android.onlinetaxi.core.Router
import ena.min.android.onlinetaxi.data.Vehicle
import ena.min.android.onlinetaxi.model.AvailableVehicles
import ena.min.android.onlinetaxi.view.VehiclesListView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.ref.SoftReference
import java.util.concurrent.TimeUnit

class VehiclesListPresenter : AbsPresenter<VehiclesListView>() {
    private val adapter = VehiclesListAdapter(ArrayList())
    private var mapPresenterReference: SoftReference<MapPresenter>? = null


    override fun createView(context: Context?): VehiclesListView {
        return VehiclesListView(context, null, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToUiEvents()
        theView?.setAdapter(adapter)
        showHamburgVehicles()
    }

    private fun subscribeToUiEvents() {
        val dispList = listOf(
                adapter.listClicks.throttleFirst(2, TimeUnit.SECONDS).subscribe(::onVehicleClicked),

                theView?.onUserRefreshedTheList?.subscribe { showHamburgVehicles() },

                theView?.fabMapClicks?.throttleFirst(2, TimeUnit.SECONDS)?.subscribe { onMapClicked() }
        )

        dispList.filterNotNull().forEach { compositeDisposable.add(it) }
    }

    private fun onMapClicked() {
        goToMap()
    }

    private fun goToMap(vehicle: Vehicle? = null, itemView: View? = null) {
        if (mapPresenterReference == null || mapPresenterReference?.get() == null) {
            mapPresenterReference = SoftReference(MapPresenter())
        }

        mapPresenterReference?.get()?.let {
            it.arguments = null

            if (vehicle != null) {
                val bundle = Bundle()
                bundle.putSerializable(MapPresenter.ARGUMENT_SELECTED_VEHICLE, vehicle)
                it.arguments = bundle
            }

            itemView?.let { ViewCompat.setTransitionName(it, context?.getString(R.string.transitionName_driver_card)) }

            Router.goTo(it, arrayOf(itemView))
            compositeDisposable.clear()
        }
    }

    private fun showHamburgVehicles() {
        fetchData(*Const.HAMBURG_AREA)
    }

    private fun fetchData(vararg area: Double) {
        theView?.showLoading()
        AvailableVehicles.fetch(*area).observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onFetchSuccessful, ::onFetchError)
    }

    private fun onFetchSuccessful(availableVehicles: AvailableVehicles) {
        adapter.items = availableVehicles.poiList
        adapter.notifyDataSetChanged()
        theView?.hideLoading()
    }

    private fun onFetchError(throwable: Throwable) {
        theView?.showError(R.string.error)
        theView?.hideLoading()
    }

    private fun onVehicleClicked(vehicleAndView: Pair<Vehicle, View>) {
        goToMap(vehicleAndView.first, vehicleAndView.second)
    }
}
