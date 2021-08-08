package com.abidzar.themoviedb.model.data.popular

data class PopularList(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)