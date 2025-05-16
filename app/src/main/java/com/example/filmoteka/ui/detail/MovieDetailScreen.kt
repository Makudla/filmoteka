package com.example.filmoteka.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.data.model.Movie
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ekran pokazujący szczegóły wybranego filmu.
 * Brak możliwości edycji, tylko podgląd.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły filmu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            movie.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Premiera: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(movie.releaseDate))}")
            Text(text = "Kategoria: ${movie.category}")
            Text(text = "Status: ${if (movie.watched) "Obejrzany" else "Nieobejrzany"}")

            movie.rating?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Ocena: $it/5")
            }
        }
    }
}
