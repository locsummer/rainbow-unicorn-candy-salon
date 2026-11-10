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
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun SettingsScreen(
    bgmEnabled: Boolean,
    sfxEnabled: Boolean,
    onBgmToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit,
    onRestorePurchases: () -> Unit,
    onBackClicked: () -> Unit
) {
    var showParentalGateForRestore by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.3f))
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
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // BGM switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_bgm),
                            style = MaterialTheme.typography.titleMedium,
                            color = CandyDeepPlum
                        )
                        Switch(
                            checked = bgmEnabled,
                            onCheckedChange = onBgmToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = CandyPink, checkedTrackColor = CandyPastelPink)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SFX switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_sfx),
                            style = MaterialTheme.typography.titleMedium,
                            color = CandyDeepPlum
                        )
                        Switch(
                            checked = sfxEnabled,
                            onCheckedChange = onSfxToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = CandyPink, checkedTrackColor = CandyPastelPink)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Restore Purchases (behind Parental Gate RULE-003, RULE-064)
                    NavButton(
                        icon = Icons.Default.Restore,
                        label = stringResource(R.string.settings_restore),
                        onClick = { showParentalGateForRestore = true },
                        containerColor = CandyPurple,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🛡️ " + stringResource(R.string.settings_privacy),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (showParentalGateForRestore) {
            ParentalGateModal(
                onVerified = {
                    showParentalGateForRestore = false
                    onRestorePurchases()
                },
                onDismiss = { showParentalGateForRestore = false }
            )
        }
    }
}
