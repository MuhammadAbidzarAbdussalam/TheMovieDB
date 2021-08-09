package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.genre.Genre
import com.abidzar.themoviedb.model.data.genre.Genres
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.source.GenreDataSource
import com.abidzar.themoviedb.model.source.MovieDetailDataSource
import io.reactivex.rxjava3.disposables.CompositeDisposable

class GenresRepository (private val apiService: Service) {

    lateinit var genreDataSource: GenreDataSource

    fun fetchGenres (compositeDisposable: CompositeDisposable): LiveData<Genres> {

        genreDataSource = GenreDataSource(apiService, compositeDisposable)
        genreDataSource.fetchDataDetails()

        return genreDataSource.downloadedGenresResponse
    }

    fun getGenreNetworkState(): LiveData<NetworkState> {
        return genreDataSource.networkState
    }

}