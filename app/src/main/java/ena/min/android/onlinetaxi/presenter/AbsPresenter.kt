package ena.min.android.onlinetaxi.presenter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

abstract class AbsPresenter<V: View>: Fragment() {

    var theView: V? = null
    val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        theView = createView(container?.context)
        return theView
    }

    abstract fun createView(context: Context?): V?

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}