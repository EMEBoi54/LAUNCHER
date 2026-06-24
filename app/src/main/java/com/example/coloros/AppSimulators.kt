package com.example.coloros

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.WallpaperBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.foundation.Canvas

// ==========================================
// 1. CAMERA APP WITH HASSELBLAD SIMULATION
// ==========================================
@Composable
fun CameraApp(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isShutterPressed by remember { mutableStateOf(false) }
    var flashMode by remember { mutableStateOf("off") } // off, on, auto
    var showFilterBar by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Camera Top Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Camera", tint = Color.White)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flash
                    IconButton(onClick = {
                        flashMode = when (flashMode) {
                            "off" -> "on"
                            "on" -> "auto"
                            else -> "off"
                        }
                    }) {
                        Icon(
                            imageVector = when (flashMode) {
                                "on" -> Icons.Filled.FlashOn
                                "auto" -> Icons.Filled.FlashAuto
                                else -> Icons.Filled.FlashOff
                            },
                            contentDescription = "Flash Mode",
                            tint = if (flashMode != "off") Color(0xFFFBBF24) else Color.White
                        )
                    }

                    // Gridlines toggle
                    IconButton(onClick = { viewModel.showGridlines = !viewModel.showGridlines }) {
                        Icon(
                            imageVector = Icons.Filled.GridOn,
                            contentDescription = "Gridlines",
                            tint = if (viewModel.showGridlines) Color(0xFF00A2E8) else Color.White
                        )
                    }

                    // Timer Mode
                    IconButton(onClick = {
                        viewModel.triggerFluidCloud(FluidCloudType.TIMER)
                        viewModel.startTimer()
                    }) {
                        Icon(imageVector = Icons.Filled.Timer, contentDescription = "Self-Timer", tint = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF5E00)) // Hasselblad Orange Accent
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("H", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            // Central Viewfinder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF151515)),
                contentAlignment = Alignment.Center
            ) {
                // Background Viewfinder Image (loading active wallpaper as our active viewfinder!)
                if (viewModel.currentWallpaper.resId == 0) {
                    WallpaperBackground(modifier = Modifier.fillMaxSize())
                } else {
                    Image(
                        painter = painterResource(id = viewModel.currentWallpaper.resId),
                        contentDescription = "Viewfinder Stream",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.85f
                    )
                }

                // Optional grid lines
                if (viewModel.showGridlines) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Vertical Lines
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                        }
                        // Horizontal Lines
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).border(0.5.dp, Color.White.copy(alpha = 0.25f)))
                        }
                    }
                }

                // Hasselblad Watermark Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "OPPO Find X8 Ultra | HASSELBLAD",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "F/1.6 | 1/250s | ISO 125 | 24mm Portrait Mode",
                        color = Color.LightGray,
                        fontSize = 8.sp
                    )
                }

                // Flash Burst Effect
                if (isShutterPressed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    )
                }
            }

            // Bottom camera panels
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .background(Color.Black)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Zoom Switcher (0.6x, 1x, 3x, 6x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.6f, 1.0f, 3.0f, 6.0f).forEach { zoomVal ->
                        val isSelected = viewModel.cameraZoom == zoomVal
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFFF5E00) else Color(0xFF222222))
                                .clickable { viewModel.cameraZoom = zoomVal },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (zoomVal == 0.6f) "0.6" else "${zoomVal.toInt()}x",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 2. Camera Mode/Filter Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showFilterBar = !showFilterBar }) {
                        Icon(
                            imageVector = Icons.Filled.FilterBAndW,
                            contentDescription = "Filters",
                            tint = if (showFilterBar) Color(0xFFFF5E00) else Color.White
                        )
                    }

                    Text(
                        text = viewModel.cameraFilters[viewModel.cameraFilterIndex].uppercase(),
                        color = Color(0xFFFF5E00),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = {
                        viewModel.cameraFilterIndex = (viewModel.cameraFilterIndex + 1) % viewModel.cameraFilters.size
                    }) {
                        Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Next Filter", tint = Color.White)
                    }
                }

                // Filter slider if enabled
                AnimatedVisibility(visible = showFilterBar) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.cameraFilters.forEachIndexed { idx, filter ->
                            val isSel = viewModel.cameraFilterIndex == idx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFFFF5E00) else Color(0xFF1E1E1E))
                                    .clickable { viewModel.cameraFilterIndex = idx }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(filter, color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                }

                // 3. Capture Controls (Gallery, Shutter Button, Switch Camera)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Shortcut (shows latest photo)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable {
                                viewModel.activeApp = AppType.PHOTOS
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.currentWallpaper.resId == 0) {
                            WallpaperBackground(modifier = Modifier.fillMaxSize())
                        } else {
                            Image(
                                painter = painterResource(id = viewModel.currentWallpaper.resId),
                                contentDescription = "Gallery Shortcut",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Shutter Button
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(4.dp, Color.Black, CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    isShutterPressed = true
                                    delay(100)
                                    isShutterPressed = false
                                    // Add to gallery
                                    viewModel.triggerFluidCloud(FluidCloudType.FILE_TRANSFER)
                                    viewModel.simulateFileTransfer("Hasselblad_Shot_${System.currentTimeMillis() % 1000}.jpg")
                                }
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5E00)) // Classic orange button
                        )
                    }

                    // Switch camera (Mock rotater)
                    val rotation = remember { Animatable(0f) }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                rotation.animateTo(
                                    targetValue = rotation.value + 180f,
                                    animationSpec = tween(400, easing = LinearOutSlowInEasing)
                                )
                            }
                        },
                        modifier = Modifier.rotate(rotation.value)
                    ) {
                        Icon(imageVector = Icons.Filled.FlipCameraAndroid, contentDescription = "Switch Camera", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. PHOTOS / GALLERY APP
// ==========================================
@Composable
fun GalleryApp(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = viewModel.isDarkMode
    var selectedPhoto by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF111318) else Color(0xFFF1F5F9))
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = if (isDark) Color.White else Color.Black)
                }
                Column {
                    Text("Oppo Photos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                    Text("Shot on Find X8 Hasselblad Camera", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        if (selectedPhoto != null) {
            // Full screen view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = selectedPhoto!!),
                    contentDescription = "Full Screen Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Info Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Shot on Find X8 Pro", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("HASSELBLAD Natural Color System | ISO 64 | 50MP Dual Periscope", color = Color.LightGray, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { selectedPhoto = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Text("Back to Gallery", color = Color.White)
                        }
                        Button(
                            onClick = {
                                // Set wallpaper
                                val wall = ColorOSWallpaper.entries.find { it.resId == selectedPhoto }
                                if (wall != null) {
                                    viewModel.currentWallpaper = wall
                                }
                                selectedPhoto = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2E8))
                        ) {
                            Text("Apply as Wallpaper", color = Color.White)
                        }
                    }
                }
            }
        } else {
            // Gallery Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewModel.capturedPhotos) { photoRes ->
                    Image(
                        painter = painterResource(id = photoRes),
                        contentDescription = "Photo thumbnail",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedPhoto = photoRes },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}


// ==========================================
// 3. LAUNCHER SETTINGS & CUSTOMIZATION
// ==========================================
@Composable
fun SettingsApp(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = viewModel.isDarkMode
    val textCol = if (isDark) Color.White else Color.Black
    val bgCol = if (isDark) Color(0xFF111318) else Color(0xFFF1F5F9)
    val cardBg = if (isDark) Color(0xFF1E2129) else Color(0xFFECEFF4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgCol)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onClose,
                colors = IconButtonDefaults.iconButtonColors(containerColor = cardBg)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = textCol)
            }
            Column {
                Text("ColorOS Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textCol)
                Text("Customize Find X8 Experience", fontSize = 11.sp, color = Color.Gray)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Wallpaper Chooser
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DESKTOP WALLPAPER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ColorOSWallpaper.entries.forEach { wall ->
                            val isSel = viewModel.currentWallpaper == wall
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBg)
                                    .border(
                                        width = if (isSel) 2.dp else 0.dp,
                                        color = if (isSel) Color(0xFF00A2E8) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.currentWallpaper = wall }
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (wall.resId == 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        WallpaperBackground(modifier = Modifier.fillMaxSize())
                                    }
                                } else {
                                    Image(
                                        painter = painterResource(id = wall.resId),
                                        contentDescription = wall.wallName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    wall.wallName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textCol,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 2. Icon Styling Customizer
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("AQUAMORPHIC ICON SYSTEM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                        // Corner radius slider (Squircle transformation!)
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Icon Shape (Squircle Corners)", fontSize = 12.sp, color = textCol)
                                Text("${viewModel.iconCornerRadius.toInt()}dp", fontSize = 11.sp, color = Color(0xFF00A2E8))
                            }
                            Slider(
                                value = viewModel.iconCornerRadius,
                                onValueChange = { viewModel.iconCornerRadius = it },
                                valueRange = 8f..32f,
                                colors = SliderDefaults.colors(activeTrackColor = Color(0xFF00A2E8))
                            )
                        }

                        // Size multiplier slider
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Icon Scale Size", fontSize = 12.sp, color = textCol)
                                Text(String.format("%.1fx", viewModel.iconSizeMultiplier), fontSize = 11.sp, color = Color(0xFF00A2E8))
                            }
                            Slider(
                                value = viewModel.iconSizeMultiplier,
                                onValueChange = { viewModel.iconSizeMultiplier = it },
                                valueRange = 0.8f..1.2f,
                                colors = SliderDefaults.colors(activeTrackColor = Color(0xFF00A2E8))
                            )
                        }

                        // App Label Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Display App Names", fontSize = 12.sp, color = textCol)
                            Switch(
                                checked = viewModel.showAppLabels,
                                onCheckedChange = { viewModel.showAppLabels = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00A2E8))
                            )
                        }
                    }
                }
            }

            // 3. Dynamic System Specs & Hardware Telemetry
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info, 
                                contentDescription = null, 
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(22.dp)
                            )
                            Text("About Find X8 Ultra", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textCol)
                        }

                        // Static Official Specifications
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val specs = listOf(
                                "Device Name" to "OPPO Find X8 Ultra 5G",
                                "OS Version" to "ColorOS 15.0 (Android 15 Base)",
                                "Processor" to "Dimensity 9400 Octa-Core (3nm)",
                                "Primary Camera" to "50MP Dual Periscope Quad Camera (Hasselblad)",
                                "Battery & Charging" to "5910 mAh Glacier Battery | 100W SuperVOOC"
                            )

                            specs.forEach { spec ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(spec.first, fontSize = 11.sp, color = Color.Gray)
                                    Text(spec.second, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textCol)
                                }
                            }
                        }

                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.06f))

                        // Dynamic Real-time Hardware Telemetry (ColorOS 15 Active Performance Monitor!)
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Memory, 
                                contentDescription = null, 
                                tint = Color(0xFF00A2E8),
                                modifier = Modifier.size(18.dp)
                            )
                            Text("ACTIVE TELEMETRY (REAL-TIME)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // 1. Dynamic Refresh Rate
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("LTPO Display Rate", fontSize = 11.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                    Text("${viewModel.refreshRate} Hz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                }
                            }

                            // 2. CPU Speed
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("CPU Clock Speed", fontSize = 11.sp, color = Color.Gray)
                                Text(String.format(Locale.US, "%.2f GHz", viewModel.cpuFrequency), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textCol)
                            }

                            // 3. Core Temperature
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("SoC Temperature", fontSize = 11.sp, color = Color.Gray)
                                Text(String.format(Locale.US, "%.1f °C", viewModel.cpuTemp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (viewModel.cpuTemp > 38f) Color(0xFFEF4444) else textCol)
                            }

                            // 4. Active RAM Allocation
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("RAM Utilization", fontSize = 11.sp, color = Color.Gray)
                                    Text(String.format(Locale.US, "%.1f GB / 16.0 GB", viewModel.ramUsedGb), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textCol)
                                }
                                LinearProgressIndicator(
                                    progress = { viewModel.ramUsedGb / 16f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = Color(0xFF00A2E8),
                                    trackColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
                                )
                            }

                            // 5. Dynamic Storage (reflects downloaded games!)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                val usedStorage = viewModel.totalStorageUsedGb
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Dynamic Storage Space", fontSize = 11.sp, color = Color.Gray)
                                    Text(String.format(Locale.US, "%.1f GB / 512.0 GB", usedStorage), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textCol)
                                }
                                LinearProgressIndicator(
                                    progress = { usedStorage / 512f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = Color(0xFFFF5E00),
                                    trackColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Default Launcher Configuration (Official Launcher Integration)
            item {
                val context = LocalContext.current
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SYSTEM HOME LAUNCHER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        
                        Text(
                            text = "You can set this premium ColorOS launcher as your default system home screen. Real apps are fully loaded and supported inside the desktop grid.",
                            fontSize = 12.sp,
                            color = textCol.copy(alpha = 0.8f)
                        )
                        
                        Button(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(android.provider.Settings.ACTION_HOME_SETTINGS).apply {
                                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    try {
                                        val intent = android.content.Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS").apply {
                                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (ex: Exception) {
                                        try {
                                            val intent = android.content.Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(intent)
                                        } catch (e2: Exception) {
                                            e2.printStackTrace()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Home, contentDescription = null, tint = Color.White)
                                Text("Set as Default Launcher", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 4. BREENO VOICE / GLOBAL SEARCH
// ==========================================
@Composable
fun BreenoSearchOverlay(
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = viewModel.isDarkMode
    var query by remember { mutableStateOf("") }
    var aiAnswer by remember { mutableStateOf<String?>(null) }
    var isThinking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val trends = listOf(
        "ColorOS 15 fluid physics animation engine",
        "Find X8 Hasselblad Master Portrait mode",
        "SUPERVOOC 100W charging protocol specs",
        "Interconnection with OPPO Pad 3 Pro"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(if (isDark) Color(0xFF14171E) else Color(0xFFF8FAFC))
                .clickable(enabled = true, onClick = {}) // consume
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Search Input
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Ask Breeno AI or search apps...", color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color(0xFF00A2E8)) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    } else {
                        Icon(imageVector = Icons.Filled.Mic, contentDescription = "Speak", tint = Color.Gray)
                    }
                },
                textStyle = TextStyle(color = if (isDark) Color.White else Color.Black, fontSize = 14.sp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("breeno_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00A2E8),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                )
            )

            // Dynamic Response or Trends
            if (isThinking) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00A2E8))
                }
            } else if (aiAnswer != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF22252C) else Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00A2E8)))
                            Text("BREENO AI ASSISTANT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00A2E8))
                        }
                        Text(
                            text = aiAnswer!!,
                            fontSize = 12.sp,
                            color = if (isDark) Color.White else Color.Black,
                            lineHeight = 16.sp
                        )
                        Button(
                            onClick = { 
                                aiAnswer = null
                                query = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2E8)),
                            modifier = Modifier.align(Alignment.End).height(28.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text("Ask another question", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TRENDING BREENO TOPICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )

                    trends.forEach { trend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0xFF22252C) else Color(0xFFE2E8F0))
                                .clickable {
                                    scope.launch {
                                        isThinking = true
                                        delay(1000)
                                        isThinking = false
                                        aiAnswer = when (trend) {
                                            "ColorOS 15 fluid physics animation engine" -> 
                                                "ColorOS 15 introduces the Aquamorphic Fluid Physics Engine 2.0. Animations respond instantly to your finger swipe, dynamically scaling speed and elasticity based on gesture velocity."
                                            "Find X8 Hasselblad Master Portrait mode" -> 
                                                "Find X8 Ultra features dual-periscope cameras calibrated by Hasselblad. It includes natural color tone preservation and a mock Hasselblad shutter click sound."
                                            "SUPERVOOC 100W charging protocol specs" -> 
                                                "OPPO SUPERVOOC uses low-voltage, high-current direct charging. Your Find X8 glacier battery will charge from 1% to 100% in just 32 minutes."
                                            else -> 
                                                "OPPO Pad 3 Pro pairs instantly with Find X8. Simply swipe two devices near each other to sync clipboards and mirror files instantly."
                                        }
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(trend, fontSize = 12.sp, color = if (isDark) Color.LightGray else Color.DarkGray)
                            Icon(imageVector = Icons.Filled.TrendingUp, contentDescription = "Trending", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. OPPO GAME CENTER & APP STORE
// ==========================================
@Composable
fun GameCenterApp(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = viewModel.isDarkMode
    val textCol = if (isDark) Color.White else Color.Black
    val bgCol = if (isDark) Color(0xFF090A0F) else Color(0xFFF3F4F6)
    val cardBg = if (isDark) Color(0xFF151722) else Color(0xFFE5E7EB)
    val accentColor = Color(0xFF10B981) // Emerald Green ColorOS Game Center theme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgCol)
    ) {
        // Top Navigation Bar (Liquid Glass Styled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = cardBg)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = textCol)
                }
                Column {
                    Text("Oppo Game Center", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textCol)
                    Text("Fluid Play with Dimensity 9400", fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Quick Spec telemetry chip
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(0.5.dp, accentColor.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Bolt, contentDescription = "GT", tint = accentColor, modifier = Modifier.size(12.dp))
                    Text("${viewModel.refreshRate}Hz LTPO", color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Spec Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF065F46))))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "COLOROS 15 GRAPHICS HYPERBOOST", 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.5.sp
                )
                Text(
                    "Optimal Performance Engine", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White
                )
                Text(
                    "Dynamic resolution scaling and real-time ray tracing optimization are fully active. Download premium games to test extreme limits.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text("CPU FREQ", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(String.format(Locale.US, "%.2f GHz", viewModel.cpuFrequency), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("TEMP", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(String.format(Locale.US, "%.1f °C", viewModel.cpuTemp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("AVAIL STORAGE", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(String.format(Locale.US, "%.1f GB", 512f - viewModel.totalStorageUsedGb), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Text(
            "AVAILABLE GAMES", 
            fontSize = 11.sp, 
            fontWeight = FontWeight.Bold, 
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            letterSpacing = 1.sp
        )

        // Store App / Game download list
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(viewModel.downloadableGames) { game ->
                val gameIcon = when (game.id) {
                    "genshin" -> Icons.Filled.FlashOn
                    "pubg" -> Icons.Filled.Adjust
                    "asphalt" -> Icons.Filled.DirectionsCar
                    "monopoly" -> Icons.Filled.Extension
                    else -> Icons.Filled.Gamepad
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            0.5.dp, 
                            if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f), 
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Game Custom Gradient Icon
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(game.gradientColors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = gameIcon, contentDescription = game.name, tint = Color.White, modifier = Modifier.size(26.dp))
                        }

                        // Game info details
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(game.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textCol)
                            Text(game.category, fontSize = 11.sp, color = Color.Gray)
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFB300), modifier = Modifier.size(10.dp))
                                    Text(game.rating.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textCol)
                                }
                                Text("•", fontSize = 10.sp, color = Color.Gray)
                                Text(game.size, fontSize = 10.sp, color = Color.Gray)
                                Text("•", fontSize = 10.sp, color = Color.Gray)
                                Text(game.downloads, fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        // Action Install Button
                        Box(
                            modifier = Modifier.width(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (game.isDownloaded) {
                                // Already Installed
                                Button(
                                    onClick = {
                                        viewModel.currentlyLaunchedGameId = game.id
                                        viewModel.activeApp = AppType.ACTIVE_GAME
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.15f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("PLAY", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (game.isDownloading) {
                                // Download in progress with custom text speed
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { game.downloadProgress },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                        color = accentColor,
                                        trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)
                                    )
                                    Text(
                                        text = game.downloadSpeed,
                                        fontSize = 8.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${(game.downloadProgress * 100).toInt()}%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                }
                            } else {
                                // Install button
                                Button(
                                    onClick = { viewModel.installGame(game.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Download, contentDescription = "Install", tint = Color.White, modifier = Modifier.size(12.dp))
                                        Text("GET", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Float text representation for floating damage popups
data class FloatingDamage(
    val id: Long,
    val text: String,
    val color: Color,
    val xOffset: Float,
    val yOffset: Float,
    val isCrit: Boolean = false
)

// ==========================================
// 6. COLOROS ACTIVE GAME APP & HYPERBOOST OVERLAY
// ==========================================
@Composable
fun ActiveGameApp(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val gameId = viewModel.currentlyLaunchedGameId ?: "genshin"
    val game = viewModel.downloadableGames.find { it.id == gameId } ?: viewModel.downloadableGames[0]
    
    // HyperBoost Gaming assistant toggles
    var showHyperBoost by remember { mutableStateOf(false) }
    var touchSensitivity by remember { mutableFloatStateOf(0.7f) }
    var isMistouchPrevented by remember { mutableStateOf(true) }
    
    // Simulated specs
    val activeFps = if (viewModel.isPerformanceMode) 120 else listOf(88, 92, 115, 120).random()
    val latencyMs = listOf(11, 12, 14, 18).random()

    // 1. GENSHIN IMPACT STATES
    var genshinBossHp by remember { mutableFloatStateOf(1f) }
    var genshinBossName by remember { mutableStateOf("Lv.90 Ruin Guard") }
    var genshinScore by remember { mutableIntStateOf(0) }
    var floatingDamages = remember { mutableStateListOf<FloatingDamage>() }
    var damageCounter by remember { mutableLongStateOf(0L) }
    var skillCooldown by remember { mutableIntStateOf(0) }
    var burstCooldown by remember { mutableIntStateOf(0) }

    // Skill cooldown timers
    LaunchedEffect(skillCooldown) {
        if (skillCooldown > 0) {
            delay(1000)
            skillCooldown--
        }
    }
    LaunchedEffect(burstCooldown) {
        if (burstCooldown > 0) {
            delay(1000)
            burstCooldown--
        }
    }

    // 2. PUBG MOBILE STATES
    var pubgAmmo by remember { mutableIntStateOf(30) }
    var pubgIsReloading by remember { mutableStateOf(false) }
    var pubgScore by remember { mutableIntStateOf(0) }
    var pubgTargetX by remember { mutableFloatStateOf(0f) }
    var pubgTargetDirection by remember { mutableFloatStateOf(1f) }
    var pubgFlashCount by remember { mutableIntStateOf(0) }

    // Target animation loop for PUBG
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            pubgTargetX += 4f * pubgTargetDirection
            if (pubgTargetX > 140f) {
                pubgTargetX = 140f
                pubgTargetDirection = -1f
            } else if (pubgTargetX < -140f) {
                pubgTargetX = -140f
                pubgTargetDirection = 1f
            }
        }
    }

    // 3. ASPHALT 9 STATES
    var asphaltSpeed by remember { mutableIntStateOf(240) }
    var asphaltLane by remember { mutableIntStateOf(1) } // 0: Left, 1: Center, 2: Right
    var asphaltScore by remember { mutableIntStateOf(0) }
    var asphaltObstacleLane by remember { mutableIntStateOf(listOf(0, 1, 2).random()) }
    var asphaltObstacleY by remember { mutableFloatStateOf(0f) }
    var asphaltNitroActive by remember { mutableStateOf(false) }
    var asphaltNitroRemaining by remember { mutableIntStateOf(0) }
    var asphaltLife by remember { mutableIntStateOf(3) }

    // Obstacle dropping thread
    LaunchedEffect(asphaltObstacleY) {
        delay(30)
        if (asphaltObstacleY < 350f) {
            asphaltObstacleY += if (asphaltNitroActive) 25f else 14f
        } else {
            // Check collision!
            if (asphaltObstacleLane == asphaltLane) {
                if (!asphaltNitroActive) {
                    asphaltLife = (asphaltLife - 1).coerceAtLeast(0)
                } else {
                    // Nitro destroys obstacles!
                    asphaltScore += 150
                }
            } else {
                // Dodged!
                asphaltScore += 50
            }
            asphaltObstacleY = 0f
            asphaltObstacleLane = listOf(0, 1, 2).random()
        }
    }

    // Asphalt Nitro countdown
    LaunchedEffect(asphaltNitroRemaining) {
        if (asphaltNitroRemaining > 0) {
            delay(1000)
            asphaltNitroRemaining--
            if (asphaltNitroRemaining == 0) {
                asphaltNitroActive = false
                asphaltSpeed = 240
            }
        }
    }

    // 4. MONOPOLY GO STATES
    var monopolyDice1 by remember { mutableIntStateOf(4) }
    var monopolyDice2 by remember { mutableIntStateOf(3) }
    var monopolyCash by remember { mutableDoubleStateOf(240.5) } // Millions
    var monopolyNetWorth by remember { mutableIntStateOf(340) }
    var monopolyEventText by remember { mutableStateOf("Welcome to the board! Tap Roll to begin.") }
    var monopolyIsRolling by remember { mutableStateOf(false) }
    var monopolyBoardPosition by remember { mutableIntStateOf(0) }

    val monopolyBoardSpaces = listOf(
        "START (Pass GO!)", "Oppo Research Labs", "Chance Card", "Dimensity Foundry",
        "Income Tax", "Breeno Headquarters", "Community Chest", "Glacier Battery Works",
        "Just Visiting", "VOOC Power Station", "Chance Card", "Hasselblad Optics Studio"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070709))
    ) {
        // Active Game Content Area
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Immersive Top Stats Hub (Liquid Glass)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 36.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Game Info & Performance status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.linearGradient(game.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(game.id) {
                                "genshin" -> Icons.Filled.FlashOn
                                "pubg" -> Icons.Filled.Adjust
                                "asphalt" -> Icons.Filled.DirectionsCar
                                "monopoly" -> Icons.Filled.Extension
                                else -> Icons.Filled.Gamepad
                            }, 
                            contentDescription = null, 
                            tint = Color.White, 
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(game.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Tech status indicators (LTPO and HyperBoost status)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Text("$activeFps FPS", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("$latencyMs ms", color = Color(0xFF00A2E8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // GAME SIMULATOR CORE SCREEN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF101116))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            ) {
                when (game.id) {
                    // 1. GENSHIN IMPACT SIMULATOR
                    "genshin" -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            // Atmospheric ambient glows using a Box with radial gradient background
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.15f), Color.Transparent)
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Boss Status
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(genshinBossName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    
                                    // Boss HP bar
                                    Box(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.1f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(genshinBossHp)
                                                .clip(CircleShape)
                                                .background(if (genshinBossHp > 0.3f) Color(0xFFEF4444) else Color(0xFFFFB300))
                                        )
                                    }
                                    Text(
                                        text = if (genshinBossHp > 0f) "${(genshinBossHp * 100).toInt()}% HP" else "DEFEATED!", 
                                        color = Color.LightGray, 
                                        fontSize = 10.sp
                                    )
                                }

                                // Interactive Target Boss Display
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                                            )
                                        )
                                        .clickable {
                                            if (genshinBossHp > 0f) {
                                                // Take normal damage
                                                val isCrit = Math.random() > 0.7
                                                val dmg = if (isCrit) (3000..5000).random() else (1200..2500).random()
                                                val damageText = if (isCrit) "CRIT! $dmg" else "$dmg"
                                                genshinBossHp = (genshinBossHp - 0.05f).coerceAtLeast(0f)
                                                
                                                floatingDamages.add(
                                                    FloatingDamage(
                                                        id = damageCounter++,
                                                        text = damageText,
                                                        color = if (isCrit) Color(0xFFFFB300) else Color(0xFF38BDF8),
                                                        xOffset = (-40..40).random().toFloat(),
                                                        yOffset = (-20..20).random().toFloat(),
                                                        isCrit = isCrit
                                                    )
                                                )
                                                genshinScore += dmg
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (genshinBossHp > 0f) {
                                        Icon(
                                            imageVector = Icons.Filled.Tv, 
                                            contentDescription = "Ruin Guard", 
                                            tint = if (viewModel.isPerformanceMode) Color(0xFFEF4444) else Color(0xFF3B82F6), 
                                            modifier = Modifier.size(60.dp)
                                        )
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = "Won", tint = Color(0xFFFFD700), modifier = Modifier.size(48.dp))
                                            Text("RECLAIM!", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Floating damage overlays
                                    floatingDamages.forEach { fd ->
                                        var active by remember { mutableStateOf(true) }
                                        var floatY by remember { mutableFloatStateOf(0f) }
                                        LaunchedEffect(fd.id) {
                                            // Animate floating damage up
                                            animate(0f, -80f, animationSpec = tween(500)) { valVal, _ ->
                                                floatY = valVal
                                            }
                                            active = false
                                        }

                                        if (active) {
                                            Box(
                                                modifier = Modifier.offset(x = fd.xOffset.dp, y = (fd.yOffset + floatY).dp)
                                            ) {
                                                Text(
                                                    text = fd.text, 
                                                    color = fd.color, 
                                                    fontWeight = if (fd.isCrit) FontWeight.Black else FontWeight.Bold,
                                                    fontSize = if (fd.isCrit) 16.sp else 12.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Gameplay actions
                                if (genshinBossHp > 0f) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Skill Button
                                        Button(
                                            onClick = {
                                                if (skillCooldown == 0 && genshinBossHp > 0f) {
                                                    genshinBossHp = (genshinBossHp - 0.18f).coerceAtLeast(0f)
                                                    genshinScore += 8000
                                                    skillCooldown = 6
                                                    floatingDamages.add(
                                                        FloatingDamage(
                                                            id = damageCounter++,
                                                            text = "Vaporize! 8,245",
                                                            color = Color(0xFFFF5252),
                                                            xOffset = 0f,
                                                            yOffset = -30f,
                                                            isCrit = true
                                                        )
                                                    )
                                                }
                                            },
                                            enabled = skillCooldown == 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text(if (skillCooldown > 0) "SKILL (${skillCooldown}s)" else "ELEMENTAL SKILL", fontSize = 10.sp)
                                        }

                                        // Ultimate Burst Button
                                        Button(
                                            onClick = {
                                                if (burstCooldown == 0 && genshinBossHp > 0f) {
                                                    genshinBossHp = (genshinBossHp - 0.35f).coerceAtLeast(0f)
                                                    genshinScore += 18000
                                                    burstCooldown = 12
                                                    floatingDamages.add(
                                                        FloatingDamage(
                                                            id = damageCounter++,
                                                            text = "SWIRL BURST! 18,420",
                                                            color = Color(0xFFA855F7),
                                                            xOffset = 0f,
                                                            yOffset = -40f,
                                                            isCrit = true
                                                        )
                                                    )
                                                }
                                            },
                                            enabled = burstCooldown == 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                                        ) {
                                            Text(if (burstCooldown > 0) "BURST (${burstCooldown}s)" else "ULTIMATE BURST", fontSize = 10.sp)
                                        }
                                    }
                                } else {
                                    // Victory state buttons
                                    Button(
                                        onClick = {
                                            genshinBossHp = 1f
                                            genshinBossName = listOf("Lv.92 Ruin Hunter", "Lv.95 Abyss Mage", "Lv.90 Primo Geovishap").random()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Text("RESPAWN NEW BOSS", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 2. PUBG MOBILE SIMULATOR
                    "pubg" -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("SCORE: $pubgScore", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("AMMO: $pubgAmmo / 30", color = if (pubgAmmo < 10) Color.Red else Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Interactive Target Canvas Area
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(Color(0xFF1A1B22))
                                        .border(1.dp, Color.White.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Simulated gun muzzle flash flash
                                    if (pubgFlashCount > 0) {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.15f)))
                                        LaunchedEffect(pubgFlashCount) {
                                            delay(80)
                                            pubgFlashCount = 0
                                        }
                                    }

                                    // Left-right moving target bullseye!
                                    Box(
                                        modifier = Modifier
                                            .offset(x = pubgTargetX.dp)
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .border(6.dp, Color.White, CircleShape)
                                            .border(12.dp, Color.Red, CircleShape)
                                            .clickable {
                                                if (pubgAmmo > 0 && !pubgIsReloading) {
                                                    pubgAmmo--
                                                    pubgFlashCount++
                                                    // High-precision touch triggers high points
                                                    pubgScore += 100
                                                }
                                            }
                                    )
                                }

                                // Interactive Gun Shooting Button Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            if (pubgAmmo > 0 && !pubgIsReloading) {
                                                pubgAmmo--
                                                pubgFlashCount++
                                                
                                                // Calculate if bullet hit moving target!
                                                // Center hit threshold
                                                if (pubgTargetX > -30f && pubgTargetX < 30f) {
                                                    pubgScore += 150
                                                } else {
                                                    pubgScore += 30
                                                }
                                            }
                                        },
                                        enabled = pubgAmmo > 0 && !pubgIsReloading,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                        modifier = Modifier.weight(1.5f).padding(horizontal = 8.dp)
                                    ) {
                                        Text("FIRE TRIGGER", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            pubgIsReloading = true
                                            scope.launch {
                                                delay(1500)
                                                pubgAmmo = 30
                                                pubgIsReloading = false
                                            }
                                        },
                                        enabled = !pubgIsReloading,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    ) {
                                        Text(if (pubgIsReloading) "RELOADING..." else "RELOAD")
                                    }
                                }
                            }
                        }
                    }

                    // 3. ASPHALT 9 ARCADE RACE SIMULATOR
                    "asphalt" -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Stats Headers
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("SPEED: $asphaltSpeed km/h", color = if (asphaltNitroActive) Color(0xFF38BDF8) else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    
                                    // Health Indicators
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        repeat(3) { index ->
                                            Icon(
                                                imageVector = Icons.Filled.Favorite, 
                                                contentDescription = null, 
                                                tint = if (index < asphaltLife) Color.Red else Color.DarkGray,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    
                                    Text("PTS: $asphaltScore", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Interactive Road Lanes Graphics representation
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .background(Color(0xFF14151B))
                                ) {
                                    // Lane Lines
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        repeat(3) { laneIdx ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .border(
                                                        width = 0.5.dp, 
                                                        color = Color.White.copy(alpha = 0.05f)
                                                    ),
                                                contentAlignment = Alignment.TopCenter
                                            ) {
                                                // Dropping Obstacle
                                                if (asphaltObstacleLane == laneIdx) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Warning, 
                                                        contentDescription = "Obstacle", 
                                                        tint = Color(0xFFFBBF24),
                                                        modifier = Modifier
                                                            .offset(y = asphaltObstacleY.dp)
                                                            .size(24.dp)
                                                    )
                                                }

                                                // User Sports Car
                                                if (asphaltLane == laneIdx) {
                                                    Icon(
                                                        imageVector = Icons.Filled.DirectionsCar, 
                                                        contentDescription = "Car", 
                                                        tint = if (asphaltNitroActive) Color(0xFF38BDF8) else Color(0xFFFF5E00),
                                                        modifier = Modifier
                                                            .align(Alignment.BottomCenter)
                                                            .padding(bottom = 8.dp)
                                                            .size(34.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Race Actions
                                if (asphaltLife > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left arrow
                                        Button(
                                            onClick = { asphaltLane = (asphaltLane - 1).coerceAtLeast(0) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                        ) {
                                            Text("◀ MOVE LEFT", fontSize = 10.sp)
                                        }

                                        // Nitro Boost Button
                                        Button(
                                            onClick = {
                                                if (!asphaltNitroActive) {
                                                    asphaltNitroActive = true
                                                    asphaltSpeed = 380
                                                    asphaltNitroRemaining = 4
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (asphaltNitroActive) Color(0xFF0284C7) else Color(0xFFFF5E00)
                                            )
                                        ) {
                                            Text(if (asphaltNitroActive) "NITRO! ($asphaltNitroRemaining s)" else "NITRO BOOST", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Right arrow
                                        Button(
                                            onClick = { asphaltLane = (asphaltLane + 1).coerceAtMost(2) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                        ) {
                                            Text("MOVE RIGHT ▶", fontSize = 10.sp)
                                        }
                                    }
                                } else {
                                    // Restart Game
                                    Button(
                                        onClick = {
                                            asphaltLife = 3
                                            asphaltScore = 0
                                            asphaltSpeed = 240
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Text("RESTART RACE", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 4. MONOPOLY GO! CASUAL SIMULATOR
                    "monopoly" -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("CASH: $${String.format(Locale.US, "%.1f", monopolyCash)}M", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("NET WORTH: $monopolyNetWorth", color = Color(0xFFFFB300), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Interactive Board Tile Display
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "CURRENT SPOT: ${monopolyBoardSpaces[monopolyBoardPosition]}", 
                                            fontSize = 11.sp, 
                                            color = Color.LightGray, 
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = monopolyEventText, 
                                            fontSize = 12.sp, 
                                            color = Color.White, 
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Dice rolling animations
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(monopolyDice1.toString(), color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(monopolyDice2.toString(), color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                // Roll Action
                                Button(
                                    onClick = {
                                        if (!monopolyIsRolling) {
                                            monopolyIsRolling = true
                                            scope.launch {
                                                // Fake Dice rolling movement
                                                repeat(6) {
                                                    monopolyDice1 = (1..6).random()
                                                    monopolyDice2 = (1..6).random()
                                                    delay(100)
                                                }
                                                val totalMoves = monopolyDice1 + monopolyDice2
                                                monopolyBoardPosition = (monopolyBoardPosition + totalMoves) % monopolyBoardSpaces.size
                                                
                                                // Trigger event based on space landed
                                                val spaceLanded = monopolyBoardSpaces[monopolyBoardPosition]
                                                val earnings = when (spaceLanded) {
                                                    "START (Pass GO!)" -> {
                                                        monopolyEventText = "Passed GO! Earned +$200M!"
                                                        200.0
                                                    }
                                                    "Oppo Research Labs" -> {
                                                        monopolyEventText = "Invested in research! Dynamic yield +$150M!"
                                                        150.0
                                                    }
                                                    "Chance Card" -> {
                                                        val lucky = listOf(
                                                            "Chance Card: Dividend payout! +$300M" to 300.0,
                                                            "Chance Card: Speed tax rebate! +$100M" to 100.0,
                                                            "Chance Card: Luxury flight cashback! +$400M" to 400.0
                                                        ).random()
                                                        monopolyEventText = lucky.first
                                                        lucky.second
                                                    }
                                                    "Dimensity Foundry" -> {
                                                        monopolyEventText = "Co-developed MediaTek Dimensity 9400 chipset! Royalties +$600M!"
                                                        600.0
                                                    }
                                                    "Breeno Headquarters" -> {
                                                        monopolyEventText = "Breeno AI customized automation! Optimization bonus +$250M!"
                                                        250.0
                                                    }
                                                    "Community Chest" -> {
                                                        monopolyEventText = "Landed on Community Chest! Shared winnings +$180M!"
                                                        180.0
                                                    }
                                                    "Hasselblad Optics Studio" -> {
                                                        monopolyEventText = "Released Hasselblad Portrait Filter! Global royalties +$450M!"
                                                        450.0
                                                    }
                                                    else -> {
                                                        monopolyEventText = "Landed on partner network spot! Earned +$80M!"
                                                        80.0
                                                    }
                                                }
                                                monopolyCash += earnings
                                                monopolyNetWorth += (earnings * 0.8).toInt()
                                                monopolyIsRolling = false
                                            }
                                        }
                                    },
                                    enabled = !monopolyIsRolling,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Text(if (monopolyIsRolling) "ROLLING BOARD..." else "ROLL THE DICE", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. COLLAPSIBLE COLOROS HYPERBOOST GAMING PANEL SIDEBAR
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pull Tab handle at the left-center of the screen
            AnimatedVisibility(
                visible = !showHyperBoost,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(
                            0.5.dp, 
                            Color.White.copy(alpha = 0.4f), 
                            RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp)
                        )
                        .clickable { showHyperBoost = true }
                )
            }

            // Actual HyperBoost Assistant slide-out panel
            AnimatedVisibility(
                visible = showHyperBoost,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it }),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .width(230.dp)
                        .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                        .background(Color(0xFF0F111A).copy(alpha = 0.94f))
                        .border(
                            0.5.dp, 
                            if (viewModel.isPerformanceMode) Color(0xFFEF4444).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f), 
                            RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.DoubleArrow, contentDescription = null, tint = Color(0xFF00A2E8), modifier = Modifier.size(16.dp))
                                    Text("HyperBoost 3.0", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { showHyperBoost = false },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text("ColorOS Gaming Engine", fontSize = 10.sp, color = Color.Gray)
                        }

                        // Gaming dials Telemetry Specs
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Simulated CPU Load", fontSize = 11.sp, color = Color.LightGray)
                                Text(if (viewModel.isPerformanceMode) "84%" else "48%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00A2E8))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active GPU Load", fontSize = 11.sp, color = Color.LightGray)
                                Text(if (viewModel.isPerformanceMode) "92%" else "54%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Display Rate", fontSize = 11.sp, color = Color.LightGray)
                                Text("${viewModel.refreshRate} Hz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Clock Speed", fontSize = 11.sp, color = Color.LightGray)
                                Text(String.format(Locale.US, "%.2f GHz", viewModel.cpuFrequency), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.1f))

                        // Toggles
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f).padding(top = 10.dp)) {
                            // GT Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GT PERFORMANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (viewModel.isPerformanceMode) Color(0xFFEF4444) else Color.White)
                                Switch(
                                    checked = viewModel.isPerformanceMode,
                                    onCheckedChange = { viewModel.isPerformanceMode = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEF4444), checkedTrackColor = Color(0xFFEF4444).copy(alpha = 0.3f))
                                )
                            }

                            // Mistouch Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mistouch Guard", fontSize = 11.sp, color = Color.LightGray)
                                Switch(
                                    checked = isMistouchPrevented,
                                    onCheckedChange = { isMistouchPrevented = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00A2E8))
                                )
                            }

                            // Touch optimization slider
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Touch Sensitivity", fontSize = 10.sp, color = Color.Gray)
                                    Text("${(touchSensitivity * 100).toInt()}%", fontSize = 10.sp, color = Color(0xFF00A2E8))
                                }
                                Slider(
                                    value = touchSensitivity,
                                    onValueChange = { touchSensitivity = it },
                                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFF00A2E8))
                                )
                            }
                        }

                        // Bottom GT notification badge
                        if (viewModel.isPerformanceMode) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                    .border(0.5.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = "GT MODE ACTIVE: 3.63GHz octa-core thermal max limits unlocked.",
                                    color = Color(0xFFEF4444),
                                    fontSize = 8.sp,
                                    lineHeight = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
