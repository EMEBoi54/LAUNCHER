package com.example.coloros

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FindXConnectHub(
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = viewModel.isDarkMode
    val cardBackground = if (isDark) Color(0xFF1E2129) else Color(0xFFECEFF4)
    val textPrimary = if (isDark) Color.White else Color.Black

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isDark) Color(0xFF2E333D) else Color(0xFFE2E8F0)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }
                Column {
                    Text(
                        text = "Oppo Link",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Legitimate ColorOS 15 Interconnectivity",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Oppo-Connect Active",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Ecosystem Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00A2E8), Color(0xFF00468C))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Filled.DeviceHub,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "ColorOS Smart Interconnection",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Connect seamlessly to your OPPO Pad, Watch, and PC for real-time clipboard sharing, multi-screen mirroring, and instant file sync via OppoShare.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // SECTION 1: AVAILABLE ECOSYSTEM DEVICES
            item {
                Text(
                    text = "NEARBY ECOSYSTEM DEVICES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }

            // OPPO Pad 3 Pro Card
            item {
                DeviceConnectCard(
                    deviceName = "OPPO Pad 3 Pro",
                    deviceType = "12.1\" Android Tablet (Dimensity 9400)",
                    icon = Icons.Filled.TabletAndroid,
                    state = viewModel.padConnectState,
                    onConnect = { viewModel.connectDevice("pad") },
                    onDisconnect = { viewModel.disconnectDevice("pad") },
                    isDark = isDark,
                    cardBg = cardBackground,
                    textPrimary = textPrimary
                ) {
                    // Custom features unlocked when connected
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Divider(color = Color.Gray.copy(alpha = 0.15f))
                        Text(
                            text = "UNLOCKED FEATURES:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00A2E8)
                        )
                        
                        // OppoShare file transfer simulation
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF2A2D35) else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "🚀 OppoShare - Quick File Transfer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = "Send full-resolution RAW Hasselblad photos or document drafts to Pad 3 Pro.",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { viewModel.simulateFileTransfer("Hasselblad_RAW_Portrait.jpg") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2E8)),
                                        enabled = !viewModel.isTransferring,
                                        modifier = Modifier.weight(1f).height(30.dp).testTag("sync_photo_button"),
                                        contentPadding = PaddingValues(vertical = 0.dp)
                                    ) {
                                        Text("Send RAW Portrait", fontSize = 10.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.simulateFileTransfer("Meeting_Notes_ColorOS15.pdf") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2E8)),
                                        enabled = !viewModel.isTransferring,
                                        modifier = Modifier.weight(1f).height(30.dp),
                                        contentPadding = PaddingValues(vertical = 0.dp)
                                    ) {
                                        Text("Send PDF Draft", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Shared Clipboard status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Assignment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Text("Shared Cloud Clipboard", fontSize = 12.sp, color = textPrimary)
                            }
                            Switch(
                                checked = true,
                                onCheckedChange = {},
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00A2E8))
                            )
                        }

                        OutlinedTextField(
                            value = viewModel.sharedClipboard,
                            onValueChange = { viewModel.sharedClipboard = it },
                            label = { Text("Clipboard Sync Content", fontSize = 10.sp) },
                            textStyle = TextStyle(fontSize = 11.sp, color = textPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00A2E8),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // OPPO Watch 4 Pro Card
            item {
                DeviceConnectCard(
                    deviceName = "OPPO Watch 4 Pro",
                    deviceType = "Curved OLED Smartwatch (Dual Engine)",
                    icon = Icons.Filled.Watch,
                    state = viewModel.watchConnectState,
                    onConnect = { viewModel.connectDevice("watch") },
                    onDisconnect = { viewModel.disconnectDevice("watch") },
                    isDark = isDark,
                    cardBg = cardBackground,
                    textPrimary = textPrimary
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Divider(color = Color.Gray.copy(alpha = 0.15f))
                        Text(
                            text = "LIVE HEALTH METRICS & TELEMETRY SYNCED:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Animated Heart Rate
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF2A2D35) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                    val scale by infiniteTransition.animateFloat(
                                        initialValue = 0.85f,
                                        targetValue = 1.15f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(600, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "heartScale"
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Heart rate",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .rotate(scale * 5f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("72 bpm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    Text("ECG & Pulse Sync", fontSize = 9.sp, color = Color.Gray)
                                }
                            }

                            // Steps count
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF2A2D35) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsRun,
                                        contentDescription = "Steps",
                                        tint = Color(0xFF00A2E8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("7,423", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    Text("Daily Steps Tracker", fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                        }

                        // Watch Remote Camera Shutter simulation
                        Button(
                            onClick = { 
                                viewModel.activeApp = AppType.CAMERA
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth().height(32.dp).testTag("watch_camera_trigger")
                        ) {
                            Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Watch Remote Shutter Camera", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // OPPO Book Pro (PC Connect) Card
            item {
                DeviceConnectCard(
                    deviceName = "ColorOS Book Pro X15",
                    deviceType = "ColorOS Link for Windows PC Mirroring",
                    icon = Icons.Filled.Computer,
                    state = viewModel.pcConnectState,
                    onConnect = { viewModel.connectDevice("pc") },
                    onDisconnect = { viewModel.disconnectDevice("pc") },
                    isDark = isDark,
                    cardBg = cardBackground,
                    textPrimary = textPrimary
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Divider(color = Color.Gray.copy(alpha = 0.15f))
                        Text(
                            text = "MULTI-SCREEN WORKSTATION RUNNING:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6)
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF2A2D35) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF8B5CF6),
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text("Workstation Mirroring Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    Text("Simulating low-latency PC screen cast.", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: CONNECTION DIAGNOSTICS & TRUST
            item {
                Text(
                    text = "CONNECTION SECURITY & FRAMEWORK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = Color(0xFF10B981))
                            Text(
                                text = "Legitimate Interconnect Signature",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }

                        Text(
                            text = "This terminal mimics genuine Oppo ColorOS 15 ecosystems using certified local protocols. Device linking is cryptographically paired and secured via the ColorOS Keystore.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp
                        )

                        Button(
                            onClick = {
                                viewModel.triggerFluidCloud(FluidCloudType.CHARGING)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF2E333D) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.fillMaxWidth().height(32.dp).testTag("diagnostics_button")
                        ) {
                            Text("Run Framework Self-Test", fontSize = 11.sp, color = textPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceConnectCard(
    deviceName: String,
    deviceType: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    state: ConnectState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    isDark: Boolean,
    cardBg: Color,
    textPrimary: Color,
    unlockedContent: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF2A2D35) else Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = deviceName,
                            tint = if (state == ConnectState.CONNECTED) Color(0xFF00A2E8) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = deviceName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = deviceType,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Connect Button State
                when (state) {
                    ConnectState.DISCONNECTED -> {
                        Button(
                            onClick = onConnect,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2E8)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("connect_${deviceName.replace(" ", "_")}")
                        ) {
                            Text("Connect", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    ConnectState.CONNECTING -> {
                        Box(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00A2E8),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    ConnectState.CONNECTED -> {
                        Button(
                            onClick = onDisconnect,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Disconnect", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Expanded customized feature options when connected!
            AnimatedVisibility(
                visible = state == ConnectState.CONNECTED,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                unlockedContent()
            }
        }
    }
}
