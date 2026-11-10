package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun HomeScreen(
    candyBalance: Int,
    onStoryModeClicked: () -> Unit,
    onPartyModeClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onShopClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(CandyPastelPink, CandyMint.copy(alpha = 0.4f), CandyYellow.copy(alpha = 0.5f))
                )
            )
            .padding(16.dp)
    ) {
        // Candy Balance Badge at Top End
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(text = "🍬", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.candy_balance, candyBalance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyDeepPlum
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "🦄 " + stringResource(R.string.app_name) + " 🍭",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CandyDeepPlum
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Main Play Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.btn_play),
                    onClick = onStoryModeClicked,
                    containerColor = CandyPink,
                    modifier = Modifier.height(58.dp)
                )

                NavButton(
                    icon = Icons.Default.Celebration,
                    label = stringResource(R.string.btn_party),
                    onClick = onPartyModeClicked,
                    containerColor = CandyPurple,
                    modifier = Modifier.height(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Utility Navigation
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NavButton(
                    icon = Icons.Default.Image,
                    label = stringResource(R.string.btn_gallery),
                    onClick = onGalleryClicked,
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum
                )

                NavButton(
                    icon = Icons.Default.ShoppingCart,
                    label = stringResource(R.string.btn_shop),
                    onClick = onShopClicked,
                    containerColor = CandyYellow,
                    contentColor = CandyDeepPlum
                )

                NavButton(
                    icon = Icons.Default.Settings,
                    label = stringResource(R.string.btn_settings),
                    onClick = onSettingsClicked,
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum
                )
            }
        }
    }
}
