package com.example.filmoteka.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Reprezentuje dzieło filmowe w bazie danych.
 */
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val releaseDate: Long,
    val category: MovieCategory,
    val watched: Boolean = false,
    val rating: Int? = null,
    val imageUri: String? = null
)
