package ena.min.android.onlinetaxi.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {

    companion object {
        private val retrofit = Retrofit.Builder()
                .baseUrl("https://fake-poi-api.mytaxi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        fun <T> newRequest(clazz: Class<T>): T {
            return retrofit.create(clazz)
        }
    }
}