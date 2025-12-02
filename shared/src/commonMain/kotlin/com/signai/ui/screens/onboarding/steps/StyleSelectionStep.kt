package com.signai.ui.screens.onboarding.steps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.signai.ui.screens.onboarding.OnboardingState
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StyleSelectionStep(
    state: OnboardingState,
    onStyleChange: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = state.selectedStyleIndex,
        pageCount = { state.styleOptions.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onStyleChange(page)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(), // Yatay padding'i kaldırdık, Pager halledecek
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Choose Your Style",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select a design aesthetic that matches you.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // DÜZELTME: Kartların ortalanması ve yanlardan görünmesi için contentPadding
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentPadding = PaddingValues(horizontal = 64.dp), // Kartı ortalar
            pageSpacing = 16.dp, // Kartlar arası boşluk
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val style = state.styleOptions[page]

            // Animasyon efekti (Seçili olmayanlar biraz küçülür ve solar)
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            StyleCardItem(
                styleTitle = style.title,
                modifier = Modifier
                    .graphicsLayer {
                        // Merkezden uzaklaştıkça küçült
                        val scale = lerp(1f, 0.85f, pageOffset.coerceIn(0f, 1f))
                        scaleX = scale
                        scaleY = scale

                        // Merkezden uzaklaştıkça şeffaflaştır
                        alpha = lerp(1f, 0.5f, pageOffset.coerceIn(0f, 1f))
                    },
                isSelected = (pagerState.currentPage == page)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nokta Göstergeleri (Indicator)
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            repeat(state.styleOptions.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width) // Seçili olan uzun çizgi olur
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun StyleCardItem(
    styleTitle: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 0.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = styleTitle.first().uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = styleTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}