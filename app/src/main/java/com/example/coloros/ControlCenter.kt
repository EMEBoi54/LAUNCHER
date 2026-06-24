package com.example.coloros

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlCenterOverlay(
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        // Sliding Sheet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (viewModel.isDarkMode) Color(0xFF111318) else Color(0xFFECEFF4),
                            if (viewModel.isDarkMode) Color(0xFF1A1D24).copy(alpha = 0.98f) else Color(0xFFF0F4F8).copy(alpha = 0.98f)
                        )
                    )
                )
                .clickable(enabled = true, onClick = {}) // consume clicks to prevent closing
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (Time, Date, Settings Shortcut)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.currentTimeString,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            color = if (viewModel.isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = viewModel.currentDateString,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick settings edit
                        IconButton(
                            onClick = { viewModel.isDarkMode = !viewModel.isDarkMode },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (viewModel.isDarkMode) Color(0xFF2E333D) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (viewModel.isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Theme Toggle",
                                tint = if (viewModel.isDarkMode) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Open Launcher settings directly
                        IconButton(
                            onClick = { 
                                viewModel.activeApp = AppType.SETTINGS
                                onDismiss()
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (viewModel.isDarkMode) Color(0xFF2E333D) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.size(36.dp).testTag("cc_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Launcher Settings",
                                tint = if (viewModel.isDarkMode) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFDC2626).copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Close",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Top Toggles WiFi & Bluetooth (Large 2x2 cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LargeCCCard(
                        title = "Wi-Fi",
                        subtitle = viewModel.wifiName,
                        isActive = viewModel.isWifiEnabled,
                        icon = Icons.Filled.Wifi,
                        onClick = { viewModel.toggleWifi() },
                        isDarkMode = viewModel.isDarkMode,
                        activeColor = Color(0xFF00A2E8), // Aquamorphic teal-blue
                        modifier = Modifier.weight(1f).testTag("cc_wifi_tile")
                    )

                    LargeCCCard(
                        title = "Bluetooth",
                        subtitle = viewModel.bluetoothDevice,
                        isActive = viewModel.isBluetoothEnabled,
                        icon = Icons.Filled.Bluetooth,
                        onClick = { viewModel.toggleBluetooth() },
                        isDarkMode = viewModel.isDarkMode,
                        activeColor = Color(0xFF00A2E8),
                        modifier = Modifier.weight(1f).testTag("cc_bluetooth_tile")
                    )
                }

                // Sliders section (Brightness & Volume side by side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CCSlider(
                        value = viewModel.screenBrightness,
                        onValueChange = { viewModel.screenBrightness = it },
                        icon = Icons.Filled.LightMode,
                        label = "Brightness",
                        isDarkMode = viewModel.isDarkMode,
                        activeColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )

                    CCSlider(
                        value = viewModel.volumeLevel,
                        onValueChange = { viewModel.volumeLevel = it },
                        icon = if (viewModel.volumeLevel == 0f) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                        label = "Volume",
                        isDarkMode = viewModel.isDarkMode,
                        activeColor = Color(0xFF00A2E8),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick Settings Grid (3x3 circular toggles)
                Text(
                    text = "QUICK FUNCTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                val quickSettings = listOf(
                    QuickSettingItem("Flashlight", Icons.Filled.FlashlightOn, viewModel.isFlashlightOn, { viewModel.toggleFlashlight() }, Color(0xFFEAB308)),
                    QuickSettingItem("Mute", Icons.Filled.VolumeOff, viewModel.isSilentMode, { viewModel.toggleSilent() }, Color(0xFFEC4899)),
                    QuickSettingItem("Eye Comfort", Icons.Filled.Visibility, viewModel.isEyeComfortEnabled, { viewModel.isEyeComfortEnabled = !viewModel.isEyeComfortEnabled }, Color(0xFF10B981)),
                    QuickSettingItem("Mobile Data", Icons.Filled.SignalCellular4Bar, viewModel.isMobileDataEnabled, { viewModel.isMobileDataEnabled = !viewModel.isMobileDataEnabled }, Color(0xFF3B82F6)),
                    QuickSettingItem("SuperVOOC Power", Icons.Filled.ElectricBolt, viewModel.isCharging, { viewModel.toggleCharging() }, Color(0xFF10B981)),
                    QuickSettingItem("Find Connect Hub", Icons.Filled.DeviceHub, viewModel.activeApp == AppType.CONNECT_HUB, { 
                        viewModel.activeApp = AppType.CONNECT_HUB
                        onDismiss()
                    }, Color(0xFF8B5CF6)),
                    QuickSettingItem("Smart Sidebar", Icons.Filled.MenuOpen, viewModel.isSmartSidebarExpanded, { viewModel.isSmartSidebarExpanded = !viewModel.isSmartSidebarExpanded }, Color(0xFF06B6D4)),
                    QuickSettingItem("Quick Timer", Icons.Filled.AlarmOn, viewModel.activeFluidCloud == FluidCloudType.TIMER, {
                        if (viewModel.activeFluidCloud == FluidCloudType.TIMER) {
                            viewModel.stopTimer()
                        } else {
                            viewModel.startTimer()
                        }
                    }, Color(0xFFF59E0B)),
                    QuickSettingItem("Screenshots", Icons.Filled.ContentCut, false, {
                        // Triggers a simulated screenshot and files transfer!
                        viewModel.simulateFileTransfer("Screenshot_FindX8_ColorOS15.png")
                        onDismiss()
                    }, Color(0xFFEC4899))
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quickSettings) { item ->
                        QuickTile(
                            item = item,
                            isDarkMode = viewModel.isDarkMode
                        )
                    }
                }

                // Mini Active Player Widget (Bottom of Control Center)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (viewModel.isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "Playing Icon",
                        tint = Color(0xFF00A2E8),
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.musicTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.isDarkMode) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = viewModel.musicArtist,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                    IconButton(
                        onClick = { viewModel.togglePlayPauseMusic() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (viewModel.musicIsPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = if (viewModel.isDarkMode) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LargeCCCard(
    title: String,
    subtitle: String,
    isActive: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isActive) activeColor else (if (isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) Color.White.copy(alpha = 0.25f) else (if (isDarkMode) Color(0xFF2E333D) else Color(0xFFCBD5E1))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) Color.White else (if (isDarkMode) Color.White else Color.Black),
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else (if (isDarkMode) Color.White else Color.Black)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isActive) Color.White.copy(alpha = 0.8f) else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CCSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    label: String,
    isDarkMode: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${(value * 100).toInt()}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Styled slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = if (isDarkMode) Color(0xFF333842) else Color(0xFFCBD5E1)
            ),
            modifier = Modifier.height(20.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class QuickSettingItem(
    val label: String,
    val icon: ImageVector,
    val isActive: Boolean,
    val onClick: () -> Unit,
    val activeColor: Color
)

@Composable
fun QuickTile(
    item: QuickSettingItem,
    isDarkMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { item.onClick() }
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (item.isActive) item.activeColor else (if (isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (item.isActive) Color.White else (if (isDarkMode) Color.White else Color.Black),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(76.dp)
        )
    }
}
