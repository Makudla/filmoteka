package com.example.filmoteka.ui.list

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.filmoteka.R
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategory
import kotlinx.coroutines.flow.Flow

/**
 * Ekran listy filmów.
 * Wyświetla wszystkie filmy i umożliwia kliknięcia oraz filtrowanie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    movies: Flow<List<Movie>>,
    onAddMovie: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onMovieLongClick: (Movie) -> Unit
) {
    val movieList by movies.collectAsState(initial = emptyList())
    val context = LocalContext.current
    
    // Stan dla filtrów
    var selectedCategory by remember { mutableStateOf<MovieCategory?>(null) }
    var filterByWatched by remember { mutableStateOf<Boolean?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Stan dla usuwania
    var showDeleteDialog by remember { mutableStateOf(false) }
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }
    
    // Wczytanie logo z assets
    val logoBitmap = remember { loadBitmapFromAssets(context, "logo_2.jpg") }
    
    // Filtrowanie filmów
    val filteredMovies = movieList.filter { movie ->
        (selectedCategory == null || movie.category == selectedCategory) &&
        (filterByWatched == null || movie.watched == filterByWatched)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Używamy obrazu z assets
                        logoBitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(stringResource(R.string.app_name))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_movie))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Aktywne filtry z przyciskiem filtrowania obok
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filters),
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Przycisk filtrowania
                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown, 
                        contentDescription = stringResource(R.string.filter_movies),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Wyświetlanie aktywnych filtrów
                Text(
                    text = when {
                        selectedCategory != null && filterByWatched != null -> 
                            "${selectedCategory?.name}, ${if (filterByWatched == true) stringResource(R.string.filter_watched) else stringResource(R.string.filter_unwatched)}"
                        selectedCategory != null -> 
                            selectedCategory?.name ?: ""
                        filterByWatched != null -> 
                            if (filterByWatched == true) stringResource(R.string.filter_watched) else stringResource(R.string.filter_unwatched)
                        else -> stringResource(R.string.filter_all)
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (selectedCategory != null || filterByWatched != null) {
                    TextButton(onClick = { 
                        selectedCategory = null
                        filterByWatched = null
                    }) {
                        Text(stringResource(R.string.clear))
                    }
                }
            }

            // Podsumowanie - liczba pozycji
            Text(
                text = stringResource(R.string.items_count, filteredMovies.size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies) { movie ->
                    MovieListItem(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onLongClick = { 
                            // dialog usuwania po długim przytrzymaniu
                            movieToDelete = movie
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
        
        // Dialog filtrowania
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(stringResource(R.string.filter_movies)) },
                text = {
                    Column {
                        Text(stringResource(R.string.filter_category), style = MaterialTheme.typography.titleMedium)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null }
                            )
                            Text(stringResource(R.string.filter_all))
                        }
                        
                        MovieCategory.values().forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                                Text(category.name)
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                        
                        Text(stringResource(R.string.filter_status), style = MaterialTheme.typography.titleMedium)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == null,
                                onClick = { filterByWatched = null }
                            )
                            Text(stringResource(R.string.filter_all))
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == true,
                                onClick = { filterByWatched = true }
                            )
                            Text(stringResource(R.string.filter_watched))
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == false,
                                onClick = { filterByWatched = false }
                            )
                            Text(stringResource(R.string.filter_unwatched))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text(stringResource(R.string.filter_apply))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        selectedCategory = null
                        filterByWatched = null
                        showFilterDialog = false
                    }) {
                        Text(stringResource(R.string.filter_reset))
                    }
                }
            )
        }
        
        // Dialog usuwania
        if (showDeleteDialog && movieToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    movieToDelete = null
                },
                title = { Text(stringResource(R.string.delete_movie_title)) },
                text = { 
                    Text(stringResource(R.string.delete_movie_message, movieToDelete?.title ?: ""))
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            movieToDelete?.let { onMovieLongClick(it) }
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { 
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

// Funkcja pomocnicza do wczytania obrazu z assets
private fun loadBitmapFromAssets(context: Context, path: String): ImageBitmap? {
    return try {
        val inputStream = context.assets.open(path)
        android.graphics.BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
