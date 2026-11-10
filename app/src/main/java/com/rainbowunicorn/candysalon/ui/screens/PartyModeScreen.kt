package com.rainbowunicorn.candysalon.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.BottomItemTray
import com.rainbowunicorn.candysalon.ui.components.CharacterCanvasRenderer
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.services.LayerComposer

@Composable
fun PartyModeScreen(
    categories: List<ItemCategory>,
    items: List<Item>,
    unlockedItems: Set<String>,
    soundManager: SoundManager,
    onSavePhotoToGallery: (Bitmap) -> Boolean,
    onBackClicked: () -> Unit
) {
    var characterState by remember { mutableStateOf(CharacterState(characterType = CharacterType.UNICORN)) }
    var photoFlash by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_exit),
                    onClick = onBackClicked,
                    containerColor = CandyPink
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "🎉 " + stringResource(R.string.btn_party),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyDeepPlum
                )

                Spacer(modifier = Modifier.weight(1f))

                // Switch Unicorn / Princess
                NavButton(
                    icon = Icons.Default.SwapHoriz,
                    label = if (characterState.characterType == CharacterType.UNICORN) "Công chúa" else "Kỳ lân",
                    onClick = {
                        val nextType = if (characterState.characterType == CharacterType.UNICORN) CharacterType.PRINCESS else CharacterType.UNICORN
                        characterState = characterState.copy(characterType = nextType)
                        soundManager.playClick()
                    },
                    containerColor = CandyPurple,
                    contentColor = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Camera Snapshot
                NavButton(
                    icon = Icons.Default.CameraAlt,
                    label = stringResource(R.string.btn_take_photo),
                    onClick = {
                        soundManager.playShutter()
                        // Create sample 400x400 bitmap representation for internal gallery
                        val sampleBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(sampleBitmap)
                        canvas.drawColor(android.graphics.Color.rgb(255, 235, 240))
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(255, 89, 150)
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText("Sweet Party Snapshot", 200f, 200f, paint)
                        onSavePhotoToGallery(sampleBitmap)
                    },
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum
                )
            }

            // Central Stage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CharacterCanvasRenderer(
                    characterState = characterState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Tray for all dressup & hair categories
            BottomItemTray(
                categories = categories,
                items = items,
                unlockedItems = unlockedItems,
                selectedItemId = null,
                onItemSelected = { item ->
                    soundManager.playClick()
                    when (item.category) {
                        "hair_styles" -> characterState = characterState.copy(hairStyleId = item.id)
                        "hair_colors" -> characterState = characterState.copy(hairColorId = item.id)
                        "eyeshadows" -> characterState = LayerComposer.applyEyeshadow(characterState, item.id)
                        "lipsticks" -> characterState = characterState.copy(makeup = characterState.makeup.copy(lipstick = item.id))
                        "blushes" -> characterState = characterState.copy(makeup = characterState.makeup.copy(blush = item.id))
                        "face_paints" -> characterState = LayerComposer.addFacePaint(characterState, item.id)
                        "outfits" -> characterState = LayerComposer.equipOutfit(characterState, item.id)
                        "wings" -> characterState = LayerComposer.equipWings(characterState, item.id)
                        "horns" -> characterState = LayerComposer.equipHorn(characterState, item.id)
                        "tiaras" -> characterState = LayerComposer.equipTiara(characterState, item.id)
                        "jewelry" -> characterState = characterState.copy(jewelryId = item.id)
                        "stickers" -> characterState = LayerComposer.addSticker(characterState, item.id).first
                    }
                },
                onLockedItemClicked = { soundManager.playClick() }
            )
        }
    }
}
