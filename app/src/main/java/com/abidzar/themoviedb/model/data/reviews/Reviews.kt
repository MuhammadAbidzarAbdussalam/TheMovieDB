package com.abidzar.themoviedb.model.data.reviews

data class Reviews(
    val id: Int,
    val page: Int,
    val results: List<ReviewResult>,
    val total_pages: Int,
    val total_results: Int
)