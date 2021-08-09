package com.abidzar.themoviedb.model.network

import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.genre.Genre
import com.abidzar.themoviedb.model.data.genre.Genres
import com.abidzar.themoviedb.model.data.popular.PopularList
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {

    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Single<PopularList>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int): Single<MovieDetails>

    @GET("discover/movie")
    fun getDiscoverMovies(@Query("page") page: Int, @Query("with_genres") genreId: Int): Single<PopularList>

    @GET("genre/movie/list")
    fun getGenre(): Single<Genres>
}