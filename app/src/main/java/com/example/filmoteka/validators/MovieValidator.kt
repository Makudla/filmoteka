package com.example.filmoteka.validators

import com.example.filmoteka.R
import com.example.filmoteka.data.model.MovieCategory
import java.util.Calendar

/**
 * Klasa do walidacji danych filmu.
 */
class MovieValidator {
    
    /**
     * Reprezentacja błędu walidacji
     */
    sealed class ValidationError(val resourceId: Int) {
        object EmptyTitle : ValidationError(R.string.error_empty_title)
        object FutureDateTooFar : ValidationError(R.string.error_future_date)
        object EmptyRating : ValidationError(R.string.error_empty_rating)
        object NoCategory : ValidationError(R.string.error_no_category)
    }
    
    /**
     * Wynik walidacji
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<ValidationError> = emptyList()
    )
    
    /**
     * Waliduje tytuł filmu
     */
    fun validateTitle(title: String): ValidationError? {
        return if (title.isBlank()) ValidationError.EmptyTitle else null
    }
    
    /**
     * Waliduje datę premiery
     */
    fun validateReleaseDate(releaseDate: Long): ValidationError? {
        val calendar = Calendar.getInstance()
        
        // Ustawienie kalendarza na 2 lata w przyszłość
        calendar.add(Calendar.YEAR, 2)
        val twoYearsFromNow = calendar.timeInMillis
        
        return if (releaseDate > twoYearsFromNow) ValidationError.FutureDateTooFar else null
    }
    
    /**
     * Waliduje ocenę dla obejrzanego filmu
     */
    fun validateRating(watched: Boolean, rating: Int?): ValidationError? {
        return if (watched && (rating == null || rating <= 0)) ValidationError.EmptyRating else null
    }
    
    /**
     * Waliduje kategorię
     */
    fun validateCategory(category: MovieCategory?): ValidationError? {
        return if (category == null) ValidationError.NoCategory else null
    }
    
    /**
     * Waliduje wszystkie pola formularza
     */
    fun validate(
        title: String,
        releaseDate: Long,
        category: MovieCategory?,
        watched: Boolean,
        rating: Int?
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        validateTitle(title)?.let { errors.add(it) }
        validateReleaseDate(releaseDate)?.let { errors.add(it) }
        validateRating(watched, rating)?.let { errors.add(it) }
        validateCategory(category)?.let { errors.add(it) }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}
