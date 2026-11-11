package com.rainbowunicorn.candysalon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.rainbowunicorn.candysalon.ui.theme.CandyTypography
import com.rainbowunicorn.candysalon.ui.theme.RainbowColorScheme
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import com.rainbowunicorn.core.services.AdPolicy
import com.rainbowunicorn.core.services.EconomyService
import com.rainbowunicorn.core.services.LevelEngine
import com.rainbowunicorn.core.services.LevelRepository
import com.rainbowunicorn.core.services.UnlockPolicy
import java.io.File
import java.io.InputStreamReader
import java.util.Locale

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

    // Latest active save state reference so onPause can persist automatically (RULE-090)
    private var latestSaveDataSnapshot: com.rainbowunicorn.core.models.SaveData? = null
    private var latestActiveCheckpointSnapshot: SavedLevelCheckpoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        storageManager = LocalStorageManager(this)
        soundManager = SoundManager()
        billingProvider = FakeBillingProvider()
        adProvider = FakeAdProvider()

        loadSpecs()

        setContent {
            androidx.compose.material3.MaterialTheme(
                colorScheme = RainbowColorScheme,
                typography = CandyTypography
            ) {
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
        val snap = latestSaveDataSnapshot
        if (snap != null) {
            storageManager.saveSaveData(snap.copy(activeCheckpoint = latestActiveCheckpointSnapshot ?: snap.activeCheckpoint))
        }
    }

    override fun onResume() {
        super.onResume()
        soundManager.onResume()
    }

    override fun onDestroy() {
        soundManager.release()
        super.onDestroy()
    }

    private fun createLocalizedContext(base: Context, languageCode: String): Context {
        val locale = Locale(languageCode.lowercase(Locale.US))
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }

    @Composable
    private fun LocalizedContent(
        language: String,
        content: @Composable () -> Unit
    ) {
        val baseContext = LocalContext.current
        val localizedContext = remember(language, baseContext) {
            createLocalizedContext(baseContext, language)
        }
        val localizedConfig = remember(localizedContext) {
            Configuration(localizedContext.resources.configuration)
        }
        key(language) {
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                content = content
            )
        }
    }

    @Composable
    private fun AppNavigator() {
        var currentScreen by remember { mutableStateOf(Screen.BOOT) }
        var saveData by remember { mutableStateOf(storageManager.loadSaveData()) }
        var selectedLevel by remember { mutableStateOf<Level?>(null) }
        var activeEngine by remember { mutableStateOf<LevelEngine?>(null) }
        var activeCheckpointForSession by remember { mutableStateOf<SavedLevelCheckpoint?>(null) }
        var lastResult by remember { mutableStateOf<StationScreenResult?>(null) }
        var candyAwardedThisRun by remember { mutableIntStateOf(0) }
        var galleryFiles by remember { mutableStateOf(storageManager.getGalleryPhotos()) }
        var shopStatusMessage by remember { mutableStateOf<String?>(null) }
        var levelSelectStatusMessage by remember { mutableStateOf<String?>(null) }

        // Keep Activity snapshots updated for lifecycle onPause auto-save
        latestSaveDataSnapshot = saveData
        latestActiveCheckpointSnapshot = if (currentScreen == Screen.STATION) {
            activeCheckpointForSession
        } else {
            saveData.activeCheckpoint
        }

        // Initialize sound settings cleanly once / on change
        LaunchedEffect(saveData.bgmEnabled, saveData.sfxEnabled) {
            soundManager.bgmEnabled = saveData.bgmEnabled
            soundManager.sfxEnabled = saveData.sfxEnabled
        }

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

        fun unlockItemWithCandy(item: Item): Boolean {
            val res = EconomyService.purchaseItem(
                itemId = item.id,
                itemPrice = item.candyPrice,
                currentCandyBalance = saveData.candyBalance,
                alreadyOwnedItemIds = saveData.unlockedItems
            )
            if (!res.isSuccess) return false
            val updated = saveData.copy(
                candyBalance = res.remainingCandy,
                unlockedItems = saveData.unlockedItems + item.id
            )
            saveData = updated
            storageManager.saveSaveData(updated)
            return true
        }

        fun unlockItemWithAd(item: Item) {
            val updated = saveData.copy(unlockedItems = saveData.unlockedItems + item.id)
            saveData = updated
            storageManager.saveSaveData(updated)
        }

        fun handlePurchaseEntitlement(entitlementId: String, onMessage: (String) -> Unit, successMsg: String, failMsg: String) {
            billingProvider.purchase(entitlementId) { success ->
                if (success) {
                    val newEntitlements = saveData.purchasedEntitlements + entitlementId
                    val hasPremium = newEntitlements.contains("premium_levels")
                    val recomputedUnlocked = UnlockPolicy.computeUnlockedLevels(saveData.bestStars, hasPremium)
                    val updated = saveData.copy(
                        purchasedEntitlements = newEntitlements,
                        unlockedLevels = recomputedUnlocked
                    )
                    saveData = updated
                    storageManager.saveSaveData(updated)
                    onMessage(successMsg)
                } else {
                    onMessage(failMsg)
                }
            }
        }

        // API <= 28 runtime WRITE_EXTERNAL_STORAGE launcher for Gallery export (API 29+ skips permission prompt)
        var pendingExportCallback by remember { mutableStateOf<Pair<File, (Boolean) -> Unit>?>(null) }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            val pair = pendingExportCallback
            pendingExportCallback = null
            if (pair != null) {
                if (granted) {
                    val bmp = BitmapFactory.decodeFile(pair.first.absolutePath)
                    val exported = if (bmp != null) storageManager.exportPhotoToMediaStore(bmp) else false
                    pair.second(exported)
                } else {
                    pair.second(false)
                }
            }
        }

        LocalizedContent(language = saveData.language) {
            val context = LocalContext.current
            val purchaseSuccessStr = context.getString(R.string.purchase_success)
            val purchaseFailedStr = context.getString(R.string.purchase_failed)

            when (currentScreen) {
                Screen.BOOT -> {
                    BootScreen(onLoaded = { currentScreen = Screen.HOME })
                }
                Screen.HOME -> {
                    HomeScreen(
                        candyBalance = saveData.candyBalance,
                        onStoryModeClicked = { currentScreen = Screen.LEVEL_SELECT },
                        onPartyModeClicked = { currentScreen = Screen.PARTY_MODE },
                        onGalleryClicked = {
                            galleryFiles = storageManager.getGalleryPhotos()
                            currentScreen = Screen.GALLERY
                        },
                        onSettingsClicked = { currentScreen = Screen.SETTINGS },
                        onShopClicked = { currentScreen = Screen.SHOP }
                    )
                }
                Screen.LEVEL_SELECT -> {
                    LevelSelectScreen(
                        levels = levels,
                        language = saveData.language,
                        candyBalance = saveData.candyBalance,
                        unlockedLevels = saveData.unlockedLevels,
                        bestStars = saveData.bestStars,
                        hasPremium = saveData.purchasedEntitlements.contains("premium_levels"),
                        activeCheckpoint = saveData.activeCheckpoint,
                        statusMessage = levelSelectStatusMessage,
                        onClearStatusMessage = { levelSelectStatusMessage = null },
                        onLevelSelected = { lvl, resumeCheckpoint ->
                            selectedLevel = lvl
                            val savedCp = saveData.activeCheckpoint
                            if (resumeCheckpoint && savedCp != null && savedCp.levelId == lvl.id) {
                                activeCheckpointForSession = savedCp
                                activeEngine = LevelEngine(lvl, checkpoint = savedCp)
                                currentScreen = Screen.STATION
                            } else {
                                if (saveData.activeCheckpoint != null) {
                                    val cleared = saveData.copy(activeCheckpoint = null)
                                    saveData = cleared
                                    storageManager.saveSaveData(cleared)
                                }
                                activeCheckpointForSession = null
                                activeEngine = LevelEngine(lvl)
                                currentScreen = Screen.LEVEL_INTRO
                            }
                        },
                        onBackClicked = { currentScreen = Screen.HOME },
                        onUnlockPremiumRequested = {
                            handlePurchaseEntitlement(
                                entitlementId = "premium_levels",
                                onMessage = { msg -> levelSelectStatusMessage = msg },
                                successMsg = purchaseSuccessStr,
                                failMsg = purchaseFailedStr
                            )
                        }
                    )
                }
                Screen.LEVEL_INTRO -> {
                    val lvl = selectedLevel
                    if (lvl != null) {
                        LevelIntroScreen(
                            level = lvl,
                            language = saveData.language,
                            onStartClicked = {
                                if (activeEngine == null || activeEngine?.level?.id != lvl.id) {
                                    activeEngine = LevelEngine(lvl)
                                }
                                currentScreen = Screen.STATION
                            },
                            onBackClicked = { currentScreen = Screen.LEVEL_SELECT }
                        )
                    } else {
                        currentScreen = Screen.LEVEL_SELECT
                    }
                }
                Screen.STATION -> {
                    val lvl = selectedLevel
                    val engine = activeEngine
                    if (lvl != null && engine != null) {
                        StationScreen(
                            level = lvl,
                            language = saveData.language,
                            categories = itemCategories,
                            items = items,
                            candyBalance = saveData.candyBalance,
                            unlockedItems = saveData.unlockedItems,
                            soundManager = soundManager,
                            adProvider = adProvider,
                            levelEngine = engine,
                            initialCheckpoint = activeCheckpointForSession,
                            onUnlockItemWithCandy = { item -> unlockItemWithCandy(item) },
                            onUnlockItemWithAd = { item -> unlockItemWithAd(item) },
                            onCheckpointChanged = { cp ->
                                activeCheckpointForSession = cp
                                val updated = saveData.copy(activeCheckpoint = cp)
                                saveData = updated
                                storageManager.saveSaveData(updated)
                            },
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
                                if (result.evaluation.isWin) {
                                    activeCheckpointForSession = null
                                }
                                storageManager.saveSaveData(updatedSave)
                                currentScreen = Screen.LEVEL_RESULT
                            },
                            onExitConfirmed = { checkpoint ->
                                activeCheckpointForSession = checkpoint
                                val updated = saveData.copy(activeCheckpoint = checkpoint)
                                saveData = updated
                                storageManager.saveSaveData(updated)
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
                            characterState = res.characterState,
                            evaluation = res.evaluation,
                            candyEarned = candyAwardedThisRun,
                            soundManager = soundManager,
                            onNextLevelClicked = {
                                val nextId = res.level.id + 1
                                val nextLevel = levels.find { it.id == nextId }
                                if (nextLevel != null && saveData.unlockedLevels.contains(nextId)) {
                                    selectedLevel = nextLevel
                                    activeCheckpointForSession = null
                                    activeEngine = LevelEngine(nextLevel)
                                    currentScreen = Screen.LEVEL_INTRO
                                } else {
                                    currentScreen = Screen.LEVEL_SELECT
                                    checkAndTriggerAd(res.level.id)
                                }
                            },
                            onEditLookClicked = {
                                // Reuses existing res.engine intact so edits stay preserved!
                                activeEngine = res.engine
                                res.engine.selectStation((res.level.stations.size - 1).coerceAtLeast(0))
                                currentScreen = Screen.STATION
                            },
                            onReplayClicked = {
                                // Starts a fresh engine run for this level!
                                activeCheckpointForSession = null
                                val cleared = saveData.copy(activeCheckpoint = null)
                                saveData = cleared
                                storageManager.saveSaveData(cleared)
                                activeEngine = LevelEngine(res.level)
                                currentScreen = Screen.STATION
                            },
                            onBackToMapClicked = {
                                currentScreen = Screen.LEVEL_SELECT
                                checkAndTriggerAd(res.level.id)
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
                        language = saveData.language,
                        candyBalance = saveData.candyBalance,
                        unlockedItems = saveData.unlockedItems,
                        galleryCount = galleryFiles.size,
                        soundManager = soundManager,
                        adProvider = adProvider,
                        onUnlockItemWithCandy = { item -> unlockItemWithCandy(item) },
                        onUnlockItemWithAd = { item -> unlockItemWithAd(item) },
                        onSavePhotoToGallery = { bitmap ->
                            val saved = storageManager.savePhotoToInternalGallery(bitmap) != null
                            if (saved) {
                                galleryFiles = storageManager.getGalleryPhotos()
                                val updated = saveData.copy(galleryCount = galleryFiles.size)
                                saveData = updated
                                storageManager.saveSaveData(updated)
                            }
                            saved
                        },
                        onBackClicked = { currentScreen = Screen.HOME }
                    )
                }
                Screen.GALLERY -> {
                    GalleryScreen(
                        photos = galleryFiles,
                        onExportPhoto = { file, onComplete ->
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            ) {
                                pendingExportCallback = file to onComplete
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            } else {
                                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                                val ok = if (bmp != null) storageManager.exportPhotoToMediaStore(bmp) else false
                                onComplete(ok)
                            }
                        },
                        onDeletePhoto = { file ->
                            val deleted = storageManager.deletePhoto(file)
                            if (deleted) {
                                galleryFiles = storageManager.getGalleryPhotos()
                                val updated = saveData.copy(galleryCount = galleryFiles.size)
                                saveData = updated
                                storageManager.saveSaveData(updated)
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
                        language = saveData.language,
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
                        onLanguageSelected = { newLang ->
                            val updated = saveData.copy(language = newLang)
                            saveData = updated
                            storageManager.saveSaveData(updated)
                        },
                        onRestorePurchases = {
                            billingProvider.restorePurchases { entitlements ->
                                val combined = saveData.purchasedEntitlements + entitlements
                                val hasPremium = combined.contains("premium_levels")
                                val updated = saveData.copy(
                                    purchasedEntitlements = combined,
                                    unlockedLevels = UnlockPolicy.computeUnlockedLevels(saveData.bestStars, hasPremium)
                                )
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
                        statusMessage = shopStatusMessage,
                        onClearStatusMessage = { shopStatusMessage = null },
                        onPurchasePremium = {
                            handlePurchaseEntitlement(
                                entitlementId = "premium_levels",
                                onMessage = { msg -> shopStatusMessage = msg },
                                successMsg = purchaseSuccessStr,
                                failMsg = purchaseFailedStr
                            )
                        },
                        onPurchaseRemoveAds = {
                            handlePurchaseEntitlement(
                                entitlementId = "remove_ads",
                                onMessage = { msg -> shopStatusMessage = msg },
                                successMsg = purchaseSuccessStr,
                                failMsg = purchaseFailedStr
                            )
                        },
                        onBackClicked = { currentScreen = Screen.HOME }
                    )
                }
            }
        }
    }
}
