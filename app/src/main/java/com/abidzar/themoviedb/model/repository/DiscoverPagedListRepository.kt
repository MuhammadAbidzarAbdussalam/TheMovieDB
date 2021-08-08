package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.postPerPage
import com.abidzar.themoviedb.model.source.DiscoverDataSource
import com.abidzar.themoviedb.model.source.DiscoverDataSourceFactory
import com.abidzar.themoviedb.model.source.PopularDataSource
import com.abidzar.themoviedb.model.source.PopularDataSourceFactory
import io.reactivex.rxjava3.disposables.CompositeDisposable

class DiscoverPagedListRepository (private val apiService: Service)  {

    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var discoverDataSourceFactory: DiscoverDataSourceFactory

    fun fetchLiveMoviePagedList (compositeDisposable: CompositeDisposable, genreId: Int) : LiveData<PagedList<Movie>> {
        discoverDataSourceFactory = DiscoverDataSourceFactory(apiService, compositeDisposable, genreId)

        val config:PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(postPerPage)
            .build()

        moviePagedList = LivePagedListBuilder(discoverDataSourceFactory, config).build()

        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState>{
        return Transformations.switchMap<DiscoverDataSource, NetworkState>(discoverDataSourceFactory.discoverLiveDataSource, DiscoverDataSource::networkState)
    }

}