package com.signai.android.onboarding

import android.graphics.PathMeasure
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signai.android.SignAIOnboardingViewModel
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    viewModel: SignAIOnboardingViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Başlıklar
        Text(
            text = "SignAI",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Seni tanıyalım, sana özel imza tasarlayalım.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Form alanları
        OutlinedTextField(
            value = state.fullName,
            onValueChange = viewModel::onFullNameChange,
            label = { Text("Ad Soyad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.nickname,
            onValueChange = viewModel::onNicknameChange,
            label = { Text("Kısa ad / lakap (opsiyonel)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.occupation,
            onValueChange = viewModel::onOccupationChange,
            label = { Text("Meslek (opsiyonel)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::onGenerateClicked,
            enabled = !state.isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(if (state.isGenerating) "Üretiliyor..." else "İmza fikri üret")
        }

        // Prompt metni
        state.aiPrompt?.let { prompt ->
            Text(
                text = "AI'ye gidecek prompt:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 16.sp
            )
        }

        // AI imza tarifi
        state.aiInstructions?.let { instructions ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI'nin imza tarifi (şimdilik demo):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = instructions,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 16.sp
            )
        }

        // Otomatik çizilen imza animasyonu
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "İmza animasyon önizlemesi:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SignaturePreviewCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        // Kullanıcı imza deneme alanı
        Spacer(modifier = Modifier.height(24.dp))
        SignaturePracticePad()
    }
}

/**
 * İnce siyah çizgiyle, gerçekten “kalemle çiziliyormuş” gibi açılan imza animasyonu.
 */
@Composable
private fun SignaturePreviewCanvas(
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            val durationMs = 1800
            val steps = 90
            for (step in 0..steps) {
                progress = step / steps.toFloat()
                delay(durationMs.toLong() / steps)
            }
            delay(500)
            progress = 0f
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val baseline = height * 0.65f

        val fullPath = Path().apply {
            moveTo(width * 0.05f, baseline)

            // ilk büyük harf gibi yükselen kısım
            cubicTo(
                width * 0.15f, height * 0.10f,
                width * 0.22f, height * 0.95f,
                width * 0.30f, baseline
            )

            // ortadaki dalgalı harfler
            cubicTo(
                width * 0.40f, height * 0.30f,
                width * 0.50f, height * 0.90f,
                width * 0.60f, baseline * 1.02f
            )

            cubicTo(
                width * 0.72f, height * 0.40f,
                width * 0.82f, height * 0.80f,
                width * 0.92f, baseline * 0.96f
            )

            // sonda ince kuyruk
            lineTo(width * 0.98f, baseline * 0.92f)
        }

        val androidPath = fullPath.asAndroidPath()
        val measure = PathMeasure(androidPath, false)
        val partial = android.graphics.Path()
        val length = measure.length
        val clamped = progress.coerceIn(0f, 1f)
        measure.getSegment(0f, length * clamped, partial, true)
        val animatedPath = partial.asComposePath()

        drawPath(
            path = animatedPath,
            color = Color.Black,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Kullanıcının parmağıyla imza atabildiği basit imza pad’i.
 */
@Composable
private fun SignaturePracticePad(
    modifier: Modifier = Modifier
) {
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "İmza deneme alanı:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.large)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isSaved = false
                            currentPath = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            currentPath = currentPath + change.position
                            change.consume()
                        },
                        onDragEnd = {
                            if (currentPath.isNotEmpty()) {
                                paths += currentPath
                            }
                            currentPath = emptyList()
                        },
                        onDragCancel = {
                            currentPath = emptyList()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )

                // bitmiş path’ler
                paths.forEach { points ->
                    if (points.size > 1) {
                        val path = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (p in points.drop(1)) {
                                lineTo(p.x, p.y)
                            }
                        }
                        drawPath(path, color = Color.Black, style = stroke)
                    }
                }

                // o anda çizilen path
                if (currentPath.size > 1) {
                    val path = Path().apply {
                        moveTo(currentPath.first().x, currentPath.first().y)
                        for (p in currentPath.drop(1)) {
                            lineTo(p.x, p.y)
                        }
                    }
                    drawPath(path, color = Color.Black, style = stroke)
                }
            }

            if (paths.isEmpty() && currentPath.isEmpty()) {
                Text(
                    text = "Parmağınla burada imzanı dene",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    paths.clear()
                    currentPath = emptyList()
                    isSaved = false
                }
            ) {
                Text("Temizle")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    // Şimdilik “kaydedildi” sadece state; ileride bitmap’e çevirip
                    // shared module’e iletiriz.
                    isSaved = paths.isNotEmpty()
                }
            ) {
                Text("Kaydet")
            }
        }

        if (isSaved) {
            Text(
                text = "İmzan kaydedildi (şimdilik demo).",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
