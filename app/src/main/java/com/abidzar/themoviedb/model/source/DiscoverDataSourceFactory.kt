package com.abidzar.themoviedb.model.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.network.Service
import io.reactivex.rxjava3.disposables.CompositeDisposable

class DiscoverDataSourceFactory(
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable, private val genreId: Int
) : DataSource.Factory<Int, Movie>() {

    val discoverLiveDataSource = MutableLiveData<DiscoverDataSource>()
    lateinit var discoverDataSource: DiscoverDataSource

    override fun create(): DataSource<Int, Movie> {
        discoverDataSource = DiscoverDataSource(apiService, compositeDisposable, genreId)

        discoverLiveDataSource.postValue(discoverDataSource)
        return discoverDataSource
    }

    fun invalidateDataSource() {
        if (discoverDataSource != null) {
            discoverDataSource.invalidate()
        }
    }

}