package ena.min.android.onlinetaxi.core

import com.google.android.gms.maps.model.LatLng

class Const {
    companion object {
        val HAMBURG_AREA = doubleArrayOf(53.694865, 9.757589, 53.394655, 10.099891)
        val HAMBURG_CENTER = LatLng(53.549086, 9.992670)
    }
}

enum class FleetType(val type: String) {
    POOLING("POOLING"),
    TAXI("TAXI")
}