package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.postPerPage
import com.abidzar.themoviedb.model.source.PopularDataSource
import com.abidzar.themoviedb.model.source.PopularDataSourceFactory
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PopularPagedListRepository (private val apiService: Service) {

    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var popularDataSourceFactory: PopularDataSourceFactory

    fun fetchLiveMoviePagedList (compositeDisposable: CompositeDisposable) : LiveData<PagedList<Movie>> {
        popularDataSourceFactory = PopularDataSourceFactory(apiService, compositeDisposable)

        val config:PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(postPerPage)
            .build()

        moviePagedList = LivePagedListBuilder(popularDataSourceFactory, config).build()

        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState>{
        return Transformations.switchMap<PopularDataSource, NetworkState>(popularDataSourceFactory.popularLiveDataSource, PopularDataSource::networkState)
    }

}