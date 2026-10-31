package com.trodar.presentation.theme

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun defaultTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
)

@Composable
fun defaultCardColors(): CardColors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
    contentColor = MaterialTheme.colorScheme.onSurface
)

@Composable
fun primaryIconTint(): Color = MaterialTheme.colorScheme.primary

@Composable
fun secondaryIconTint(): Color = MaterialTheme.colorScheme.secondary

@Composable
fun errorIconTint(): Color = MaterialTheme.colorScheme.error

@Composable
fun primaryTextColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun secondaryTextColor(): Color = MaterialTheme.colorScheme.secondary

@Composable
fun onSurfaceTextColor(): Color = MaterialTheme.colorScheme.onSurface
