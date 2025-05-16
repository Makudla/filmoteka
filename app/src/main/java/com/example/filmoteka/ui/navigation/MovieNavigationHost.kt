package com.example.filmoteka.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.filmoteka.ui.addedit.AddEditMovieScreen
import com.example.filmoteka.ui.detail.MovieDetailScreen
import com.example.filmoteka.ui.list.MovieListScreen
import com.example.filmoteka.data.model.MovieViewModel

@Composable
fun MovieNavigationHost(
    movieViewModel: MovieViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Stan dla wybranego obrazu
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    NavHost(
        navController = navController,
        startDestination = "movieList"
    ) {
        // Ekran listy filmów
        composable("movieList") {
            val movies = movieViewModel.allMovies
            MovieListScreen(
                movies = movies,
                onAddMovie = { navController.navigate("addMovie") },
                onMovieClick = { movie ->
                    // Jeśli film nie jest obejrzany, przekieruj do ekranu edycji
                    // W przeciwnym razie pokaż szczegóły
                    if (!movie.watched) {
                        navController.navigate("editMovie/${movie.id}")
                    } else {
                        navController.navigate("movieDetail/${movie.id}")
                    }
                },
                onMovieLongClick = { movie ->
                    // Implementacja usuwania filmu
                    movieViewModel.deleteMovie(movie)
                }
            )
        }

        // Ekran szczegółów filmu
        composable(
            route = "movieDetail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val movieFlow = movieViewModel.getMovieById(movieId)
            val movie by movieFlow.collectAsState(initial = null)

            movie?.let {
                MovieDetailScreen(
                    movie = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Ekran dodawania filmu
        composable("addMovie") {
            AddEditMovieScreen(
                initialMovie = null,
                pickedImageUri = pickedImageUri,
                onPickImage = { uri -> pickedImageUri = uri },
                onSave = { movie ->
                    movieViewModel.addMovie(movie)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // Ekran edycji filmu
        composable(
            route = "editMovie/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val movieFlow = movieViewModel.getMovieById(movieId)
            val movie by movieFlow.collectAsState(initial = null)

            movie?.let {
                AddEditMovieScreen(
                    initialMovie = it,
                    pickedImageUri = pickedImageUri,
                    onPickImage = { uri -> pickedImageUri = uri },
                    onSave = { updatedMovie ->
                        movieViewModel.updateMovie(updatedMovie)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
