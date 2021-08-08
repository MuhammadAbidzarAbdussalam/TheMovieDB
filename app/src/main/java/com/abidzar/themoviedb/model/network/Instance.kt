package com.abidzar.themoviedb.model.network

import com.abidzar.themoviedb.model.data.popular.PopularList
import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

const val baseURL = "https://api.themoviedb.org/3/"
const val apiKey = "bf6d11cb64e709f0cf4f06b6f12ce785"
const val posterBaseURL = "https://image.tmdb.org/t/p/original/"

const val firstPage = 1
const val postPerPage = 20

class Instance {

    companion object {

        fun getInstance() : Service {

            val requestInterceptor = Interceptor {chain ->


                val url : HttpUrl = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build()

                val request:Request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient:OkHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(requestInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseURL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service::class.java)
        }
    }

}