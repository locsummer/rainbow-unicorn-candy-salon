package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.rainbowunicorn.candysalon.ui.components.CandyBalanceBadge
import com.rainbowunicorn.candysalon.ui.components.CharacterPose
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.MakeupState

@Composable
fun HomeScreen(
    candyBalance: Int,
    onStoryModeClicked: () -> Unit,
    onPartyModeClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onShopClicked: () -> Unit
) {
    val mascotState = remember {
        CharacterState(
            characterType = CharacterType.UNICORN,
            hairStyleId = "hair_long_waves",
            hairColorId = "hair_rainbow_pastel",
            makeup = MakeupState(eyeshadow = "eyeshadow_berry_pink", lipstick = "lipstick_bubblegum_pink", blush = "blush_rose_petal"),
            outfitId = "outfit_ball_gown",
            wingsId = "wings_rainbow",
            hornId = "horn_gold",
            tiaraId = "tiara_star_gold"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CandyPastelPink, CandyMint.copy(alpha = 0.38f), CandyYellow.copy(alpha = 0.45f), Color.White)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with Candy Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CandyBalanceBadge(candyBalance = candyBalance)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hero Title
            Text(
                text = "🦄 " + stringResource(R.string.app_name) + " 🍭",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CandyDeepPlum,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Center Live Mascot Showcase (fills remaining vertical space cleanly in Portrait)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(2.5.dp, CandyPastelPink),
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(230.dp)
                ) {
                    AnimatedCharacterStage(
                        characterState = mascotState,
                        pose = CharacterPose.WAVE,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }

            // Portrait Primary Action Stack
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NavButton(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.btn_play),
                    onClick = onStoryModeClicked,
                    containerColor = CandyActionPink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                )

                NavButton(
                    icon = Icons.Default.Celebration,
                    label = stringResource(R.string.btn_party),
                    onClick = onPartyModeClicked,
                    containerColor = CandyActionPurple,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Utility Navigation Bar (3 equal-weight compact Portrait buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavButton(
                        icon = Icons.Default.Image,
                        label = stringResource(R.string.btn_gallery),
                        onClick = onGalleryClicked,
                        containerColor = CandyMint,
                        contentColor = CandyDeepPlum,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )

                    NavButton(
                        icon = Icons.Default.ShoppingCart,
                        label = stringResource(R.string.btn_shop),
                        onClick = onShopClicked,
                        containerColor = CandyYellow,
                        contentColor = CandyDeepPlum,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )

                    NavButton(
                        icon = Icons.Default.Settings,
                        label = stringResource(R.string.btn_settings),
                        onClick = onSettingsClicked,
                        containerColor = Color.White,
                        contentColor = CandyDeepPlum,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
