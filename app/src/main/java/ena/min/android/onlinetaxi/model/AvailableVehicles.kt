package ena.min.android.onlinetaxi.model

import ena.min.android.onlinetaxi.data.Vehicle
import ena.min.android.onlinetaxi.network.AvailableVehiclesRequest
import ena.min.android.onlinetaxi.network.Network
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class AvailableVehicles {

    var poiList = ArrayList<Vehicle>()

    companion object {
        /**
         * @param area is a double array of size 4 {lat1, lng1, lat2, lng2}
         */
        fun fetch(vararg area: Double): Observable<AvailableVehicles> {
            if (area.size != 4) {
                return Observable.error(IllegalArgumentException("need a Double array of size 4"))
            }

            return Network.newRequest(AvailableVehiclesRequest::class.java)
                    .fetch(area[0], area[1], area[2], area[3])
                    .subscribeOn(Schedulers.newThread())
        }
    }
}

