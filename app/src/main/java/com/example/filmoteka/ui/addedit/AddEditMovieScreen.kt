package com.example.filmoteka.ui.addedit

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.R
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategory
import com.example.filmoteka.validators.MovieValidator
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
    val validator = remember { MovieValidator() }

    // Stan dla pól formularza
    var title by remember { mutableStateOf(initialMovie?.title ?: "") }
    var releaseDate by remember { mutableStateOf(initialMovie?.releaseDate ?: System.currentTimeMillis()) }
    var category by remember { mutableStateOf(initialMovie?.category ?: MovieCategory.FILM) }
    var watched by remember { mutableStateOf(initialMovie?.watched ?: false) }
    var rating by remember { mutableStateOf(initialMovie?.rating ?: 0) }
    var imageUri by remember { mutableStateOf(initialMovie?.imageUri) }
    
    // Stan dla błędów walidacji
    var titleError by remember { mutableStateOf<MovieValidator.ValidationError?>(null) }
    var releaseDateError by remember { mutableStateOf<MovieValidator.ValidationError?>(null) }
    var ratingError by remember { mutableStateOf<MovieValidator.ValidationError?>(null) }
    var categoryError by remember { mutableStateOf<MovieValidator.ValidationError?>(null) }
    
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
            // Walidacja daty po wyborze
            releaseDateError = validator.validateReleaseDate(releaseDate)
        },
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.YEAR),
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.MONTH),
        Calendar.getInstance().apply { timeInMillis = releaseDate }.get(Calendar.DAY_OF_MONTH)
    )

    // Funkcja walidująca wszystkie pola
    fun validateForm(): Boolean {
        titleError = validator.validateTitle(title)
        releaseDateError = validator.validateReleaseDate(releaseDate)
        ratingError = validator.validateRating(watched, rating)
        categoryError = validator.validateCategory(category)
        
        return titleError == null && releaseDateError == null && 
               ratingError == null && categoryError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (initialMovie == null) 
                            stringResource(R.string.add_movie_title) 
                        else 
                            stringResource(R.string.edit_movie_title)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plakat filmu - o stałej wysokości 250dp, wyśrodkowany
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                // Tło (w przypadku gdy nie ma obrazu)
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .aspectRatio(2f/3f) // Typowe proporcje plakatu filmowego
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pickedImageUri == null && imageUri == null) {
                        Text(
                            text = stringResource(R.string.select_poster),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                
                // Obraz plakatu
                if (pickedImageUri != null || imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = pickedImageUri ?: imageUri
                        ),
                        contentDescription = stringResource(R.string.movie_poster),
                        modifier = Modifier
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }

            // Tytuł filmu pod plakatem
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = validator.validateTitle(title)
                },
                label = { Text(stringResource(R.string.movie_title)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                isError = titleError != null,
                supportingText = {
                    titleError?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = it.resourceId),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )

            // Informacje o filmie w układzie karty
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Data premiery
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            Text(
                                text = stringResource(
                                    R.string.movie_release_date, 
                                    dateFormat.format(Date(releaseDate))
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (releaseDateError != null) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                            
                            releaseDateError?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(id = it.resourceId),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                Icons.Default.DateRange, 
                                contentDescription = stringResource(R.string.pick_date),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Kategoria
                    Column {
                        Text(
                            stringResource(R.string.filter_category),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (categoryError != null) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                        
                        categoryError?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = it.resourceId),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        MovieCategory.values().forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = category == cat,
                                    onClick = { 
                                        category = cat
                                        categoryError = null
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    cat.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Status obejrzenia
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = watched,
                            onCheckedChange = { 
                                watched = it
                                if (it) {
                                    // Gdy zaznaczamy jako obejrzane, sprawdzamy czy rating jest ustawiony
                                    ratingError = validator.validateRating(true, rating)
                                } else {
                                    // Gdy odznaczamy (nieobejrzane), resetujemy błąd oceny
                                    ratingError = null
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            stringResource(R.string.watched),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Ocena (jeśli obejrzany)
                    if (watched) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(R.string.movie_rating, rating),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (ratingError != null) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                ratingError?.let {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(id = it.resourceId),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            
                            Slider(
                                value = rating.toFloat(),
                                onValueChange = { 
                                    rating = it.toInt()
                                    ratingError = validator.validateRating(watched, rating)
                                },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                            
                            // Gwiazdki reprezentujące ocenę
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < rating) 
                                            MaterialTheme.colorScheme.tertiary 
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Przyciski akcji
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
                
                Button(
                    onClick = {
                        if (validateForm()) {
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
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.save))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
