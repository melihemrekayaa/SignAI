package com.signai.ui.screens.creation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --------------------------------------
// MODELS
// --------------------------------------

enum class SignatureInputMode {
    Draw,
    Type
}

data class SignatureStyleItem(
    val id: String,
    val label: String
)

data class StrokeSegment(
    val path: Path,
    val strokeWidthPx: Float
)

// --------------------------------------
// ROOT SCREEN
// --------------------------------------

@Composable
fun CreateSignatureScreen(
    onBackClick: () -> Unit = {},
    onOpenSettingsClick: () -> Unit = {},
    onGenerateClick: () -> Unit = {}
) {
    var inputMode by rememberSaveable { mutableStateOf(SignatureInputMode.Draw) }

    // Çizim durumu
    var strokes by remember { mutableStateOf<List<StrokeSegment>>(emptyList()) }
    var undoneStrokes by remember { mutableStateOf<List<StrokeSegment>>(emptyList()) }

    val canUndo = strokes.isNotEmpty()
    val canRedo = undoneStrokes.isNotEmpty()

    val styles = remember {
        listOf(
            SignatureStyleItem("professional", "Professional"),
            SignatureStyleItem("elegant", "Elegant"),
            SignatureStyleItem("casual", "Casual"),
            SignatureStyleItem("creative", "Creative")
        )
    }
    var selectedStyleId by rememberSaveable { mutableStateOf("professional") }

    val thicknessOptions = listOf(2f, 3f, 4f, 6f, 8f)
    var selectedThicknessIndex by remember { mutableStateOf(2) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "Create Signature",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(onClick = onOpenSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGenerateClick
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoFixHigh,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Generate Signature")
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Later: maybe preview */ }
                ) {
                    Text(text = "Skip for now")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SignatureModeTabs(
                currentMode = inputMode,
                onModeChange = { inputMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ÇİZİM ALANI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.22f
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        when (inputMode) {
                            SignatureInputMode.Draw -> SignatureDrawingCanvas(
                                strokes = strokes,
                                selectedStrokeWidthDp = thicknessOptions[selectedThicknessIndex],
                                onStrokeCompleted = { newStroke ->
                                    strokes = strokes + newStroke
                                    undoneStrokes = emptyList()
                                }
                            )

                            SignatureInputMode.Type -> TypePlaceholder()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (inputMode) {
                    SignatureInputMode.Draw -> "Draw your signature in the area above."
                    SignatureInputMode.Type -> "Typing mode is coming soon."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // KALINLIK SEÇİMİ
            Text(
                text = "Stroke thickness",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ThicknessSelectorRow(
                optionsDp = thicknessOptions,
                selectedIndex = selectedThicknessIndex,
                onSelectedChange = { selectedThicknessIndex = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // UNDO / REDO / CLEAR BUTONLARI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallActionButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = "Undo",
                    enabled = canUndo
                ) {
                    if (strokes.isNotEmpty()) {
                        val last = strokes.last()
                        strokes = strokes.dropLast(1)
                        undoneStrokes = undoneStrokes + last
                    }
                }

                SmallActionButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    label = "Redo",
                    enabled = canRedo
                ) {
                    if (undoneStrokes.isNotEmpty()) {
                        val last = undoneStrokes.last()
                        undoneStrokes = undoneStrokes.dropLast(1)
                        strokes = strokes + last
                    }
                }

                SmallActionButton(
                    icon = Icons.Filled.Clear,
                    label = "Clear",
                    enabled = strokes.isNotEmpty() || undoneStrokes.isNotEmpty()
                ) {
                    strokes = emptyList()
                    undoneStrokes = emptyList()
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // STİL SEÇİMİ
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Choose a style",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.forEach { style ->
                        val selected = style.id == selectedStyleId
                        StyleChip(
                            label = style.label,
                            selected = selected,
                            onClick = { selectedStyleId = style.id }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI SONUÇ ALANI (Placeholder)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your AI signatures",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.18f
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Your signatures will appear here\nafter generation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --------------------------------------
// COMPONENTLER
// --------------------------------------

@Composable
private fun SignatureModeTabs(
    currentMode: SignatureInputMode,
    onModeChange: (SignatureInputMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeTab(
            label = "Draw",
            selected = currentMode == SignatureInputMode.Draw,
            onClick = { onModeChange(SignatureInputMode.Draw) }
        )
        ModeTab(
            label = "Type",
            selected = currentMode == SignatureInputMode.Type,
            onClick = { onModeChange(SignatureInputMode.Type) }
        )
    }
}

@Composable
private fun RowScope.ModeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val content =
        if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = content
        )
    }
}

@Composable
private fun SmallActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val bgColor =
        if (enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)

    val contentColor =
        if (enabled) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = contentColor
        )
    }
}

@Composable
private fun RowScope.StyleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (selected) Color.White
        else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ThicknessSelectorRow(
    optionsDp: List<Float>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        optionsDp.forEachIndexed { index, dpValue ->
            val selected = index == selectedIndex
            ThicknessDot(
                thicknessDp = dpValue,
                selected = selected,
                onClick = { onSelectedChange(index) }
            )
        }
    }
}

@Composable
private fun ThicknessDot(
    thicknessDp: Float,
    selected: Boolean,
    onClick: () -> Unit
) {
    val outerSize = 32.dp
    val innerSize = thicknessDp.dp.coerceAtLeast(4.dp)

    Box(
        modifier = Modifier
            .size(outerSize)
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
private fun SignatureDrawingCanvas(
    strokes: List<StrokeSegment>,
    selectedStrokeWidthDp: Float,
    onStrokeCompleted: (StrokeSegment) -> Unit
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { selectedStrokeWidthDp.dp.toPx() }

    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(selectedStrokeWidthDp) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPathPoints = listOf(offset)
                    },
                    onDrag = { change, _ ->
                        currentPathPoints = currentPathPoints + change.position
                    },
                    onDragEnd = {
                        if (currentPathPoints.isNotEmpty()) {
                            val finalPath = createSmoothPath(currentPathPoints)
                            onStrokeCompleted(
                                StrokeSegment(
                                    path = finalPath,
                                    strokeWidthPx = strokeWidthPx
                                )
                            )
                        }
                        currentPathPoints = emptyList()
                    },
                    onDragCancel = {
                        currentPathPoints = emptyList()
                    }
                )
            }
    ) {
        strokes.forEach { stroke ->
            drawPath(
                path = stroke.path,
                color = Color.Black,
                style = Stroke(
                    width = stroke.strokeWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        if (currentPathPoints.size >= 2) {
            val tempPath = createSmoothPath(currentPathPoints)
            drawPath(
                path = tempPath,
                color = Color.Black,
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun createSmoothPath(points: List<Offset>): Path {
    if (points.isEmpty()) return Path()

    val path = Path()

    if (points.size == 1) {
        path.moveTo(points[0].x, points[0].y)
        return path
    }

    if (points.size == 2) {
        path.moveTo(points[0].x, points[0].y)
        path.lineTo(points[1].x, points[1].y)
        return path
    }

    path.moveTo(points[0].x, points[0].y)

    for (i in 1 until points.size - 1) {
        val currentPoint = points[i]
        val nextPoint = points[i + 1]
        val controlPointX = (currentPoint.x + nextPoint.x) / 2f
        val controlPointY = (currentPoint.y + nextPoint.y) / 2f

        path.quadraticBezierTo(
            currentPoint.x, currentPoint.y,
            controlPointX, controlPointY
        )
    }

    path.lineTo(points.last().x, points.last().y)

    return path
}

@Composable
private fun TypePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Typing mode is coming soon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}