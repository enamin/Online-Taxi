package ena.min.android.onlinetaxi.network

import ena.min.android.onlinetaxi.model.AvailableVehicles
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// example: https://fake-poi-api.mytaxi.com/?p1Lat=53.694865&p1Lon=9.757589&p2Lat=53.394655&p2Lon=10.099891
interface AvailableVehiclesRequest {
    @GET("/")
    fun fetch(@Query("p1Lat") lat1: Double,
              @Query("p1Lon") lng1: Double,
              @Query("p2Lat") lat2: Double,
              @Query("p2Lon") lng2: Double
    ): Observable<AvailableVehicles>
}