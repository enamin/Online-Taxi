package ena.min.android.onlinetaxi.core

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ena.min.android.onlinetaxi.R
import ena.min.android.onlinetaxi.presenter.VehiclesListPresenter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Router.init(supportFragmentManager, R.id.flFragmentContainer)
        Router.goTo(VehiclesListPresenter())
    }
}
