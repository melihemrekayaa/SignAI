package com.signai.android.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.signai.android.SignAIOnboardingUiState
import com.signai.android.SignAIOnboardingViewModel

// === Root composable ===

@Composable
fun OnboardingScreen(
    viewModel: SignAIOnboardingViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        when (state.currentStep) {
            1 -> StyleSelectionStep(
                state = state,
                onBackClick = { viewModel.back() },
                onStyleChange = { index -> viewModel.selectStyle(index) },
                onNextClick = { viewModel.next() },
                onSkipClick = { viewModel.skip() }
            )

            2 -> TraitSelectionStep(
                state = state,
                onBackClick = { viewModel.back() },
                onTraitClick = { id -> viewModel.toggleTrait(id) },
                onNextClick = { viewModel.next() },
                onSkipClick = { viewModel.skip() }
            )

            3 -> PersonalityIntroStep(
                state = state,
                onBackClick = { viewModel.back() },
                onNextClick = { viewModel.next() },
                onSkipClick = { viewModel.skip() }
            )

            4 -> PersonalityReviewStep(
                state = state,
                onBackClick = { viewModel.back() },
                onEditClick = { key ->
                    when (key) {
                        "formality", "style" -> viewModel.goToStep(1)
                        "confidence", "creativity" -> viewModel.goToStep(2)
                    }
                },
                onConfirmClick = {
                    onFinished()
                }
            )
        }
    }
}

// === Common top row ===

@Composable
private fun RowWithBackAndStep(
    currentStep: Int,
    totalSteps: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Dummy spacer to balance the back button
        Spacer(modifier = Modifier.size(48.dp))
    }
}

// === STEP 1 – style carousel ===

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StyleSelectionStep(
    state: SignAIOnboardingUiState,
    onBackClick: () -> Unit,
    onStyleChange: (Int) -> Unit,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = state.selectedStyleIndex,
        pageCount = { state.styleOptions.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onStyleChange(pagerState.currentPage)
    }

    val isNextEnabled = state.styleOptions.isNotEmpty()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RowWithBackAndStep(
                    currentStep = state.currentStep,
                    totalSteps = state.totalSteps,
                    onBackClick = onBackClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = state.currentStep.toFloat() / state.totalSteps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
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
                    onClick = onNextClick,
                    enabled = isNextEnabled
                ) {
                    Text("Next")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkipClick
                ) {
                    Text("Skip for now")
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
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "What's your style?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) { page ->
                val style = state.styleOptions[page]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.25f
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Style preview",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = style.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Swipe left or right to choose",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.7f
                            )
                        )
                    }
                }
            }
        }
    }
}

// === STEP 2 – traits (Your Personality) ===

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TraitSelectionStep(
    state: SignAIOnboardingUiState,
    onBackClick: () -> Unit,
    onTraitClick: (String) -> Unit,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val isNextEnabled = state.selectedTraitIds.isNotEmpty()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RowWithBackAndStep(
                    currentStep = state.currentStep,
                    totalSteps = state.totalSteps,
                    onBackClick = onBackClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = state.currentStep.toFloat() / state.totalSteps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
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
                    onClick = onNextClick,
                    enabled = isNextEnabled
                ) {
                    Text("Next")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkipClick
                ) {
                    Text("Skip for now")
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
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Which words best describe your style?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap up to 3 traits that feel most like you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TOP BOX – only selected traits, blue pills
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val selectedTraits = state.traitOptions.filter {
                    it.id in state.selectedTraitIds
                }

                if (selectedTraits.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Your Personality",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selected traits will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Your Personality",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedTraits.forEach { trait ->
                                SelectedTraitChip(
                                    label = trait.label,
                                    onClick = { onTraitClick(trait.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM – full pool, grey chips; selected ones have blue border
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.traitOptions.forEach { trait ->
                    val isSelected = trait.id in state.selectedTraitIds
                    TraitPoolChip(
                        label = trait.label,
                        selected = isSelected,
                        onClick = { onTraitClick(trait.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SelectedTraitChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TraitPoolChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline

    val textColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

// === STEP 3 – “Your Personality, Your Signature” intro ===

@Composable
private fun PersonalityIntroStep(
    state: SignAIOnboardingUiState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RowWithBackAndStep(
                    currentStep = state.currentStep,
                    totalSteps = state.totalSteps,
                    onBackClick = onBackClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = state.currentStep.toFloat() / state.totalSteps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
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
                    onClick = onNextClick
                ) {
                    Text("Find My Style")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkipClick
                ) {
                    Text("Skip for now")
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
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Illustration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Personality, Your Signature",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Answer a few quick questions to help our AI craft a signature that truly represents you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            PersonalityIntroFeatureRow(
                title = "A signature that reflects you",
                subtitle = "Get a design that mirrors your personal traits and style."
            )
            Spacer(modifier = Modifier.height(12.dp))
            PersonalityIntroFeatureRow(
                title = "AI-powered and unique",
                subtitle = "Every suggestion is generated just for you."
            )
            Spacer(modifier = Modifier.height(12.dp))
            PersonalityIntroFeatureRow(
                title = "Quick and fun process",
                subtitle = "Just a few taps to get started."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PersonalityIntroFeatureRow(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

// === STEP 4 – Review your personality ===

private data class PersonalityReviewItem(
    val key: String,
    val title: String,
    val subtitle: String,
    val iconTint: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun PersonalityReviewStep(
    state: SignAIOnboardingUiState,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    val items = buildPersonalityReviewItems(state)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RowWithBackAndStep(
                    currentStep = state.currentStep,
                    totalSteps = state.totalSteps,
                    onBackClick = onBackClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = state.currentStep.toFloat() / state.totalSteps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
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
                    onClick = onConfirmClick
                ) {
                    Text("Confirm & Continue")
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
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Review Your Personality",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Here’s the personality profile you've built. Our AI will use this to craft your unique signature.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            items.forEachIndexed { index, item ->
                PersonalityReviewCard(
                    item = item,
                    onEditClick = onEditClick
                )
                if (index != items.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PersonalityReviewCard(
    item: PersonalityReviewItem,
    onEditClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(item.iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        IconButton(onClick = { onEditClick(item.key) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit ${item.title}"
            )
        }
    }
}

@Composable
private fun buildPersonalityReviewItems(
    state: SignAIOnboardingUiState
): List<PersonalityReviewItem> {
    val traits = state.selectedTraitIds
    val styleTitle = state.styleOptions
        .getOrNull(state.selectedStyleIndex)
        ?.title ?: "Minimalist"

    val formalitySubtitle = when {
        "formal" in traits || "elegant" in traits ->
            "Polished and professional"

        "playful" in traits || "creative" in traits ->
            "Casual and friendly"

        else ->
            "Balanced between casual and formal"
    }

    val confidenceSubtitle =
        if ("bold" in traits) "Bold and confident"
        else "Calm and steady"

    val creativitySubtitle =
        if ("creative" in traits) "Abstract and expressive"
        else "Clean and simple"

    return listOf(
        PersonalityReviewItem(
            key = "formality",
            title = "Formality",
            subtitle = formalitySubtitle,
            iconTint = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Edit
        ),
        PersonalityReviewItem(
            key = "style",
            title = "Style",
            subtitle = styleTitle,
            iconTint = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Edit
        ),
        PersonalityReviewItem(
            key = "confidence",
            title = "Confidence",
            subtitle = confidenceSubtitle,
            iconTint = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Edit
        ),
        PersonalityReviewItem(
            key = "creativity",
            title = "Creativity",
            subtitle = creativitySubtitle,
            iconTint = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Edit
        )
    )
}
