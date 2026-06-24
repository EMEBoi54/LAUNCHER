package com.example.coloros

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ColorOSWallpaper(val resId: Int, val wallName: String) {
    GRADIENT_POLISH(0, "Professional Polish (Gradient)"),
    NEON_SILK_TURQUOISE(R.drawable.img_coloros_wall_1_1782310207051, "Neon Aqua Silk"),
    GOLDEN_COBALT_WAVE(R.drawable.img_coloros_wall_2_1782310223302, "Sunset Wave")
}

enum class ConnectState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

enum class FluidCloudType {
    NONE,
    MUSIC,
    FILE_TRANSFER,
    CHARGING,
    TIMER
}

enum class AppType {
    NONE,
    CAMERA,
    PHOTOS,
    CONNECT_HUB,
    SETTINGS,
    BREENO_SEARCH,
    GAME_CENTER,
    ACTIVE_GAME
}

data class DownloadableGame(
    val id: String,
    val name: String,
    val category: String,
    val size: String,
    val rating: Float,
    val downloads: String,
    val description: String,
    val gradientColors: List<Color>,
    var isDownloaded: Boolean = false,
    var isDownloading: Boolean = false,
    var downloadProgress: Float = 0f,
    var downloadSpeed: String = "0 MB/s"
)

class LauncherViewModel : ViewModel() {
    // Styling & Theme Settings
    var currentWallpaper by mutableStateOf(ColorOSWallpaper.GRADIENT_POLISH)
    var isDarkMode by mutableStateOf(true)
    var iconCornerRadius by mutableFloatStateOf(24f) // dp-like scaling factor (squircle corner radius)
    var iconSizeMultiplier by mutableFloatStateOf(1f) // 0.8 to 1.2
    var showAppLabels by mutableStateOf(true)

    // Downloadable Games Database
    var downloadableGames by mutableStateOf(listOf(
        DownloadableGame(
            id = "genshin",
            name = "Genshin Impact",
            category = "Adventure • RPG",
            size = "3.2 GB",
            rating = 4.8f,
            downloads = "100M+",
            description = "Explore a massive fantasy world with real-time elemental combat and high-performance physics rendering optimized for Dimensity 9400.",
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) // Blue theme
        ),
        DownloadableGame(
            id = "pubg",
            name = "PUBG Mobile",
            category = "Action • Shooter",
            size = "1.8 GB",
            rating = 4.6f,
            downloads = "500M+",
            description = "The original battle royale style mobile game with 120FPS ultra fluid high-refresh display options.",
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFB45309)) // Amber theme
        ),
        DownloadableGame(
            id = "asphalt",
            name = "Asphalt 9",
            category = "Racing • Arcade",
            size = "2.4 GB",
            rating = 4.7f,
            downloads = "50M+",
            description = "Tear up the asphalt and face off against the world's most fearless drivers in console-quality racing simulator.",
            gradientColors = listOf(Color(0xFFEF4444), Color(0xFFB91C1C)) // Red theme
        ),
        DownloadableGame(
            id = "monopoly",
            name = "Monopoly Go!",
            category = "Casual • Board",
            size = "220 MB",
            rating = 4.5f,
            downloads = "10M+",
            description = "Roll the dice, build your empire, and rob your friends in this beautifully animated liquid-physics board game.",
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF047857)) // Emerald theme
        )
    ))

    var activeDownloadingGameId by mutableStateOf<String?>(null)
    
    // Active launched game state
    var currentlyLaunchedGameId by mutableStateOf<String?>(null)

    // Real-time animated device specifications
    var cpuFrequency by mutableFloatStateOf(1.5f) // GHz
    var cpuTemp by mutableFloatStateOf(34.2f) // °C
    var refreshRate by mutableIntStateOf(60) // Hz (LTPO simulation: 1, 10, 60, 120)
    var ramUsedGb by mutableFloatStateOf(8.4f) // GB out of 16GB
    var baseStorageUsedGb by mutableFloatStateOf(162.4f) // GB out of 512GB
    var isPerformanceMode by mutableStateOf(false) // GT Mode / Pro Gamer Mode
    
    // Calculated total storage based on installed games
    val totalStorageUsedGb: Float
        get() {
            var additional = 0f
            downloadableGames.forEach { game ->
                if (game.isDownloaded) {
                    val sizeNum = game.size.split(" ")[0].toFloatOrNull() ?: 0f
                    val sizeUnit = game.size.split(" ")[1]
                    if (sizeUnit == "GB") {
                        additional += sizeNum
                    } else if (sizeUnit == "MB") {
                        additional += sizeNum / 1024f
                    }
                }
            }
            return baseStorageUsedGb + additional
        }

    // Hardware/Settings Toggles
    var isWifiEnabled by mutableStateOf(true)
    var wifiName by mutableStateOf("OPPO_FindX_5G")
    var isBluetoothEnabled by mutableStateOf(true)
    var bluetoothDevice by mutableStateOf("OPPO AirBuds 3")
    var isMobileDataEnabled by mutableStateOf(true)
    var isFlashlightOn by mutableStateOf(false)
    var isSilentMode by mutableStateOf(false)
    var isEyeComfortEnabled by mutableStateOf(false)
    var screenBrightness by mutableFloatStateOf(0.75f)
    var volumeLevel by mutableFloatStateOf(0.6f)
    
    // System Status
    var batteryLevel by mutableIntStateOf(84)
    var isCharging by mutableStateOf(false)
    var currentTimeString by mutableStateOf("12:00")
    var currentDateString by mutableStateOf("Wednesday, June 24")

    // Fluid Cloud States
    var activeFluidCloud by mutableStateOf(FluidCloudType.NONE)
    var musicTitle by mutableStateOf("Colors - Find X8 theme")
    var musicArtist by mutableStateOf("OPPO Sound Labs")
    var musicIsPlaying by mutableStateOf(false)
    var timerDurationSeconds by mutableIntStateOf(180) // 3-minute quick timer
    var timerRemainingSeconds by mutableIntStateOf(0)
    private var timerJob: Job? = null
    
    // Find X Legitimate ColorOS Connect Link Status
    var padConnectState by mutableStateOf(ConnectState.DISCONNECTED)
    var watchConnectState by mutableStateOf(ConnectState.DISCONNECTED)
    var pcConnectState by mutableStateOf(ConnectState.DISCONNECTED)
    
    // Multi-screen collaboration features
    var sharedClipboard by mutableStateOf("Copied high-res photo path from Find X8 Pro")
    var lastTransferredFile by mutableStateOf<String?>(null)
    var transferProgress by mutableFloatStateOf(0f)
    var isTransferring by mutableStateOf(false)

    // UI Overlay Toggles
    var isControlCenterExpanded by mutableStateOf(false)
    var isSmartSidebarExpanded by mutableStateOf(false)
    var activeApp by mutableStateOf(AppType.NONE)

    // Sidebar quick utilities
    var sidebarNoteText by mutableStateOf("Draft: Meeting with Oppo engineering at 10 AM. Discuss ColorOS 15 fluid physics animations and Dimensity 9400 power optimization.")
    
    // Camera Simulator Specific States
    var cameraZoom by mutableFloatStateOf(1.0f) // 0.6x, 1x, 3x, 6x (Hasselblad Portrait Zoom)
    var cameraFilterIndex by mutableIntStateOf(0)
    val cameraFilters = listOf("Hasselblad Natural", "Radiant Portrait", "Classic Mono", "Oppo Warm", "Cinematic")
    var showGridlines by mutableStateOf(true)
    var capturedPhotos = mutableListOf<Int>() // Resources or visual placeholders

    init {
        // Start live system clock updater
        viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val sdfDate = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                currentTimeString = sdfTime.format(cal.time)
                currentDateString = sdfDate.format(cal.time)
                delay(1000)
            }
        }
        
        // Start live system specs updater loop (ColorOS 15 active telemetry simulation!)
        viewModelScope.launch {
            while (true) {
                // Update refresh rate, CPU speed, RAM usage and temperature dynamically
                if (currentlyLaunchedGameId != null) {
                    refreshRate = if (isPerformanceMode) 120 else listOf(90, 120).random()
                    cpuFrequency = if (isPerformanceMode) 3.63f else (2.8f + (Math.random() * 0.4).toFloat())
                    cpuTemp = if (isPerformanceMode) (41.5f + (Math.random() * 1.5).toFloat()) else (37.2f + (Math.random() * 1.2).toFloat())
                    ramUsedGb = (11.2f + (Math.random() * 0.5).toFloat()).coerceAtMost(16.0f)
                } else {
                    // Normal app or desktop
                    if (activeApp != AppType.NONE) {
                        refreshRate = listOf(60, 90, 120).random()
                        cpuFrequency = (1.8f + (Math.random() * 0.6).toFloat())
                        cpuTemp = (35.0f + (Math.random() * 0.8).toFloat())
                        ramUsedGb = (9.1f + (Math.random() * 0.3).toFloat())
                    } else {
                        // Desktop idle - LTPO technology drops screen refresh rate to conserve battery!
                        refreshRate = listOf(1, 10, 30, 60).random()
                        cpuFrequency = (1.2f + (Math.random() * 0.3).toFloat())
                        cpuTemp = (33.8f + (Math.random() * 0.4).toFloat())
                        ramUsedGb = (8.2f + (Math.random() * 0.2).toFloat())
                    }
                }
                delay(1500)
            }
        }
        
        // Setup initial captured images from the assets we generated!
        capturedPhotos.add(ColorOSWallpaper.NEON_SILK_TURQUOISE.resId)
        capturedPhotos.add(ColorOSWallpaper.GOLDEN_COBALT_WAVE.resId)
    }

    // Controls
    fun toggleWifi() {
        isWifiEnabled = !isWifiEnabled
        wifiName = if (isWifiEnabled) "OPPO_FindX_5G" else "Disconnected"
    }

    fun toggleBluetooth() {
        isBluetoothEnabled = !isBluetoothEnabled
        bluetoothDevice = if (isBluetoothEnabled) "OPPO AirBuds 3" else "Disconnected"
    }

    fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
    }

    fun toggleSilent() {
        isSilentMode = !isSilentMode
    }

    fun togglePlayPauseMusic() {
        musicIsPlaying = !musicIsPlaying
        if (musicIsPlaying) {
            triggerFluidCloud(FluidCloudType.MUSIC)
        } else if (activeFluidCloud == FluidCloudType.MUSIC) {
            activeFluidCloud = FluidCloudType.NONE
        }
    }

    fun toggleCharging() {
        isCharging = !isCharging
        if (isCharging) {
            batteryLevel = (batteryLevel + 1).coerceAtMost(100)
            triggerFluidCloud(FluidCloudType.CHARGING)
        } else {
            if (activeFluidCloud == FluidCloudType.CHARGING) {
                activeFluidCloud = FluidCloudType.NONE
            }
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        timerRemainingSeconds = timerDurationSeconds
        triggerFluidCloud(FluidCloudType.TIMER)
        timerJob = viewModelScope.launch {
            while (timerRemainingSeconds > 0) {
                delay(1000)
                timerRemainingSeconds--
            }
            // Timer finished!
            activeFluidCloud = FluidCloudType.NONE
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerRemainingSeconds = 0
        if (activeFluidCloud == FluidCloudType.TIMER) {
            activeFluidCloud = FluidCloudType.NONE
        }
    }

    fun triggerFluidCloud(type: FluidCloudType) {
        activeFluidCloud = type
        // Auto dismiss transient events after a few seconds, except music and timer
        if (type == FluidCloudType.CHARGING) {
            viewModelScope.launch {
                delay(4000)
                if (activeFluidCloud == FluidCloudType.CHARGING) {
                    activeFluidCloud = FluidCloudType.NONE
                }
            }
        }
    }

    fun connectDevice(device: String) {
        viewModelScope.launch {
            when (device) {
                "pad" -> {
                    padConnectState = ConnectState.CONNECTING
                    delay(1500)
                    padConnectState = ConnectState.CONNECTED
                    triggerFluidCloud(FluidCloudType.FILE_TRANSFER)
                }
                "watch" -> {
                    watchConnectState = ConnectState.CONNECTING
                    delay(1200)
                    watchConnectState = ConnectState.CONNECTED
                }
                "pc" -> {
                    pcConnectState = ConnectState.CONNECTING
                    delay(1800)
                    pcConnectState = ConnectState.CONNECTED
                }
            }
        }
    }

    fun disconnectDevice(device: String) {
        when (device) {
            "pad" -> padConnectState = ConnectState.DISCONNECTED
            "watch" -> watchConnectState = ConnectState.DISCONNECTED
            "pc" -> pcConnectState = ConnectState.DISCONNECTED
        }
    }

    fun simulateFileTransfer(fileName: String) {
        if (padConnectState != ConnectState.CONNECTED && pcConnectState != ConnectState.CONNECTED) {
            return
        }
        isTransferring = true
        lastTransferredFile = fileName
        transferProgress = 0f
        triggerFluidCloud(FluidCloudType.FILE_TRANSFER)
        viewModelScope.launch {
            while (transferProgress < 1f) {
                delay(150)
                transferProgress += 0.1f
            }
            transferProgress = 1.0f
            delay(800)
            isTransferring = false
            if (activeFluidCloud == FluidCloudType.FILE_TRANSFER) {
                activeFluidCloud = FluidCloudType.NONE
            }
        }
    }

    // Download/Install simulation for the App Store & Game Center
    fun installGame(gameId: String) {
        val gamesList = downloadableGames.toMutableList()
        val index = gamesList.indexOfFirst { it.id == gameId }
        if (index == -1) return
        
        val game = gamesList[index]
        if (game.isDownloaded || game.isDownloading) return
        
        // Start downloading
        game.isDownloading = true
        game.downloadProgress = 0f
        activeDownloadingGameId = gameId
        downloadableGames = gamesList
        
        // Trigger fluid cloud indicator for App Downloading!
        triggerFluidCloud(FluidCloudType.FILE_TRANSFER) // Reuse file transfer as download cloud animation!
        
        viewModelScope.launch {
            var progress = 0f
            while (progress < 1f) {
                delay(200)
                progress += (0.05f + (Math.random() * 0.1).toFloat())
                if (progress > 1f) progress = 1f
                
                // Fluctuating download speed
                val speed = String.format(Locale.US, "%.1f MB/s", 45f + (Math.random() * 25).toFloat())
                
                val currentList = downloadableGames.toMutableList()
                val idx = currentList.indexOfFirst { it.id == gameId }
                if (idx != -1) {
                    currentList[idx] = currentList[idx].copy(
                        downloadProgress = progress,
                        downloadSpeed = speed
                    )
                    downloadableGames = currentList
                }
            }
            
            // Download completed!
            delay(500)
            val finalList = downloadableGames.toMutableList()
            val finalIdx = finalList.indexOfFirst { it.id == gameId }
            if (finalIdx != -1) {
                finalList[finalIdx] = finalList[finalIdx].copy(
                    isDownloaded = true,
                    isDownloading = false,
                    downloadProgress = 1.0f
                )
                downloadableGames = finalList
            }
            activeDownloadingGameId = null
            
            // Completed notification!
            if (activeFluidCloud == FluidCloudType.FILE_TRANSFER) {
                activeFluidCloud = FluidCloudType.NONE
            }
        }
    }
    
    fun uninstallGame(gameId: String) {
        val gamesList = downloadableGames.toMutableList()
        val index = gamesList.indexOfFirst { it.id == gameId }
        if (index != -1) {
            gamesList[index] = gamesList[index].copy(
                isDownloaded = false,
                isDownloading = false,
                downloadProgress = 0f
            )
            downloadableGames = gamesList
        }
    }

    // Real-device launcher integration
    var installedRealApps by mutableStateOf<List<InstalledApp>>(emptyList())
    var activeAppTab by mutableIntStateOf(0) // 0: ColorOS Simulator, 1: Real Device Apps

    fun loadInstalledApps(packageManager: android.content.pm.PackageManager, currentPackageName: String) {
        viewModelScope.launch {
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
                    addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = packageManager.queryIntentActivities(intent, 0)
                val list = resolveInfos.mapNotNull { info ->
                    val packageName = info.activityInfo.packageName
                    if (packageName == currentPackageName) {
                        null
                    } else {
                        InstalledApp(
                            packageName = packageName,
                            label = info.loadLabel(packageManager).toString(),
                            iconDrawable = info.loadIcon(packageManager)
                        )
                    }
                }.sortedBy { it.label.lowercase() }
                installedRealApps = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun launchRealApp(context: android.content.Context, packageName: String) {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class InstalledApp(
    val packageName: String,
    val label: String,
    val iconDrawable: android.graphics.drawable.Drawable? = null
)
