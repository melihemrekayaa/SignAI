package com.signai.ui.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
// Standart İkonlar (Kütüphane gerektirmez, %100 çalışır)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.signai.domain.model.SignatureStyle
import com.signai.features.signature.creation.CreateSignatureViewModel
import com.signai.ui.components.StyleCard
// DÜZELTME: KoinInject Importu
import org.koin.compose.koinInject

@Composable
fun StyleSelectionScreen(
    onNextClick: () -> Unit,
    // DÜZELTME: koinViewModel() yerine koinInject() kullanıyoruz
    viewModel: CreateSignatureViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            Button(
                onClick = onNextClick,
                enabled = state.selectedStyle != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Text("Next Step")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Choose your style",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select a style that matches your needs.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SignatureStyle.entries) { style ->
                    StyleCard(
                        title = style.name.lowercase().replaceFirstChar { it.uppercase() },
                        description = getStyleDescription(style),
                        icon = getStyleIcon(style),
                        isSelected = state.selectedStyle == style,
                        onClick = { viewModel.onStyleSelected(style) }
                    )
                }
            }
        }
    }
}

fun getStyleDescription(style: SignatureStyle): String {
    return when(style) {
        SignatureStyle.PROFESSIONAL -> "Clean, corporate & serious"
        SignatureStyle.ARTISTIC -> "Expressive, unique & bold"
        SignatureStyle.CASUAL -> "Relaxed, simple & friendly"
        SignatureStyle.RETRO -> "Classic, vintage & ink"
    }
}

// Standart ikonlara çevrildi (Hata vermez)
fun getStyleIcon(style: SignatureStyle): ImageVector {
    return when(style) {
        SignatureStyle.PROFESSIONAL -> Icons.Default.AccountCircle
        SignatureStyle.ARTISTIC -> Icons.Default.Star
        SignatureStyle.CASUAL -> Icons.Default.Create
        SignatureStyle.RETRO -> Icons.Default.Edit
    }
}