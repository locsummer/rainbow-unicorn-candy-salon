package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.AnimatedCharacterStage
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.StationType

@Composable
fun stationLocalizedLabel(station: StationType): String = when (station) {
    StationType.SPA -> stringResource(R.string.station_spa)
    StationType.HAIR -> stringResource(R.string.station_hair)
    StationType.MAKEUP -> stringResource(R.string.station_makeup)
    StationType.DRESSUP -> stringResource(R.string.station_dressup)
    StationType.CAKE -> stringResource(R.string.station_cake)
    StationType.PET -> stringResource(R.string.station_pet)
    StationType.PHOTO -> stringResource(R.string.station_photo)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LevelIntroScreen(
    level: Level,
    language: String,
    onStartClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val previewCharacter = remember(level) { CharacterState(characterType = level.characterType) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CandyPastelPink, CandyYellow.copy(alpha = 0.28f), Color.White)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyActionPink,
                    compact = true
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.level_short_format, level.id),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Portrait Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = level.localizedName(language),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(2.5.dp, CandyPastelPink),
                    shadowElevation = 5.dp,
                    modifier = Modifier.size(164.dp)
                ) {
                    AnimatedCharacterStage(
                        characterState = previewCharacter,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.level_stations_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = CandyDeepPlum.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3
                ) {
                    for (st in level.stations) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CandyMint.copy(alpha = 0.55f)
                        ) {
                            Text(
                                text = stationLocalizedLabel(st),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyDeepPlum,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Customer Request Card (full portrait width)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "📋 " + stringResource(R.string.level_request_card),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CandyDeepPlum
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        for (criterion in level.criteria) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CandyPink,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(20.dp)
                                )
                                Text(
                                    text = criterion.localizedDescription(language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = CandyDeepPlum
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Anchored Bottom Start Button
            NavButton(
                icon = Icons.Default.PlayArrow,
                label = stringResource(R.string.btn_start),
                onClick = onStartClicked,
                containerColor = CandyActionPink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}
