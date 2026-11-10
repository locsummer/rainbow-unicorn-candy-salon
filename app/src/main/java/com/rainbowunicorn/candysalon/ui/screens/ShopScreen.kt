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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.ParentalGateModal
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun ShopScreen(
    candyBalance: Int,
    hasPremium: Boolean,
    hasRemoveAds: Boolean,
    onPurchasePremium: () -> Unit,
    onPurchaseRemoveAds: () -> Unit,
    onBackClicked: () -> Unit
) {
    var pendingPurchase by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.25f))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyPink
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "🍭 " + stringResource(R.string.shop_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.candy_balance, candyBalance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyPink
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Premium packages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Premium Levels card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👑", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.shop_premium_levels),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CandyDeepPlum
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (hasPremium) {
                            Text(
                                text = "✓ " + stringResource(R.string.shop_purchased),
                                color = CandyMint,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            NavButton(
                                icon = Icons.Default.Star,
                                label = "Mở khoá",
                                onClick = { pendingPurchase = "premium_levels" },
                                containerColor = CandyPink
                            )
                        }
                    }
                }

                // Remove Ads card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🚫", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.shop_remove_ads),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CandyDeepPlum
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (hasRemoveAds || hasPremium) {
                            Text(
                                text = "✓ " + stringResource(R.string.shop_purchased),
                                color = CandyMint,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            NavButton(
                                icon = Icons.Default.Star,
                                label = "Mở khoá",
                                onClick = { pendingPurchase = "remove_ads" },
                                containerColor = CandyPurple
                            )
                        }
                    }
                }
            }
        }

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
