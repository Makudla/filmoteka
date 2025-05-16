package com.example.filmoteka.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.data.model.Movie
import java.text.SimpleDateFormat
import java.util.*

/**
 * Komponent wyświetlający pojedynczy film w liście.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieListItem(
    movie: Movie,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Obrazek plakatu
            movie.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Tekstowe informacje o filmie
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Premiera: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(movie.releaseDate))}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Kategoria: ${movie.category}",
                    style = MaterialTheme.typography.bodySmall
                )

                movie.rating?.let { ratingValue ->
                    Text(
                        text = "Ocena: $ratingValue/5",
                        style = MaterialTheme.typography.bodySmall
                    )
                } ?: run {
                    Text(
                        text = "Status: ${if (movie.watched) "Obejrzany" else "Nieobejrzany"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
