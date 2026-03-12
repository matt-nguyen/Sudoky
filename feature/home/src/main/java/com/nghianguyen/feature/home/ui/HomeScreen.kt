package com.nghianguyen.feature.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nghianguyen.feature.home.viewmodel.HomeAction
import com.nghianguyen.feature.home.viewmodel.HomeEvent
import com.nghianguyen.feature.home.viewmodel.HomeScreenState
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    event: SharedFlow<HomeEvent>,
    onAction: (HomeAction) -> Unit,
    onHomeScreenResult: (HomeScreenResult) -> Unit
) {

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                is HomeEvent.NavigateToScanner -> onHomeScreenResult(HomeScreenResult.ScanSudoku)
                is HomeEvent.NavigateToPlay -> onHomeScreenResult(HomeScreenResult.ContinueGame(it.gameId))
            }
        }
    }

    val spacing = LocalSpacing.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Sudoku",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(spacing.xLarge))

            ScanPuzzleButton(onClick = { onAction(HomeAction.OnScanClick) })

            Spacer(modifier = Modifier.height(spacing.medium))

            ContinueGameButton(state.gameInProgress, onClick = { onAction(HomeAction.OnContinueClick) })

            Spacer(modifier = Modifier.height(spacing.large))

            Text(
                text = "Scan a Sudoku puzzle or continue where you left off.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ScanPuzzleButton(onClick: () -> Unit) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {

        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(LocalSpacing.current.medium))

        Text(
            text = "Scan Puzzle",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ContinueGameButton(isEnabled: Boolean, onClick: () -> Unit) {


    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        enabled = isEnabled,
        border = BorderStroke(1.dp, if (isEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {

        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(LocalSpacing.current.medium))

        Text(
            text = "Continue Game",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
