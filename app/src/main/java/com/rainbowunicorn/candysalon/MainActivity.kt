package com.rainbowunicorn.candysalon

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rainbowunicorn.candysalon.ads.AdProvider
import com.rainbowunicorn.candysalon.ads.FakeAdProvider
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.billing.BillingProvider
import com.rainbowunicorn.candysalon.billing.FakeBillingProvider
import com.rainbowunicorn.candysalon.storage.LocalStorageManager
import com.rainbowunicorn.candysalon.ui.screens.BootScreen
import com.rainbowunicorn.candysalon.ui.screens.GalleryScreen
import com.rainbowunicorn.candysalon.ui.screens.HomeScreen
import com.rainbowunicorn.candysalon.ui.screens.LevelIntroScreen
import com.rainbowunicorn.candysalon.ui.screens.LevelResultScreen
import com.rainbowunicorn.candysalon.ui.screens.LevelSelectScreen
import com.rainbowunicorn.candysalon.ui.screens.PartyModeScreen
import com.rainbowunicorn.candysalon.ui.screens.SettingsScreen
import com.rainbowunicorn.candysalon.ui.screens.ShopScreen
import com.rainbowunicorn.candysalon.ui.screens.StationScreen
import com.rainbowunicorn.candysalon.ui.screens.StationScreenResult
import com.rainbowunicorn.candysalon.ui.theme.RainbowColorScheme
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.services.AdPolicy
import com.rainbowunicorn.core.services.EconomyService
import com.rainbowunicorn.core.services.LevelRepository
import java.io.InputStreamReader

enum class Screen {
    BOOT,
    HOME,
    LEVEL_SELECT,
    LEVEL_INTRO,
    STATION,
    LEVEL_RESULT,
    PARTY_MODE,
    GALLERY,
    SETTINGS,
    SHOP
}

class MainActivity : ComponentActivity() {

    private lateinit var storageManager: LocalStorageManager
    private lateinit var soundManager: SoundManager
    private lateinit var billingProvider: BillingProvider
    private lateinit var adProvider: AdProvider

    private var levels: List<Level> = emptyList()
    private var itemCategories: List<ItemCategory> = emptyList()
    private var items: List<Item> = emptyList()

    private var lastAdTimeMillis: Long = 0L
    private var levelsPlayedSinceLastAd: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageManager = LocalStorageManager(this)
        soundManager = SoundManager()
        billingProvider = FakeBillingProvider()
        adProvider = FakeAdProvider()

        loadSpecs()

        setContent {
            androidx.compose.material3.MaterialTheme(colorScheme = RainbowColorScheme) {
                AppNavigator()
            }
        }
    }

    private fun loadSpecs() {
        try {
            assets.open("levels.json").use { stream ->
                val json = InputStreamReader(stream, Charsets.UTF_8).readText()
                levels = LevelRepository.loadLevels(json)
            }
            assets.open("items.json").use { stream ->
                val json = InputStreamReader(stream, Charsets.UTF_8).readText()
                val (cats, itms) = LevelRepository.loadItems(json)
                itemCategories = cats
                items = itms
            }
        } catch (_: Exception) {}
    }

    override fun onPause() {
        super.onPause()
        soundManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        soundManager.onResume()
    }

    @Composable
    private fun AppNavigator() {
        var currentScreen by remember { mutableStateOf(Screen.BOOT) }
        var saveData by remember { mutableStateOf(storageManager.loadSaveData()) }
        var selectedLevel by remember { mutableStateOf<Level?>(null) }
        var lastResult by remember { mutableStateOf<StationScreenResult?>(null) }
        var candyAwardedThisRun by remember { mutableIntStateOf(0) }

        // Sync initial sound settings
        soundManager.bgmEnabled = saveData.bgmEnabled
        soundManager.sfxEnabled = saveData.sfxEnabled

        fun checkAndTriggerAd(completedLevelId: Int) {
            val hasRemoveAds = saveData.purchasedEntitlements.contains("remove_ads")
            val hasPremium = saveData.purchasedEntitlements.contains("premium_levels")
            val secondsSinceAd = (System.currentTimeMillis() - lastAdTimeMillis) / 1000L

            if (AdPolicy.shouldShowInterstitialAd(
                    hasRemoveAds = hasRemoveAds,
                    hasPremiumLevels = hasPremium,
                    completedLevelId = completedLevelId,
                    secondsSinceLastAd = secondsSinceAd,
                    levelsPlayedSinceLastAd = levelsPlayedSinceLastAd,
                    currentScreen = "level_select"
                )
            ) {
                adProvider.showInterstitial {
                    lastAdTimeMillis = System.currentTimeMillis()
                    levelsPlayedSinceLastAd = 0
                }
            }
        }

        when (currentScreen) {
            Screen.BOOT -> {
                BootScreen(onLoaded = { currentScreen = Screen.HOME })
            }
            Screen.HOME -> {
                HomeScreen(
                    candyBalance = saveData.candyBalance,
                    onStoryModeClicked = { currentScreen = Screen.LEVEL_SELECT },
                    onPartyModeClicked = { currentScreen = Screen.PARTY_MODE },
                    onGalleryClicked = { currentScreen = Screen.GALLERY },
                    onSettingsClicked = { currentScreen = Screen.SETTINGS },
                    onShopClicked = { currentScreen = Screen.SHOP }
                )
            }
            Screen.LEVEL_SELECT -> {
                LevelSelectScreen(
                    levels = levels,
                    unlockedLevels = saveData.unlockedLevels,
                    bestStars = saveData.bestStars,
                    hasPremium = saveData.purchasedEntitlements.contains("premium_levels"),
                    onLevelSelected = { lvl ->
                        selectedLevel = lvl
                        currentScreen = Screen.LEVEL_INTRO
                    },
                    onBackClicked = { currentScreen = Screen.HOME },
                    onUnlockPremiumRequested = {
                        billingProvider.purchase("premium_levels") { success ->
                            if (success) {
                                val updated = saveData.copy(
                                    purchasedEntitlements = saveData.purchasedEntitlements + "premium_levels"
                                )
                                saveData = updated
                                storageManager.saveSaveData(updated)
                            }
                        }
                    }
                )
            }
            Screen.LEVEL_INTRO -> {
                val lvl = selectedLevel
                if (lvl != null) {
                    LevelIntroScreen(
                        level = lvl,
                        onStartClicked = { currentScreen = Screen.STATION },
                        onBackClicked = { currentScreen = Screen.LEVEL_SELECT }
                    )
                } else {
                    currentScreen = Screen.LEVEL_SELECT
                }
            }
            Screen.STATION -> {
                val lvl = selectedLevel
                if (lvl != null) {
                    StationScreen(
                        level = lvl,
                        categories = itemCategories,
                        items = items,
                        unlockedItems = saveData.unlockedItems,
                        soundManager = soundManager,
                        onLevelFinished = { result ->
                            lastResult = result
                            levelsPlayedSinceLastAd++

                            val oldStars = saveData.bestStars[lvl.id] ?: 0
                            val award = EconomyService.calculateStarAward(
                                initialBestStars = oldStars,
                                levelEarnedStars = result.evaluation.stars,
                                currentCandyBalance = saveData.candyBalance
                            )
                            candyAwardedThisRun = if (result.evaluation.isWin) award.candyEarned else 0

                            val updatedSave = result.engine.applyResultsToSaveData(saveData, result.evaluation)
                            saveData = updatedSave
                            storageManager.saveSaveData(updatedSave)

                            currentScreen = Screen.LEVEL_RESULT
                        },
                        onExitConfirmed = {
                            currentScreen = Screen.LEVEL_SELECT
                            checkAndTriggerAd(lvl.id)
                        }
                    )
                } else {
                    currentScreen = Screen.LEVEL_SELECT
                }
            }
            Screen.LEVEL_RESULT -> {
                val res = lastResult
                if (res != null) {
                    LevelResultScreen(
                        evaluation = res.evaluation,
                        candyEarned = candyAwardedThisRun,
                        soundManager = soundManager,
                        onNextLevelClicked = {
                            val nextId = res.level.id + 1
                            val nextLevel = levels.find { it.id == nextId }
                            if (nextLevel != null && saveData.unlockedLevels.contains(nextId)) {
                                selectedLevel = nextLevel
                                currentScreen = Screen.LEVEL_INTRO
                            } else {
                                currentScreen = Screen.LEVEL_SELECT
                                checkAndTriggerAd(res.level.id)
                            }
                        },
                        onEditLookClicked = {
                            currentScreen = Screen.STATION
                        },
                        onReplayClicked = {
                            currentScreen = Screen.STATION
                        }
                    )
                } else {
                    currentScreen = Screen.LEVEL_SELECT
                }
            }
            Screen.PARTY_MODE -> {
                PartyModeScreen(
                    categories = itemCategories,
                    items = items,
                    unlockedItems = saveData.unlockedItems,
                    soundManager = soundManager,
                    onSavePhotoToGallery = { bitmap ->
                        val saved = storageManager.savePhotoToInternalGallery(bitmap) != null
                        if (saved) {
                            saveData = saveData.copy(galleryCount = storageManager.getGalleryPhotos().size)
                            storageManager.saveSaveData(saveData)
                        }
                        saved
                    },
                    onBackClicked = { currentScreen = Screen.HOME }
                )
            }
            Screen.GALLERY -> {
                GalleryScreen(
                    photos = storageManager.getGalleryPhotos(),
                    onExportPhoto = { file ->
                        val bmp = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                        if (bmp != null) storageManager.exportPhotoToMediaStore(bmp) else false
                    },
                    onDeletePhoto = { file ->
                        val deleted = storageManager.deletePhoto(file)
                        if (deleted) {
                            saveData = saveData.copy(galleryCount = storageManager.getGalleryPhotos().size)
                            storageManager.saveSaveData(saveData)
                        }
                        deleted
                    },
                    onBackClicked = { currentScreen = Screen.HOME }
                )
            }
            Screen.SETTINGS -> {
                SettingsScreen(
                    bgmEnabled = saveData.bgmEnabled,
                    sfxEnabled = saveData.sfxEnabled,
                    onBgmToggle = { enabled ->
                        val updated = saveData.copy(bgmEnabled = enabled)
                        saveData = updated
                        soundManager.bgmEnabled = enabled
                        storageManager.saveSaveData(updated)
                    },
                    onSfxToggle = { enabled ->
                        val updated = saveData.copy(sfxEnabled = enabled)
                        saveData = updated
                        soundManager.sfxEnabled = enabled
                        storageManager.saveSaveData(updated)
                    },
                    onRestorePurchases = {
                        billingProvider.restorePurchases { entitlements ->
                            val updated = saveData.copy(purchasedEntitlements = saveData.purchasedEntitlements + entitlements)
                            saveData = updated
                            storageManager.saveSaveData(updated)
                        }
                    },
                    onBackClicked = { currentScreen = Screen.HOME }
                )
            }
            Screen.SHOP -> {
                ShopScreen(
                    candyBalance = saveData.candyBalance,
                    hasPremium = saveData.purchasedEntitlements.contains("premium_levels"),
                    hasRemoveAds = saveData.purchasedEntitlements.contains("remove_ads"),
                    onPurchasePremium = {
                        billingProvider.purchase("premium_levels") { success ->
                            if (success) {
                                val updated = saveData.copy(purchasedEntitlements = saveData.purchasedEntitlements + "premium_levels")
                                saveData = updated
                                storageManager.saveSaveData(updated)
                            }
                        }
                    },
                    onPurchaseRemoveAds = {
                        billingProvider.purchase("remove_ads") { success ->
                            if (success) {
                                val updated = saveData.copy(purchasedEntitlements = saveData.purchasedEntitlements + "remove_ads")
                                saveData = updated
                                storageManager.saveSaveData(updated)
                            }
                        }
                    },
                    onBackClicked = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}
