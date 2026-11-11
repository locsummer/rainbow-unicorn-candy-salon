package com.rainbowunicorn.candysalon.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rainbowunicorn.candysalon.ui.components.CandyBalanceBadge
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.ParentalGateModal
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandySuccessGreen

@Composable
fun ShopScreen(
    candyBalance: Int,
    hasPremium: Boolean,
    hasRemoveAds: Boolean,
    statusMessage: String?,
    onClearStatusMessage: () -> Unit,
    onPurchasePremium: () -> Unit,
    onPurchaseRemoveAds: () -> Unit,
    onBackClicked: () -> Unit
) {
    var pendingPurchase by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        if (!statusMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusMessage)
            onClearStatusMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CandyPastelPink.copy(alpha = 0.35f), Color.White)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyActionPink,
                    compact = true
                )
                CandyBalanceBadge(candyBalance = candyBalance)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🍭 " + stringResource(R.string.shop_title),
                style = MaterialTheme.typography.titleLarge,
                color = CandyDeepPlum,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Vertical Portrait Product Cards Stack
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.shop_premium_levels),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = CandyDeepPlum,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.shop_premium_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = CandyDeepPlum.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    if (hasPremium) {
                        Text(
                            text = "✓ " + stringResource(R.string.shop_purchased),
                            color = CandySuccessGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        NavButton(
                            icon = Icons.Default.Star,
                            label = stringResource(R.string.btn_buy),
                            onClick = { pendingPurchase = "premium_levels" },
                            containerColor = CandyActionPink,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🚫", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.shop_remove_ads),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = CandyDeepPlum,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.shop_remove_ads_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = CandyDeepPlum.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    if (hasRemoveAds || hasPremium) {
                        Text(
                            text = "✓ " + stringResource(R.string.shop_purchased),
                            color = CandySuccessGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        NavButton(
                            icon = Icons.Default.Star,
                            label = stringResource(R.string.btn_buy),
                            onClick = { pendingPurchase = "remove_ads" },
                            containerColor = CandyActionPurple,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        pendingPurchase?.let { prod ->
            ParentalGateModal(
                onVerified = {
                    if (prod == "premium_levels") onPurchasePremium()
                    else if (prod == "remove_ads") onPurchaseRemoveAds()
                    pendingPurchase = null
                },
                onDismiss = { pendingPurchase = null }
            )
        }
    }
}
