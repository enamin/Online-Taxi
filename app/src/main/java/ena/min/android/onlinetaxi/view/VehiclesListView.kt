package ena.min.android.onlinetaxi.view

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.adapter.VehiclesListAdapter
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_vehicle_list.view.*

class VehiclesListView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_vehicle_list, this, true)
        toolbarVehicleList.setTitle(R.string.toolbar_vehicle_list)
        (context as? AppCompatActivity)?.setSupportActionBar(toolbarVehicleList)
        rvVehicleList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        srlVehicleList.setColorSchemeResources(R.color.colorPrimary)
        srlVehicleList.setOnRefreshListener { onUserRefreshedTheList.onNext(Unit) }
        fabMap.setOnClickListener { fabMapClicks.onNext(Unit) }
        fabMap.startAnimation(AnimationUtils.loadAnimation(context, R.anim.beating))
    }

    val onUserRefreshedTheList: PublishSubject<Unit> = PublishSubject.create()
    val fabMapClicks: PublishSubject<Unit> = PublishSubject.create()

    fun showLoading() {
        srlVehicleList.isRefreshing = true
    }

    fun hideLoading() {
        srlVehicleList.isRefreshing = false
    }

    fun setAdapter(adapter: VehiclesListAdapter) {
        rvVehicleList.adapter = adapter
    }

    fun showError(@StringRes error: Int) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }
}