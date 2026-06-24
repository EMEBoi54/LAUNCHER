package com.example.coloros

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FluidCloudCapsule(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    // Auto collapse if the active event becomes NONE
    LaunchedEffect(viewModel.activeFluidCloud) {
        if (viewModel.activeFluidCloud == FluidCloudType.NONE) {
            isExpanded = false
        } else {
            // Briefly expand transient ones
            if (viewModel.activeFluidCloud == FluidCloudType.CHARGING) {
                isExpanded = true
                kotlinx.coroutines.delay(2500)
                isExpanded = false
            }
        }
    }

    val activeType = viewModel.activeFluidCloud

    // If there is nothing, show a small punch-hole/camera pill, which also serves as an entry point!
    val backgroundColor = Color.Black
    val cornerRadius = 24.dp

    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .widthIn(min = 100.dp, max = 340.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable {
                if (activeType != FluidCloudType.NONE) {
                    isExpanded = !isExpanded
                } else {
                    // Tap black hole to show a fun Quick Alert!
                    viewModel.triggerFluidCloud(FluidCloudType.CHARGING)
                }
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (activeType == FluidCloudType.NONE) {
            // Standard punch-hole camera styling
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF222222))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Breeno Sense",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Active Fluid Cloud Pill
            if (!isExpanded) {
                // COLLAPSED PILL STATE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(22.dp)
                ) {
                    when (activeType) {
                        FluidCloudType.MUSIC -> {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = "Music Playing",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = viewModel.musicTitle,
                                color = Color.White,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 120.dp)
                            )
                        }
                        FluidCloudType.TIMER -> {
                            Icon(
                                imageVector = Icons.Filled.HourglassTop,
                                contentDescription = "Timer Running",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(14.dp)
                            )
                            val remaining = viewModel.timerRemainingSeconds
                            val mins = remaining / 60
                            val secs = remaining % 60
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        FluidCloudType.CHARGING -> {
                            Icon(
                                imageVector = Icons.Filled.FlashOn,
                                contentDescription = "SuperVOOC",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SUPERVOOC ${viewModel.batteryLevel}%",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        FluidCloudType.FILE_TRANSFER -> {
                            CircularProgressIndicator(
                                progress = { viewModel.transferProgress },
                                color = Color(0xFF10B981),
                                trackColor = Color(0xFF333333),
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (viewModel.isTransferring) "Sending..." else "Synced",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                        else -> {}
                    }
                }
            } else {
                // EXPANDED LIVE ACTIVITY CARD STATE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (activeType) {
                        FluidCloudType.MUSIC -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF1E3A8A))))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MusicNote,
                                        contentDescription = "Disc",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = viewModel.musicTitle,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = viewModel.musicArtist,
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.togglePlayPauseMusic() },
                                        modifier = Modifier.size(28.dp).testTag("fluid_music_play_pause")
                                    ) {
                                        Icon(
                                            imageVector = if (viewModel.musicIsPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.activeFluidCloud = FluidCloudType.NONE },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Dismiss",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            // Custom progress bar
                            LinearProgressIndicator(
                                progress = { 0.45f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp)),
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0xFF222222)
                            )
                        }

                        FluidCloudType.TIMER -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Alarm,
                                    contentDescription = "Timer Icon",
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(28.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    val remaining = viewModel.timerRemainingSeconds
                                    val mins = remaining / 60
                                    val secs = remaining % 60
                                    Text(
                                        text = String.format("%02d:%02d", mins, secs),
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ColorOS Smart Timer",
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { viewModel.stopTimer() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("fluid_timer_stop")
                                ) {
                                    Text("Stop", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        FluidCloudType.CHARGING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF065F46)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.FlashOn,
                                        contentDescription = "Flash Charging",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SUPERVOOC 100W",
                                        color = Color(0xFF10B981),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Charging: ${viewModel.batteryLevel}%",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        FluidCloudType.FILE_TRANSFER -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF111827)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Transferring",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = viewModel.lastTransferredFile ?: "OppoShare Syncing",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val pct = (viewModel.transferProgress * 100).toInt()
                                    Text(
                                        text = "Connecting to Pad Pro... $pct%",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                                CircularProgressIndicator(
                                    progress = { viewModel.transferProgress },
                                    color = Color(0xFF10B981),
                                    trackColor = Color(0xFF222222),
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
