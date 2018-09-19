package ena.min.android.onlinetaxi.data

import java.io.Serializable

data class Vehicle(
        var id: String,
        var coordinate: Coordinate,
        var fleetType: String,
        var heading: Double
) : Serializable