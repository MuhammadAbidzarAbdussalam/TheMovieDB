package com.abidzar.themoviedb.model.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.network.Service
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PopularDataSourceFactory(
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Movie>() {

    val popularLiveDataSource = MutableLiveData<PopularDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val popularDataSource = PopularDataSource(apiService, compositeDisposable)

        popularLiveDataSource.postValue(popularDataSource)
        return popularDataSource
    }

}