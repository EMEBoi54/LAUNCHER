package com.example.coloros

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SmartSidebarHandle(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Box(
        modifier = modifier
            .width(8.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
            .background(Color.White.copy(alpha = 0.45f))
            .clickable { onClick() }
            .testTag("smart_sidebar_handle")
    )
}

@Composable
fun SmartSidebarPanel(
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculator States
    var calcInput by remember { mutableStateOf("") }
    var calcResult by remember { mutableStateOf("") }
    var activeOp by remember { mutableStateOf<String?>(null) }
    var opVal by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        // Sidebar panel slide from right
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .align(Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            if (viewModel.isDarkMode) Color(0xFF1E2129).copy(alpha = 0.96f) else Color(0xFFF1F5F9).copy(alpha = 0.96f),
                            if (viewModel.isDarkMode) Color(0xFF14171E) else Color(0xFFE2E8F0)
                        )
                    )
                )
                .clickable(enabled = true, onClick = {}) // consume clicks
                .padding(horizontal = 14.dp)
                .padding(top = 48.dp, bottom = 16.dp)
        ) {
            // Sidebar header or handle
            Column(
                modifier = Modifier
                    .width(14.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Collapse",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sidebar main content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuOpen,
                        contentDescription = null,
                        tint = Color(0xFF00A2E8),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Smart Sidebar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.isDarkMode) Color.White else Color.Black
                    )
                }

                Divider(color = Color.Gray.copy(alpha = 0.2f))

                // Section 1: Quick Tools
                Text(
                    text = "QUICK TOOLS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SidebarToolButton(
                        label = "Screenshot",
                        icon = Icons.Filled.ContentCut,
                        color = Color(0xFFEC4899),
                        isDarkMode = viewModel.isDarkMode,
                        onClick = {
                            viewModel.simulateFileTransfer("Screenshot_FindX8_Sidebar.png")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).testTag("sidebar_screenshot")
                    )

                    SidebarToolButton(
                        label = "Translate",
                        icon = Icons.Filled.Translate,
                        color = Color(0xFF10B981),
                        isDarkMode = viewModel.isDarkMode,
                        onClick = {
                            viewModel.sharedClipboard = "Translated via Breeno Sense: 'Hi, Find X8 Pro'"
                            viewModel.triggerFluidCloud(FluidCloudType.FILE_TRANSFER)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SidebarToolButton(
                        label = "Oppo Link",
                        icon = Icons.Filled.DeviceHub,
                        color = Color(0xFF8B5CF6),
                        isDarkMode = viewModel.isDarkMode,
                        onClick = {
                            viewModel.activeApp = AppType.CONNECT_HUB
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Section 2: Quick Note Widget
                Text(
                    text = "QUICK MEMO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = viewModel.sidebarNoteText,
                    onValueChange = { viewModel.sidebarNoteText = it },
                    placeholder = { Text("Write a quick memo...", fontSize = 11.sp) },
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = if (viewModel.isDarkMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("sidebar_notes_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (viewModel.isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0),
                        unfocusedContainerColor = if (viewModel.isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF00A2E8),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                // Section 3: ColorOS Smart Calculator
                Text(
                    text = "SMART CALCULATOR",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (viewModel.isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
                        .padding(10.dp)
                ) {
                    // Calculator Display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (calcInput.isEmpty()) "0" else calcInput,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.isDarkMode) Color.White else Color.Black,
                            maxLines = 1
                        )
                        if (calcResult.isNotEmpty()) {
                            Text(
                                text = "= $calcResult",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Keypad Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("7", "8", "9", "/").forEach { char ->
                            CalcKey(
                                char = char,
                                isDarkMode = viewModel.isDarkMode,
                                isOp = char == "/",
                                onClick = {
                                    if (char == "/") {
                                        if (calcInput.isNotEmpty()) {
                                            opVal = calcInput.toFloatOrNull() ?: 0f
                                            activeOp = "/"
                                            calcInput += " / "
                                        }
                                    } else {
                                        calcInput += char
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Keypad Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("4", "5", "6", "*").forEach { char ->
                            CalcKey(
                                char = char,
                                isDarkMode = viewModel.isDarkMode,
                                isOp = char == "*",
                                onClick = {
                                    if (char == "*") {
                                        if (calcInput.isNotEmpty()) {
                                            opVal = calcInput.toFloatOrNull() ?: 0f
                                            activeOp = "*"
                                            calcInput += " * "
                                        }
                                    } else {
                                        calcInput += char
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Keypad Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1", "2", "3", "-").forEach { char ->
                            CalcKey(
                                char = char,
                                isDarkMode = viewModel.isDarkMode,
                                isOp = char == "-",
                                onClick = {
                                    if (char == "-") {
                                        if (calcInput.isNotEmpty()) {
                                            opVal = calcInput.toFloatOrNull() ?: 0f
                                            activeOp = "-"
                                            calcInput += " - "
                                        }
                                    } else {
                                        calcInput += char
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Keypad Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CalcKey(
                            char = "C",
                            isDarkMode = viewModel.isDarkMode,
                            isOp = true,
                            onClick = {
                                calcInput = ""
                                calcResult = ""
                                activeOp = null
                                opVal = 0f
                            },
                            modifier = Modifier.weight(1f).testTag("calc_clear")
                        )
                        CalcKey(
                            char = "0",
                            isDarkMode = viewModel.isDarkMode,
                            isOp = false,
                            onClick = { calcInput += "0" },
                            modifier = Modifier.weight(1f)
                        )
                        CalcKey(
                            char = "=",
                            isDarkMode = viewModel.isDarkMode,
                            isOp = true,
                            onClick = {
                                if (activeOp != null && calcInput.isNotEmpty()) {
                                    val parts = calcInput.split(" ")
                                    if (parts.size >= 3) {
                                        val val2 = parts.last().toFloatOrNull() ?: 0f
                                        val res = when (activeOp) {
                                            "+" -> opVal + val2
                                            "-" -> opVal - val2
                                            "*" -> opVal * val2
                                            "/" -> if (val2 != 0f) opVal / val2 else 0f
                                            else -> 0f
                                        }
                                        calcResult = if (res % 1 == 0f) res.toInt().toString() else String.format("%.2f", res)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("calc_equals")
                        )
                        CalcKey(
                            char = "+",
                            isDarkMode = viewModel.isDarkMode,
                            isOp = true,
                            onClick = {
                                if (calcInput.isNotEmpty()) {
                                    opVal = calcInput.toFloatOrNull() ?: 0f
                                    activeOp = "+"
                                    calcInput += " + "
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarToolButton(
    label: String,
    icon: ImageVector,
    color: Color,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDarkMode) Color(0xFF22252C) else Color(0xFFE2E8F0))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CalcKey(
    char: String,
    isDarkMode: Boolean,
    isOp: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isOp) Color(0xFF00A2E8) else (if (isDarkMode) Color(0xFF2F333D) else Color(0xFFCBD5E1))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOp) Color.White else (if (isDarkMode) Color.White else Color.Black)
        )
    }
}
