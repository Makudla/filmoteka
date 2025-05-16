package com.example.filmoteka.data.model

import androidx.room.TypeConverter

/**
 * Konwerter dla Room do konwersji pomiędzy MovieCategory a String.
 * Pozwala na przechowywanie wartości enum w bazie danych.
 */
class MovieCategoryConverter {
    @TypeConverter
    fun fromMovieCategory(category: MovieCategory): String {
        return category.name
    }

    @TypeConverter
    fun toMovieCategory(categoryName: String): MovieCategory {
        return try {
            MovieCategory.valueOf(categoryName)
        } catch (e: IllegalArgumentException) {
            // Wartość domyślna w przypadku błędu
            MovieCategory.INNA
        }
    }
}
