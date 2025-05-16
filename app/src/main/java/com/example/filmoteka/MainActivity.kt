package com.example.filmoteka

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.ui.navigation.MovieNavigationHost
import com.example.filmoteka.ui.theme.FilmotekaTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.filmoteka.data.model.MovieCategory
import com.example.filmoteka.ui.list.MovieListItem
import com.example.filmoteka.data.model.MovieViewModel

/**
 * Główna aktywność aplikacji - obsługuje całą nawigację i logikę UI.
 */
class MainActivity : ComponentActivity() {

    // Inicjalizacja ViewModelu
    private lateinit var movieViewModel: MovieViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Log.d("Permissions", "All gallery permissions granted")
        } else {
            Toast.makeText(this, "Gallery permissions needed to display images", Toast.LENGTH_LONG)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicjalizacja ViewModelu
        movieViewModel = ViewModelProvider(
            this, 
            MovieViewModel.Factory(application)
        )[MovieViewModel::class.java]
        
        enableEdgeToEdge()
        requestGalleryPermissions()
        setContent {
            FilmotekaTheme {
                MovieNavigationHost(movieViewModel)
            }
        }
    }

    private fun requestGalleryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    @Preview(showBackground = true)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ItemPreview() {
        FilmotekaTheme {
            MovieListItem(
                movie = Movie(
                    id = 0,
                    imageUri = "file:///android_asset/missing.jpg",
                    title = "Inception",
                    releaseDate = System.currentTimeMillis(),
                    category = MovieCategory.INNA,
                    watched = false,
                    rating = null
                ),
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
