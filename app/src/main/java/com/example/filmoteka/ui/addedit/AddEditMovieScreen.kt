package com.example.filmoteka.ui.addedit

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ekran umożliwiający dodanie nowego lub edycję istniejącego filmu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    initialMovie: Movie? = null,
    pickedImageUri: Uri?,
    onPickImage: (Uri) -> Unit,
    onSave: (Movie) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Stan dla pól formularza
    var title by remember { mutableStateOf(initialMovie?.title ?: "") }
    var releaseDate by remember { mutableStateOf(initialMovie?.releaseDate ?: System.currentTimeMillis()) }
    var category by remember { mutableStateOf(initialMovie?.category ?: MovieCategory.FILM) }
    var watched by remember { mutableStateOf(initialMovie?.watched ?: false) }
    var rating by remember { mutableStateOf(initialMovie?.rating ?: 0) }
    var imageUri by remember { mutableStateOf(initialMovie?.imageUri) }
    
    // Dla wybrania obrazka z galerii
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it.toString()
            onPickImage(it)
        }
    }
    
    // Dialog wyboru daty
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            releaseDate = calendar.timeInMillis
        },
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.YEAR),
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.MONTH),
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initialMovie == null) "Dodaj film" else "Edytuj film") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Pole na plakat filmu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (pickedImageUri != null || imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = pickedImageUri ?: imageUri ?: "file:///android_asset/missing.jpg"
                        ),
                        contentDescription = "Plakat filmu",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Kliknij, aby wybrać plakat", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tytuł
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Data premiery
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data premiery: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(releaseDate))}",
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Wybierz datę")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Kategoria
            Text("Kategoria:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MovieCategory.values().forEach { cat ->
                    RadioButton(
                        selected = category == cat,
                        onClick = { category = cat }
                    )
                    Text(cat.name)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status obejrzenia
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = watched,
                    onCheckedChange = { watched = it }
                )
                Text("Obejrzany")
            }
            
            // Ocena (jeśli obejrzany)
            if (watched) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ocena:")
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text("Ocena: $rating/5")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Przyciski akcji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anuluj")
                }
                
                Button(
                    onClick = {
                        val movieToSave = Movie(
                            id = initialMovie?.id ?: 0,
                            title = title,
                            releaseDate = releaseDate,
                            category = category,
                            watched = watched,
                            rating = if (watched) rating else null,
                            imageUri = imageUri ?: pickedImageUri?.toString()
                        )
                        onSave(movieToSave)
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Zapisz")
                }
            }
        }
    }
}
