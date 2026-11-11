package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.ParentalGateModal
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    bgmEnabled: Boolean,
    sfxEnabled: Boolean,
    language: String,
    onBgmToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onRestorePurchases: () -> Unit,
    onBackClicked: () -> Unit
) {
    var showParentalGateForRestore by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val restoreSuccessText = stringResource(R.string.restore_success)

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyActionPink,
                    compact = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
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

                    // Language Switcher (English vs Tiếng Việt - RULE-005 / Bilingual requirement)
                    Text(
                        text = stringResource(R.string.settings_language),
                        style = MaterialTheme.typography.titleMedium,
                        color = CandyDeepPlum
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            "en" to stringResource(R.string.lang_english),
                            "vi" to stringResource(R.string.lang_vietnamese)
                        ).forEach { (code, label) ->
                            val isSelected = language.equals(code, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) CandyActionPink else CandyPastelPink.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) CandyDeepPlum else CandyPurple.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onLanguageSelected(code) }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else CandyDeepPlum,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Restore Purchases (behind Parental Gate RULE-003, RULE-064)
                    NavButton(
                        icon = Icons.Default.Restore,
                        label = stringResource(R.string.settings_restore),
                        onClick = { showParentalGateForRestore = true },
                        containerColor = CandyActionPurple,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🛡️ " + stringResource(R.string.settings_privacy),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showParentalGateForRestore) {
            ParentalGateModal(
                onVerified = {
                    showParentalGateForRestore = false
                    onRestorePurchases()
                    scope.launch { snackbarHostState.showSnackbar(restoreSuccessText) }
                },
                onDismiss = { showParentalGateForRestore = false }
            )
        }
    }
}
