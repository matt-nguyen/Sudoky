package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nghianguyen.ui.theme.LocalSpacing

/**
 * A dialog displayed when the Sudoku game is finished.
 *
 * @param onDismiss Callback when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishedGameDialog(onDismiss: () -> Unit) {
    val spacing = LocalSpacing.current

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(PlayScreenConstants.FINISHED_DIALOG_CORNER_RADIUS),
            border =
                BorderStroke(
                    PlayScreenConstants.DEFAULT_BORDER_WIDTH,
                    MaterialTheme.colorScheme.outline,
                ),
        ) {
            Column(
                modifier = Modifier.padding(spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Game Finished!")
            }
        }
    }
}
