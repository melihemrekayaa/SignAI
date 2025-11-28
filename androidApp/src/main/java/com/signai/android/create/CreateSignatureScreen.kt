package com.signai.android.create

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class SignatureInputMode {
    Draw, Type
}

data class SignatureStyle(
    val id: String,
    val label: String
)

private data class SignatureStroke(
    val path: Path,
    val strokeWidthPx: Float
)

@Composable
fun CreateSignatureScreen(
    onBackClick: () -> Unit = {},
    onOpenSettingsClick: () -> Unit = {},
    onGenerateClick: () -> Unit = {}
) {
    // Draw / Type tab
    var inputMode by remember { mutableStateOf(SignatureInputMode.Draw) }

    // Strokes + undo / redo
    var strokes by remember { mutableStateOf<List<SignatureStroke>>(emptyList()) }
    var undoneStrokes by remember { mutableStateOf<List<SignatureStroke>>(emptyList()) }

    val canUndo = strokes.isNotEmpty()
    val canRedo = undoneStrokes.isNotEmpty()

    // Pen thickness (dp) – index state, crash yok
    val thicknessOptions = listOf(2.dp, 3.dp, 4.dp, 5.dp, 7.dp)
    var selectedThicknessIndex by remember { mutableIntStateOf(2) }
    val selectedThickness = thicknessOptions[selectedThicknessIndex]

    // Style chips
    val styles = remember {
        listOf(
            SignatureStyle("professional", "Professional"),
            SignatureStyle("elegant", "Elegant"),
            SignatureStyle("casual", "Casual"),
            SignatureStyle("creative", "Creative")
        )
    }
    var selectedStyleId by remember { mutableStateOf("professional") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Create signature",
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
                    onClick = { /* later: maybe preview */ }
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tabs: Draw / Type
            SignatureModeTabs(
                currentMode = inputMode,
                onModeChange = { inputMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Signature card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.22f
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // White drawing canvas – büyütülmüş alan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        when (inputMode) {
                            SignatureInputMode.Draw -> SignatureDrawingCanvas(
                                strokes = strokes,
                                penThickness = selectedThickness,
                                modifier = Modifier.fillMaxSize(),
                                onStrokeCompleted = { stroke ->
                                    strokes = strokes + stroke
                                    undoneStrokes = emptyList()
                                }
                            )

                            SignatureInputMode.Type -> TypePlaceholder()
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (inputMode == SignatureInputMode.Draw)
                            "Draw your signature here"
                        else
                            "Type your name and we'll style it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Undo / Redo / Clear row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SmallActionButton(
                            icon = Icons.Filled.Undo,
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
                            icon = Icons.Filled.Redo,
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
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pen thickness selector
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pen thickness",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    thicknessOptions.forEachIndexed { index, option ->
                        val selected = index == selectedThicknessIndex
                        PenThicknessChip(
                            thickness = option,
                            selected = selected,
                            onClick = { selectedThicknessIndex = index }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Choose a style
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

            Spacer(modifier = Modifier.height(20.dp))

            // Your AI signatures
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
    val content = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

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
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = content
        )
    }
}


@Composable
private fun SmallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun PenThicknessChip(
    thickness: Dp,
    selected: Boolean,
    onClick: () -> Unit
) {
    val circleColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant

    val lineColor =
        if (selected) Color.White
        else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(circleColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Basit bir kalınlık göstergesi
        val factor = thickness.value / 4f   // 2dp -> 0.5, 7dp -> ~1.75
        Box(
            modifier = Modifier
                .width(22.dp)
                .height((4.dp * factor).coerceAtLeast(2.dp))
                .clip(RoundedCornerShape(999.dp))
                .background(lineColor)
        )
    }
}

@Composable
private fun SignatureDrawingCanvas(
    strokes: List<SignatureStroke>,
    penThickness: Dp,
    modifier: Modifier = Modifier,
    onStrokeCompleted: (SignatureStroke) -> Unit
) {
    val density = LocalDensity.current
    var currentStroke by remember { mutableStateOf<SignatureStroke?>(null) }

    Canvas(
        modifier = modifier.pointerInput(penThickness) {
            detectDragGestures(
                onDragStart = { offset ->
                    val widthPx = with(density) { penThickness.toPx() }
                    currentStroke = SignatureStroke(
                        path = Path().apply { moveTo(offset.x, offset.y) },
                        strokeWidthPx = widthPx
                    )
                },
                onDrag = { change, _ ->
                    currentStroke?.path?.lineTo(change.position.x, change.position.y)
                },
                onDragEnd = {
                    currentStroke?.let { stroke ->
                        onStrokeCompleted(stroke)
                    }
                    currentStroke = null
                },
                onDragCancel = {
                    currentStroke = null
                }
            )
        }
    ) {
        // Daha önce çizilen stroke'lar
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

        // Şu an çizilmekte olan stroke (parmağı kaldırmadan gözüken kısım)
        currentStroke?.let { stroke ->
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
    }
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
