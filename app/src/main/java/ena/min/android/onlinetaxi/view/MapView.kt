package ena.min.android.onlinetaxi.view

import android.content.Context
import android.graphics.Color
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.VisibleRegion
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.core.FleetType
import ena.min.android.onlinetaxi.data.Vehicle
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_map.view.*

class MapView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    val mapReady = PublishSubject.create<Unit>()
    val cameraIdle = PublishSubject.create<Unit>()
    val closeTheMap = PublishSubject.create<Unit>()
    private var theMap: GoogleMap? = null

    init {
        if (context is AppCompatActivity) {
            val f = context.supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment?
            if (f != null) {
                context.supportFragmentManager.beginTransaction().remove(f).commit()
            }

            post {
                LayoutInflater.from(context).inflate(R.layout.view_map, this, true)
                vClose.setOnClickListener { closeTheMap.onNext(Unit) }
                pbLoading.isIndeterminate = true
                pbLoading.indeterminateDrawable.setColorFilter(Color.parseColor("#dd673ab7"), android.graphics.PorterDuff.Mode.SRC_IN)
                postDelayed({
                    val mapFragment = context.supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment?
                    mapFragment?.getMapAsync(::onMapReady)
                }, 1000)
            }
        }
    }

    private fun onMapReady(googleMap: GoogleMap) {
        theMap = googleMap
        theMap?.setOnCameraIdleListener(::onCameraIdle)
        mapReady.onNext(Unit)
    }

    private fun onCameraIdle() {
        cameraIdle.onNext(Unit)
    }

    fun getBounds(): VisibleRegion? {
        return theMap?.projection?.visibleRegion
    }

    fun addMarker(markerOptions: MarkerOptions?): Marker? {
        markerOptions ?: return null
        return theMap?.addMarker(markerOptions)
    }

    fun aniamteTo(latLng: LatLng, zoomLevel: Int) {
        theMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel.toFloat()))
    }

    fun jumpTo(latLng: LatLng, zoomLevel: Int) {
        theMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel.toFloat()))
    }

    fun showMessage(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showLoading() {
        pbLoading.visibility = View.VISIBLE
    }

    fun hideLoading() {
        pbLoading.visibility = View.GONE
    }

    fun hideDriverCard() {
        cvDriverCard.visibility = View.GONE
    }

    fun showDriverCard() {
        cvDriverCard.visibility = View.VISIBLE
    }

    fun setDriverInfo(vehicle: Vehicle?) {
        vehicle?: return
        if (vehicle.fleetType == FleetType.TAXI.type) {
            tvIndicator.setBackgroundResource(R.drawable.shape_taxi_indicator)
            tvIndicator.setText(R.string.taxi)
        } else if (vehicle.fleetType == FleetType.POOLING.type) {
            tvIndicator.setBackgroundResource(R.drawable.shape_pooling_indicator)
            tvIndicator.setText(R.string.pooling)
        } else {
            tvIndicator.visibility = View.INVISIBLE
        }
    }
}
