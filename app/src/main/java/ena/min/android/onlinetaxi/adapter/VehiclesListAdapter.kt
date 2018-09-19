package ena.min.android.onlinetaxi.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.core.FleetType
import ena.min.android.onlinetaxi.data.Vehicle
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_vehicle.view.*

class VehiclesListAdapter(var items: List<Vehicle>) : RecyclerView.Adapter<VehiclesListAdapter.VH>() {

    val listClicks: PublishSubject<Pair<Vehicle, View>> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val view = holder.itemView ?: return
        view.setOnClickListener {
            listClicks.onNext(item to view)
        }

        if (item.fleetType == FleetType.TAXI.type) {
            view.tvIndicator.setBackgroundResource(R.drawable.shape_taxi_indicator)
            view.tvIndicator.setText(R.string.taxi)
        } else if (item.fleetType == FleetType.POOLING.type) {
            view.tvIndicator.setBackgroundResource(R.drawable.shape_pooling_indicator)
            view.tvIndicator.setText(R.string.pooling)
        } else {
            view.tvIndicator.visibility = View.INVISIBLE
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}

