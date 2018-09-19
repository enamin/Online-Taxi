package ena.min.android.onlinetaxi.core

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewCompat
import android.view.View
import ena.min.android.onlinetaxi.presenter.AbsPresenter
import ena.min.android.onlinetaxi.presenter.VehiclesListPresenter
import kotlin.properties.Delegates

class Router {
    companion object {
        private var supportFragmentManager: FragmentManager by Delegates.notNull()
        private var containerResId: Int by Delegates.notNull()

        fun init(supportFragmentManager: FragmentManager, containerResId: Int) {
            Router.supportFragmentManager = supportFragmentManager
            Router.containerResId = containerResId
        }

        fun goTo(presenter: AbsPresenter<*>, sharedElements: Array<View?>? = null) {
            val transaction = supportFragmentManager.beginTransaction()
            sharedElements?.let {
                it.filterNotNull().forEach {
                    transaction.addSharedElement(it, ViewCompat.getTransitionName(it))
                }
            }
            transaction.replace(containerResId, presenter)
            decideOnBackStack(transaction, presenter)

            transaction.commit()
        }

        private fun decideOnBackStack(transaction: FragmentTransaction, presenter: AbsPresenter<*>): FragmentTransaction {
            if (presenter is VehiclesListPresenter) {
                return transaction
            }

            transaction.addToBackStack(null)
            return transaction
        }

    }
}