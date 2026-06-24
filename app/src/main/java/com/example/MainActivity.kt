package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coloros.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ColorOSLauncherApp()
            }
        }
    }
}

@Composable
fun ColorOSLauncherApp(
    viewModel: LauncherViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context.packageManager, context.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Wallpaper background
        if (viewModel.currentWallpaper.resId == 0) {
            WallpaperBackground()
        } else {
            Image(
                painter = painterResource(id = viewModel.currentWallpaper.resId),
                contentDescription = "Desktop Wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 2. Main Desktop UI Structure
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Status Bar Overlay (clickable to pull down Control Center)
            StatusHeader(
                viewModel = viewModel,
                onClick = { viewModel.isControlCenterExpanded = true },
                modifier = Modifier.testTag("status_bar_tap_trigger")
            )

            // Fluid Cloud (Dynamic Capsule)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FluidCloudCapsule(viewModel = viewModel)
            }

            // Desktop scrollable/main contents
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Weather & Clock Widget
                    OppoWeatherClockWidget(
                        viewModel = viewModel,
                        onClick = { viewModel.activeApp = AppType.BREENO_SEARCH }
                    )

                    // Find X Interconnect Quick Widget
                    OppoInterconnectWidget(
                        viewModel = viewModel,
                        onOpenHub = { viewModel.activeApp = AppType.CONNECT_HUB }
                    )
                }

                // Grid of core Apps
                LauncherAppGrid(
                    viewModel = viewModel,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Translucent Bottom Dock
                LauncherDock(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }

        // 3. Side Smart Sidebar Handle overlay
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 0.dp)
        ) {
            SmartSidebarHandle(
                isVisible = !viewModel.isSmartSidebarExpanded && viewModel.activeApp == AppType.NONE,
                onClick = { viewModel.isSmartSidebarExpanded = true }
            )
        }

        // 4. OVERLAYS (Control Center, Sidebar, Breeno AI)
        
        // Smart Sidebar Overlay Panel
        AnimatedVisibility(
            visible = viewModel.isSmartSidebarExpanded,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut()
        ) {
            SmartSidebarPanel(
                viewModel = viewModel,
                onDismiss = { viewModel.isSmartSidebarExpanded = false },
                modifier = Modifier.testTag("smart_sidebar_panel")
            )
        }

        // Control Center Overlay Panel
        AnimatedVisibility(
            visible = viewModel.isControlCenterExpanded,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut()
        ) {
            ControlCenterOverlay(
                viewModel = viewModel,
                onDismiss = { viewModel.isControlCenterExpanded = false },
                modifier = Modifier.testTag("control_center_panel")
            )
        }

        // Breeno Voice / Global Search Overlay
        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.BREENO_SEARCH,
            enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
        ) {
            BreenoSearchOverlay(
                viewModel = viewModel,
                onDismiss = { viewModel.activeApp = AppType.NONE }
            )
        }

        // 5. FULL-SCREEN INTERACTIVE APPS (Camera, Gallery, Connect Hub, Settings, Game Center, Active Game)
        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.CAMERA,
            enter = scaleIn(
                initialScale = 0.85f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = scaleOut(
                targetScale = 0.82f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            CameraApp(
                viewModel = viewModel,
                onClose = { viewModel.activeApp = AppType.NONE }
            )
        }

        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.PHOTOS,
            enter = scaleIn(
                initialScale = 0.85f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = scaleOut(
                targetScale = 0.82f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            GalleryApp(
                viewModel = viewModel,
                onClose = { viewModel.activeApp = AppType.NONE }
            )
        }

        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.CONNECT_HUB,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            FindXConnectHub(
                viewModel = viewModel,
                onClose = { viewModel.activeApp = AppType.NONE },
                modifier = Modifier.testTag("find_x_connect_hub_app")
            )
        }

        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.SETTINGS,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            SettingsApp(
                viewModel = viewModel,
                onClose = { viewModel.activeApp = AppType.NONE },
                modifier = Modifier.testTag("settings_app")
            )
        }

        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.GAME_CENTER,
            enter = scaleIn(
                initialScale = 0.85f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = scaleOut(
                targetScale = 0.82f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            GameCenterApp(
                viewModel = viewModel,
                onClose = { viewModel.activeApp = AppType.NONE },
                modifier = Modifier.testTag("game_center_app")
            )
        }

        AnimatedVisibility(
            visible = viewModel.activeApp == AppType.ACTIVE_GAME,
            enter = scaleIn(
                initialScale = 0.9f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = tween(350)),
            exit = scaleOut(
                targetScale = 0.85f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            ActiveGameApp(
                viewModel = viewModel,
                onClose = { 
                    viewModel.currentlyLaunchedGameId = null
                    viewModel.activeApp = AppType.NONE 
                },
                modifier = Modifier.testTag("active_game_app")
            )
        }

        // 6. EYE COMFORT AMBER LAYER FILTER
        if (viewModel.isEyeComfortEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                    .pointerInput(Unit) {} // pass-through drawing but do not consume touches
            )
        }
    }
}

@Composable
fun StatusHeader(
    viewModel: LauncherViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Time & Carrier
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewModel.currentTimeString,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "OPPO 5G",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Right: Hardware Status Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.isSilentMode) {
                Icon(
                    imageVector = Icons.Filled.VolumeOff,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            if (viewModel.isWifiEnabled) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "WiFi Connected",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            if (viewModel.isBluetoothEnabled) {
                Icon(
                    imageVector = Icons.Filled.Bluetooth,
                    contentDescription = "Bluetooth Connected",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.SignalCellular4Bar,
                contentDescription = "5G Signal",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
            
            // Styled battery horizontal capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                if (viewModel.isCharging) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = "Charging",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(9.dp)
                    )
                }
                Text(
                    text = "${viewModel.batteryLevel}%",
                    color = if (viewModel.batteryLevel <= 20) Color.Red else (if (viewModel.isCharging) Color(0xFF10B981) else Color.White),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OppoWeatherClockWidget(
    viewModel: LauncherViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Clock Widget Large Time
        Text(
            text = viewModel.currentTimeString,
            fontSize = 72.sp,
            fontWeight = FontWeight.W100,
            letterSpacing = (-1.5).sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
        // Date uppercase centered
        Text(
            text = viewModel.currentDateString.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
        // Elegant weather status pill below
        Row(
            modifier = Modifier
                .padding(top = 14.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pulsating green status indicator
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34D399))
            )
            Text(
                text = "28°C Sunny • Shenzhen | AQI 32",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OppoInterconnectWidget(
    viewModel: LauncherViewModel,
    onOpenHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPadConnected = viewModel.padConnectState == ConnectState.CONNECTED
    val isWatchConnected = viewModel.watchConnectState == ConnectState.CONNECTED
    val isPcConnected = viewModel.pcConnectState == ConnectState.CONNECTED
    val hasConnections = isPadConnected || isWatchConnected || isPcConnected

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .clickable { onOpenHub() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeviceHub,
                        contentDescription = "Interconnect",
                        tint = Color(0xFF00A2E8),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Legitimate ColorOS Link",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (hasConnections) Color(0xFF10B981).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (hasConnections) "Linked" else "Scanning",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasConnections) Color(0xFF10B981) else Color.LightGray
                    )
                }
            }

            // Connection description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (hasConnections) {
                        val devices = mutableListOf<String>()
                        if (isPadConnected) devices.add("Pad 3 Pro")
                        if (isWatchConnected) devices.add("Watch 4 Pro")
                        if (isPcConnected) devices.add("PC Book")
                        
                        Text(
                            text = "Connected: ${devices.joinToString(", ")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Shared Cloud Clipboard Active",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    } else {
                        Text(
                            text = "No other Oppo devices linked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Tap to pair with Oppo Pad, Watch, or Book Pro.",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Filled.NavigateNext,
                    contentDescription = "Details",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LauncherAppGrid(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coreItems = listOf(
        LauncherAppItem(
            name = "Camera",
            isRealApp = false,
            icon = Icons.Filled.Camera,
            gradientColors = listOf(Color(0xFF27272A), Color(0xFF52525B)),
            appType = AppType.CAMERA,
            testTag = "launcher_camera"
        ),
        LauncherAppItem(
            name = "Photos",
            isRealApp = false,
            icon = Icons.Filled.PhotoLibrary,
            gradientColors = listOf(Color(0xFFFF7E40), Color(0xFFFF2E93)),
            appType = AppType.PHOTOS,
            testTag = "launcher_photos"
        ),
        LauncherAppItem(
            name = "Oppo Link",
            isRealApp = false,
            icon = Icons.Filled.DeviceHub,
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFFC084FC)),
            appType = AppType.CONNECT_HUB,
            testTag = "launcher_oppolink"
        ),
        LauncherAppItem(
            name = "Settings",
            isRealApp = false,
            icon = Icons.Filled.Settings,
            gradientColors = listOf(Color(0xFFE4E4E7), Color(0xFFA1A1AA)),
            appType = AppType.SETTINGS,
            testTag = "launcher_settings"
        ),
        LauncherAppItem(
            name = "Game Center",
            isRealApp = false,
            icon = Icons.Filled.SportsEsports,
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
            appType = AppType.GAME_CENTER,
            testTag = "launcher_gamecenter"
        ),
        LauncherAppItem(
            name = "Breeno AI",
            isRealApp = false,
            icon = Icons.Filled.AutoAwesome,
            gradientColors = listOf(Color(0xFF00A2E8), Color(0xFF38BDF8)),
            appType = AppType.BREENO_SEARCH,
            testTag = "launcher_breeno"
        )
    )

    // Map installed games dynamically as desktop shortcuts!
    val installedGames = viewModel.downloadableGames.filter { it.isDownloaded }
    val dynamicItems = installedGames.map { game ->
        val gameIcon = when (game.id) {
            "genshin" -> Icons.Filled.FlashOn
            "pubg" -> Icons.Filled.Adjust
            "asphalt" -> Icons.Filled.DirectionsCar
            "monopoly" -> Icons.Filled.Extension
            else -> Icons.Filled.Gamepad
        }
        LauncherAppItem(
            name = game.name,
            isRealApp = false,
            icon = gameIcon,
            gradientColors = game.gradientColors,
            appType = AppType.ACTIVE_GAME,
            testTag = "launcher_game_${game.id}",
            gameId = game.id
        )
    }

    // Convert real installed apps to LauncherAppItem
    val realItems = viewModel.installedRealApps.map { app ->
        LauncherAppItem(
            name = app.label,
            isRealApp = true,
            iconDrawable = app.iconDrawable,
            appType = AppType.NONE,
            testTag = "launcher_real_${app.packageName}",
            packageName = app.packageName
        )
    }

    val items = coreItems + dynamicItems + realItems

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (app.isRealApp) {
                            viewModel.launchRealApp(context, app.packageName)
                        } else {
                            if (app.gameId.isNotEmpty()) {
                                viewModel.currentlyLaunchedGameId = app.gameId
                                viewModel.activeApp = AppType.ACTIVE_GAME
                            } else {
                                viewModel.activeApp = app.appType
                            }
                        }
                    }
                    .testTag(app.testTag)
            ) {
                // Customized Squircle Icon Shape based on settings slider!
                var boxModifier = Modifier
                    .size((56f * viewModel.iconSizeMultiplier).dp)
                    .clip(RoundedCornerShape(viewModel.iconCornerRadius.dp))
                
                boxModifier = if (app.isRealApp) {
                    boxModifier.background(Color.White.copy(alpha = 0.15f))
                } else {
                    boxModifier.background(Brush.linearGradient(app.gradientColors))
                }

                if (app.isRealApp) {
                    boxModifier = boxModifier.border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(viewModel.iconCornerRadius.dp)
                    )
                }

                boxModifier = boxModifier.shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(viewModel.iconCornerRadius.dp)
                )

                Box(
                    modifier = boxModifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (app.isRealApp && app.iconDrawable != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = app.iconDrawable),
                            contentDescription = app.name,
                            modifier = Modifier.size((38f * viewModel.iconSizeMultiplier).dp)
                        )
                    } else if (app.icon != null) {
                        Icon(
                            imageVector = app.icon,
                            contentDescription = app.name,
                            tint = if (app.gradientColors.first() == Color(0xFFE4E4E7)) Color(0xFF27272A) else Color.White,
                            modifier = Modifier.size((28f * viewModel.iconSizeMultiplier).dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Apps,
                            contentDescription = app.name,
                            tint = Color.White,
                            modifier = Modifier.size((28f * viewModel.iconSizeMultiplier).dp)
                        )
                    }
                }
                
                if (viewModel.showAppLabels) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun LauncherDock(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(32.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dockItems = listOf(
                    DockItem("Phone", Icons.Filled.Phone, listOf(Color(0xFF10B981), Color(0xFF34D399))),
                    DockItem("Messages", Icons.Filled.Email, listOf(Color(0xFF2563EB), Color(0xFF38BDF8))),
                    DockItem("Breeno AI", Icons.Filled.Mic, listOf(Color(0xFF27272A), Color(0xFF52525B))),
                    DockItem("Oppo Hub", Icons.Filled.DeviceHub, listOf(Color(0xFF8B5CF6), Color(0xFFC084FC)))
                )

                dockItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(item.gradient)
                            )
                            .clickable {
                                if (item.label == "Oppo Hub") {
                                    viewModel.activeApp = AppType.CONNECT_HUB
                                } else {
                                    viewModel.activeApp = AppType.BREENO_SEARCH
                                }
                            }
                            .testTag("dock_${item.label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Gesture Navigation Pill from the Professional Polish theme
        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 2.dp)
                .width(110.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        )
    }
}

data class LauncherAppItem(
    val name: String,
    val isRealApp: Boolean,
    val icon: ImageVector? = null,
    val iconDrawable: android.graphics.drawable.Drawable? = null,
    val gradientColors: List<Color> = listOf(Color(0xFF27272A), Color(0xFF52525B)),
    val appType: AppType = AppType.NONE,
    val testTag: String,
    val gameId: String = "",
    val packageName: String = ""
)

data class DockItem(
    val label: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@Composable
fun WallpaperBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw gradient glows mirroring the Professional Polish tailwind blur design!
            // Top left: Blue glow (absolute top-[-10%] left-[-20%] w-[100%] h-[100%] bg-blue-600 rounded-full blur-[120px] opacity-40)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2563EB).copy(alpha = 0.45f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(-size.width * 0.15f, -size.height * 0.1f),
                    radius = size.width * 1.3f
                )
            )
            // Bottom right: Purple glow (absolute bottom-[-10%] right-[-20%] w-[100%] h-[100%] bg-purple-900 rounded-full blur-[120px] opacity-40)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF581C87).copy(alpha = 0.40f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 1.2f, size.height * 1.1f),
                    radius = size.width * 1.3f
                )
            )
            // Top/Center right: Emerald/Teal glow (absolute top-[20%] right-[-10%] w-[60%] h-[60%] bg-emerald-500 rounded-full blur-[100px] opacity-40)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF10B981).copy(alpha = 0.35f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.25f),
                    radius = size.width * 0.8f
                )
            )
        }
    }
}
